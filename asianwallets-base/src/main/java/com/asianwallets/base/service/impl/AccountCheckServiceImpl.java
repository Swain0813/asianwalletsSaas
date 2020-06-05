package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.*;
import com.asianwallets.base.rabbitmq.RabbitMQSender;
import com.asianwallets.base.service.AccountCheckService;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.FinanceConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.AD3CheckAccountDTO;
import com.asianwallets.common.dto.AccCheckDTO;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 对账业务接口实现类
 */
@Slf4j
@Service
@Transactional
public class AccountCheckServiceImpl implements AccountCheckService {

    //ad3的商户编号
    @Value("${custom.merchantCode}")
    private String merchantCode;

    //Help2Pay商户编号
    @Value("${custom.help2PayMerchantCode}")
    private String help2PayMerchantCode;

    @Autowired
    private CheckAccountLogMapper checkAccountLogMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CheckAccountMapper checkAccountMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private CheckAccountAuditMapper checkAccountAuditMapper;

    @Autowired
    private CheckAccountAuditHistoryMapper checkAccountAuditHistoryMapper;


    /**
     * 分页查询对账日志管理
     * @param searchAccountCheckDTO
     * @return
     */
    @Override
    public PageInfo<CheckAccountLog> pageAccountCheckLog(SearchAccountCheckDTO searchAccountCheckDTO) {
        return new PageInfo<CheckAccountLog>(checkAccountLogMapper.pageAccountCheckLog(searchAccountCheckDTO));
    }

    /**
     * 导入通道对账单
     * @param file
     * @return
     */
    @Override
    public Object channelAccountCheck(String username,MultipartFile file) {
        //获取文件名
        String fileName = file.getOriginalFilename();
        String[] name = fileName.split("\\.")[0].split("_");

        //校验文件是否上传
        if (StringUtils.isNotEmpty(redisService.get(fileName))) {
            throw new BusinessException(EResultEnum.FILE_EXIST.getCode());
        } else {
            redisService.set(fileName, fileName, 24 * 60 * 60);
        }

        //校验文件格式
        if (StringUtils.isEmpty(fileName) || !fileName.toLowerCase().matches("^.+\\.(?i)(csv)$")) {
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        //校验文件名称 (AD3_20190404.csv)
        if (!FinanceConstant.FinanceChannelNameMap.containsKey(name[0])) {
            throw new BusinessException(EResultEnum.NAME_ERROR.getCode());
        }
        try {
            AccCheckDTO accCheckDTO = null;
            //ad3对账单退款单号为退款单原订单号，不支持对账
            if (name[0].equals("AD3")) {
                accCheckDTO = ad3Read(file);
            } else if (name[0].equals("HELP2PAY")) {
                //针对HELP2PAY的对账逻辑是对账单上游退款和收单都是成功的状态
                accCheckDTO = help2PayRead(file);
            }
            //check账单
            this.doCheck(accCheckDTO.getAd3SDMap(), accCheckDTO.getAd3TKMap(), name,username);
        } catch (Exception e) {
            log.error("***********************ad3通道对账发生异常*******************", e);
        }
        return null;
    }


    /**
     *check账单
     * @param ad3SDMap
     * @param ad3TKMap
     * @param name
     */
    @Async
    public void doCheck(Map<String, AD3CheckAccountDTO> ad3SDMap, Map<String, AD3CheckAccountDTO> ad3TKMap, String[] name,String username) {
        //获取昨天起始时间
        Date startTime = DateToolUtils.getDayStart(DateToolUtils.addDay(DateToolUtils.StringToDate(name[1]), -1));
        //获取昨天结束时间
        Date endTime = DateToolUtils.getDayEnd(DateToolUtils.addDay(DateToolUtils.StringToDate(name[1]), -1));
        //文件对应的通道编号
        List<String> list = FinanceConstant.FinanceChannelNameMap.get(name[0]);
        if(list.size()==0){
            log.info("************************通道编号数据不存在************************");
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        List<String> tkList = Lists.newArrayList();//退款补单队列
        List<String> sdList = Lists.newArrayList();//收单补单队列
        List<CheckAccount> checkAccountList = checkAccountMapper.getDataByType(FinanceConstant.FINACE_WAIT, startTime, endTime);//获取前一天未对账状态的对账单数据
        List<Orders> ordersList = ordersMapper.getYesterDayDate(startTime, endTime, list);
        if(ordersList.size()==0){
            log.info("************************订单表里获取昨天的数据不存在************************");
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        List<OrderRefund> orderRefundList = orderRefundMapper.getYesterDayDate(startTime, endTime, list);
        int cCount = ad3SDMap.size() + ad3TKMap.size();
        int sCount = ordersList.size() + orderRefundList.size();
        BigDecimal uTotalAmount = BigDecimal.ZERO;//平台交易总金额
        BigDecimal cTotalAmount = BigDecimal.ZERO;//通道交易总金额
        BigDecimal uTotalFee = BigDecimal.ZERO;//平台交易手续费
        BigDecimal cTotalFee = BigDecimal.ZERO;//通道交易手续费
        /****************************************************校验收单************************************************************/
        List<CheckAccount> calist = Lists.newArrayList();
        for (Orders orders : ordersList) {
            //累计平台交易金额不添加订单状态为待支付和支付中的金额
            if (!(TradeConstant.ORDER_WAIT_PAY.equals(orders.getTradeStatus()) || TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus()))) {
                uTotalAmount = uTotalAmount.add(orders.getTradeAmount() == null ? BigDecimal.ZERO : orders.getTradeAmount());//累计平台交易金额
                uTotalFee = uTotalFee.add(orders.getChannelFee() == null ? BigDecimal.ZERO : orders.getChannelFee());//累计平台通道手续费
            }
            if (ad3SDMap.containsKey(orders.getId())) {
                redisService.set(orders.getId() + "_" + DateToolUtils.getReqDateE(), orders.getId(), 24 * 60 * 60);
                cTotalAmount = cTotalAmount.add(ad3SDMap.get(orders.getId()).getTradeAmount());//累计上游交易金额
                cTotalFee = cTotalFee.add(ad3SDMap.get(orders.getId()).getChannelRate());//累计上游通道手续费

                CheckAccount checkAccount = new CheckAccount(orders, ad3SDMap.get(orders.getId()));
                checkAccount.setCreateTime(new Date());
                checkAccount.setId(IDS.uuid2());
                checkAccount.setChannelCode(orders.getChannelCode());
                //币种不等
                if (!orders.getTradeCurrency().equals(ad3SDMap.get(orders.getId()).getTradeCurrency())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("币种不等");
                    calist.add(checkAccount);
                    ad3SDMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                //金额不等
                if (orders.getTradeAmount().compareTo(ad3SDMap.get(orders.getId()).getTradeAmount()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("交易金额不等");
                    calist.add(checkAccount);
                    ad3SDMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                //手续费金额不等
                BigDecimal oChanFee = orders.getChannelFee() == null ? BigDecimal.ZERO : orders.getChannelFee();
                if (oChanFee.compareTo(ad3SDMap.get(orders.getId()).getChannelRate()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("手续费金额不等");
                    calist.add(checkAccount);
                    ad3SDMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                if (!orders.getTradeStatus().equals(ad3SDMap.get(orders.getId()).getStatus())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_BUDAN);
                    checkAccount.setRemark("订单状态异常");
                    calist.add(checkAccount);
                    if (TradeConstant.ORDER_PAY_SUCCESS.equals(ad3SDMap.get(orders.getId()).getStatus())) {
                        sdList.add(orders.getId());//平台状态成功添加到补单队列
                    }
                    ad3SDMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                calist.add(checkAccount);
            } else {
                //平台有数据，通道无数据
                if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus()) && !TradeConstant.ORDER_CANNEL_SUCCESS.equals(orders.getCancelStatus())) {
                    CheckAccount checkAccount = new CheckAccount(orders);
                    checkAccount.setCreateTime(new Date());
                    checkAccount.setId(IDS.uuid2());
                    checkAccount.setErrorType(FinanceConstant.FINACE_WAIT);
                    calist.add(checkAccount);
                }
            }
            ad3SDMap.remove(orders.getId());//移除通道map数据
        }
        /****************************************************   校验退款单   ************************************************************/
        for (OrderRefund orders : orderRefundList) {
            uTotalAmount = uTotalAmount.subtract(orders.getTradeAmount() == null ? BigDecimal.ZERO : orders.getTradeAmount());//累计平台交易金额
            uTotalFee = uTotalFee.subtract(orders.getChannelFee() == null ? BigDecimal.ZERO : orders.getChannelFee());//累计平台通道手续费
            //}
            if (ad3TKMap.containsKey(orders.getId())) {
                redisService.set(orders.getId() + "_" + DateToolUtils.getReqDateE(), orders.getId(), 24 * 60 * 60);
                cTotalAmount = cTotalAmount.subtract(ad3TKMap.get(orders.getId()).getTradeAmount());//累计上游交易金额
                cTotalFee = cTotalFee.subtract(ad3TKMap.get(orders.getId()).getChannelRate());//累计上游通道手续费

                CheckAccount checkAccount = new CheckAccount(orders, ad3TKMap.get(orders.getId()));
                checkAccount.setId(IDS.uuid2());
                checkAccount.setChannelCode(orders.getChannelCode());
                checkAccount.setCreateTime(new Date());
                //币种不等
                if (!orders.getTradeCurrency().equals(ad3TKMap.get(orders.getId()).getTradeCurrency())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("币种不等");
                    calist.add(checkAccount);
                    ad3TKMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                //金额不等
                if (orders.getTradeAmount().compareTo(ad3TKMap.get(orders.getId()).getTradeAmount()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("交易金额不等");
                    calist.add(checkAccount);
                    ad3TKMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                //手续费金额不等
                BigDecimal oChanFee = orders.getChannelFee() == null ? BigDecimal.ZERO : orders.getChannelFee();
                if (oChanFee.compareTo(ad3TKMap.get(orders.getId()).getChannelRate()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("手续费金额不等");
                    calist.add(checkAccount);
                    ad3TKMap.remove(orders.getId());//移除通道map数据
                    continue;
                }
                if (!orders.getRefundStatus().equals(ad3TKMap.get(orders.getId()).getStatus())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_BUDAN);
                    checkAccount.setRemark("订单状态异常");
                    calist.add(checkAccount);
                    if (TradeConstant.REFUND_SUCCESS.equals(ad3TKMap.get(orders.getId()).getStatus())) {
                        tkList.add(orders.getId());//添加到补单队列
                    }
                    ad3TKMap.remove(orders.getOrderId());//移除通道map数据\
                    continue;
                }
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                calist.add(checkAccount);
            } else {
                //平台有数据，通道无数据
                CheckAccount checkAccount = new CheckAccount(orders);
                checkAccount.setCreateTime(new Date());
                checkAccount.setId(IDS.uuid2());
                checkAccount.setErrorType(FinanceConstant.FINACE_WAIT);
                calist.add(checkAccount);
            }
            ad3TKMap.remove(orders.getOrderId());//移除通道map数据

        }
        /****************************************************    校验前一天对账记录 - 平台无数据，通道有数据   ******************************************/
        for (CheckAccount checkAccount : checkAccountList) {
            if (ad3SDMap.containsKey(checkAccount.getUOrderId())) { //未结算订单
                redisService.set(checkAccount.getUOrderId() + "_" + DateToolUtils.getReqDateE(), checkAccount.getUOrderId(), 24 * 60 * 60);
                cTotalAmount = cTotalAmount.add(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//累计上游交易金额
                cTotalFee = cTotalFee.add(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//累计上游通道手续费
                //币种不等
                if (!checkAccount.getUChannelNumber().equals(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("币种不等");
                    checkAccount.setCChannelNumber(ad3SDMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3SDMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3SDMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //金额不等
                if (checkAccount.getUTradeAmount().compareTo(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("交易金额不等");
                    checkAccount.setCChannelNumber(ad3SDMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3SDMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3SDMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //手续费金额不等
                if (checkAccount.getUFee().compareTo(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("手续费金额不等");
                    checkAccount.setCChannelNumber(ad3SDMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3SDMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3SDMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //订单状态 (付款成功，退款，撤销中，撤销失败，撤销成功)
                if (!checkAccount.getUStatus().equals(ad3SDMap.get(checkAccount.getUOrderId()).getStatus())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_BUDAN);
                    checkAccount.setRemark("订单状态异常");
                    checkAccount.setCChannelNumber(ad3SDMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3SDMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3SDMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    if(TradeConstant.ORDER_PAY_SUCCESS.equals(ad3SDMap.get(checkAccount.getUOrderId()).getStatus())){
                        sdList.add(checkAccount.getUOrderId());//添加到补单队列
                    }
                    continue;
                }
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                checkAccount.setCChannelNumber(ad3SDMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                checkAccount.setCFee(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                checkAccount.setCOrderId(ad3SDMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                checkAccount.setCTradeAmount(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                checkAccount.setCTradeCurrency(ad3SDMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                ad3SDMap.remove(checkAccount.getUOrderId());//移除通道map数据
                checkAccountMapper.updateByPrimaryKeySelective(checkAccount);

            } else if (ad3TKMap.containsKey(checkAccount.getUOrderId())) {//未结算退款单
                redisService.set(checkAccount.getUOrderId() + "_" + DateToolUtils.getReqDateE(), checkAccount.getUOrderId(), 24 * 60 * 60);
                cTotalAmount = cTotalAmount.subtract(ad3SDMap.get(checkAccount.getUOrderId()).getTradeAmount());//累计上游交易金额
                cTotalFee = cTotalFee.subtract(ad3SDMap.get(checkAccount.getUOrderId()).getChannelRate());//累计上游通道手续费
                //币种不等
                if (!checkAccount.getUChannelNumber().equals(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("币种不等");
                    checkAccount.setCChannelNumber(ad3TKMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3TKMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3TKMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //金额不等
                if (checkAccount.getUTradeAmount().compareTo(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("交易金额不等");
                    checkAccount.setCChannelNumber(ad3TKMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3TKMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3TKMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //手续费金额不等
                if (checkAccount.getUFee().compareTo(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate()) != 0) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
                    checkAccount.setRemark("手续费金额不等");
                    checkAccount.setCChannelNumber(ad3TKMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3TKMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3TKMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    continue;
                }
                //订单状态 (退款成功)
                if (!checkAccount.getUStatus().equals(ad3TKMap.get(checkAccount.getUOrderId()).getStatus())) {
                    checkAccount.setErrorType(FinanceConstant.FINACE_BUDAN);
                    checkAccount.setRemark("订单状态异常");
                    checkAccount.setCChannelNumber(ad3TKMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                    checkAccount.setCFee(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                    checkAccount.setCOrderId(ad3TKMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                    checkAccount.setCTradeAmount(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                    checkAccount.setCTradeCurrency(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                    ad3TKMap.remove(checkAccount.getUOrderId());//移除通道map数据
                    checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
                    if(TradeConstant.REFUND_SUCCESS.equals(ad3TKMap.get(checkAccount.getUOrderId()).getStatus())) {
                        tkList.add(checkAccount.getUOrderId());//添加到补单队列
                    }
                    continue;
                }
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                checkAccount.setCChannelNumber(ad3TKMap.get(checkAccount.getUOrderId()).getChannelNumber());//通道业务流水号
                checkAccount.setCFee(ad3TKMap.get(checkAccount.getUOrderId()).getChannelRate());//通道手续费
                checkAccount.setCOrderId(ad3TKMap.get(checkAccount.getUOrderId()).getOrderId());//通道商户流水号
                checkAccount.setCTradeAmount(ad3TKMap.get(checkAccount.getUOrderId()).getTradeAmount());//通道交易金额
                checkAccount.setCTradeCurrency(ad3TKMap.get(checkAccount.getUOrderId()).getTradeCurrency());//通道交易币种
                ad3TKMap.remove(checkAccount.getUOrderId());//移除通道map数据
                checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
            }
        }
        /******************************************     通道有数据，平台无数据      ***************************************/
        for (String key : ad3SDMap.keySet()) {
            redisService.set(key + "_" + DateToolUtils.getReqDateE(), key, 24 * 60 * 60);
            CheckAccount checkAccount = new CheckAccount(1, ad3SDMap.get(key));
            checkAccount.setCreateTime(new Date());
            checkAccount.setId(IDS.uuid2());
            checkAccount.setCStatus(ad3SDMap.get(key).getStatus());
            checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
            checkAccount.setRemark("通道有数据平台无数据");
            calist.add(checkAccount);
            cTotalAmount = cTotalAmount.add(ad3SDMap.get(key).getTradeAmount());//累计上游交易金额
            cTotalFee = cTotalFee.add(ad3SDMap.get(key).getChannelRate());//累计上游通道手续费
        }
        for (String key : ad3TKMap.keySet()) {
            redisService.set(key + "_" + DateToolUtils.getReqDateE(), key, 24 * 60 * 60);
            CheckAccount checkAccount = new CheckAccount(2, ad3TKMap.get(key));
            checkAccount.setCreateTime(new Date());
            checkAccount.setId(IDS.uuid2());
            checkAccount.setCStatus(ad3TKMap.get(key).getStatus());
            checkAccount.setErrorType(FinanceConstant.FINACE_CACUO);
            checkAccount.setRemark("通道有数据平台无数据");
            calist.add(checkAccount);
            cTotalAmount = cTotalAmount.subtract(ad3TKMap.get(key).getTradeAmount());//累计上游交易金额
            cTotalFee = cTotalFee.subtract(ad3TKMap.get(key).getChannelRate());//累计上游通道手续费
        }
        checkAccountMapper.insertList(calist);//保存详细记录
        /****************************************************   保存日志记录   ************************************************************/
        CheckAccountLog checkAccountLog = new CheckAccountLog();
        checkAccountLog.setCheckTime(DateToolUtils.addDay(new Date(), -1));
        checkAccountLog.setCreateTime(new Date());
        checkAccountLog.setChaTotalAmount(cTotalAmount);
        checkAccountLog.setChaTotalFee(cTotalFee);
        checkAccountLog.setChaTradeCount(cCount);
        checkAccountLog.setSysTradeAmount(uTotalAmount);
        checkAccountLog.setSysTradeFee(uTotalFee);
        checkAccountLog.setSysTradeCount(sCount);
        checkAccountLog.setErrorAmount(uTotalAmount.subtract(cTotalAmount));
        checkAccountLog.setErrorCount(checkAccountMapper.getErrorCount(new Date()));
        checkAccountLog.setCheckFileName(name[0]);
        checkAccountLog.setCreator(username);
        checkAccountLogMapper.insert(checkAccountLog);
        doSupplement(tkList, sdList);//系统补单
    }

    /**
     * 系统补单队列
     * @param tkList
     * @param sdList
     */
    public void doSupplement(List<String> tkList, List<String> sdList) {
        for (String s : tkList) {//退款补单队列
            log.info("------------------ 退款补单队列 -------------- 订单号 ： {}", s);
            rabbitMQSender.send(AD3MQConstant.TC_MQ_FINANCE_TKBUDAN_DL, s);
        }
        for (String s : sdList) {//收单补单队列
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, s);
            log.info("------------------ 收单补单队列 -------------- 订单号 ： {}", s);
            rabbitMQSender.send(AD3MQConstant.TC_MQ_FINANCE_SDBUDAN_DL, JSON.toJSONString(rabbitMassage));
        }
    }

    /**
     *help2Pay 文档解析
     * @param file
     * @return
     */
    private AccCheckDTO help2PayRead(MultipartFile file) {
        AccCheckDTO checkDTO = new AccCheckDTO();
        try {
            //获取文件输入流
            InputStream inputStream = file.getInputStream();
            //字符缓冲流
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            //文件内容Map
            Map<String, AD3CheckAccountDTO> ad3SDMap = new HashMap<>();//收单订单
            Map<String, AD3CheckAccountDTO> ad3TKMap = new HashMap<>();//退款订单
            int num = 0;//订单条数
            String str;
            while ((str = br.readLine()) != null) {
                if (num == 0) {
                    //校验文件内容
                    if (!str.contains("Reference") || !str.contains("Currency") || !str.contains("Amount")) {
                        //文件内容错误
                        log.info("=============== 文件内容错误 ===============");
                        throw new BusinessException(EResultEnum.FILE_CONTENT_ERROR.getCode());
                    }
                } else {
                    if (!StringUtils.isEmpty(str)) {
                        String[] s = str.split("\\,");
                        if (s[3].equals(help2PayMerchantCode)) {
                            //判断内容是否重复
                            if (StringUtils.isNotEmpty(redisService.get(s[2] + "_" + DateToolUtils.getReqDateE()))) {
                                throw new BusinessException(EResultEnum.FILE_EXIST.getCode(), s[3]);
                            }
                            if (s[2].startsWith("O")) {
                                AD3CheckAccountDTO ad3CheckAccountDTO = createAD3CheckAccountDTO(s, "HELP2PAY");
                                ad3SDMap.put(s[2], ad3CheckAccountDTO);
                            } else if (s[2].startsWith("R")) {
                                AD3CheckAccountDTO ad3CheckAccountDTO = createAD3CheckAccountDTO(s, "HELP2PAY");
                                ad3TKMap.put(s[2], ad3CheckAccountDTO);
                            }

                        } else {
                            log.info("----------------商户对账单商户编号异常记录-----------------订单【{}】", str);
                        }
                    }
                }
                num++;
            }
            checkDTO.setAd3SDMap(ad3SDMap);
            checkDTO.setAd3TKMap(ad3TKMap);
        } catch (Exception e) {
            log.info("----------------ad3 文档解析 异常-----------------e【{}】", e);
        }
        return checkDTO;
    }

    /**
     *ad3 文档解析
     * @param file
     * @return
     */
    private AccCheckDTO ad3Read(MultipartFile file) {
        AccCheckDTO checkDTO = new AccCheckDTO();
        try {
            //获取文件输入流
            InputStream inputStream = file.getInputStream();
            //字符缓冲流
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            //文件内容Map
            Map<String, AD3CheckAccountDTO> ad3SDMap = new HashMap<>();//收单订单
            Map<String, AD3CheckAccountDTO> ad3TKMap = new HashMap<>();//退款订单
            int num = 0;//订单条数
            String str;
            while ((str = br.readLine()) != null) {
                if (num == 0) {
                    //校验文件内容
                    if (!str.contains("系统流水号") || !str.contains("交易币种") || !str.contains("交易金额")) {
                        //文件内容错误
                        log.info("=============== 文件内容错误 ===============");
                        throw new BusinessException(EResultEnum.FILE_CONTENT_ERROR.getCode());
                    }
                } else {
                    if (!StringUtils.isEmpty(str)) {
                        String[] s = str.split("\\,");
                        if (s[0].equals(merchantCode)) {
                            //判断内容是否重复
                            if (StringUtils.isNotEmpty(redisService.get(s[3] + "_" + DateToolUtils.getReqDateE()))) {
                                throw new BusinessException(EResultEnum.FILE_EXIST.getCode(), s[3]);
                            }
                            if (s[1].equals("收单")) {
                                AD3CheckAccountDTO ad3CheckAccountDTO = createAD3CheckAccountDTO(s, "AD3");
                                ad3SDMap.put(s[3], ad3CheckAccountDTO);
                            } else if (s[1].equals("退款")) {
                                AD3CheckAccountDTO ad3CheckAccountDTO = createAD3CheckAccountDTO(s, "AD3");
                                ad3TKMap.put(s[3], ad3CheckAccountDTO);
                            }

                        } else {
                            log.info("----------------商户对账单商户编号异常记录-----------------订单【{}】", str);
                        }
                    }
                }
                num++;
            }
            checkDTO.setAd3SDMap(ad3SDMap);
            checkDTO.setAd3TKMap(ad3TKMap);
        } catch (Exception e) {
            log.info("----------------ad3 文档解析 异常-----------------e【{}】", e);
        }
        return checkDTO;
    }

    /**
     *根据文件创建解析之后的数据对象
     * @param s
     * @param name
     * @return
     */
    private AD3CheckAccountDTO createAD3CheckAccountDTO(String[] s, String name) {
        AD3CheckAccountDTO ad3CheckAccountDTO = new AD3CheckAccountDTO();
        if (name.equals("AD3")) {
            ad3CheckAccountDTO.setType(s[1]);
            ad3CheckAccountDTO.setChannelNumber(s[2]);
            ad3CheckAccountDTO.setOrderId(s[3]);
            ad3CheckAccountDTO.setTradeCurrency(s[4]);
            ad3CheckAccountDTO.setTradeAmount(BigDecimal.valueOf(Double.parseDouble(s[5].trim())));
            ad3CheckAccountDTO.setChannelRate(BigDecimal.valueOf(Double.parseDouble(s[6].trim())));
            ad3CheckAccountDTO.setTradeTime(s[7]);
            if (s[1].equals("收单")) {
                ad3CheckAccountDTO.setStatus(TradeConstant.PAYMENT_SUCCESS);
            } else if (s[1].equals("退款")) {
                ad3CheckAccountDTO.setStatus(TradeConstant.REFUND_SUCCESS);
            }
        } else if (name.equals("HELP2PAY")) {
            if (s[2].startsWith("O")) {
                ad3CheckAccountDTO.setType("收单");
                if (s[0].equals("Success")) {
                    ad3CheckAccountDTO.setStatus(TradeConstant.PAYMENT_SUCCESS);
                }
            } else {
                ad3CheckAccountDTO.setType("退款");
                if (s[0].equals("Success")) {
                    ad3CheckAccountDTO.setStatus(TradeConstant.REFUND_SUCCESS);
                }
            }
            //回调我们的通道流水号是055146982，对账单里是55146982
            ad3CheckAccountDTO.setChannelNumber("0" + s[1]);
            ad3CheckAccountDTO.setOrderId(s[2]);
            ad3CheckAccountDTO.setTradeCurrency(s[6]);
            ad3CheckAccountDTO.setTradeAmount(new BigDecimal(s[7]));
            ad3CheckAccountDTO.setChannelRate(new BigDecimal(s[10]));
            ad3CheckAccountDTO.setTradeTime(s[4]);
        }
        return ad3CheckAccountDTO;
    }

    /**
     * 分页查询对账管理详情
     * @param searchAccountCheckDTO
     * @return
     */
    @Override
    public PageInfo<CheckAccount> pageAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO) {
        return new PageInfo<CheckAccount>(checkAccountMapper.pageAccountCheck(searchAccountCheckDTO));
    }

    /**
     * 导出对账管理详情
     * @param searchAccountCheckDTO
     * @return
     */
    @Override
    public List<CheckAccount> exportAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO) {
        return checkAccountMapper.exportAccountCheck(searchAccountCheckDTO);
    }

    /**
     * 差错处理
     *
     * @param
     * @return
     */
    @Override
    public int updateCheckAccount(String checkAccountId, String remark) {
        if (checkAccountAuditMapper.selectByPrimaryKey(checkAccountId) != null) {
            throw new BusinessException(EResultEnum.AUDIT_INFO_EXIENT.getCode());
        }
        CheckAccount checkAccount = checkAccountMapper.selectByPrimaryKey(checkAccountId);
        CheckAccountAudit checkAccountAudit = new CheckAccountAudit();
        BeanUtils.copyProperties(checkAccount, checkAccountAudit);
        checkAccountAudit.setAuditStatus(FinanceConstant.AUDIT_WAIT);
        checkAccountAudit.setErrorType(FinanceConstant.FINACE_SUCCESS);
        checkAccountAudit.setRemark1(remark);
        return checkAccountAuditMapper.insert(checkAccountAudit);
    }

    /**
     * 分页查询对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    @Override
    public PageInfo<CheckAccountAudit> pageAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO) {
        return new PageInfo<CheckAccountAudit>(checkAccountAuditMapper.pageAccountCheckAudit(searchAccountCheckDTO));
    }

    /**
     * 导出对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    @Override
    public List<CheckAccountAudit> exportAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO) {
        return checkAccountAuditMapper.exportAccountCheckAudit(searchAccountCheckDTO);
    }

    /**
     * 差错复核
     *
     * @param
     * @return
     */
    @Override
    public int auditCheckAccount(String checkAccountId, Boolean enable, String remark) {
        CheckAccountAudit ck = checkAccountAuditMapper.selectByPrimaryKey(checkAccountId);
        if (enable) {
            if (ck.getErrorType() == FinanceConstant.FINACE_CACUO) { //差错处理
                CheckAccount checkAccount = new CheckAccount();
                checkAccount.setId(checkAccountId);
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                checkAccount.setRemark1(ck.getRemark1());
                checkAccountAuditMapper.deleteByPrimaryKey(checkAccountId);
                return checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
            } else {//补单
                CheckAccount checkAccount = new CheckAccount();
                if (ck.getTradeType() == 1) {
                    ordersMapper.supplementStatus(ck.getUOrderId(), TradeConstant.ORDER_PAY_SUCCESS, "补单成功");
                    checkAccount.setUStatus(TradeConstant.ORDER_PAY_SUCCESS);
                } else {
                    Orders orders = ordersMapper.selectByPrimaryKey(ck.getUOrderId());
                    if (orders == null) {//订单不存在的场合的判断
                        throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
                    }
                    if (TradeConstant.ORDER_CANNELING.equals(orders.getTradeStatus()) || TradeConstant.ORDER_CANNEL_FALID.equals(orders.getTradeStatus())) {
                        ordersMapper.supplementStatus(ck.getUOrderId(), TradeConstant.ORDER_CANNEL_SUCCESS, "补单成功");
                    }
                    orderRefundMapper.supplementStatus(ck.getUOrderId(), TradeConstant.ORDER_PAY_SUCCESS, "补单成功");
                    checkAccount.setUStatus(TradeConstant.ORDER_PAY_SUCCESS);
                }
                checkAccount.setId(checkAccountId);
                checkAccount.setErrorType(FinanceConstant.FINACE_SUCCESS);
                checkAccount.setRemark1(ck.getRemark1());
                checkAccountAuditMapper.deleteByPrimaryKey(checkAccountId);
                return checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
            }
        } else {
            CheckAccount checkAccount = new CheckAccount();
            checkAccount.setId(checkAccountId);
            checkAccount.setRemark2(remark);
            checkAccountMapper.updateByPrimaryKeySelective(checkAccount);
            ck.setAuditStatus(FinanceConstant.AUDIT_FAIL);
            ck.setRemark2(remark);
            CheckAccountAuditHistory checkAccountAuditHistory = new CheckAccountAuditHistory();
            BeanUtils.copyProperties(ck, checkAccountAuditHistory);
            checkAccountAuditHistory.setId(IDS.uuid2());
            checkAccountAuditHistory.setCId(ck.getId());
            checkAccountAuditHistoryMapper.insert(checkAccountAuditHistory);
            return checkAccountAuditMapper.deleteByPrimaryKey(checkAccountId);
        }

    }

}

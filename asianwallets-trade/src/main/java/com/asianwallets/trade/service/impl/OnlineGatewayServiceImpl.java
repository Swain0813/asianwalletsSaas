package com.asianwallets.trade.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.CashierDTO;
import com.asianwallets.common.dto.MockOrdersDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.common.vo.OnlineTradeScanVO;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.dao.BankIssuerIdMapper;
import com.asianwallets.trade.dao.MerchantMapper;
import com.asianwallets.trade.dao.MerchantProductMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.*;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OnlineGatewayService;
import com.asianwallets.trade.utils.HandlerContext;
import com.asianwallets.trade.utils.SettleDateUtil;
import com.asianwallets.trade.vo.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
public class OnlineGatewayServiceImpl implements OnlineGatewayService {

    @Autowired
    private BankIssuerIdMapper bankIssuerIdMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private MerchantProductMapper merchantProductMapper;

    @Autowired
    private HandlerContext handlerContext;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private Ad3Service ad3Service;

    //收银台地址
    @Value("${custom.cashierDeskUrl}")
    private String cashierDeskUrl;

    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    @Override
    public BaseResponse gateway(OnlineTradeDTO onlineTradeDTO) {

        //判断
        if (!StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {
            //直连
            return directConnection(onlineTradeDTO);
        }
        //间连
        return indirectConnection(onlineTradeDTO);
    }

    /**
     * 直连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private BaseResponse directConnection(OnlineTradeDTO onlineTradeDTO) {
        //信息落地
        log.info("---------------【线上直连收单开始】---------------OnlineTradeDTO:{}", JSON.toJSONString(onlineTradeDTO));
        Currency currency = commonRedisDataService.getCurrencyByCode(onlineTradeDTO.getOrderCurrency());
        //检查订单
        checkOnlineOrders(onlineTradeDTO, currency);
        //检查商户信息
        Merchant merchant = checkMerchant(onlineTradeDTO);
        //检查机构信息
        Institution institution = commonRedisDataService.getInstitutionById(merchant.getInstitutionId());
        //可选参数校验
        InstitutionRequestParameters institutionRequestParameters = commonRedisDataService.getInstitutionRequestByIdAndDirection(institution.getId(), TradeConstant.TRADE_ONLINE);
        checkRequestParameters(onlineTradeDTO, institutionRequestParameters);
        //校验订单金额
        if (onlineTradeDTO.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("-----------------【线上交易】下单信息记录--------------【订单金额不合法】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //获取商户信息
        BasicInfoVO basicInfoVO = getOnlineInfo(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getIssuerId());
        basicInfoVO.setMerchant(merchant);
        basicInfoVO.setInstitution(institution);
        //设置订单属性
        Orders orders = setOnlineOrdersInfo(onlineTradeDTO, basicInfoVO);
        //截取币种默认值
        commonBusinessService.interceptDigit(orders, currency);
        //校验是否换汇
        commonBusinessService.swapRateByPayment(basicInfoVO, orders);
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        orders.setTradeStatus(TradeConstant.PAYMENT_WAIT);
        log.info("-----------------【线上直连】--------------【订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        log.info("---------------【线上直连落地结束】---------------");
        log.info("---------------【线上直连收单上报通道开始】---------------");
        //上报通道
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        try {
            //上报通道
            ChannelsAbstract channelsAbstract = handlerContext.getInstance(basicInfoVO.getChannel().getServiceNameMark());
            BaseResponse baseResponse = channelsAbstract.onlinePay(orders, basicInfoVO.getChannel());
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            onlineTradeVO = (OnlineTradeVO) baseResponse.getData();
            //间联
            if (!StringUtils.isEmpty(baseResponse.getData()) && TradeConstant.INDIRECTCONNECTION.equals(orders.getConnectMethod())) {
                if (!StringUtils.isEmpty(onlineTradeVO.getRespCode())) {
                    OnlineTradeScanVO onlineTradeScanVO = new OnlineTradeScanVO();
                    BeanUtils.copyProperties(onlineTradeVO, onlineTradeScanVO);
                    onlineTradeScanVO.setTradeAmount(orders.getTradeAmount());
                    onlineTradeScanVO.setTradeCurrency(orders.getTradeCurrency());
                    baseResponse.setData(onlineTradeScanVO);
                    log.info("---------------【线上直连收单结束】--------------- onlineTradeScanVO:{}", JSON.toJSONString(onlineTradeScanVO));
                    return baseResponse;
                }
            }
            baseResponse.setData(onlineTradeVO);
            log.info("---------------【线上直连收单结束】--------------- onlineTradeVO:{}", JSON.toJSONString(onlineTradeVO));
            return baseResponse;
        } catch (Exception e) {
            log.info("==================【线上直连】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
    }

    /**
     * 间连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return BaseResponse 线上收单输出实体
     */
    private BaseResponse indirectConnection(OnlineTradeDTO onlineTradeDTO) {
        //信息落地
        log.info("---------------【线上间连收单开始】---------------OnlineTradeDTO:{}", JSON.toJSONString(onlineTradeDTO));
        Currency currency = commonRedisDataService.getCurrencyByCode(onlineTradeDTO.getOrderCurrency());
        //检查订单
        checkOnlineOrders(onlineTradeDTO, currency);
        //检查商户信息
        Merchant merchant = checkMerchant(onlineTradeDTO);
        //检查机构信息
        Institution institution = commonRedisDataService.getInstitutionById(merchant.getInstitutionId());
        //可选参数校验
        InstitutionRequestParameters institutionRequestParameters = commonRedisDataService.getInstitutionRequestByIdAndDirection(institution.getId(), TradeConstant.TRADE_ONLINE);
        checkRequestParameters(onlineTradeDTO, institutionRequestParameters);
        //校验订单金额
        if (onlineTradeDTO.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("-----------------【线上间连交易】下单信息记录--------------【订单金额不合法】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        OnlineMerchantVO onlineMerchantVO = merchantMapper.selectRelevantInfo(onlineTradeDTO.getMerchantId(), null, TradeConstant.TRADE_ONLINE, onlineTradeDTO.getLanguage());
        //检查订单
        if (onlineMerchantVO.getProductList() == null || onlineMerchantVO.getProductList().size() == 0) {
            log.info("==================【间连下单校验订单信息】==================获取产品信息异常 商户订单号:{}", onlineTradeDTO.getOrderNo());
            //获取产品信息异常
            throw new BusinessException(EResultEnum.GET_PRODUCT_INFO_ERROR.getCode());
        }
        if (onlineMerchantVO.getProductList().get(0).getChannelList() == null || onlineMerchantVO.getProductList().get(0).getChannelList().size() == 0) {
            log.info("==================【间连下单校验订单信息】==================获取通道信息失败 商户订单号:{}", onlineTradeDTO.getOrderNo());
            //获取通道信息异常
            throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
        }
        //收银台
        CheckoutCounterURLVO checkoutCounterURLVO = new CheckoutCounterURLVO();
        Orders orders = new Orders();
        //设置间连订单属性
        setIndirectConnectionValue(onlineTradeDTO, merchant, institution, orders);
        ordersMapper.insert(orders);//落地
        //响应实体
        checkoutCounterURLVO.setCheckoutCounterURL(cashierDeskUrl + "?id=" + orders.getId());
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(checkoutCounterURLVO);
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        log.info("---------------【线上间连收单结束】---------------checkoutCounterURLVO:{}", JSON.toJSONString(checkoutCounterURLVO));
        return baseResponse;
    }

    /**
     * 收银台收单
     *
     * @param cashierDTO 收银台收单实体
     * @return BaseResponse
     */
    @Override
    public BaseResponse cashierGateway(CashierDTO cashierDTO) {
        log.info("----------【线上收银台】下单开始----------【请求参数】 cashierDTO:{}", JSON.toJSONString(cashierDTO));
        //解密线上参数
        CashierDTO dto;
        try {
            dto = decryptCashierSignMsg(cashierDTO);
        } catch (Exception e) {
            log.info("----------【线上收银台】下单信息记录----------收银台【参数解密】错误");
            throw new BusinessException(EResultEnum.CASH_COUNTER_PARAMETER_DECRYPTION_ERROR.getCode());
        }
        log.info("--------------【线上收银台】收银台解密后参数--------------dto:{}", JSON.toJSONString(dto));
        if (StringUtils.isEmpty(dto.getExchangeTime()) || "false".equals(dto.getExchangeTime())) {
            log.info("----------【线上收银台】下单信息记录----------收银台【换汇时间】不存在");
            throw new BusinessException(EResultEnum.CASHIER_EXCHANGE_TIME_DOES_NOT_EXIST.getCode());
        }
        if (StringUtils.isEmpty(dto.getOriginalRate()) || "false".equals(dto.getOriginalRate())) {
            log.info("----------【线上收银台】下单信息记录----------收银台【原始汇率】不存在");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //重复请求
        if (!commonBusinessService.repeatedRequests(dto.getMerchantId(), dto.getOrderId())) {
            log.info("-----------------【线上收银台】下单信息记录--------------【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //判断原始订单存不存在
        Orders orders = ordersMapper.selectByMerchantOrderId(dto.getMerchantOrderId());
        if (orders == null) {
            log.info("----------【线上收银台】下单信息记录----------收银台原始订单不存在 merchantOrderId:{}", cashierDTO.getMerchantOrderId());
            throw new BusinessException(EResultEnum.ORIGINAL_ORDER_DOES_NOT_EXIST.getCode());
        }
        if (!StringUtils.isEmpty(orders.getReportNumber())) {
            log.info("-----------------【线上收银台】下单信息记录-----------------订单号已存在");
            throw new BusinessException(EResultEnum.MERCHANT_ORDER_ID_EXIST.getCode());
        }
        //获取商户信息
        Merchant merchant = commonRedisDataService.getMerchantById(dto.getMerchantId());
        BasicInfoVO basicInfoVO = getOnlineInfo(dto.getMerchantId(), dto.getIssuerId());
        basicInfoVO.setInstitution(commonRedisDataService.getInstitutionById(merchant.getInstitutionId()));
        basicInfoVO.setMerchant(merchant);
        //截取币种默认值
        Currency currency = commonRedisDataService.getCurrencyByCode(dto.getOrderCurrency());
        commonBusinessService.interceptDigit(orders, currency);
        setCashierValue(basicInfoVO, orders);
        //校验是否换汇
        commonBusinessService.swapRateByPayment(basicInfoVO, orders);
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        orders.setTradeStatus(TradeConstant.PAYMENT_WAIT);
        //用作判断收银台的重复订单号
        orders.setReportNumber("Q" + IDS.uniqueID().toString());
        log.info("-----------------【线上收银台】--------------【订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.updateByPrimaryKey(orders);
        log.info("---------------【线上收银台落地结束】---------------");
        log.info("---------------【线上收银台收单上报通道开始】---------------");
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        try {
            //上报通道
            ChannelsAbstract channelsAbstract = handlerContext.getInstance(basicInfoVO.getChannel().getServiceNameMark());
            BaseResponse baseResponse = channelsAbstract.onlinePay(orders, basicInfoVO.getChannel());
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            onlineTradeVO = (OnlineTradeVO) baseResponse.getData();
            //间联
            if (!StringUtils.isEmpty(baseResponse.getData()) && TradeConstant.INDIRECTCONNECTION.equals(orders.getConnectMethod())) {
                if (!StringUtils.isEmpty(onlineTradeVO.getRespCode())) {
                    OnlineTradeScanVO onlineTradeScanVO = new OnlineTradeScanVO();
                    BeanUtils.copyProperties(onlineTradeVO, onlineTradeScanVO);
                    onlineTradeScanVO.setTradeAmount(orders.getTradeAmount());
                    onlineTradeScanVO.setTradeCurrency(orders.getTradeCurrency());
                    baseResponse.setData(onlineTradeScanVO);
                    log.info("---------------【线上收银台收单结束】--------------- onlineTradeScanVO:{}", JSON.toJSONString(onlineTradeScanVO));
                    return baseResponse;
                }
            }
            baseResponse.setData(onlineTradeVO);
            log.info("---------------【线上收银台收单结束】--------------- onlineTradeVO:{}", JSON.toJSONString(onlineTradeVO));
            return baseResponse;
        } catch (Exception e) {
            log.info("==================【线上收银台】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
    }

    /**
     * 设置直连订单属性
     *
     * @param onlineTradeDTO 收单实体
     * @param basicInfoVO    基础信息实体
     * @return Orders
     */
    private Orders setOnlineOrdersInfo(OnlineTradeDTO onlineTradeDTO, BasicInfoVO basicInfoVO) {
        Merchant merchant = basicInfoVO.getMerchant();
        Institution institution = basicInfoVO.getInstitution();
        MerchantProduct merchantProduct = basicInfoVO.getMerchantProduct();
        Product product = basicInfoVO.getProduct();
        Channel channel = basicInfoVO.getChannel();
        Orders orders = new Orders();
        orders.setId("O" + IDS.uniqueID().toString().substring(0, 15));
        orders.setInstitutionId(institution.getId());
        orders.setInstitutionName(institution.getCnName());
        orders.setMerchantId(merchant.getId());
        orders.setMerchantType(merchant.getMerchantType());
        orders.setMerchantName(merchant.getCnName());
        orders.setSecondMerchantName(merchant.getCnName());
        orders.setSecondMerchantCode(merchant.getId());
        orders.setAgentCode(merchant.getAgentId());
        //INDIRECTCONNECTION 间连
        orders.setConnectMethod(StringUtils.isEmpty(onlineTradeDTO.getIssuerId()) ? TradeConstant.INDIRECTCONNECTION : TradeConstant.DIRECTCONNECTION);
        orders.setAgentName(commonRedisDataService.getMerchantById(merchant.getId()).getCnName());
//        orders.setGroupMerchantCode(merchant.getGroupMasterAccount());
//        orders.setGroupMerchantName(merchant.getGroupMasterAccount());
        //代理商
        if (!StringUtils.isEmpty(merchant.getAgentId())) {
            Merchant agentMerchant = commonRedisDataService.getMerchantById(merchant.getAgentId());
            if (agentMerchant != null) {
                orders.setAgentCode(agentMerchant.getId());
                orders.setAgentName(agentMerchant.getCnName());
            }
        }
        //截取URL
        commonBusinessService.getUrl(onlineTradeDTO.getServerUrl(), orders);
        orders.setTradeType(product.getTransType());
        orders.setTradeDirection(product.getTradeDirection());
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG((onlineTradeDTO.getOrderTime())));
        orders.setMerchantOrderId(onlineTradeDTO.getOrderNo());
        orders.setOrderAmount(onlineTradeDTO.getOrderAmount());
        orders.setOrderCurrency(onlineTradeDTO.getOrderCurrency());
        orders.setProductCode(product.getProductCode());
        orders.setProductName(onlineTradeDTO.getProductName());
        orders.setProductDescription(onlineTradeDTO.getProductDescription());
        orders.setChannelCode(channel.getChannelCode());
        orders.setRemark8(channel.getChannelAgentId());
        orders.setChannelName(channel.getChannelCnName());
        orders.setTradeCurrency(channel.getCurrency());
        orders.setTradeStatus(TradeConstant.PAYMENT_START);
        orders.setPayMethod(merchantProduct.getPayType());
        orders.setPayerName(onlineTradeDTO.getPayerName());
        orders.setPayerAccount(onlineTradeDTO.getPayerAccount());
        orders.setPayerBank(onlineTradeDTO.getPayerBank());
        orders.setPayerEmail(onlineTradeDTO.getPayerEmail());
        orders.setPayerPhone(onlineTradeDTO.getPayerPhone());
        orders.setPayerAddress(onlineTradeDTO.getPayerAddress());
        //判断结算周期类型
        if (TradeConstant.DELIVERED.equals(merchantProduct.getSettleCycle())) {
            //妥投结算
            orders.setProductSettleCycle(TradeConstant.FUTURE_TIME);
        } else {
            //产品结算周期
            orders.setProductSettleCycle(SettleDateUtil.getSettleDate(merchantProduct.getSettleCycle()));
        }
        orders.setFloatRate(merchantProduct.getFloatRate());
        orders.setIssuerId(channel.getIssuerId());
        orders.setBankName(basicInfoVO.getBankName());
        orders.setBrowserUrl(onlineTradeDTO.getBrowserUrl());
        orders.setServerUrl(onlineTradeDTO.getServerUrl());
        orders.setLanguage(onlineTradeDTO.getLanguage());
        orders.setSign(null);
        orders.setCreateTime(new Date());
        orders.setCreator(merchant.getCnName());
        orders.setRemark1(onlineTradeDTO.getRemark1());
        orders.setRemark2(onlineTradeDTO.getRemark2());
        orders.setRemark3(onlineTradeDTO.getRemark3());
        return orders;
    }

    /**
     * 间连订单设置属性
     *
     * @param onlineTradeDTO 订单实体
     * @param merchant       商户
     * @param institution    机构
     */
    private void setIndirectConnectionValue(OnlineTradeDTO onlineTradeDTO, Merchant merchant, Institution institution, Orders orders) {
        orders.setId("O" + IDS.uniqueID().toString().substring(0, 15));
        orders.setInstitutionId(institution.getId());
        orders.setInstitutionName(institution.getCnName());
        orders.setMerchantId(merchant.getId());
        orders.setMerchantName(merchant.getCnName());
        orders.setSecondMerchantName(merchant.getCnName());
        orders.setSecondMerchantCode(merchant.getId());
        orders.setAgentCode(merchant.getAgentId());
        //INDIRECTCONNECTION 间连
        orders.setConnectMethod(StringUtils.isEmpty(onlineTradeDTO.getIssuerId()) ? TradeConstant.INDIRECTCONNECTION : TradeConstant.DIRECTCONNECTION);
        orders.setAgentName(commonRedisDataService.getMerchantById(merchant.getId()).getCnName());
//        orders.setGroupMerchantCode(merchant.getGroupMasterAccount());
//        orders.setGroupMerchantName(merchant.getGroupMasterAccount());
        //代理商
        if (!StringUtils.isEmpty(merchant.getAgentId())) {
            Merchant agentMerchant = commonRedisDataService.getMerchantById(merchant.getAgentId());
            if (agentMerchant != null) {
                orders.setAgentCode(agentMerchant.getId());
                orders.setAgentName(agentMerchant.getCnName());
            }
        }
        //截取URL
        commonBusinessService.getUrl(onlineTradeDTO.getServerUrl(), orders);
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG((onlineTradeDTO.getOrderTime())));
        orders.setMerchantOrderId(onlineTradeDTO.getOrderNo());
        orders.setMerchantType(merchant.getMerchantType());
        orders.setOrderAmount(onlineTradeDTO.getOrderAmount());
        orders.setOrderCurrency(onlineTradeDTO.getOrderCurrency());
        orders.setProductName(onlineTradeDTO.getProductName());
        orders.setProductDescription(onlineTradeDTO.getProductDescription());
        orders.setTradeStatus(TradeConstant.PAYMENT_START);
        orders.setPayerName(onlineTradeDTO.getPayerName());
        orders.setPayerAccount(onlineTradeDTO.getPayerAccount());
        orders.setPayerBank(onlineTradeDTO.getPayerBank());
        orders.setPayerEmail(onlineTradeDTO.getPayerEmail());
        orders.setPayerPhone(onlineTradeDTO.getPayerPhone());
        orders.setPayerAddress(onlineTradeDTO.getPayerAddress());
        orders.setBrowserUrl(onlineTradeDTO.getBrowserUrl());
        orders.setServerUrl(onlineTradeDTO.getServerUrl());
        orders.setLanguage(onlineTradeDTO.getLanguage());
        orders.setSign(null);
        orders.setCreateTime(new Date());
        orders.setCreator(merchant.getCnName());
        orders.setRemark1(onlineTradeDTO.getRemark1());
        orders.setRemark2(onlineTradeDTO.getRemark2());
        orders.setRemark3(onlineTradeDTO.getRemark3());
    }

    /**
     * 收银台订单设置属性
     *
     * @param basicInfoVO 基础信息
     * @param orders      订单实体
     */
    private void setCashierValue(BasicInfoVO basicInfoVO, Orders orders) {
        MerchantProduct merchantProduct = basicInfoVO.getMerchantProduct();
        Product product = basicInfoVO.getProduct();
        Channel channel = basicInfoVO.getChannel();
        orders.setTradeType(product.getTransType());
        orders.setTradeDirection(product.getTradeDirection());
        orders.setProductCode(product.getProductCode());
        orders.setChannelCode(channel.getChannelCode());
        orders.setChannelName(channel.getChannelCnName());
        orders.setRemark8(channel.getChannelAgentId());
        orders.setTradeCurrency(channel.getCurrency());
        orders.setPayMethod(merchantProduct.getPayType());
        //判断结算周期类型
        if (TradeConstant.DELIVERED.equals(merchantProduct.getSettleCycle())) {
            //妥投结算
            orders.setProductSettleCycle(TradeConstant.FUTURE_TIME);
        } else {
            //产品结算周期
            orders.setProductSettleCycle(SettleDateUtil.getSettleDate(merchantProduct.getSettleCycle()));
        }
        orders.setFloatRate(merchantProduct.getFloatRate());
        orders.setIssuerId(channel.getIssuerId());
        orders.setBankName(basicInfoVO.getBankName());
    }

    /**
     * 收银台基础信息
     *
     * @param orderId  订单ID
     * @param language 语言
     * @return BaseResponse
     */
    @Override
    public BaseResponse cashier(String orderId, String language) {
        log.info("---------------【收银台获取基础信息】---------------orderId:{}", JSON.toJSONString(orderId));
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Orders orders = ordersMapper.selectByPrimaryKey(orderId);
        if (orders == null) {
            log.info("--------------收银台订单不存在-------------- 订单号:{}", orderId);
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        OnlineMerchantVO onlineMerchantVO = merchantMapper.selectRelevantInfo(orders.getMerchantId(), null, TradeConstant.TRADE_ONLINE, language);
        if (onlineMerchantVO == null) {
            log.info("-----------收银台商户CODE对应的产品通道信息不存在-----------订单id:{},orders:{},商户code:{}", orderId, JSON.toJSON(orders), orders.getMerchantId());
            throw new BusinessException(EResultEnum.MERCHANT_PRODUCT_DOES_NOT_EXIST.getCode());//机构产品通道信息不存在
        }
        onlineMerchantVO.setOrderId(orders.getId());
        onlineMerchantVO.setOrders(orders);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        baseResponse.setData(onlineMerchantVO);
        return baseResponse;
    }

    /**
     * 收银台换汇金额计算
     *
     * @param calcRateDTO 订单输入实体
     * @return 换汇计算输出实体
     */
    @Override
    public BaseResponse calcCashierExchangeRate(CalcRateDTO calcRateDTO) {
        log.info("---------收银台换汇开始---------CalcRateDTO:{}", JSON.toJSON(calcRateDTO));
        OnlineMerchantVO online = merchantMapper.selectRelevantInfo(calcRateDTO.getMerchantCode(), calcRateDTO.getPayType(), TradeConstant.TRADE_ONLINE, null);
        if (online.getProductList().size() == 0) {
            throw new BusinessException(EResultEnum.INSTITUTION_PRODUCT_STATUS_ABNORMAL.getCode());
        }
        OnlineProductVO product = online.getProductList().get(0);
        BigDecimal ipFloatRate = product.getIpFloatRate();
        CashierCalcRateVO calcRateVO = new CashierCalcRateVO();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(EResultEnum.SUCCESS.getCode());
        String defaultValue = commonRedisDataService.getCurrencyByCode(calcRateDTO.getTradeCurrency()).getDefaults();
        if (StringUtils.isEmpty(defaultValue)) {
            log.info("-----------换汇错误 结束换汇 交易币种不存在-----------CalcRateDTO:{}", JSON.toJSON(calcRateDTO));
            throw new BusinessException(EResultEnum.PRODUCT_CURRENCY_NO_SUPPORT.getCode());
        }
        if (calcRateDTO.getOrderCurrency().equals(calcRateDTO.getTradeCurrency())) {
            log.info("---------同币种---------");
            calcRateVO.setExchangeTime(new Date());
            int bitPos = defaultValue.indexOf(".");
            int numOfBits = 0;
            if (bitPos != -1) {
                numOfBits = defaultValue.length() - bitPos - 1;
            }
            calcRateVO.setTradeAmount(String.valueOf(calcRateDTO.getAmount().setScale(numOfBits, BigDecimal.ROUND_HALF_UP)));
            calcRateVO.setExchangeStatus(TradeConstant.SWAP_SUCCESS);
            calcRateVO.setOriginalRate(BigDecimal.ONE);//原始汇率
            //交易币种转订单币种汇率
            calcRateVO.setReverseRate(BigDecimal.ONE);
            baseResponse.setData(calcRateVO);
            log.info("---------收银台换汇结束【同币种】---------CashierCalcRateVO:{}", JSON.toJSON(calcRateVO));
            return baseResponse;
        }
        CalcExchangeRateVO crv = commonBusinessService.calcExchangeRate(calcRateDTO.getOrderCurrency(), calcRateDTO.getTradeCurrency(), ipFloatRate, calcRateDTO.getAmount());
        BigDecimal tradeAmount = crv.getTradeAmount();
        if (tradeAmount == null) {
            log.info("-----------换汇错误 结束换汇-----------CalcRateDTO:{}", JSON.toJSON(calcRateDTO));
            throw new BusinessException(EResultEnum.SYS_ERROR_CREATE_ORDER_FAIL.getCode());
        }
        calcRateVO.setExchangeRate(crv.getExchangeRate());
        calcRateVO.setExchangeStatus(crv.getExchangeStatus());
        calcRateVO.setExchangeTime(crv.getExchangeTime());
        //订单币种转交易币种汇率
        calcRateVO.setOriginalRate(crv.getOriginalRate());
        //交易币种转订单币种汇率
        calcRateVO.setReverseRate(crv.getReverseRate());
        int bitPos = defaultValue.indexOf(".");
        int numOfBits = 0;
        if (bitPos != -1) {
            numOfBits = defaultValue.length() - bitPos - 1;
        }
        calcRateVO.setTradeAmount(String.valueOf(crv.getTradeAmount().setScale(numOfBits, BigDecimal.ROUND_HALF_UP)));
        baseResponse.setData(calcRateVO);
        baseResponse.setMsg("SUCCESS");
        log.info("---------收银台换汇结束---------CashierCalcRateVO:{}", JSON.toJSON(calcRateVO));
        return baseResponse;
    }

    /**
     * 检查商户信息
     *
     * @param onlineTradeDTO 线上订单输入实体
     * @return Merchant
     */
    private Merchant checkMerchant(OnlineTradeDTO onlineTradeDTO) {
        //商户
        Merchant merchant = commonRedisDataService.getMerchantById(onlineTradeDTO.getMerchantId());
        if (merchant == null) {
            log.info("-----------------【线上直连】下单信息记录--------------【商户不存在】");
            throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
        }
        if (!merchant.getEnabled()) {
            log.info("-----------------【线上直连】下单信息记录--------------【商户被禁用】");
            throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
        }
        return merchant;
    }

    /**
     * 检查订单
     *
     * @param onlineTradeDTO 订单输入实体
     * @param currency       币种
     */
    private void checkOnlineOrders(OnlineTradeDTO onlineTradeDTO, Currency currency) {
        //重复请求
        if (!commonBusinessService.repeatedRequests(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getOrderNo())) {
            log.info("-----------------【线上直连】下单信息记录--------------【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
       /* //签名校验
        if (!commonBusinessService.checkUniversalSign(onlineTradeDTO)) {
            log.info("-----------------【线上下单】下单信息记录--------------【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }*/
       /* //查询订单号是否重复
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(onlineTradeDTO.getOrderNo());
        if (oldOrder != null) {
            log.info("-----------------【线上下单】下单信息记录-----------------订单号已存在");
            throw new BusinessException(EResultEnum.INSTITUTION_ORDER_ID_EXIST.getCode());
        }*/
        //检查币种默认值
        if (!commonBusinessService.checkOrderCurrency(onlineTradeDTO.getOrderCurrency(), onlineTradeDTO.getOrderAmount(), currency)) {
            log.info("-----------------【线上下单】下单信息记录--------------【订单金额不符合的当前币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
    }

    /**
     * 获取线上基础信息
     *
     * @param merchantId 商户ID
     * @param issuerId   issuerId
     * @return BasicInfoVO 线上基础信息实体
     */
    private BasicInfoVO getOnlineInfo(String merchantId, String issuerId) {
        List<OnlineInfoDetailVO> onlineInfoDetailVOList = merchantProductMapper.selectOnlineInfoDetail(merchantId, issuerId);
        if (onlineInfoDetailVOList == null || onlineInfoDetailVOList.size() == 0) {
            log.info("-----------------【线上获取基础信息】-----------------商户产品通道信息不存在 直连从数据库查询关系信息为空");
            throw new BusinessException(EResultEnum.INSTITUTION_PRODUCT_CHANNEL_NOT_EXISTS.getCode());
        }
        BasicInfoVO basicInfoVO = new BasicInfoVO();
        for (OnlineInfoDetailVO onlineInfoDetailVO : onlineInfoDetailVOList) {
            //通道
            Channel channel = commonRedisDataService.getChannelByChannelCode(onlineInfoDetailVO.getChannelCode());
            //映射表
            BankIssuerId bankIssuerId = bankIssuerIdMapper.selectBankAndIssuerId(channel.getCurrency(), onlineInfoDetailVO.getBankName(), channel.getChannelCode());
            if (bankIssuerId != null) {
                channel.setIssuerId(bankIssuerId.getIssuerId());
            } else {
                continue;
            }
            //产品
            Product product = commonRedisDataService.getProductByCode(onlineInfoDetailVO.getProductCode());
            //商户产品
            MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(merchantId, product.getId());
            basicInfoVO.setBankName(onlineInfoDetailVO.getBankName());
            basicInfoVO.setChannel(channel);
            basicInfoVO.setProduct(product);
            basicInfoVO.setMerchantProduct(merchantProduct);
        }
        if (basicInfoVO.getChannel() == null) {
            log.info("-----------------【线上获取基础信息】-----------------映射表信息未配置");
            throw new BusinessException(EResultEnum.INSTITUTION_PRODUCT_CHANNEL_NOT_EXISTS.getCode());
        }
        return basicInfoVO;
    }

    /**
     * 校验请求参数
     *
     * @param onlineTradeDTO               线上收单实体
     * @param institutionRequestParameters 机构请求参数实体
     */
    private void checkRequestParameters(OnlineTradeDTO onlineTradeDTO, InstitutionRequestParameters institutionRequestParameters) {
        if (institutionRequestParameters.getBrowserUrl() && StringUtils.isEmpty(onlineTradeDTO.getBrowserUrl())) {
            log.info("==================【线上收单校验机构请求参数】==================【浏览器回调地址为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductName() && StringUtils.isEmpty(onlineTradeDTO.getProductName())) {
            log.info("==================【线上收单校验机构请求参数】==================【商品名称为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductDescription() && StringUtils.isEmpty(onlineTradeDTO.getProductDescription())) {
            log.info("==================【线上收单校验机构请求参数】==================【商品描述为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerName() && StringUtils.isEmpty(onlineTradeDTO.getPayerName())) {
            log.info("==================【线上收单校验机构请求参数】==================【付款人姓名为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerPhone() && StringUtils.isEmpty(onlineTradeDTO.getPayerPhone())) {
            log.info("==================【线上收单校验机构请求参数】==================【付款人手机为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerEmail() && StringUtils.isEmpty(onlineTradeDTO.getPayerEmail())) {
            log.info("==================【线上收单校验机构请求参数】==================【付款人邮箱为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerBank() && StringUtils.isEmpty(onlineTradeDTO.getPayerBank())) {
            log.info("==================【线上收单校验机构请求参数】==================【付款人银行为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getLanguage() && StringUtils.isEmpty(onlineTradeDTO.getLanguage())) {
            log.info("==================【线上收单校验机构请求参数】==================【语言为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark1() && StringUtils.isEmpty(onlineTradeDTO.getRemark1())) {
            log.info("==================【线上收单校验机构请求参数】==================【备注1为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark2() && StringUtils.isEmpty(onlineTradeDTO.getRemark2())) {
            log.info("==================【线上收单校验机构请求参数】==================【备注2为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark3() && StringUtils.isEmpty(onlineTradeDTO.getRemark3())) {
            log.info("==================【线上收单校验机构请求参数】==================【备注3为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
    }

    /**
     * 收银台参数解密
     *
     * @param cd 未解密收银台DTO
     * @return CashierDTO 解密后参数
     * @throws Exception e
     */
    public CashierDTO decryptCashierSignMsg(CashierDTO cd) throws Exception {
        HashMap<String, Object> originalMap = BeanToMapUtil.beanToMap(cd);
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId(AsianWalletConstant.ATTESTATION_CACHE_PLATFORM_KEY);
        HashMap<String, String> map = new HashMap<>();
        Set<String> originalSet = originalMap.keySet();
        for (String key : originalSet) {
            String originalValue = String.valueOf(originalMap.get(key));
            if (StringUtils.isEmpty(originalValue)) {
                log.info("---------------收银台参数含空---------------key:{},CashierDTO:{}", key, JSON.toJSON(cd));
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            String value = RSAUtils.decryptByPriKey(originalValue, attestation.getPrikey());
            map.put(key, value);
        }
        return JSON.parseObject(JSON.toJSONString(map), CashierDTO.class);
    }

    /**
     * 查询订单
     *
     * @param onlineCheckOrdersDTO 订单查询实体
     * @return OnlineCheckOrdersVO
     */
    @Override
    public List<OnlineCheckOrdersVO> checkOrder(OnlineCheckOrdersDTO onlineCheckOrdersDTO) {
        log.info("==================【线上查询订单】==================【请求参数】 onlineCheckOrdersDTO: {}", JSON.toJSONString(onlineCheckOrdersDTO));
       /* //验签
        if (!commonBusinessService.checkUniversalSign(onlineCheckOrdersDTO)) {
            log.info("==================【线上查询订单】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }*/

        //页码默认为1
        if (onlineCheckOrdersDTO.getPageNum() == null) {
            onlineCheckOrdersDTO.setPageNum(1);
        }
        //每页默认30
        if (onlineCheckOrdersDTO.getPageSize() == null) {
            onlineCheckOrdersDTO.setPageSize(30);
        }
        //分页查询订单
        List<OnlineCheckOrdersVO> onlineCheckOrdersVOList = ordersMapper.onlineCheckOrders(onlineCheckOrdersDTO);
        log.info("==================【线上查询订单】==================【响应参数】 onlineCheckOrdersVOList: {}", JSON.toJSONString(onlineCheckOrdersVOList));
        return onlineCheckOrdersVOList;
    }

    /**
     * 模拟界面所需信息
     *
     * @param merchantId 商户ID
     * @param language   语言
     * @return OnlineMerchantVO
     */
    @Override
    public OnlineMerchantVO simulation(String merchantId, String language) {
        return merchantMapper.selectRelevantInfo(merchantId, null, TradeConstant.TRADE_ONLINE, language);
    }

    /**
     * 模拟界面查询订单信息
     *
     * @param ordersDTO OrdersDTOaa
     * @return List<OrdersVOaa>
     */
    @Override
    public PageInfo<MockOrdersVO> getByMultipleConditions(MockOrdersDTO ordersDTO) {
        if (StringUtils.isEmpty(ordersDTO.getMerchantId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        return new PageInfo<>(ordersMapper.getByMultipleConditions(ordersDTO));
    }

    /**
     * 收银台查询订单状态
     *
     * @param onlineOrderQueryDTO
     * @return
     */
    @Override
    public BaseResponse onlineOrderQuery(OnlineOrderQueryDTO onlineOrderQueryDTO) {
        log.info("-----------线上通道订单状态查询开始-----------onlineOrderQueryDTO:{}", JSON.toJSON(onlineOrderQueryDTO));
        Orders orders = ordersMapper.selectByPrimaryKey(onlineOrderQueryDTO.getOrderNo());
        BaseResponse response = new BaseResponse();
        //订单不存在
        if (orders == null) {
            log.info("-------------订单不存在------------ad3OnlineOrderQueryVO:{}", JSON.toJSON(onlineOrderQueryDTO));
            response.setCode(EResultEnum.ORDER_NOT_EXIST.getCode());
            return response;
        }
        //返回结果
        OnlineQueryOrderVO onlineQueryOrderVO = new OnlineQueryOrderVO();
        //查询通道
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        if (!orders.getTradeStatus().equals(TradeConstant.ORDER_PAYING)) {
            onlineQueryOrderVO.setOrderNo(orders.getMerchantOrderId());
            onlineQueryOrderVO.setTxnstatus(orders.getTradeStatus());
            response.setData(onlineQueryOrderVO);
            return response;
        }
        //判断通道
        if (channel.getServiceNameMark().equals(TradeConstant.AD3)) {
            AD3OnlineOrderQueryDTO ad3OnlineOrderQueryDTO = new AD3OnlineOrderQueryDTO(orders, channel);
            ad3OnlineOrderQueryDTO.setMerorderDatetime(DateUtil.format(orders.getReportChannelTime(), "yyyyMMddHHmmss"));
            HttpResponse httpResponse = ad3Service.ad3OnlineOrderQuery(ad3OnlineOrderQueryDTO, null, channel);
            if (!httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                log.info("------------------向上游查询订单状态异常------------------OnlineOrderQueryDTO:{},ad3OnlineOrderQueryDTO:{}", JSON.toJSON(onlineOrderQueryDTO), JSON.toJSON(ad3OnlineOrderQueryDTO));
                throw new BusinessException(EResultEnum.QUERY_ORDER_ERROR.getCode());
            }
            AD3OnlineOrderQueryVO ad3OnlineOrderQueryVO = JSON.parseObject(httpResponse.getJsonObject().toJSONString(), AD3OnlineOrderQueryVO.class);
            if (ad3OnlineOrderQueryVO == null || StringUtils.isEmpty(ad3OnlineOrderQueryVO.getState())) {
                log.info("---------------上游返回的查询信息为空---------------");
                response.setCode(EResultEnum.QUERY_ORDER_ERROR.getCode());
                return response;
            }
            orders.setChannelNumber(ad3OnlineOrderQueryVO.getTxnId());//通道流水号
            //更新时间
            orders.setUpdateTime(new Date());
            //通道回调时间
            orders.setChannelCallbackTime(DateUtil.parse(ad3OnlineOrderQueryVO.getTxnDate(), "yyyyMMddHHmmss"));
            String status = ad3OnlineOrderQueryVO.getState();
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("tradeStatus", "2");
            criteria.andEqualTo("id", orders.getId());
            if (AD3Constant.ORDER_SUCCESS.equals(status)) {
                orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
                int i = ordersMapper.updateByExampleSelective(orders, example);
                if (i > 0) {
                    log.info("=================【线上通道订单状态查询】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("=================【线上通道订单状态查询】=================【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                       /* if (!StringUtils.isEmpty(orders.getAgencyCode())) {
                            rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orders.getId());
                        }*/
                        //更新成功,上报清结算
                        FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                        //上报清结算资金变动接口
                        BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                        if (fundChangeResponse.getCode() != null && TradeConstant.HTTP_SUCCESS.equals(fundChangeResponse.getCode())) {
                            //请求成功
                            FundChangeVO fundChangeVO = (FundChangeVO) fundChangeResponse.getData();
                            if (!fundChangeVO.getRespCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                                //业务处理失败
                                log.info("=================【线上通道订单状态查询】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                            }
                        } else {
                            log.info("=================【线上通道订单状态查询】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                            rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                        }
                    } catch (Exception e) {
                        log.error("=================【线上通道订单状态查询】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    log.info("=================【线上通道订单状态查询】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if (AD3Constant.ORDER_FAILED.equals(status)) {
                log.info("=================【线上通道订单状态查询】=================【订单已支付失败】 orderId: {}", orders.getId());
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark4(status);
                //计算支付失败时通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【线上通道订单状态查询】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                } else {
                    log.info("=================【线上通道订单状态查询】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else {
                log.info("=================【线上通道订单状态查询】=================【订单为支付中】");
            }
        }
        onlineQueryOrderVO.setOrderNo(orders.getMerchantOrderId());
        onlineQueryOrderVO.setTxnstatus(orders.getTradeStatus());
        log.info("--------------返回给收银台参数--------------onlineQueryOrderVO:{}", JSON.toJSONString(onlineQueryOrderVO));
        response.setData(onlineQueryOrderVO);
        response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        response.setCode(TradeConstant.HTTP_SUCCESS);
        return response;
    }
}

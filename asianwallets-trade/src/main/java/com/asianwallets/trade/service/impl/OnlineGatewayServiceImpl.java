package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.OnlineTradeScanVO;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.dao.BankIssuerIdMapper;
import com.asianwallets.trade.dao.MerchantProductMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OnlineGatewayService;
import com.asianwallets.trade.utils.HandlerContext;
import com.asianwallets.trade.utils.SettleDateUtil;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.OnlineInfoDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
public class OnlineGatewayServiceImpl implements OnlineGatewayService {

    @Autowired
    private BankIssuerIdMapper bankIssuerIdMapper;

    @Autowired
    private Help2PayService help2PayService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private MerchantProductMapper merchantProductMapper;

    @Autowired
    private HandlerContext handlerContext;

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

        /*help2PayService.onlinePay(null,null);

        try {
            ChannelsAbstract channelsAbstract = (Help2PayServiceImpl)Class.forName("com.asianwallets.trade.channels.help2pay.impl.Help2PayServiceImpl").newInstance();

            channelsAbstract.offlineBSC(null,null,null);
        }catch (Exception e) {
            e.printStackTrace();
        }


        if (StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {

        }
        return null;*/
    }


    /* TODO 收银台查询订单
    //查询订单号是否重复
        Orders orders = ordersMapper.selectByMerchantOrderId(onlineTradeDTO.getMerchantId());
        if (orders!=null) {
            if (orders.getTradeStatus().equals(TradeConstant.PAYMENT_SUCCESS)) {
                log.info("-----------------【线上直连】下单信息记录--------------【订单已支付】");
                throw new BusinessException(EResultEnum.ORDER_PAID_ERROR.getCode());
            }
            if (orders.getTradeStatus().equals(TradeConstant.PAYMENT_FAIL)) {
                log.info("-----------------【线上直连】下单信息记录--------------【订单已支付失败】");
                throw new BusinessException(EResultEnum.ORDER_PAYMENT_FAILED.getCode());
            }
        }*/


    /**
     * 间连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private BaseResponse indirectConnection(OnlineTradeDTO onlineTradeDTO) {
        return null;
    }

    /**
     * 直连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private BaseResponse directConnection(OnlineTradeDTO onlineTradeDTO) {
        //信息落地
        log.info("---------------【线上直连收单输入实体】---------------OnlineTradeDTO:{}", JSON.toJSONString(onlineTradeDTO));
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
        //校验是否换汇
        commonBusinessService.swapRateByPayment(basicInfoVO, orders);
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //截取币种默认值
        commonBusinessService.interceptDigit(orders, currency);
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        log.info("-----------------【线上直连】--------------【订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        //上报通道
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        try {
            //上报通道
            ChannelsAbstract channelsAbstract = handlerContext.getInstance(basicInfoVO.getChannel().getServiceNameMark());
            BaseResponse baseResponse = channelsAbstract.onlinePay(orders, basicInfoVO.getChannel());
            baseResponse.setMsg("SUCCESS");
            onlineTradeVO = (OnlineTradeVO) baseResponse.getData();
            //间联
            if (!StringUtils.isEmpty(baseResponse.getData()) && TradeConstant.INDIRECTCONNECTION.equals(orders.getConnectMethod())) {
                if (!StringUtils.isEmpty(onlineTradeVO.getRespCode())) {
                    OnlineTradeScanVO ad3OnlineScanVO = new OnlineTradeScanVO();
                    BeanUtils.copyProperties(onlineTradeVO, ad3OnlineScanVO);
                    ad3OnlineScanVO.setTradeAmount(orders.getTradeAmount());
                    ad3OnlineScanVO.setTradeCurrency(orders.getTradeCurrency());
                    baseResponse.setData(ad3OnlineScanVO);
                    return baseResponse;
                }
            }
            baseResponse.setData(onlineTradeVO);
            return baseResponse;
        } catch (Exception e) {
            log.info("==================【线上直连】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
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

    private void checkOnlineOrders(OnlineTradeDTO onlineTradeDTO, Currency currency) {
        //重复请求
        if (!commonBusinessService.repeatedRequests(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getOrderNo())) {
            log.info("-----------------【线上直连】下单信息记录--------------【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
       /* //签名校验
        if (!commonBusinessService.checkUniversalSign(onlineTradeDTO)) {
            log.info("-----------------【线上直连】下单信息记录--------------【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }*/
       /* //查询订单号是否重复
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(onlineTradeDTO.getOrderNo());
        if (oldOrder != null) {
            log.info("-----------------【线上直连】下单信息记录-----------------订单号已存在");
            throw new BusinessException(EResultEnum.INSTITUTION_ORDER_ID_EXIST.getCode());
        }*/
        //检查币种默认值
        if (!commonBusinessService.checkOrderCurrency(onlineTradeDTO.getOrderCurrency(), onlineTradeDTO.getOrderAmount(), currency)) {
            log.info("-----------------【线上直连】下单信息记录--------------【订单金额不符合的当前币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
    }

    /**
     * 设置订单属性
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
        orders.setMerchantName(merchant.getCnName());
        orders.setSecondMerchantName(merchant.getCnName());
        orders.setSecondMerchantCode(merchant.getId());
        orders.setAgentCode(merchant.getAgentId());
        //INDIRECTCONNECTION 间连
        orders.setConnectMethod(StringUtils.isEmpty(onlineTradeDTO.getIssuerId()) ? TradeConstant.INDIRECTCONNECTION : TradeConstant.INDIRECTCONNECTION);
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
        orders.setSign(onlineTradeDTO.getSign());
        orders.setCreateTime(new Date());
        orders.setRemark1(onlineTradeDTO.getRemark1());
        orders.setRemark2(onlineTradeDTO.getRemark2());
        orders.setRemark3(onlineTradeDTO.getRemark3());
        return orders;
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
}

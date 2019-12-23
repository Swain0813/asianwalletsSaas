package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.dao.BankIssuerIdMapper;
import com.asianwallets.trade.dao.MerchantProductMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OnlineGatewayService;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.OnlineInfoDetailVO;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    @Override
    public OnlineTradeVO gateway(OnlineTradeDTO onlineTradeDTO) {

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
    private OnlineTradeVO indirectConnection(OnlineTradeDTO onlineTradeDTO) {
        return null;
    }

    /**
     * 直连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private OnlineTradeVO directConnection(OnlineTradeDTO onlineTradeDTO) {
        //信息落地
        log.info("---------------【线上直连收单输入实体】---------------OnlineTradeDTO:{}", JSON.toJSONString(onlineTradeDTO));
        //重复请求
        if (!commonBusinessService.repeatedRequests(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getOrderNo())) {
            log.info("-----------------【线上直连】下单信息记录--------------【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //检查币种默认值
        if (!commonBusinessService.checkOrderCurrency(onlineTradeDTO.getOrderCurrency(), onlineTradeDTO.getOrderAmount())) {
            log.info("-----------------【线上直连】下单信息记录--------------【订单金额不符合的当前币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //签名校验
        if (!commonBusinessService.checkUniversalSign(onlineTradeDTO)) {
            log.info("-----------------【线上直连】下单信息记录--------------【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
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
        //机构
        Institution institution = commonRedisDataService.getInstitutionById(merchant.getInstitutionId());
        if (institution == null) {
            log.info("-----------------【线上直连】下单信息记录--------------【机构不存在】");
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!institution.getEnabled()) {
            log.info("-----------------【线上直连】下单信息记录--------------【机构被禁用】");
            throw new BusinessException(EResultEnum.INSTITUTION_DOES_NOT_EXIST.getCode());
        }
        //可选参数校验
        InstitutionRequestParameters institutionRequestParameters = commonRedisDataService.getInstitutionRequestByIdAndDirection(institution.getId(), TradeConstant.TRADE_ONLINE);
        checkRequestParameters(onlineTradeDTO, institutionRequestParameters);
        //校验订单金额
        if (onlineTradeDTO.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("-----------------【线上直连】下单信息记录--------------【订单金额不合法】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //查询订单号是否重复
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(onlineTradeDTO.getMerchantId());
        if (oldOrder != null) {
            log.info("-----------------【线上直连】下单信息记录-----------------订单号已存在");
            throw new BusinessException(EResultEnum.INSTITUTION_ORDER_ID_EXIST.getCode());
        }
        //获取商户信息
        BasicInfoVO basicInfoVO = getOnlineInfo(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getIssuerId());
        //设置订单属性
        Orders orders = new Orders();
        orders.setId(String.valueOf(IDS.uniqueID()));
        orders.setInstitutionId(institution.getId());
        orders.setInstitutionName(institution.getCnName());
        orders.setMerchantId(merchant.getId());
        orders.setMerchantName(merchant.getCnName());
        orders.setSecondMerchantName("");
        orders.setSecondMerchantCode("");
        orders.setAgentCode(merchant.getAgentId());
        orders.setAgentName(commonRedisDataService.getMerchantById(merchant.getId()).getCnName());
        orders.setGroupMerchantCode("");
        orders.setGroupMerchantName("");
       /* orders.setTradeType();
        orders.setTradeDirection(basicInfoVO.getProduct().getTradeDirection());*/
        orders.setMerchantOrderTime(new Date(onlineTradeDTO.getOrderTime()));
        orders.setMerchantOrderId(onlineTradeDTO.getOrderNo());
        orders.setOrderAmount(onlineTradeDTO.getOrderAmount());
        orders.setOrderCurrency(onlineTradeDTO.getOrderCurrency());
        orders.setProductCode(basicInfoVO.getProduct().getProductCode());
        orders.setProductName(basicInfoVO.getProduct().getProductName());
        orders.setProductDescription(onlineTradeDTO.getProductDescription());
        orders.setChannelCode(basicInfoVO.getChannel().getChannelCode());
        orders.setChannelName(basicInfoVO.getChannel().getChannelCnName());
        orders.setTradeCurrency(basicInfoVO.getChannel().getCurrency());
        orders.setTradeStatus(TradeConstant.PAYMENT_START);


        orders.setPayMethod(basicInfoVO.getMerchantProduct().getPayType());

        orders.setPayerName(onlineTradeDTO.getPayerName());
        orders.setPayerAccount(onlineTradeDTO.getPayerAccount());
        orders.setPayerBank(onlineTradeDTO.getPayerBank());
        orders.setPayerEmail(onlineTradeDTO.getPayerEmail());
        orders.setPayerPhone(onlineTradeDTO.getPayerPhone());
        orders.setPayerAddress(onlineTradeDTO.getPayerAddress());
        orders.setProductSettleCycle(basicInfoVO.getMerchantProduct().getSettleCycle());
        orders.setIssuerId(basicInfoVO.getChannel().getIssuerId());
        orders.setBankName(basicInfoVO.getBankName());
        orders.setBrowserUrl(onlineTradeDTO.getBrowserUrl());
        orders.setServerUrl(onlineTradeDTO.getServerUrl());
        orders.setFeePayer((byte) 0);
        orders.setLanguage(onlineTradeDTO.getLanguage());
        orders.setSign(onlineTradeDTO.getSign());
        orders.setCreateTime(new Date());

        //校验是否换汇
        commonBusinessService.calcExchangeRateBak(basicInfoVO, orders);
        /*if (!orders.getTradeCurrency().equals(basicInfoVO.getChannel().getCurrency())) {
            //校验机构DCC
            if (!basicInfoVO.getInstitution().getDcc()) {
                orders.setRemark("机构不支持DCC");
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.DCC_IS_NOT_OPEN.getCode());
            }
            //换汇计算
            CalcExchangeRateVO calcExchangeRateVO = commonBusinessService.calcExchangeRate(orders.getOrderCurrency(), orders.getTradeCurrency(), basicInfoVO.getMerchantProduct().getFloatRate(), onlineTradeDTO.getOrderAmount());
            orders.setExchangeTime(calcExchangeRateVO.getExchangeTime());
            orders.setExchangeStatus(calcExchangeRateVO.getExchangeStatus());
            if (TradeConstant.SWAP_FALID.equals(calcExchangeRateVO.getExchangeStatus())) {
                log.info("-----------------【线上直连】下单信息记录--------------【换汇失败】");
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark("换汇失败");
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.SYS_ERROR_CREATE_ORDER_FAIL.getCode());
            }
            orders.setExchangeRate(calcExchangeRateVO.getExchangeRate());
            orders.setTradeAmount(calcExchangeRateVO.getTradeAmount());
            orders.setOrderForTradeRate(calcExchangeRateVO.getOriginalRate());
            orders.setTradeForOrderRate(calcExchangeRateVO.getReverseRate());
        } else {
            log.info("-----------------【线上直连】下单信息记录--------------【订单未换汇】");
            orders.setTradeAmount(orders.getOrderAmount());
            orders.setOrderForTradeRate(BigDecimal.ONE);
            orders.setTradeForOrderRate(BigDecimal.ONE);
            orders.setExchangeRate(BigDecimal.ONE);
        }*/
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        log.info("-----------------【线上直连】--------------【订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        //上报通道
        return null;
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
            if (!product.getEnabled()) {
                log.info("-----------------【线上获取基础信息】----------------- 产品被禁用");
                throw new BusinessException(EResultEnum.PRODUCT_STATUS_ABNORMAL.getCode());
            }
            //商户产品
            MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(merchantId, product.getId());
            if (!merchantProduct.getEnabled()) {
                log.info("-----------------【线上获取基础信息】----------------- 商户产品被禁用");
                throw new BusinessException(EResultEnum.MERCHANT_PRODUCT_IS_DISABLED.getCode());
            }
            basicInfoVO.setBankName(onlineInfoDetailVO.getBankName());
            basicInfoVO.setChannel(channel);
            basicInfoVO.setProduct(product);
            basicInfoVO.setMerchantProduct(merchantProduct);
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
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductName() && StringUtils.isEmpty(onlineTradeDTO.getProductName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductDescription() && StringUtils.isEmpty(onlineTradeDTO.getProductDescription())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerName() && StringUtils.isEmpty(onlineTradeDTO.getPayerName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerPhone() && StringUtils.isEmpty(onlineTradeDTO.getPayerPhone())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerEmail() && StringUtils.isEmpty(onlineTradeDTO.getPayerEmail())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerBank() && StringUtils.isEmpty(onlineTradeDTO.getPayerBank())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getLanguage() && StringUtils.isEmpty(onlineTradeDTO.getLanguage())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark1() && StringUtils.isEmpty(onlineTradeDTO.getRemark1())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark2() && StringUtils.isEmpty(onlineTradeDTO.getRemark2())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark3() && StringUtils.isEmpty(onlineTradeDTO.getRemark3())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
    }
}

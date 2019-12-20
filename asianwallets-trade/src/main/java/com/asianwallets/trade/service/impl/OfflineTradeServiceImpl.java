package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.dao.BankIssuerIdMapper;
import com.asianwallets.trade.dao.DeviceBindingMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.SysUserMapper;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OfflineTradeService;
import com.asianwallets.trade.utils.SettleDateUtil;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.CsbDynamicScanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private DeviceBindingMapper deviceBindingMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BankIssuerIdMapper bankIssuerIdMapper;

    /**
     * 校验请求参数
     *
     * @param offlineTradeDTO              线下交易输入实体
     * @param institutionRequestParameters 机构请求参数实体
     */
    private void checkRequestParameters(OfflineTradeDTO offlineTradeDTO, InstitutionRequestParameters institutionRequestParameters) {
        if (institutionRequestParameters.getBrowserUrl() && StringUtils.isEmpty(offlineTradeDTO.getBrowserUrl())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getIssuerId() && StringUtils.isEmpty(offlineTradeDTO.getIssuerId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductName() && StringUtils.isEmpty(offlineTradeDTO.getProductName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductDescription() && StringUtils.isEmpty(offlineTradeDTO.getProductDescription())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerName() && StringUtils.isEmpty(offlineTradeDTO.getPayerName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerPhone() && StringUtils.isEmpty(offlineTradeDTO.getPayerPhone())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerEmail() && StringUtils.isEmpty(offlineTradeDTO.getPayerEmail())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerBank() && StringUtils.isEmpty(offlineTradeDTO.getPayerBank())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getLanguage() && StringUtils.isEmpty(offlineTradeDTO.getLanguage())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark1() && StringUtils.isEmpty(offlineTradeDTO.getRemark1())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark2() && StringUtils.isEmpty(offlineTradeDTO.getRemark2())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark3() && StringUtils.isEmpty(offlineTradeDTO.getRemark3())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
    }

    /**
     * 校验输入参数的合法性
     *
     * @param offlineTradeDTO 线下交易输入实体
     */
    private void checkParamValidity(OfflineTradeDTO offlineTradeDTO) {
        //验签
//        if (!commonBusinessService.checkSignByMd5(offlineTradeDTO)) {
//            log.info("==================【线下CSB动态扫码】==================【签名不匹配】");
//            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
//        }
        //校验订单金额
        if (offlineTradeDTO.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("==================【线下CSB动态扫码】==================【订单金额不合法】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //校验Token信息
//        SysUserVO sysUserVO = JSON.parseObject(redisService.get(offlineTradeDTO.getToken()), SysUserVO.class);
//        if (sysUserVO == null || !(offlineTradeDTO.getOperatorId().concat(offlineTradeDTO.getMerchantId()).equals(sysUserVO.getUsername()))) {
//            log.info("==================【线下CSB动态扫码】==================【Token不合法】");
//            throw new BusinessException(EResultEnum.TOKEN_IS_INVALID.getCode());
//        }
        //校验币种信息
        if (!commonBusinessService.checkOrderCurrency(offlineTradeDTO.getOrderCurrency(), offlineTradeDTO.getOrderAmount())) {
            log.info("==================【线下CSB动态扫码】==================【订单金额不符合币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //校验订单号
        if (ordersMapper.selectByMerchantOrderId(offlineTradeDTO.getOrderNo()) != null) {
            log.info("==================【线下CSB动态扫码】==================【商户订单号已存在】");
            throw new BusinessException(EResultEnum.INSTITUTION_ORDER_ID_EXIST.getCode());
        }
        //校验设备信息
        checkDevice(offlineTradeDTO.getMerchantId(), offlineTradeDTO.getImei(), offlineTradeDTO.getOperatorId());
    }

    /**
     * 校验设备信息
     *
     * @param merchantId 商户ID
     * @param imei       设备号
     * @param operatorId 操作员ID
     */
    private void checkDevice(String merchantId, String imei, String operatorId) {
        //校验商户绑定设备
        DeviceBinding deviceBinding = deviceBindingMapper.selectByMerchantIdAndImei(merchantId, imei);
        if (deviceBinding == null) {
            log.info("================【线下业务接口】================【设备编号不合法】");
            throw new BusinessException(EResultEnum.DEVICE_CODE_INVALID.getCode());
        }
        //校验设备操作员
        SysUser sysUser = sysUserMapper.selectByUsername(operatorId.concat(merchantId));
        if (sysUser == null) {
            log.info("================【线下业务接口】================【设备操作员不合法】");
            throw new BusinessException(EResultEnum.DEVICE_OPERATOR_INVALID.getCode());
        }
    }

    /**
     * 获取收单基础信息并校验
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 基础配置输出实体
     */
    private BasicInfoVO getBasicAndCheck(OfflineTradeDTO offlineTradeDTO) {
        Merchant merchant = commonRedisDataService.getMerchantById(offlineTradeDTO.getMerchantId());
        if (merchant == null) {
            log.info("==================【线下收单】==================【商户不存在】");
            throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
        }
        if (!merchant.getEnabled()) {
            log.info("==================【线下收单】==================【商户被禁用】");
            throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
        }
        Institution institution = commonRedisDataService.getInstitutionById(merchant.getInstitutionId());
        if (institution == null) {
            log.info("==================【线下收单】==================【机构不存在】");
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!institution.getEnabled()) {
            log.info("==================【线下收单】==================【机构被禁用】");
            throw new BusinessException(EResultEnum.INSTITUTION_DOES_NOT_EXIST.getCode());
        }
        InstitutionRequestParameters institutionRequestParameters = commonRedisDataService.getInstitutionRequestByIdAndDirection(institution.getId(), TradeConstant.TRADE_UPLINE);
        if (institutionRequestParameters == null) {
            log.info("==================【线下收单】==================【机构请求参数信息不合法】");
            return null;
        }
        //校验机构必填请求输入参数
        checkRequestParameters(offlineTradeDTO, institutionRequestParameters);
        //校验输入参数合法性
        checkParamValidity(offlineTradeDTO);
        Product product = commonRedisDataService.getProductByCode(offlineTradeDTO.getProductCode());
        if (product == null || !product.getEnabled()) {
            log.info("==================【线下收单】==================【产品信息不合法】");
            return null;
        }
        MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(merchant.getId(), product.getId());
        if (merchantProduct == null || !merchantProduct.getEnabled()) {
            log.info("==================【线下收单】==================【商户产品信息不合法】");
            return null;
        }
        List<String> chaBankIdList = commonRedisDataService.getChaBankIdByMerProId(merchantProduct.getId());
        if (ArrayUtil.isEmpty(chaBankIdList)) {
            log.info("==================【线下收单】==================【通道银行信息不存在】");
            return null;
        }
        List<ChannelBank> channelBankList = new ArrayList<>();
        for (String chaBankId : chaBankIdList) {
            ChannelBank channelBank = commonRedisDataService.getChaBankById(chaBankId);
            if (channelBank != null) {
                channelBankList.add(channelBank);
            }
        }
        Channel channel = null;
        BankIssuerId bankIssuerId = null;
        for (ChannelBank channelBank : channelBankList) {
            channel = commonRedisDataService.getChannelById(channelBank.getChannelId());
            if (channel != null && channel.getEnabled()) {
                bankIssuerId = bankIssuerIdMapper.selectByChannelCode(channel.getChannelCode());
                if (bankIssuerId != null) {
                    log.info("==================【线下收单】==================【通道】  channel: {}", JSON.toJSONString(channel));
                    log.info("==================【线下收单】==================【银行机构映射】  bankIssuerId: {}", JSON.toJSONString(bankIssuerId));
                    break;
                }
            }
        }
        if (channel == null || !channel.getEnabled()) {
            log.info("==================【线下收单】==================【通道信息不合法】");
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        if (bankIssuerId == null || !bankIssuerId.getEnabled()) {
            log.info("==================【线下收单】==================【银行机构映射信息不合法】");
            throw new BusinessException(EResultEnum.BANK_MAPPING_NO_EXIST.getCode());
        }
        BasicInfoVO basicsInfoVO = new BasicInfoVO();
        channel.setIssuerId(bankIssuerId.getIssuerId());
        basicsInfoVO.setBankName(bankIssuerId.getBankName());
        basicsInfoVO.setMerchant(merchant);
        basicsInfoVO.setProduct(product);
        basicsInfoVO.setChannel(channel);
        basicsInfoVO.setMerchantProduct(merchantProduct);
        basicsInfoVO.setInstitution(institution);
        return basicsInfoVO;
    }


    /**
     * 设置订单属性
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @param basicInfoVO     交易基础信息实体
     * @return 订单
     */
    private Orders setAttributes(OfflineTradeDTO offlineTradeDTO, BasicInfoVO basicInfoVO) {
        Institution institution = basicInfoVO.getInstitution();
        Merchant merchant = basicInfoVO.getMerchant();
        Product product = basicInfoVO.getProduct();
        Channel channel = basicInfoVO.getChannel();
        MerchantProduct merchantProduct = basicInfoVO.getMerchantProduct();
        Orders orders = new Orders();
        orders.setId("O" + IDS.uniqueID().toString().substring(0, 15));
        orders.setInstitutionId(institution.getId());
        orders.setInstitutionName(institution.getCnName());
        orders.setMerchantId(merchant.getId());
        orders.setMerchantName(merchant.getCnName());
//        orders.setSecondMerchantName("");
//        orders.setSecondMerchantCode("");
        if (!StringUtils.isEmpty(merchant.getAgentId())) {
            Merchant agentMerchant = commonRedisDataService.getMerchantById(merchant.getAgentId());
            if (agentMerchant != null) {
                orders.setAgentCode(agentMerchant.getId());
                orders.setAgentName(agentMerchant.getCnName());
            }
        }
//        orders.setGroupMerchantCode("");
//        orders.setGroupMerchantName("");
        orders.setTradeType(TradeConstant.GATHER_TYPE);
        orders.setTradeDirection(TradeConstant.TRADE_UPLINE);
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG(offlineTradeDTO.getOrderTime()));
        orders.setMerchantOrderId(offlineTradeDTO.getOrderNo());
        orders.setOrderAmount(offlineTradeDTO.getOrderAmount());
        orders.setOrderCurrency(offlineTradeDTO.getOrderCurrency());
        orders.setTradeCurrency(channel.getCurrency());
        orders.setImei(offlineTradeDTO.getImei());
        orders.setOperatorId(offlineTradeDTO.getOperatorId());
        orders.setProductCode(offlineTradeDTO.getProductCode());
        orders.setProductName(offlineTradeDTO.getProductName());
        orders.setProductDescription(offlineTradeDTO.getProductDescription());
        orders.setChannelCode(channel.getChannelCode());
        orders.setChannelName(channel.getChannelCnName());
        orders.setPayMethod(product.getPayType());
        commonBusinessService.getUrl(offlineTradeDTO.getServerUrl(), orders);
        //orders.setChannelAmount(new BigDecimal("0"));
        orders.setFloatRate(merchantProduct.getFloatRate());
        orders.setReportChannelTime(new Date());
        orders.setPayerName(offlineTradeDTO.getPayerName());
        orders.setPayerBank(offlineTradeDTO.getPayerBank());
        orders.setPayerEmail(offlineTradeDTO.getPayerEmail());
        orders.setPayerPhone(offlineTradeDTO.getPayerPhone());
        //判断结算周期类型
        if (TradeConstant.DELIVERED.equals(merchantProduct.getSettleCycle())) {
            //妥投结算
            orders.setProductSettleCycle(TradeConstant.FUTURE_TIME);
        } else {
            //产品结算周期
            orders.setProductSettleCycle(SettleDateUtil.getSettleDate(merchantProduct.getSettleCycle()));
        }
        orders.setIssuerId(channel.getIssuerId());
        orders.setBankName(basicInfoVO.getBankName());
        orders.setServerUrl(offlineTradeDTO.getServerUrl());
        orders.setLanguage(offlineTradeDTO.getLanguage());
        orders.setRemark1(offlineTradeDTO.getRemark1());
        orders.setRemark2(offlineTradeDTO.getRemark2());
        orders.setRemark3(offlineTradeDTO.getRemark3());
        orders.setCreateTime(new Date());
        orders.setCreator(merchant.getCnName());
        return orders;
    }

    @Autowired
    private ChannelsFeign channelsFeign;
    /**
     * 线下同机构CSB动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    @Override
    public CsbDynamicScanVO csbDynamicScan(OfflineTradeDTO offlineTradeDTO) {
        log.info("==================【线下CSB动态扫码】==================【请求参数】 offlineTradeDTO: {}", JSON.toJSONString(offlineTradeDTO));
        BaseResponse channelResponse = channelsFeign.ad3OfflineCsb(null);
        //重复请求
        if (!commonBusinessService.repeatedRequests(offlineTradeDTO.getMerchantId(), offlineTradeDTO.getOrderNo())) {
            log.info("==================【线下CSB动态扫码】==================【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //获取收单基础信息并校验
        BasicInfoVO basicInfoVO = getBasicAndCheck(offlineTradeDTO);
        //设置订单属性
        Orders orders = setAttributes(offlineTradeDTO, basicInfoVO);
        //校验是否换汇
        if (!orders.getOrderCurrency().equals(orders.getTradeCurrency())) {
            //校验机构DCC
            if (!basicInfoVO.getInstitution().getDcc()) {
                orders.setRemark("机构不支持DCC");
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.DCC_IS_NOT_OPEN.getCode());
            }
            //换汇计算
            CalcExchangeRateVO calcExchangeRateVO = commonBusinessService.calcExchangeRate(orders.getOrderCurrency(), orders.getTradeCurrency(), basicInfoVO.getMerchantProduct().getFloatRate(), offlineTradeDTO.getOrderAmount());
            orders.setExchangeTime(calcExchangeRateVO.getExchangeTime());
            orders.setExchangeStatus(calcExchangeRateVO.getExchangeStatus());
            if (TradeConstant.SWAP_FALID.equals(calcExchangeRateVO.getExchangeStatus())) {
                log.info("==================【线下CSB动态扫码】==================【换汇失败】");
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
            log.info("==================【线下CSB动态扫码】==================【订单未换汇】");
            orders.setTradeAmount(orders.getOrderAmount());
            orders.setOrderForTradeRate(BigDecimal.ONE);
            orders.setTradeForOrderRate(BigDecimal.ONE);
            orders.setExchangeRate(BigDecimal.ONE);
        }
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        log.info("==================【线下CSB动态扫码】==================【落地订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        //上报通道
        CsbDynamicScanVO csbDynamicScanVO = new CsbDynamicScanVO();
        try {
            ChannelsAbstract channelsAbstract = (ChannelsAbstract) Class.forName(TradeConstant.channelsMap.get(basicInfoVO.getChannel().getServiceNameMark())).newInstance();
            BaseResponse baseResponse = channelsAbstract.offlineCSB(orders, basicInfoVO.getChannel());
            csbDynamicScanVO.setQrCodeUrl(String.valueOf(baseResponse.getData()));
        } catch (Exception e) {
            log.info("==================【线下CSB动态扫码】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        csbDynamicScanVO.setOrderNo(orders.getMerchantOrderId());
        csbDynamicScanVO.setDecodeType("0");
        log.info("==================【线下CSB动态扫码】==================【下单结束】");
        return csbDynamicScanVO;
    }
}

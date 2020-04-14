package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.PosQueryOrderListDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.RedisSysUserVO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.dto.OfflineCheckOrdersDTO;
import com.asianwallets.trade.dto.OfflineLoginDTO;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.dto.PosGetMerProDTO;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OfflineTradeService;
import com.asianwallets.trade.utils.HandlerContext;
import com.asianwallets.trade.utils.SettleDateUtil;
import com.asianwallets.trade.utils.TokenUtils;
import com.asianwallets.trade.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    @Autowired
    private TokenUtils tokenUtils;

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

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private MerchantProductMapper merchantProductMapper;

    @Autowired
    private HandlerContext handlerContext;


    /**
     * 线下登录
     *
     * @param offlineLoginDTO 线下登录实体
     * @return token
     */
    @Override
    public String login(OfflineLoginDTO offlineLoginDTO) {
        log.info("===========【线下登录】==========【请求参数】 offlineLoginDTO: {}", JSON.toJSONString(offlineLoginDTO));
        //校验商户信息
        commonRedisDataService.getMerchantById(offlineLoginDTO.getMerchantId());
        //校验商户绑定设备
        DeviceBinding deviceBinding = deviceBindingMapper.selectByMerchantIdAndImei(offlineLoginDTO.getMerchantId(), offlineLoginDTO.getImei());
        if (deviceBinding == null) {
            log.info("================【线下登录】================【设备未绑定】");
            throw new BusinessException(EResultEnum.DEVICE_CODE_INVALID.getCode());
        }
        //拼接用户名
        String username = offlineLoginDTO.getOperatorId().concat(offlineLoginDTO.getMerchantId());
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        if (sysUser == null) {
            log.info("===========【线下登录】==========【用户不存在】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //校验密码
        if (!BCryptUtils.matches(offlineLoginDTO.getPassword(), sysUser.getPassword())) {
            log.info("===========【线下登录】==========【密码错误】");
            throw new BusinessException(EResultEnum.USER_OR_PASSWORD_INCORRECT.getCode());
        }
        //生成Token
        String token = tokenUtils.generateToken(sysUser.getUsername());
        if (StringUtils.isEmpty(token)) {
            log.info("===========【线下登录】==========【Token生成失败】");
            throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
        }
        //存放token相关信息到redis
        RedisSysUserVO redisSysUserVO = new RedisSysUserVO();
        redisSysUserVO.setUsername(username);
        redisSysUserVO.setTradePassword(sysUser.getTradePassword());
        redisService.set(token, JSON.toJSONString(redisSysUserVO));
        return token;
    }

    public static void main(String[] args) {
        //String url = "http://localhost:5010/offline/csbDynamicScan";
        String url = "http://localhost:5010/offline/bscDynamicScan";
        String md5Key = "47ac097138814db98436dd293edb5b49";
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwME0yMDE5MTIyMDMzNjAiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE1NzkxNDIwODg5NTEsImV4cCI6MTU3OTIyODQ4OH0.-L7jU7A0ZB2rMK6_Hs9kKIHD8puGDxmCLXB5fxvQ6IvFSFFfSVTQJ0fWU4bAR6fD0D1ArL8TT3ZwvYceRh4aoA";
        OfflineTradeDTO offlineTradeDTO = new OfflineTradeDTO();
        offlineTradeDTO.setMerchantId("M201912203360");
        offlineTradeDTO.setOrderNo(IDS.uniqueID().toString());
        offlineTradeDTO.setOrderCurrency("SGD");
        offlineTradeDTO.setOrderAmount(new BigDecimal("0.01").setScale(2, BigDecimal.ROUND_DOWN));
        offlineTradeDTO.setOrderTime(DateToolUtils.formatDate(new Date()));
        offlineTradeDTO.setProductCode(44);
//        offlineTradeDTO.setIssuerId("Alipay");
        offlineTradeDTO.setImei("线下CSB");
        offlineTradeDTO.setOperatorId("00");
        offlineTradeDTO.setToken(token);
        offlineTradeDTO.setServerUrl("test");
        offlineTradeDTO.setBrowserUrl("test");
        offlineTradeDTO.setProductName("test");
        offlineTradeDTO.setProductDescription("test");
        offlineTradeDTO.setPayerName("test");
        offlineTradeDTO.setPayerBank("test");
        offlineTradeDTO.setPayerEmail("test");
        offlineTradeDTO.setPayerPhone("test");
        offlineTradeDTO.setLanguage("zh-cn");
        offlineTradeDTO.setRemark1("test");
        offlineTradeDTO.setRemark2("test");
        offlineTradeDTO.setRemark3("test");
        offlineTradeDTO.setSignType("2");
        offlineTradeDTO.setAuthCode("284216970393036884");
        //获得对象属性名对应的属性值Map
        Map<String, String> paramMap = ReflexClazzUtils.getFieldForStringValue(offlineTradeDTO);
        String cleatText = SignTools.getSignStr(paramMap) + md5Key;
        log.info("=======【AW线下测试】=======【签名前的明文】 cleatText: {}", cleatText);
        String sign = MD5Util.getMD5String(cleatText);
        log.info("=======【AW线下测试】=======【签名后的密文】 sign: {}", sign);
        offlineTradeDTO.setSign(sign);
        HttpClientUtils.reqPost(url, offlineTradeDTO, null);
    }

    /**
     * 校验请求参数
     *
     * @param offlineTradeDTO              线下交易输入实体
     * @param institutionRequestParameters 机构请求参数实体
     */
    private void checkRequestParameters(OfflineTradeDTO offlineTradeDTO, InstitutionRequestParameters institutionRequestParameters) {
        if (institutionRequestParameters.getBrowserUrl() && StringUtils.isEmpty(offlineTradeDTO.getBrowserUrl())) {
            log.info("==================【线下收单校验机构请求参数】==================【浏览器回调地址为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getIssuerId() && StringUtils.isEmpty(offlineTradeDTO.getIssuerId())) {
            log.info("==================【线下收单校验机构请求参数】==================【银行机构号为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductName() && StringUtils.isEmpty(offlineTradeDTO.getProductName())) {
            log.info("==================【线下收单校验机构请求参数】==================【商品名称为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductDescription() && StringUtils.isEmpty(offlineTradeDTO.getProductDescription())) {
            log.info("==================【线下收单校验机构请求参数】==================【商品描述为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerName() && StringUtils.isEmpty(offlineTradeDTO.getPayerName())) {
            log.info("==================【线下收单校验机构请求参数】==================【付款人姓名为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerPhone() && StringUtils.isEmpty(offlineTradeDTO.getPayerPhone())) {
            log.info("==================【线下收单校验机构请求参数】==================【付款人手机为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerEmail() && StringUtils.isEmpty(offlineTradeDTO.getPayerEmail())) {
            log.info("==================【线下收单校验机构请求参数】==================【付款人邮箱为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerBank() && StringUtils.isEmpty(offlineTradeDTO.getPayerBank())) {
            log.info("==================【线下收单校验机构请求参数】==================【付款人银行为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getLanguage() && StringUtils.isEmpty(offlineTradeDTO.getLanguage())) {
            log.info("==================【线下收单校验机构请求参数】==================【语言为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark1() && StringUtils.isEmpty(offlineTradeDTO.getRemark1())) {
            log.info("==================【线下收单校验机构请求参数】==================【备注1为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark2() && StringUtils.isEmpty(offlineTradeDTO.getRemark2())) {
            log.info("==================【线下收单校验机构请求参数】==================【备注2为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark3() && StringUtils.isEmpty(offlineTradeDTO.getRemark3())) {
            log.info("==================【线下收单校验机构请求参数】==================【备注3为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
    }

    /**
     * 校验输入参数的合法性
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @param currency        币种
     */
    private void checkParamValidity(OfflineTradeDTO offlineTradeDTO, Currency currency) {
        //校验订单金额
        if (offlineTradeDTO.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("==================【线下CSB动态扫码】==================【订单金额不合法】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //校验Token信息
        RedisSysUserVO sysUser = JSON.parseObject(redisService.get(offlineTradeDTO.getToken()), RedisSysUserVO.class);
        if (sysUser == null) {
            log.info("==================【线下CSB动态扫码】==================【Token不合法】");
            throw new BusinessException(EResultEnum.TOKEN_IS_INVALID.getCode());
        }
        //校验币种信息
        if (!commonBusinessService.checkOrderCurrency(offlineTradeDTO.getOrderCurrency(), offlineTradeDTO.getOrderAmount(), currency)) {
            log.info("==================【线下CSB动态扫码】==================【订单金额不符合币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        //校验订单号
        if (ordersMapper.selectByMerchantOrderId(offlineTradeDTO.getOrderNo()) != null) {
            log.info("==================【线下CSB动态扫码】==================【商户订单号已存在】");
            throw new BusinessException(EResultEnum.MERCHANT_ORDER_ID_EXIST.getCode());
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
        Institution institution = commonRedisDataService.getInstitutionById(merchant.getInstitutionId());
        InstitutionRequestParameters institutionRequestParameters = commonRedisDataService.getInstitutionRequestByIdAndDirection(institution.getId(), TradeConstant.TRADE_UPLINE);
        //校验机构必填请求输入参数
        checkRequestParameters(offlineTradeDTO, institutionRequestParameters);
        Currency currency = commonRedisDataService.getCurrencyByCode(offlineTradeDTO.getOrderCurrency());
        //校验输入参数合法性
        checkParamValidity(offlineTradeDTO, currency);
        Product product = commonRedisDataService.getProductByCode(offlineTradeDTO.getProductCode());
        MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(merchant.getId(), product.getId());
        List<String> chaBankIdList = commonRedisDataService.getChaBankIdByMerProId(merchantProduct.getId());
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
        if (channel == null) {
            log.info("==================【线下收单】==================【通道信息不合法】");
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        if (!channel.getEnabled()) {
            log.info("==================【线下收单】==================【通道信息不合法】");
            throw new BusinessException(EResultEnum.CHANNEL_STATUS_ABNORMAL.getCode());
        }
        if (bankIssuerId == null) {
            log.info("==================【线下收单】==================【银行机构映射信息不存在】");
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
        basicsInfoVO.setCurrency(currency);
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
        if (!StringUtils.isEmpty(merchant.getAgentId())) {
            Merchant agentMerchant = commonRedisDataService.getMerchantById(merchant.getAgentId());
            orders.setAgentCode(agentMerchant.getId());
            orders.setAgentName(agentMerchant.getCnName());
        }
        orders.setGroupMerchantCode(merchant.getGroupMasterAccount());
        orders.setGroupMerchantName(commonRedisDataService.getMerchantById(merchant.getGroupMasterAccount()).getCnName());
        orders.setTradeType(TradeConstant.GATHER_TYPE);
        orders.setTradeDirection(TradeConstant.TRADE_UPLINE);
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG(offlineTradeDTO.getOrderTime()));
        orders.setMerchantOrderId(offlineTradeDTO.getOrderNo());
        orders.setMerchantType(merchant.getMerchantType());
        orders.setOrderAmount(offlineTradeDTO.getOrderAmount());
        orders.setOrderCurrency(offlineTradeDTO.getOrderCurrency());
        orders.setTradeCurrency(channel.getCurrency());
        orders.setImei(offlineTradeDTO.getImei());
        orders.setOperatorId(offlineTradeDTO.getOperatorId());
        orders.setProductCode(product.getProductCode());
        orders.setProductName(offlineTradeDTO.getProductName());
        orders.setProductDescription(offlineTradeDTO.getProductDescription());
        orders.setChannelCode(channel.getChannelCode());
        orders.setChannelName(channel.getChannelCnName());
        orders.setPayMethod(product.getPayType());
        commonBusinessService.getUrl(offlineTradeDTO.getServerUrl(), orders);
        orders.setFloatRate(merchantProduct.getFloatRate());
        /*最大值*/
        orders.setMaxTate(merchantProduct.getMaxTate());
        /*最小值*/
        orders.setMinTate(merchantProduct.getMinTate());
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
        orders.setRemark8(channel.getChannelAgentId());
        orders.setCreateTime(new Date());
        orders.setCreator(merchant.getCnName());
        return orders;
    }

    /**
     * 线下同机构CSB动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    @Override
    public CsbDynamicScanVO csbDynamicScan(OfflineTradeDTO offlineTradeDTO) {
        log.info("==================【线下CSB动态扫码】==================【请求参数】 offlineTradeDTO: {}", JSON.toJSONString(offlineTradeDTO));
        //重复请求
        if (!commonBusinessService.repeatedRequests(offlineTradeDTO.getMerchantId(), offlineTradeDTO.getOrderNo())) {
            log.info("==================【线下CSB动态扫码】==================【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //验签
        if (!commonBusinessService.checkUniversalSign(offlineTradeDTO)) {
            log.info("==================【线下CSB动态扫码】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //获取收单基础信息并校验
        BasicInfoVO basicInfoVO = getBasicAndCheck(offlineTradeDTO);
        //设置订单属性
        Orders orders = setAttributes(offlineTradeDTO, basicInfoVO);
        //换汇
        commonBusinessService.swapRateByPayment(basicInfoVO, orders);
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //截取币种默认值
        commonBusinessService.interceptDigit(orders, basicInfoVO.getCurrency());
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        log.info("==================【线下CSB动态扫码】==================【落地订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        CsbDynamicScanVO csbDynamicScanVO = new CsbDynamicScanVO();
        try {
            //上报通道
            ChannelsAbstract channelsAbstract = handlerContext.getInstance(basicInfoVO.getChannel().getServiceNameMark());
            BaseResponse baseResponse = channelsAbstract.offlineCSB(orders, basicInfoVO.getChannel());
            csbDynamicScanVO.setQrCodeUrl(String.valueOf(baseResponse.getData()));
        } catch (Exception e) {
            log.info("==================【线下CSB动态扫码】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        csbDynamicScanVO.setOrderNo(orders.getMerchantOrderId());
        //Enets通道需要Base64解码
        if (AD3Constant.ENETS.equalsIgnoreCase(basicInfoVO.getChannel().getIssuerId())) {
            csbDynamicScanVO.setDecodeType(TradeConstant.BASE_64);
        }
        log.info("==================【线下CSB动态扫码】==================【下单结束】");
        return csbDynamicScanVO;
    }

    /**
     * 线下同机构BSC动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构BSC动态扫码输出实体
     */
    @Override
    public BscDynamicScanVO bscDynamicScan(OfflineTradeDTO offlineTradeDTO) {
        log.info("==================【线下BSC动态扫码】==================【请求参数】 offlineTradeDTO: {}", JSON.toJSONString(offlineTradeDTO));
        if (StringUtils.isEmpty(offlineTradeDTO.getAuthCode())) {
            log.info("==================【线下BSC动态扫码】==================【付款编码为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //重复请求
        if (!commonBusinessService.repeatedRequests(offlineTradeDTO.getMerchantId(), offlineTradeDTO.getOrderNo())) {
            log.info("==================【线下BSC动态扫码】==================【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //验签
        if (!commonBusinessService.checkUniversalSign(offlineTradeDTO)) {
            log.info("==================【线下BSC动态扫码,币种信息】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //获取收单基础信息并校验
        BasicInfoVO basicInfoVO = getBasicAndCheck(offlineTradeDTO);
        //设置订单属性
        Orders orders = setAttributes(offlineTradeDTO, basicInfoVO);
        //换汇
        commonBusinessService.swapRateByPayment(basicInfoVO, orders);
        //校验商户产品与通道的限额
        commonBusinessService.checkQuota(orders, basicInfoVO.getMerchantProduct(), basicInfoVO.getChannel());
        //截取币种默认值
        commonBusinessService.interceptDigit(orders, basicInfoVO.getCurrency());
        //计算手续费
        commonBusinessService.calculateCost(basicInfoVO, orders);
        orders.setReportChannelTime(new Date());
        orders.setTradeStatus(TradeConstant.ORDER_PAYING);
        log.info("==================【线下BSC动态扫码】==================【落地订单信息】 orders:{}", JSON.toJSONString(orders));
        ordersMapper.insert(orders);
        try {
            //上报通道
            ChannelsAbstract channelsAbstract = handlerContext.getInstance(basicInfoVO.getChannel().getServiceNameMark());
            channelsAbstract.offlineBSC(orders, basicInfoVO.getChannel(), offlineTradeDTO.getAuthCode());
        } catch (Exception e) {
            log.info("==================【线下BSC动态扫码】==================【上报通道异常】", e);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        log.info("==================【线下BSC动态扫码】==================【下单结束】");
        return new BscDynamicScanVO(orders, offlineTradeDTO.getOrderTime());
    }

    /**
     * 线下查询订单列表【对外API】
     *
     * @param offlineCheckOrdersDTO 查询订单输入实体
     * @return 订单集合
     */
    @Override
    public List<OfflineCheckOrdersVO> checkOrder(OfflineCheckOrdersDTO offlineCheckOrdersDTO) {
        log.info("==================【线下查询订单】==================【请求参数】 offlineCheckOrdersDTO: {}", JSON.toJSONString(offlineCheckOrdersDTO));
        //验签
        if (!commonBusinessService.checkUniversalSign(offlineCheckOrdersDTO)) {
            log.info("==================【线下查询订单】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //校验设备
        checkDevice(offlineCheckOrdersDTO.getMerchantId(), offlineCheckOrdersDTO.getImei(), offlineCheckOrdersDTO.getOperatorId());
        //页码默认为1
        if (offlineCheckOrdersDTO.getPageNum() == null) {
            offlineCheckOrdersDTO.setPageNum(1);
        }
        //每页默认30
        if (offlineCheckOrdersDTO.getPageSize() == null) {
            offlineCheckOrdersDTO.setPageSize(30);
        }
        //分页查询订单
        List<OfflineCheckOrdersVO> offlineCheckOrdersVOList = ordersMapper.offlineCheckOrders(offlineCheckOrdersDTO);
        log.info("==================【线下查询订单】==================【响应参数】 offlineOrdersVOS: {}", JSON.toJSONString(offlineCheckOrdersVOList));
        return offlineCheckOrdersVOList;
    }

    /**
     * POS机查询商户产品,币种信息
     *
     * @param posGetMerProDTO POS机查询商户产品信息输入实体
     * @return POS机查询商户产品, 币种信息输出实体集合
     */
    @Override
    public PosMerProCurVO posGetMerPro(PosGetMerProDTO posGetMerProDTO) {
        log.info("===================【POS机查询商户产品,币种信息】===================【参数记录】 posGetMerProDTO: {}", JSON.toJSONString(posGetMerProDTO));
        //验签
        if (!commonBusinessService.checkUniversalSign(posGetMerProDTO)) {
            log.info("==================【POS机查询商户产品,币种信息】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //校验设备信息
        checkDevice(posGetMerProDTO.getMerchantId(), posGetMerProDTO.getImei(), posGetMerProDTO.getOperatorId());
        //查询AW支持币种
        List<PosCurrencyVO> currencies = currencyMapper.selectAllCodeAndDefaults();
        if (ArrayUtil.isEmpty(currencies)) {
            log.info("===================【POS机查询商户产品,币种信息】===================【币种信息不存在】");
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        if (StringUtils.isEmpty(posGetMerProDTO.getLanguage())) {
            posGetMerProDTO.setLanguage(TradeConstant.EN_US);
        }
        //查询机构产品信息
        List<PosMerProVO> posMerProVOList = merchantProductMapper.selectMerPro(posGetMerProDTO.getMerchantId(), TradeConstant.PRODUCT_OFFLINE, posGetMerProDTO.getLanguage(), posGetMerProDTO.getTradeType());
        if (ArrayUtil.isEmpty(posMerProVOList)) {
            log.info("===================【POS机查询商户产品,币种信息】===================【商户产品不存在】");
            throw new BusinessException(EResultEnum.INSTITUTIONAL_PRODUCTS_DO_NOT_EXIST.getCode());
        }
        for (PosMerProVO posMerProVO : posMerProVOList) {
            //截取支付方式名称
            if (!StringUtils.isEmpty(posMerProVO.getPayTypeName())) {
                if (posMerProVO.getPayTypeName().contains("-")) {
                    posMerProVO.setPayTypeName(posMerProVO.getPayTypeName().substring(0, posMerProVO.getPayTypeName().indexOf("-")));
                } else if (posMerProVO.getPayTypeName().contains("CSB")) {
                    posMerProVO.setPayTypeName(posMerProVO.getPayTypeName().substring(0, posMerProVO.getPayTypeName().indexOf("C")));
                } else if (posMerProVO.getPayTypeName().contains("BSC")) {
                    posMerProVO.setPayTypeName(posMerProVO.getPayTypeName().substring(0, posMerProVO.getPayTypeName().indexOf("B")));
                }
            }

        }
        List<String> flagList = new ArrayList<>();
        //排序机构产品集合
        List<PosMerProResultVO> sortProductList = new ArrayList<>();
        for (int i = 0; i < posMerProVOList.size(); i++) {
            boolean flagOne = false;
            for (String flagName : flagList) {
                if (posMerProVOList.get(i).getPayTypeName().equalsIgnoreCase(flagName)) {
                    flagOne = true;
                    break;
                }
            }
            if (flagOne) {
                continue;
            }
            boolean flagTwo = true;
            PosMerProResultVO posMerProResultVO = new PosMerProResultVO();
            posMerProResultVO.setProductDetailsLogo(posMerProVOList.get(i).getProductDetailsLogo());
            posMerProResultVO.setProductPrintLogo(posMerProVOList.get(i).getProductPrintLogo());
            posMerProResultVO.setPayTypeName(posMerProVOList.get(i).getPayTypeName());
            for (int j = i + 1; j < posMerProVOList.size(); j++) {
                if (posMerProVOList.get(i).getPayTypeName().equalsIgnoreCase(posMerProVOList.get(j).getPayTypeName())) {
                    if ("BSC".equalsIgnoreCase(posMerProVOList.get(i).getFlag())) {
                        posMerProResultVO.setBSCProductCode(posMerProVOList.get(i).getProductCode());
                        posMerProResultVO.setCSBProductCode(posMerProVOList.get(j).getProductCode());
                    } else {
                        posMerProResultVO.setBSCProductCode(posMerProVOList.get(j).getProductCode());
                        posMerProResultVO.setCSBProductCode(posMerProVOList.get(i).getProductCode());
                    }
                    posMerProResultVO.setBSC("1");
                    posMerProResultVO.setCSB("1");
                    flagList.add(posMerProVOList.get(i).getPayTypeName());
                    flagTwo = false;
                    break;
                }
            }
            if (flagTwo) {
                if ("BSC".equalsIgnoreCase(posMerProVOList.get(i).getFlag())) {
                    posMerProResultVO.setBSCProductCode(posMerProVOList.get(i).getProductCode());
                    posMerProResultVO.setBSC("1");
                } else {
                    posMerProResultVO.setCSBProductCode(posMerProVOList.get(i).getProductCode());
                    posMerProResultVO.setCSB("1");
                }
            }
            sortProductList.add(posMerProResultVO);
        }
        return new PosMerProCurVO(currencies, sortProductList);
    }

    /**
     * POS机查询订单列表信息
     *
     * @param posQueryOrderListDTO POS机查询订单接口输入实体
     * @return 订单列表
     */
    @Override
    public List<PosQueryOrderListVO> posQueryOrderList(PosQueryOrderListDTO posQueryOrderListDTO) {
        log.info("===================【POS机查询订单列表信息】===================【参数记录】 posQueryOrderListDTO: {}", JSON.toJSONString(posQueryOrderListDTO));
        //验签
        if (!commonBusinessService.checkUniversalSign(posQueryOrderListDTO)) {
            log.info("==================【POS机查询订单列表信息】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //校验设备
        checkDevice(posQueryOrderListDTO.getMerchantId(), posQueryOrderListDTO.getImei(), posQueryOrderListDTO.getOperatorId());
        //页码默认为1
        if (posQueryOrderListDTO.getPageNum() == null) {
            posQueryOrderListDTO.setPageNum(1);
        }
        //每页默认30
        if (posQueryOrderListDTO.getPageSize() == null) {
            posQueryOrderListDTO.setPageSize(30);
        }
        if (StringUtils.isEmpty(posQueryOrderListDTO.getLanguage())) {
            posQueryOrderListDTO.setLanguage(TradeConstant.EN_US);
        }
        List<PosQueryOrderListVO> posQueryOrderListVOList = ordersMapper.posQueryOrderList(posQueryOrderListDTO);
        for (PosQueryOrderListVO posQueryOrderListVO : posQueryOrderListVOList) {
            //截取币种默认值
            if (!StringUtils.isEmpty(posQueryOrderListVO.getDefaultValue())) {
                String defaultValue = posQueryOrderListVO.getDefaultValue();
                int bitPos = defaultValue.indexOf(".");
                int numOfBits;
                if (bitPos == -1) {
                    numOfBits = 0;
                } else {
                    numOfBits = defaultValue.length() - bitPos - 1;
                }
                posQueryOrderListVO.setOrderAmount(String.valueOf(posQueryOrderListVO.getAmount().setScale(numOfBits, BigDecimal.ROUND_DOWN)));
            }
            //截取支付方式名称
            if (!StringUtils.isEmpty(posQueryOrderListVO.getPayTypeName())) {
                if (posQueryOrderListVO.getPayTypeName().contains("-")) {
                    posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("-")));
                } else if (posQueryOrderListVO.getPayTypeName().contains("CSB")) {
                    posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("C")));
                } else if (posQueryOrderListVO.getPayTypeName().contains("BSC")) {
                    posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("B")));
                }
            }
        }
        return posQueryOrderListVOList;
    }

    /**
     * POS机查询订单详情
     *
     * @param posQueryOrderListDTO POS机查询订单详情输入实体
     * @return 订单
     */
    @Override
    public PosQueryOrderListVO posQueryOrderDetail(PosQueryOrderListDTO posQueryOrderListDTO) {
        log.info("===================【POS机查询订单详情】===================【参数记录】 posQueryOrderListDTO: {}", JSON.toJSONString(posQueryOrderListDTO));
        if (StringUtils.isEmpty(posQueryOrderListDTO.getOrderNo())) {
            log.info("===================【POS机查询订单详情】===================【商户订单号为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //验签
        if (!commonBusinessService.checkUniversalSign(posQueryOrderListDTO)) {
            log.info("==================【POS机查询订单详情】==================【签名不匹配】");
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        //校验设备
        checkDevice(posQueryOrderListDTO.getMerchantId(), posQueryOrderListDTO.getImei(), posQueryOrderListDTO.getOperatorId());
        if (StringUtils.isEmpty(posQueryOrderListDTO.getLanguage())) {
            posQueryOrderListDTO.setLanguage(TradeConstant.EN_US);
        }
        PosQueryOrderListVO posQueryOrderListVO = ordersMapper.posQueryOrderDetail(posQueryOrderListDTO);
        //截取币种默认值
        if (!StringUtils.isEmpty(posQueryOrderListVO.getDefaultValue())) {
            String defaultValue = posQueryOrderListVO.getDefaultValue();
            int bitPos = defaultValue.indexOf(".");
            int numOfBits;
            if (bitPos == -1) {
                numOfBits = 0;
            } else {
                numOfBits = defaultValue.length() - bitPos - 1;
            }
            posQueryOrderListVO.setOrderAmount(String.valueOf(posQueryOrderListVO.getAmount().setScale(numOfBits, BigDecimal.ROUND_DOWN)));
        }
        //截取支付方式名称
        if (!StringUtils.isEmpty(posQueryOrderListVO.getPayTypeName())) {
            if (posQueryOrderListVO.getPayTypeName().contains("-")) {
                posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("-")));
            } else if (posQueryOrderListVO.getPayTypeName().contains("CSB")) {
                posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("C")));
            } else if (posQueryOrderListVO.getPayTypeName().contains("BSC")) {
                posQueryOrderListVO.setPayTypeName(posQueryOrderListVO.getPayTypeName().substring(0, posQueryOrderListVO.getPayTypeName().indexOf("B")));
            }
        }
        return posQueryOrderListVO;
    }

}
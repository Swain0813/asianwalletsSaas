package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.vo.BasicInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.Map;


/**
 * 通用业务接口
 */
@Slf4j
@Service
public class CommonBusinessServiceImpl implements CommonBusinessService {

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Value("${custom.warning.mobile}")
    private String warningMobile;

    @Value("${custom.warning.email}")
    private String warningEmail;

    /**
     * 校验MD5签名
     *
     * @param obj 验签对象
     * @return 布尔值
     */
    @Override
    public boolean checkSignByMd5(Object obj) {
        try {
            //将对象转换成Map
            Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
            Attestation attestation = commonRedisDataService.getAttestationByMerchantId(map.get("merchantId"));
            if (attestation == null) {
                log.info("===============【校验MD5签名】===============【密钥不存在】");
                return false;
            }
            //取出签名字段
            String sign = map.get("sign");
            map.put("sign", null);
            //将请求参数排序后与md5Key拼接
            String clearText = SignTools.getSignStr(map) + attestation.getMd5key();
            log.info("===============【校验MD5签名】===============【签名前的明文】 clearText: {}", clearText);
            String decryptSign = MD5Util.getMD5String(clearText);
            log.info("===============【校验MD5签名】===============【签名后的密文】 decryptSign: {}", decryptSign);
            return sign.equalsIgnoreCase(decryptSign);
        } catch (Exception e) {
            log.info("===============【校验MD5签名】===============【验签异常】", e);
        }
        return false;
    }

    /**
     * 通用签名校验
     *
     * @param obj 验签对象
     * @return 布尔值
     */
    @Override
    public boolean checkUniversalSign(Object obj) {
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        String sign = map.get("sign");
        String signType = map.get("signType");
        String merchantId = map.get("merchantId");
        if (sign == null || "".equals(sign)) {
            throw new BusinessException(EResultEnum.SIGNATURE_CANNOT_BE_EMPTY.getCode());
        }
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId((map.get("merchantId")));
        if (attestation == null) {
            log.info("===============【通用签名校验方法】===============【密钥不存在】");
            return false;
        }
        if (signType.equals(TradeConstant.RSA)) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] signMsg = decoder.decode(sign);
            map.put("sign", null);
            byte[] data = SignTools.getSignStr(map).getBytes();
            try {
                return RSAUtils.verify(data, signMsg, attestation.getMerPubkey());
            } catch (Exception e) {
                log.info("-----------  【通用签名校验】 RSA校验发生错误----------商户id:{},签名signMsg:{}", merchantId, signMsg);
            }
        } else if (signType.equals(TradeConstant.MD5)) {
            String str = SignTools.getSignStr(map) + attestation.getMd5key();
            log.info("----------【通用签名校验】 MD5加密前明文----------str:{}", str);
            String decryptSign = MD5Util.getMD5String(str);
            return sign.equalsIgnoreCase(decryptSign);
        }
        return false;
    }

    /**
     * 换汇计算
     *
     * @param localCurrency   本币
     * @param foreignCurrency 外币
     * @param floatRate       浮动率
     * @param amount          金额
     * @return 换汇输出实体
     */
    @Override
    public CalcExchangeRateVO calcExchangeRate(String localCurrency, String foreignCurrency, BigDecimal floatRate, BigDecimal amount) {
        log.info("==================【换汇计算】==================【换汇开始】");
        CalcExchangeRateVO calcExchangeRateVO = new CalcExchangeRateVO();
        calcExchangeRateVO.setExchangeTime(new Date());
        try {
            ExchangeRate localToForeignRate = commonRedisDataService.getExchangeRateByCurrency(localCurrency, foreignCurrency);
            if (localToForeignRate == null || localToForeignRate.getBuyRate() == null) {
                messageFeign.sendSimple(warningMobile, "换汇计算:查询汇率异常!本位币种:" + localCurrency + " 目标币种:" + foreignCurrency);
                messageFeign.sendSimpleMail(warningEmail, "换汇计算:查询汇率异常!", "换汇计算:查询汇率异常!本位币种:" + localCurrency + " 目标币种:" + foreignCurrency);
                calcExchangeRateVO.setExchangeStatus(TradeConstant.SWAP_FALID);
                return calcExchangeRateVO;
            }
            ExchangeRate foreignToLocalRate = commonRedisDataService.getExchangeRateByCurrency(foreignCurrency, localCurrency);
            if (foreignToLocalRate == null || foreignToLocalRate.getBuyRate() == null) {
                messageFeign.sendSimple(warningMobile, "换汇计算:查询汇率异常!本位币种:" + foreignCurrency + " 目标币种:" + localCurrency);
                messageFeign.sendSimpleMail(warningEmail, "换汇计算:查询汇率异常!", "换汇计算:查询汇率异常!本位币种:" + foreignCurrency + " 目标币种:" + localCurrency);
                calcExchangeRateVO.setExchangeStatus(TradeConstant.SWAP_FALID);
                return calcExchangeRateVO;
            }
            //浮动率为空,默认为0
            if (floatRate == null) {
                floatRate = new BigDecimal(0);
            }
            //换汇汇率 = 汇率 * (1 + 浮动率)
            BigDecimal swapRate = localToForeignRate.getBuyRate().multiply(floatRate.add(new BigDecimal(1)));
            //交易金额 = 订单金额 * 换汇汇率
            BigDecimal tradeAmount = amount.multiply(swapRate);
            //四舍五入保留2位
            calcExchangeRateVO.setTradeAmount(tradeAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            //换汇汇率
            calcExchangeRateVO.setExchangeRate(swapRate);
            //本币转外币汇率
            calcExchangeRateVO.setOriginalRate(localToForeignRate.getBuyRate());
            //外币转本币汇率
            calcExchangeRateVO.setReverseRate(foreignToLocalRate.getBuyRate());
            //换汇成功
            calcExchangeRateVO.setExchangeStatus(TradeConstant.SWAP_SUCCESS);
            log.info("==================【换汇计算】==================【换汇结果】 calcExchangeRateVO: {}", JSON.toJSONString(calcExchangeRateVO));
        } catch (Exception e) {
            log.info("==================【换汇计算】==================【换汇异常】", e);
            calcExchangeRateVO.setExchangeStatus(TradeConstant.SWAP_FALID);
        }
        return calcExchangeRateVO;
    }

    /**
     * 校验重复请求【线上与线下下单】
     *
     * @param merchantId      商户编号
     * @param merchantOrderId 商户订单号
     * @return 布尔值
     */
    @Override
    public boolean repeatedRequests(String merchantId, String merchantOrderId) {
        //拼接重复请求KEY
        String redisKey = TradeConstant.REPEATED_REQUEST_KEY.concat(merchantId.concat("_").concat(merchantOrderId));
        if (!StringUtils.isEmpty(redisService.get(redisKey)) && redisService.get(redisKey).equals(merchantOrderId)) {
            return false;
        }
        redisService.set(redisKey, merchantOrderId, 2);
        return true;
    }

    /**
     * 校验订单币种是否支持与默认值【线上与线下下单】
     *
     * @param orderCurrency 订单币种
     * @param orderAmount   订单金额
     * @return 布尔值
     */
    @Override
    public boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount) {
        Currency currency = commonRedisDataService.getCurrencyByCode(orderCurrency);
        if (currency == null) {
            throw new BusinessException(EResultEnum.PRODUCT_CURRENCY_NO_SUPPORT.getCode());
        }
        return new StringBuilder(currency.getDefaults()).reverse().indexOf(".") >= new StringBuilder(String.valueOf(orderAmount)).reverse().indexOf(".");
    }

    /**
     * 校验商户产品与通道的限额【线上与线下下单】
     *
     * @param orders          订单
     * @param merchantProduct 商户产品
     * @param channel         通道
     */
    @Override
    public void checkQuota(Orders orders, MerchantProduct merchantProduct, Channel channel) {
        //校验通道限额
        if ((channel.getLimitMinAmount() != null && channel.getLimitMaxAmount() != null) &&
                (channel.getLimitMinAmount().compareTo(BigDecimal.ZERO) != 0 && channel.getLimitMaxAmount().compareTo(BigDecimal.ZERO) != 0)) {
            if (orders.getTradeAmount().compareTo(channel.getLimitMinAmount()) < 0) {
                log.info("==================【校验商户产品与通道的限额】==================【小于通道单笔金额限制】 TradeAmount: {} | LimitMinAmount: {} ", orders.getTradeAmount(), channel.getLimitMinAmount());
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark("小于通道单笔金额限制");
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.LESS_THAN_EEOR.getCode());
            }
            if (orders.getTradeAmount().compareTo(channel.getLimitMaxAmount()) > 0) {
                log.info("==================【校验商户产品与通道的限额】==================【大于通道单笔金额限制】 TradeAmount: {} | LimitMaxAmount: {} ", orders.getTradeAmount(), channel.getLimitMaxAmount());
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark("大于通道单笔金额限制");
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.LIMIT_AMOUNT_ERROR.getCode());
            }
        }
        //校验机构产品限额
        if (merchantProduct.getAuditStatus() != null && TradeConstant.AUDIT_SUCCESS.equals(merchantProduct.getAuditStatus())) {
            if (merchantProduct.getLimitAmount() != null && orders.getTradeAmount().compareTo(merchantProduct.getLimitAmount()) > 0) {
                log.info("==================【校验商户产品与通道的限额】==================【交易金额大于商户产品单笔限额】");
                orders.setRemark("交易金额大于商户产品单笔限额");
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                ordersMapper.insert(orders);
                throw new BusinessException(EResultEnum.LIMIT_AMOUNT_ERROR.getCode());
            }
            //获取今天日期
            String todayDate = DateToolUtils.getReqDateE();
            //日交易限额key
            String dailyTotalAmountKey = TradeConstant.DAILY_TOTAL_AMOUNT_KEY.concat("_").concat(orders.getMerchantId()).concat("_").concat(String.valueOf(orders.getProductCode())).concat("_").concat(todayDate);
            //日交易笔数key
            String dailyTotalCountKey = TradeConstant.DAILY_TRADING_COUNT_KEY.concat("_").concat(orders.getMerchantId()).concat("_").concat(String.valueOf(orders.getProductCode()).concat("_").concat(todayDate));
            String dailyAmount = redisService.get(dailyTotalAmountKey);
            String dailyCount = redisService.get(dailyTotalCountKey);
            if (StringUtils.isEmpty(dailyAmount) || StringUtils.isEmpty(dailyCount)) {
                //用户第一次下单时
                redisService.set(dailyTotalAmountKey, "0", 24 * 60 * 60);
                redisService.set(dailyTotalCountKey, "0", 24 * 60 * 60);
            } else {
                //日交易笔数
                Integer dailyTradingCount = Integer.parseInt(dailyCount);
                if (merchantProduct.getDailyTradingCount() != null && dailyTradingCount >= merchantProduct.getDailyTradingCount()) {
                    log.info("==================【校验商户产品与通道的限额】==================【日交易笔数不合法】 dailyTradingCount: {}", dailyTradingCount);
                    orders.setRemark("日交易笔数不合法");
                    orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                    ordersMapper.insert(orders);
                    throw new BusinessException(EResultEnum.TRADE_COUNT_ERROR.getCode());
                }
                //TODO 日交易限额
        /*        BigDecimal dailyTotalAmount = new BigDecimal(dailyAmount);
                if (merchantProduct.getDailyTotalAmount() != null && dailyTotalAmount.compareTo(merchantProduct.getDailyTotalAmount()) >= 0) {
                    log.info("==================【校验商户产品与通道的限额】==================【日交易金额不合法】 dailyTotalAmount: {}", dailyTotalAmount);
                    orders.setRemark("日交易金额不合法");
                    orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                    ordersMapper.insert(orders);
                    baseResponse.setCode(EResultEnum.TRADE_AMOUNT_ERROR.getCode());
                    return baseResponse;
                }*/
            }
        }
    }

    /**
     * 截取Url
     *
     * @param serverUrl 服务器回调地址
     * @param orders    订单
     */
    @Override
    public void getUrl(String serverUrl, Orders orders) {
        try {
            if (!StringUtils.isEmpty(serverUrl)) {
                String[] split = serverUrl.split("/");
                StringBuffer sb = new StringBuffer();
                if (serverUrl.contains("http")) {
                    for (int i = 0; i < split.length; i++) {
                        if (i == 2) {
                            sb.append(split[i]);
                            break;
                        } else {
                            sb.append(split[i]).append("/");
                        }
                    }
                } else {
                    sb.append(split[0]);
                }
                orders.setReqIp(String.valueOf(sb));//请求ip
            } else {
                orders.setReqIp(auditorProvider.getReqIp());//请求ip
            }
        } catch (Exception e) {
            log.info("===============【截取网站URL异常】===============", e);
        }
    }

    /**
     * 计算手续费
     *
     * @param basicInfoVO 交易基础信息实体
     * @param orders      orders
     */
    @Override
    public void calculateCost(BasicInfoVO basicInfoVO, Orders orders) {
        log.info("-----------------【计费信息记录】-----------------计算手续费开始");
        //机构产品
        MerchantProduct merchantProduct = basicInfoVO.getMerchantProduct();
        if (!orders.getOrderCurrency().equals(basicInfoVO.getChannel().getCurrency()) && !basicInfoVO.getInstitution().getDcc()) {
            log.info("-----------------【计算手续费】-----------------交易币种与订单币种不一致 机构未开通DCC");
            orders.setTradeStatus(TradeConstant.PAYMENT_FAIL);
            orders.setRemark("机构不支持DCC");
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.DCC_IS_NOT_OPEN.getCode());
        }
        //查询出商户对应产品的费率信息
        if (merchantProduct.getRate() == null || merchantProduct.getRateType() == null || merchantProduct.getAddValue() == null) {
            log.info("-----------------【计算手续费】-----------------商户产品配置信息错误 商户产品信息:{}", JSON.toJSONString(merchantProduct));
            orders.setRemark("商户产品配置信息错误");
            orders.setTradeStatus(TradeConstant.PAYMENT_FAIL);
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.MERCHANT_PRODUCT_CONFIGURATION_INFORMATION_ERROR.getCode());
        }
        BigDecimal orderFee = BigDecimal.ZERO;
        //单笔费率
        if (merchantProduct.getRateType().equals(TradeConstant.FEE_TYPE_RATE)) {
            //手续费=交易金额*单笔费率+附加值
            orderFee = orders.getTradeAmount().multiply(merchantProduct.getRate()).add(merchantProduct.getAddValue());
            //判断手续费是否小于最小值，大于最大值
            if (merchantProduct.getMinTate() != null && orderFee.compareTo(merchantProduct.getMinTate()) < 0) {
                orderFee = merchantProduct.getMinTate();
            }
            if (merchantProduct.getMaxTate() != null && orderFee.compareTo(merchantProduct.getMaxTate()) > 0) {
                orderFee = merchantProduct.getMaxTate();
            }
        }
        //单笔定额
        if (merchantProduct.getRateType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
            //手续费=单笔定额值+附加值
            orderFee = merchantProduct.getRate().add(merchantProduct.getAddValue());
        }
        //订单币种与交易币种不一致时，转换为订单币种的手续费
        if (!orders.getOrderCurrency().equals(orders.getTradeCurrency())) {
            //将手续费转换成订单币种的金额
            BigDecimal amount = orderFee.multiply(orders.getTradeForOrderRate());
            //四舍五入保留2位
            orderFee = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        orders.setFee(orderFee);
        orders.setChargeStatus(TradeConstant.CHARGE_STATUS_SUCCESS);
        orders.setChargeTime(new Date());
        orders.setRateType(merchantProduct.getRateType());
        orders.setAddValue(merchantProduct.getAddValue());
        orders.setFeePayer(merchantProduct.getFeePayer());
        log.info("-----------------【计费信息记录】-----------------计算手续费结束 手续费:{}", orderFee);

        log.info("-----------------【计费信息记录】-----------------计算通道手续费开始");
        Channel channel = basicInfoVO.getChannel();
        if (channel.getChannelRate() == null || channel.getChannelMinRate() == null
                || channel.getChannelMaxRate() == null) {
            log.info("-----------------【计算通道手续费】-----------------通道计费信息错误 channel:{}", JSON.toJSONString(channel));
            orders.setTradeStatus(TradeConstant.PAYMENT_FAIL);
            orders.setRemark("通道计费信息错误");
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.CALCCHANNEL_POUNDAGE_FAILURE.getCode());
        }
        BigDecimal channelFee = BigDecimal.ZERO;
        //通道单笔费率
        if (channel.getChannelFeeType().equals(TradeConstant.FEE_TYPE_RATE)) {
            //通道手续费=交易金额*费率
            channelFee = orders.getTradeAmount().multiply(channel.getChannelRate());
            //判断通道手续费是否小于最小值，大于最大值
            if (channel.getChannelMinRate() != null && channelFee.compareTo(channel.getChannelMinRate()) < 0) {
                channelFee = channel.getChannelMinRate();
            }
            if (channel.getChannelMaxRate() != null && channelFee.compareTo(channel.getChannelMaxRate()) > 0) {
                channelFee = channel.getChannelMaxRate();
            }
        } else if (channel.getChannelFeeType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
            //通道单笔定额
            //通道手续费=通道单笔定额
            channelFee = channel.getChannelRate();
        }
        orders.setChannelFee(channelFee);
        orders.setChannelFeeType(channel.getChannelFeeType());
        orders.setChannelRate(channel.getChannelRate());
        log.info("-----------------【计费信息记录】-----------------计算通道手续费结束 通道手续费:{}", channelFee);
        //计算通道网关手续费
        if (TradeConstant.CHANNEL_GATEWAY_CHARGE_YES.equals(channel.getChannelGatewayCharge())
                && TradeConstant.CHANNEL_GATEWAY_CHARGE_ALL_STATUS.equals(channel.getChannelGatewayStatus())) {
            CalcGatewayFee(orders, channel);
        }
    }

    /**
     * 计算通道网关手续费
     * @param orders  订单
     * @param channel 通道
     */
    public void CalcGatewayFee(Orders orders, Channel channel) {
        log.info("-----------------【计费信息记录】-----------------计算通道网关手续费开始");
        BigDecimal channelGatewayFee = BigDecimal.ZERO;
        if (channel.getChannelGatewayRate() == null || channel.getChannelGatewayMinRate() == null ||
                channel.getChannelGatewayMaxRate() == null) {
            log.info("-----------------【计算通道网关手续费】-----------------通道网关计费信息错误 channel:{}", JSON.toJSONString(channel));
            orders.setTradeStatus(TradeConstant.PAYMENT_FAIL);
            orders.setRemark("通道网关计费信息错误");
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.CALCCHANNEL_GATEWAYPOUNDAGE_FAILURE.getCode());
        }
        //单笔费率
        if (channel.getChannelGatewayFeeType().equals(TradeConstant.FEE_TYPE_RATE)) {
            //手续费=交易金额*费率
            channelGatewayFee = orders.getTradeAmount().multiply(channel.getChannelGatewayRate());
            //判断手续费是否小于最小值，大于最大值
            if (channel.getChannelGatewayMinRate() != null && channelGatewayFee.compareTo(channel.getChannelGatewayMinRate()) < 0) {
                channelGatewayFee = channel.getChannelGatewayMinRate();
            }
            if (channel.getChannelGatewayMaxRate() != null && channelGatewayFee.compareTo(channel.getChannelGatewayMaxRate()) > 0) {
                channelGatewayFee = channel.getChannelGatewayMaxRate();
            }
        }
        //单笔定额
        if (channel.getChannelGatewayFeeType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
            //手续费=通道网关手续费
            channelGatewayFee = channel.getChannelGatewayRate();
        }
        orders.setChannelGatewayFee(channelGatewayFee);
        orders.setChannelGatewayCharge(channel.getChannelGatewayCharge());
        orders.setChannelGatewayStatus(channel.getChannelGatewayStatus());
        orders.setChannelGatewayFeeType(channel.getChannelGatewayFeeType());
        orders.setChannelGatewayRate(channel.getChannelGatewayRate());
        log.info("-----------------【计费信息记录】-----------------计算通道手续费结束 通道手续费:{}", channelGatewayFee);
    }

    /**
     * 退款和撤销成功的场合
     *
     * @param orderRefund
     */
    @Override
    public void updateOrderRefundSuccess(OrderRefund orderRefund) {
        if(orderRefund.getRemark4()!=null && TradeConstant.RV.equals(orderRefund.getRemark4())){
            //撤销成功-更新订单的撤销状态
            ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), null, TradeConstant.ORDER_CANNEL_SUCCESS);
        } else {
            //退款成功的场合
            if (TradeConstant.REFUND_TYPE_TOTAL.equals(orderRefund.getRefundType())) {
                ordersMapper.updateOrderRefundStatus(orderRefund.getMerchantOrderId(), TradeConstant.ORDER_REFUND_SUCCESS);
            } else {
                if (orderRefund.getRemark2().equals("全额")) {
                    ordersMapper.updateOrderRefundStatus(orderRefund.getMerchantOrderId(), TradeConstant.ORDER_REFUND_SUCCESS);
                } else {
                    ordersMapper.updateOrderRefundStatus(orderRefund.getMerchantOrderId(), TradeConstant.ORDER_REFUND_PART_SUCCESS);
                }
            }
        }
    }

    /**
     * 退款和撤销失败的场合
     * @param orderRefund
     */
    @Override
    public void updateOrderRefundFail(OrderRefund orderRefund){
        if(orderRefund.getRemark4()!=null && TradeConstant.RV.equals(orderRefund.getRemark4())){
            //撤销失败
            ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), null, TradeConstant.ORDER_CANNEL_FALID);
        }else{
            //退款失败的场合
            if (TradeConstant.REFUND_TYPE_TOTAL.equals(orderRefund.getRefundType())) {
                ordersMapper.updateOrderRefundStatus(orderRefund.getMerchantOrderId(), TradeConstant.ORDER_REFUND_FAIL);
            } else {
                BigDecimal oldRefundAmount = orderRefundMapper.getTotalAmountByOrderId(orderRefund.getOrderId()); //已退款金额
                oldRefundAmount = oldRefundAmount == null ? BigDecimal.ZERO : oldRefundAmount;
                if (oldRefundAmount.compareTo(BigDecimal.ZERO) == 0) {
                    ordersMapper.updateOrderRefundStatus(orderRefund.getMerchantOrderId(), TradeConstant.ORDER_REFUND_FAIL);
                }
            }
        }
    }
    /**
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate 创建调账单
     * @return
     **/
    @Override
    public Reconciliation createReconciliation(String type,OrderRefund orderRefund, String remark) {
        //调账订单id
        String reconciliationId = "T" + IDS.uniqueID();
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setOrderId(orderRefund.getOrderId());
        reconciliation.setRefundOrderId(orderRefund.getId());
        reconciliation.setChannelNumber(orderRefund.getChannelNumber());
        reconciliation.setRefundChannelNumber(orderRefund.getRefundChannelNumber());
        reconciliation.setMerchantOrderId(orderRefund.getMerchantOrderId());
        reconciliation.setReconciliationType(AsianWalletConstant.RECONCILIATION_IN);
        reconciliation.setMerchantName(orderRefund.getMerchantName());
        reconciliation.setMerchantId(orderRefund.getMerchantId());
        reconciliation.setAmount(orderRefund.getOrderAmount().subtract(orderRefund.getRefundFee()).add(orderRefund.getRefundOrderFee()));
        if(type.equals(TradeConstant.RA)){
            reconciliation.setAccountType(1);
        }else if(type.equals(TradeConstant.AA)){
            reconciliation.setAccountType(2);
        }
        reconciliation.setCurrency(orderRefund.getOrderCurrency());
        reconciliation.setStatus(TradeConstant.RECONCILIATION_WAIT);
        reconciliation.setChangeType(TradeConstant.TRANSFER);
        reconciliation.setRemark1(null);
        reconciliation.setRemark2(null);
        reconciliation.setRemark3(null);
        reconciliation.setId(reconciliationId);
        reconciliation.setCreateTime(new Date());
        reconciliation.setRemark(remark);
        return reconciliation;
    }
}

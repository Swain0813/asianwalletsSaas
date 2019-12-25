package com.asianwallets.trade.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.dao.AccountMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.SettleControlMapper;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.CalcFeeVO;
import com.asianwallets.trade.vo.OnlineCallbackURLVO;
import com.asianwallets.trade.vo.OnlineCallbackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


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

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private SettleControlMapper settleControlMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Value("${custom.warning.mobile}")
    private String warningMobile;

    @Value("${custom.warning.email}")
    private String warningEmail;

    /**
     * 使用机构对应平台的RSA私钥生成签名【回调时用】
     *
     * @param obj 对象
     * @return 签名
     */
    @Override
    public String generateSignatureUsePlatRSA(Object obj) {
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId(map.get("merchantId"));
        String clearText = SignTools.getSignStr(map);
        log.info("===============【使用机构对应平台的私钥生成签名】==============【签名前的明文】 clearText:{}", clearText);
        byte[] msg = clearText.getBytes();
        String sign = null;
        try {
            sign = RSAUtils.sign(msg, attestation.getPrikey());
            log.info("===============【使用机构对应平台的私钥生成签名】==============【签名后的密文】 sign:{}", sign);
        } catch (Exception e) {
            log.info("===============【使用机构对应平台的私钥生成签名】==============【签名异常】", e);
        }
        return sign;
    }

    /**
     * 使用机构对应平台的MD5生成签名【回调时用】
     *
     * @param obj 对象
     * @return 签名
     */
    @Override
    public String generateSignatureUsePlatMD5(Object obj) {
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId(map.get("merchantId"));
        map.put("sign", null);
        String clearText = SignTools.getSignStr(map) + attestation.getMd5key();
        log.info("===============【使用机构对应平台的MD5生成签名】==============【签名前的明文】 clearText:{}", clearText);
        String sign = MD5Util.getMD5String(clearText);
        log.info("===============【使用机构对应平台的MD5生成签名】==============【签名后的密文】 sign:{}", sign);
        return sign;
    }

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
        log.info("===============【通用签名校验方法】===============【验签开始】");
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        String sign = map.get("sign");
        String signType = map.get("signType");
        String merchantId = map.get("merchantId");
        if (sign == null || "".equals(sign)) {
            throw new BusinessException(EResultEnum.SIGNATURE_CANNOT_BE_EMPTY.getCode());
        }
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId((map.get("merchantId")));
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
        log.info("===============【通用签名校验方法】===============【验签结束】");
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
     * 下单换汇【线上与线下下单】
     *
     * @param basicInfoVO 基础信息
     * @param orders      订单
     */
    @Override
    public void swapRateByPayment(BasicInfoVO basicInfoVO, Orders orders) {
        if (StringUtils.isEmpty(orders.getOrderCurrency()) || StringUtils.isEmpty(orders.getTradeCurrency())) {
            log.info("==================【下单换汇】==================【换汇币种为空】");
            orders.setRemark4("换汇币种为空");
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
        }
        //币种一致时,不需要换汇
        if (orders.getOrderCurrency().equalsIgnoreCase(orders.getTradeCurrency())) {
            log.info("==================【下单换汇】==================【币种相同,无需换汇】");
            orders.setTradeAmount(orders.getOrderAmount());
            orders.setOrderForTradeRate(BigDecimal.ONE);
            orders.setTradeForOrderRate(BigDecimal.ONE);
            orders.setExchangeRate(BigDecimal.ONE);
            orders.setExchangeStatus(TradeConstant.SWAP_SUCCESS);
            orders.setExchangeTime(new Date());
            return;
        }
        //校验机构DCC
        if (!basicInfoVO.getInstitution().getDcc()) {
            log.info("==================【下单换汇】==================【机构不支持DCC】");
            orders.setRemark4("机构不支持DCC");
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.DCC_IS_NOT_OPEN.getCode());
        }
        //换汇计算
        CalcExchangeRateVO calcExchangeRateVO = calcExchangeRate(orders.getOrderCurrency(), orders.getTradeCurrency(), basicInfoVO.getMerchantProduct().getFloatRate(), orders.getOrderAmount());
        orders.setExchangeTime(calcExchangeRateVO.getExchangeTime());
        orders.setExchangeStatus(calcExchangeRateVO.getExchangeStatus());
        if (TradeConstant.SWAP_FALID.equals(calcExchangeRateVO.getExchangeStatus())) {
            log.info("==================【下单换汇】==================【换汇失败】");
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark4("换汇失败");
            ordersMapper.insert(orders);
            throw new BusinessException(EResultEnum.SYS_ERROR_CREATE_ORDER_FAIL.getCode());
        }
        orders.setExchangeRate(calcExchangeRateVO.getExchangeRate());
        orders.setTradeAmount(calcExchangeRateVO.getTradeAmount());
        orders.setOrderForTradeRate(calcExchangeRateVO.getOriginalRate());
        orders.setTradeForOrderRate(calcExchangeRateVO.getReverseRate());
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
     * @param currency      币种
     * @return 布尔值
     */
    @Override
    public boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount, Currency currency) {
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
        //TODO 校验机构产品限额
/*        if (merchantProduct.getAuditStatus() != null && TradeConstant.AUDIT_SUCCESS.equals(merchantProduct.getAuditStatus())) {
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
                BigDecimal dailyTotalAmount = new BigDecimal(dailyAmount);
                if (merchantProduct.getDailyTotalAmount() != null && dailyTotalAmount.compareTo(merchantProduct.getDailyTotalAmount()) >= 0) {
                    log.info("==================【校验商户产品与通道的限额】==================【日交易金额不合法】 dailyTotalAmount: {}", dailyTotalAmount);
                    orders.setRemark("日交易金额不合法");
                    orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                    ordersMapper.insert(orders);
                    baseResponse.setCode(EResultEnum.TRADE_AMOUNT_ERROR.getCode());
                    return baseResponse;
                }
            }
        }*/
    }

    /**
     * 截取币种默认值
     *
     * @param orders   订单
     * @param currency 币种
     */
    @Override
    public void interceptDigit(Orders orders, Currency currency) {
        int bitPos = currency.getDefaults().indexOf(".");
        int numOfBits = 0;
        if (bitPos != -1) {
            numOfBits = currency.getDefaults().length() - bitPos - 1;
        }
        //交易金额
        orders.setTradeAmount((orders.getTradeAmount().setScale(numOfBits, BigDecimal.ROUND_HALF_UP)));
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
        BigDecimal feeTrade = BigDecimal.ZERO;
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
            feeTrade = orderFee;
        } else {
            feeTrade = orderFee;
        }
        //手续费(订单币种对交易币种的手续费)
        orders.setFee(orderFee);
        //手续费(交易币种对订单币种的手续费)
        orders.setFeeTrade(feeTrade);
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
     *
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
        if (orderRefund.getRemark4() != null && TradeConstant.RV.equals(orderRefund.getRemark4())) {
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
     *
     * @param orderRefund
     */
    @Override
    public void updateOrderRefundFail(OrderRefund orderRefund) {
        if (orderRefund.getRemark4() != null && TradeConstant.RV.equals(orderRefund.getRemark4())) {
            //撤销失败
            ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), null, TradeConstant.ORDER_CANNEL_FALID);
        } else {
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
     * @return
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate 创建调账单
     **/
    @Override
    public Reconciliation createReconciliation(String type, OrderRefund orderRefund, String remark) {
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
        if (orderRefund.getFeePayer() == 1) {
            reconciliation.setAmount(orderRefund.getOrderAmount().subtract(orderRefund.getRefundFee()).add(orderRefund.getRefundOrderFee()));
        } else {
            reconciliation.setAmount(orderRefund.getOrderAmount());
        }
        if (type.equals(TradeConstant.RA)) {
            reconciliation.setAccountType(1);
        } else if (type.equals(TradeConstant.AA)) {
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

    /**
     * 创建商户对应币种的账户
     *
     * @param orders 订单
     */
    @Override
    public void createAccount(Orders orders) {
        try {
            //添加账户
            Account account = new Account();
            account.setId(IDS.uuid2());
            account.setAccountCode(IDS.uniqueID().toString());
            account.setMerchantId(orders.getMerchantId());
            account.setMerchantName(orders.getMerchantName());
            account.setCurrency(orders.getOrderCurrency());
            //默认结算金额为0
            account.setSettleBalance(BigDecimal.ZERO);
            //默认清算金额为0
            account.setClearBalance(BigDecimal.ZERO);
            //默认冻结金额为0
            account.setFreezeBalance(BigDecimal.ZERO);
            account.setEnabled(true);
            account.setCreateTime(new Date());
            account.setCreator("sys");
            account.setRemark("下单时系统自动创建的对应订单币种的账户");
            SettleControl settleControl = new SettleControl();
            settleControl.setAccountId(account.getId());
            settleControl.setId(IDS.uuid2());
            settleControl.setMinSettleAmount(BigDecimal.ZERO);
            settleControl.setSettleSwitch(false);
            settleControl.setCreateTime(new Date());
            settleControl.setEnabled(true);
            settleControl.setCreator("sys");
            settleControl.setRemark("下单时系统自动创建自动创建币种的结算控制信息");
            if (accountMapper.insertSelective(account) > 0 && settleControlMapper.insertSelective(settleControl) > 0) {
                redisService.set(AsianWalletConstant.ACCOUNT_CACHE_KEY.concat("_").concat(orders.getMerchantId()).concat("_").concat(orders.getOrderCurrency()), JSON.toJSONString(account));
            }
        } catch (Exception e) {
            log.error("===============【创建商户对应币种的账户】==============【创建异常】", e);
        }
    }

    /**
     * 配置限额限次信息
     *
     * @param merchantId  商户编号
     * @param productCode 产品编号
     * @param amount      金额
     */
    @Override
    public void quota(String merchantId, Integer productCode, BigDecimal amount) {
        log.info("=============【添加限额限次】============= START");
        //今日日期
        String todayDate = DateToolUtils.getReqDateE();
        //添加分布式锁
        String lockQuota = AsianWalletConstant.QUOTA + "_" + merchantId.concat("_").concat(String.valueOf(productCode)).concat("_").concat(todayDate);
        try {
            //日交易限额key
            String dailyTotalAmountKey = TradeConstant.DAILY_TOTAL_AMOUNT.concat("_").concat(merchantId).concat("_").concat(productCode.toString()).concat("_").concat(todayDate);
            //日交易笔数key
            String dailyTotalCountKey = TradeConstant.DAILY_TRADING_COUNT.concat("_").concat(merchantId).concat("_").concat(productCode.toString()).concat("_").concat(todayDate);
            if (redisService.lock(lockQuota, 30 * 1000)) {
                String dailyAmount = redisService.get(dailyTotalAmountKey);
                String dailyCount = redisService.get(dailyTotalCountKey);
                if (StringUtils.isEmpty(dailyAmount) || StringUtils.isEmpty(dailyCount)) {
                    dailyAmount = "0";
                    dailyCount = "0";
                }
                //日交易限额
                BigDecimal dailyTotalAmount = new BigDecimal(dailyAmount);
                //日交易笔数
                int dailyTradingCount = Integer.parseInt(dailyCount);
                //加上交易金额
                dailyTotalAmount = dailyTotalAmount.add(amount);
                //加上交易笔数
                dailyTradingCount = dailyTradingCount + 1;
                redisService.set(dailyTotalAmountKey, String.valueOf(dailyTotalAmount), 24 * 60 * 60);
                redisService.set(dailyTotalCountKey, String.valueOf(dailyTradingCount), 24 * 60 * 60);
                log.info("=============【添加限额限次】=============【添加限额限次信息成功】");
            } else {
                log.info("=============【添加限额限次】=============【获取分布式锁异常】");
            }
        } catch (NumberFormatException e) {
            log.error("=============【添加限额限次】=============【添加异常】", e);
        } finally {
            //释放锁
            redisService.releaseLock(lockQuota);
            log.info("=============【添加限额限次】============= END");
        }
    }

    /**
     * 回调时计算通道网关手续费【回调交易成功时收取】
     *
     * @param orders orders
     */
    @Override
    public void calcCallBackGatewayFeeSuccess(Orders orders) {
        try {
            //获取通道信息
            Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
            if (channel.getChannelGatewayRate() != null && TradeConstant.CHANNEL_GATEWAY_CHARGE_YES.equals(channel.getChannelGatewayCharge())
                    && TradeConstant.CHANNEL_GATEWAY_CHARGE_SUCCESS_STATUS.equals(channel.getChannelGatewayStatus())) {
                CalcFeeVO channelGatewayPoundage = calcChannelGatewayPoundage(orders.getTradeAmount(), channel);
                //通道网关手续费
                orders.setChannelGatewayFee(channelGatewayPoundage.getFee());
                int i = ordersMapper.updateByPrimaryKeySelective(orders);
                if (i == 1) {
                    log.info("===============【计算支付成功时的通道网关手续费成功】===============");
                }
            }
        } catch (Exception e) {
            log.error("===============【计算支付成功时的通道网关手续费发生异常】===============", e);
        }
    }

    /**
     * 回调时计算通道网关手续费【回调交易失败时收取】
     *
     * @param orders orders
     */
    @Override
    public void calcCallBackGatewayFeeFailed(Orders orders) {
        try {
            //获取通道信息
            Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
            if (channel.getChannelGatewayRate() != null && TradeConstant.CHANNEL_GATEWAY_CHARGE_YES.equals(channel.getChannelGatewayCharge())
                    && TradeConstant.CHANNEL_GATEWAY_CHARGE_FAILURE_STATUS.equals(channel.getChannelGatewayStatus())) {
                CalcFeeVO channelGatewayPoundage = calcChannelGatewayPoundage(orders.getTradeAmount(), channel);
                //通道网关手续费
                orders.setChannelGatewayFee(channelGatewayPoundage.getFee());
                int i = ordersMapper.updateByPrimaryKeySelective(orders);
                if (i == 1) {
                    log.info("===============【计算支付失败时的通道网关手续费成功】===============");
                }
            }
        } catch (Exception e) {
            log.error("===============【计算支付失败时的通道网关手续费发生异常】===============", e);
        }
    }

    /**
     * 计算通道网关手续费【回调时用】
     *
     * @param amount 交易金额
     * @return CalcFeeVO  通道费用输出实体
     */
    @Override
    public CalcFeeVO calcChannelGatewayPoundage(BigDecimal amount, Channel channel) {
        BigDecimal poundage = new BigDecimal(0);
        CalcFeeVO calcFeeVO = new CalcFeeVO();
        //单笔费率
        if (channel.getChannelGatewayFeeType().equals(TradeConstant.FEE_TYPE_RATE)) {
            if (channel.getChannelGatewayRate() == null || channel.getChannelGatewayMinRate() == null ||
                    channel.getChannelGatewayMaxRate() == null) {
                calcFeeVO.setChargeStatus(TradeConstant.CHARGE_STATUS_FALID);
                return calcFeeVO;
            }
            //手续费=交易金额*费率
            poundage = amount.multiply(channel.getChannelGatewayRate());
            //判断手续费是否小于最小值，大于最大值
            if (channel.getChannelGatewayMinRate() != null && poundage.compareTo(channel.getChannelGatewayMinRate()) == -1) {
                poundage = channel.getChannelGatewayMinRate();
            }
            if (channel.getChannelGatewayMaxRate() != null && poundage.compareTo(channel.getChannelGatewayMaxRate()) == 1) {
                poundage = channel.getChannelGatewayMaxRate();
            }
        }
        //单笔定额
        if (channel.getChannelGatewayFeeType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
            if (channel.getChannelGatewayRate() == null) {
                calcFeeVO.setChargeStatus(TradeConstant.CHARGE_STATUS_FALID);
                return calcFeeVO;
            }
            //手续费=通道网关手续费
            poundage = channel.getChannelGatewayRate();
        }
        //通道网关手续费
        calcFeeVO.setFee(poundage);
        calcFeeVO.setChargeStatus(TradeConstant.CHARGE_STATUS_SUCCESS);
        calcFeeVO.setChargeTime(new Date());
        return calcFeeVO;
    }

    /**
     * 回调商户服务器地址【回调接口】
     *
     * @param orders 订单
     */
    @Override
    public void replyReturnUrl(Orders orders) {
        log.info("==================【回调商户服务器】==================【回调订单信息记录】 orders: {}", JSON.toJSONString(orders));
        OnlineCallbackURLVO onlineCallbackURLVO = new OnlineCallbackURLVO(orders);
        OnlineCallbackVO onlineCallbackVO = onlineCallbackURLVO.getOnlineCallbackVO();
        if (orders.getTradeDirection().equals(TradeConstant.TRADE_ONLINE)) {
            //线上签名
            onlineCallbackVO.setSign(generateSignatureUsePlatRSA(onlineCallbackVO));
        } else {
            //线下签名
            onlineCallbackVO.setSign(generateSignatureUsePlatMD5((onlineCallbackVO)));
        }
        log.info("==================【回调商户服务器】==================【商户回调接口URL记录】  serverUrl: {}", orders.getServerUrl());
        log.info("==================【回调商户服务器】==================【回调参数记录】  onlineCallbackVO: {}", JSON.toJSON(onlineCallbackVO));
        try {
            cn.hutool.http.HttpResponse execute = HttpRequest.post(orders.getServerUrl())
                    .header(Header.CONTENT_TYPE, "application/json")
                    .body(JSON.toJSONString(onlineCallbackVO))
                    .timeout(30000)
                    .execute();
            String body = execute.body();
            log.info("==================【回调商户服务器】==================【HTTP状态码】 status: {}  | 【响应结果记录】 body: {}", execute.getStatus(), body);
            if (StringUtils.isEmpty(body) || !body.equalsIgnoreCase(AsianWalletConstant.CALLBACK_SUCCESS)) {
                log.info("==================【回调商户服务器】==================【商户响应结果不正确,上报回调商户队列】 【MQ_AW_CALLBACK_URL_FAIL】");
                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(onlineCallbackURLVO));
                rabbitMQSender.send(AD3MQConstant.E_MQ_AW_CALLBACK_URL_FAIL, JSON.toJSONString(rabbitMassage));
            }
        } catch (Exception e) {
            log.info("==================【回调商户服务器】==================【httpException异常,上报回调商户队列】 【MQ_AW_CALLBACK_URL_FAIL】", e);
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(onlineCallbackURLVO));
            rabbitMQSender.send(AD3MQConstant.E_MQ_AW_CALLBACK_URL_FAIL, JSON.toJSONString(rabbitMassage));
        }
    }

    /**
     * 支付成功发送邮件给付款人
     *
     * @param orders 订单
     */
    @Override
    @Async
    public void sendEmail(Orders orders) {
        log.info("*********************支付成功发送支付通知邮件 Start*************************************");
        try {
            if (!StringUtils.isEmpty(orders.getPayerEmail())) {
                log.info("*******************支付成功订单对应的付款人邮箱是: ******************* email: {}", orders.getPayerEmail());
                Map<String, Object> map = new HashMap<>();
                map.put("orderCurrency", orders.getOrderCurrency());//订单币种
                map.put("amount", orders.getOrderAmount());//订单金额
                map.put("reqIp", orders.getReqIp());//请求的网站url
                SimpleDateFormat sf = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);//外国时间格式
                map.put("channelCallbackTime", sf.format(orders.getChannelCallbackTime()));//支付完成时间
                map.put("merchantOrderId", orders.getMerchantOrderId());//机构订单号
                map.put("productName", orders.getProductName());//商品名称
                map.put("issuerId", orders.getBankName());//付款银行
                map.put("referenceNo", orders.getId());//AW交易流水号
                messageFeign.sendTemplateMail(orders.getPayerEmail(), orders.getLanguage(), Status._1, map);
            }
        } catch (Exception e) {
            messageFeign.sendSimple("18800330943", "支付成功发送支付通知邮件失败:" + orders.getPayerEmail());
            log.error("支付成功发送支付通知邮件失败: {}==={}", orders.getPayerEmail(), e.getMessage());
        }
        log.info("*********************支付成功发送支付通知邮件 End*************************************");
    }
}

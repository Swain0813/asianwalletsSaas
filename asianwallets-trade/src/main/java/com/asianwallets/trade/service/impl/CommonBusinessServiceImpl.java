package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
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
        //获取币种默认值
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
            if (orders.getTradeAmount().compareTo(merchantProduct.getLimitAmount()) > 0) {
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
                if (dailyTradingCount >= merchantProduct.getDailyTradingCount()) {
                    log.info("==================【校验商户产品与通道的限额】==================【日交易笔数不合法】 dailyTradingCount: {}", dailyTradingCount);
                    orders.setRemark("日交易笔数不合法");
                    orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                    ordersMapper.insert(orders);
                    throw new BusinessException(EResultEnum.TRADE_COUNT_ERROR.getCode());
                }
                //TODO 日交易限额
        /*        BigDecimal dailyTotalAmount = new BigDecimal(dailyAmount);
                if (dailyTotalAmount.compareTo(merchantProduct.getDailyTotalAmount()) >= 0) {
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
}

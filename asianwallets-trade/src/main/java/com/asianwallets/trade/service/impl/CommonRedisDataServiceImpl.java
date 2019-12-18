package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通用获取数据接口
 */
@Service
@Slf4j
public class CommonRedisDataServiceImpl implements CommonRedisDataService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private AttestationMapper attestationMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private InstitutionRequestParametersMapper institutionRequestParametersMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ChannelMapper channelMapper;

    /**
     * 根据币种编码获取币种
     *
     * @param code 币种编码
     * @return 币种
     */
    @Override
    public Currency getCurrencyByCode(String code) {
        //当前币种的默认值
        Currency currency = null;
        try {
            currency = JSON.parseObject(redisService.get(AsianWalletConstant.CURRENCY_DEFAULT.concat(code)), Currency.class);
            if (currency == null) {
                currency = currencyMapper.selectByCurrency(code);
                if (currency == null) {
                    log.info("==================【根据币种编码获取币种】==================【币种不存在】 code: {}", code);
                    return null;
                }
                redisService.set(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency, JSON.toJSONString(currency));
            }
        } catch (Exception e) {
            log.info("==================【根据币种编码获取币种】==================【获取异常】", e);
        }
        log.info("==================【根据币种编码获取币种】==================【币种信息】 currency: {}", JSON.toJSONString(currency));
        return currency;
    }

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 密钥
     */
    @Override
    public Attestation getAttestationByMerchantId(String merchantId) {
        Attestation attestation = null;
        try {
            attestation = JSON.parseObject(redisService.get(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat(merchantId)), Attestation.class);
            if (attestation == null) {
                attestation = attestationMapper.selectByMerchantId(merchantId);
                if (attestation == null) {
                    log.info("==================【根据商户ID获取密钥对象】==================【密钥对象不存在】 merchantId: {}", merchantId);
                    return null;
                }
                redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(merchantId), JSON.toJSONString(attestation));
            }
        } catch (Exception e) {
            log.info("==================【根据商户ID获取密钥对象】==================【获取异常】", e);
        }
        log.info("==================【根据商户ID获取密钥对象】==================【密钥信息】 attestation: {}", JSON.toJSONString(attestation));
        return attestation;
    }

    /**
     * 根据本币与外币获取汇率
     *
     * @param localCurrency   本币
     * @param foreignCurrency 外币
     * @return 汇率
     */
    @Override
    public ExchangeRate getExchangeRateByCurrency(String localCurrency, String foreignCurrency) {
        ExchangeRate exchangeRate = null;
        try {
            exchangeRate = JSON.parseObject(redisService.get(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(localCurrency).concat("_").concat(foreignCurrency)), ExchangeRate.class);
            if (exchangeRate == null || !exchangeRate.getEnabled()) {
                exchangeRate = exchangeRateMapper.selectByLocalCurrencyAndForeignCurrency(localCurrency, foreignCurrency);
                if (exchangeRate == null) {
                    log.info("==================【根据本币与外币获取汇率对象】==================【汇率对象不存在】 localCurrency: {} | foreignCurrency: {}", localCurrency, foreignCurrency);
                    return null;
                }
                redisService.set(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(localCurrency).concat("_").concat(foreignCurrency), JSON.toJSONString(exchangeRate));
            }
        } catch (Exception e) {
            log.info("==================【根据本币与外币获取汇率对象】==================【获取异常】", e);
        }
        log.info("==================【根据本币与外币获取汇率对象】==================【汇率信息】 exchangeRate: {}", JSON.toJSONString(exchangeRate));
        return exchangeRate;
    }

    /**
     * 根据机构ID获取机构
     *
     * @param institutionId 机构ID
     * @return 机构
     */
    @Override
    public Institution getInstitutionById(String institutionId) {
        Institution institution = null;
        try {
            institution = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institutionId)), Institution.class);
            if (institution == null) {
                institution = institutionMapper.selectByPrimaryKey(institutionId);
                if (institution == null) {
                    log.info("==================【根据机构ID获取机构】==================【机构对象不存在】 institutionId: {}", institutionId);
                    return null;
                }
                redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
            }
        } catch (Exception e) {
            log.info("==================【根据机构ID获取机构】==================【获取异常】", e);
        }
        log.info("==================【根据机构ID获取机构】==================【机构信息】 institution: {}", JSON.toJSONString(institution));
        return institution;
    }

    /**
     * 根据机构ID与交易方向查询获取机构请求参数
     *
     * @param institutionId  机构ID
     * @param tradeDirection 交易方向
     * @return 机构
     */
    @Override
    public InstitutionRequestParameters getInstitutionRequestByIdAndDirection(String institutionId, Byte tradeDirection) {
        InstitutionRequestParameters institutionRequestParameters = null;
        try {
            institutionRequestParameters = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionId).concat("_") + tradeDirection), InstitutionRequestParameters.class);
            if (institutionRequestParameters == null) {
                institutionRequestParameters = institutionRequestParametersMapper.selectByInstitutionIdAndTradeDirection(institutionId, tradeDirection);
                if (institutionRequestParameters == null) {
                    log.info("==================【根据机构ID与交易方向查询获取机构请求参数】==================【机构请求参数对象不存在】 institutionId: {} | tradeDirection: {}", institutionId, tradeDirection);
                    return null;
                }
                redisService.set(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionRequestParameters.getInstitutionCode()).concat("_") + institutionRequestParameters.getTradeDirection(),
                        JSON.toJSONString(institutionRequestParameters));
            }
        } catch (Exception e) {
            log.info("==================【根据机构ID与交易方向查询获取机构请求参数】==================【获取异常】", e);
        }
        log.info("==================【根据机构ID与交易方向查询获取机构请求参数】==================【机构请求参数信息】 institutionRequestParameters: {}", JSON.toJSONString(institutionRequestParameters));
        return institutionRequestParameters;
    }

    /**
     * 根据商户ID获取商户
     *
     * @param merchantId 商户ID
     * @return 商户
     */
    @Override
    public Merchant getMerchantById(String merchantId) {
        Merchant merchant = null;
        try {
            merchant = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchantId)), Merchant.class);
            if (merchant == null) {
                merchant = merchantMapper.selectByPrimaryKey(merchantId);
                if (merchant == null) {
                    log.info("==================【根据商户ID获取商户】==================【商户对象不存在】 merchantId: {}", merchantId);
                    return null;
                }
                redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
            }
        } catch (Exception e) {
            log.info("==================【根据商户ID获取商户】==================【获取异常】", e);
        }
        log.info("==================【根据商户ID获取商户】==================【商户信息】 merchant: {}", JSON.toJSONString(merchant));
        return merchant;
    }

    /**
     * 根据产品编码获取产品
     *
     * @param productCode 产品编码
     * @return 产品
     */
    @Override
    public Product getProductByCode(String productCode) {
        Product product = null;
        try {
            product = JSON.parseObject(redisService.get(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_") + productCode), Product.class);
            if (product == null) {
                product = productMapper.selectByProductCode(productCode);
                if (product == null) {
                    log.info("==================【根据产品编码获取产品】==================【产品对象不存在】 productCode: {}", productCode);
                    return null;
                }
                redisService.set(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_") + productCode, JSON.toJSONString(product));
            }
        } catch (Exception e) {
            log.info("==================【根据产品编码获取产品】==================【获取异常】", e);
        }
        log.info("==================【根据产品编码获取产品】==================【商户信息】 product: {}", JSON.toJSONString(product));
        return product;
    }

    /**
     * 根据通道code从redis获取通道信息
     *
     * @param channelCode 通道code
     */
    @Override
    public Channel getChannelByChannelCode(String channelCode) {
        //从redis获取
        Channel channel = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode)), Channel.class);
        if (channel == null) {
            //redis为空从数据库获取
            channel = channelMapper.selectByChannelCode(channelCode);
            if (channel == null) {
                log.info("-----------------通道信息不存在 ----------------- channelCode:{}", channelCode);
                //通道信息不存在
                throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode), JSON.toJSONString(channel));
        }
        if (!channel.getEnabled()) {
            log.info("-----------------通道禁用 ----------------- channelCode:{}", channelCode);
            throw new BusinessException(EResultEnum.CHANNEL_STATUS_ABNORMAL.getCode());
        }
        log.info("================== CommonService getChannelByChannelCode =================== channel: {}", JSON.toJSONString(channel));
        return channel;
    }
}

package com.asianwallets.trade.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 从redis里通用获取数据接口
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
    private MerchantProductMapper merchantProductMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private MerchantChannelMapper merchantChannelMapper;

    @Autowired
    private ChannelBankMapper channelBankMapper;

    @Autowired
    private AccountMapper accountMapper;

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
            currency = JSON.parseObject(redisService.get(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(code)), Currency.class);
            if (currency == null) {
                currency = currencyMapper.selectByCurrency(code);
                if (currency == null) {
                    log.info("==================【根据币种编码获取币种】==================【币种不存在】 code: {}", code);
                    return null;
                }
                redisService.set(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(code), JSON.toJSONString(currency));
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
    public Product getProductByCode(Integer productCode) {
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
     * 根据商户ID与产品ID查询商户产品
     *
     * @param merchantId 商户ID
     * @param productId  产品ID
     * @return 商户产品
     */
    @Override
    public MerchantProduct getMerProByMerIdAndProId(String merchantId, String productId) {
        MerchantProduct merchantProduct = null;
        try {
            merchantProduct = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANTPRODUCT_CACHE_KEY.concat("_").concat(merchantId.concat("_").concat(productId))), MerchantProduct.class);
            if (merchantProduct == null) {
                merchantProduct = merchantProductMapper.selectByMerchantIdAndProductId(merchantId, productId);
                if (merchantProduct == null) {
                    log.info("==================【根据商户ID与产品ID查询商户产品】==================【商户产品对象不存在】 merchantId: {} | productId: {}", merchantId, productId);
                    return null;
                }
                redisService.set(AsianWalletConstant.MERCHANTPRODUCT_CACHE_KEY.concat("_").concat(merchantProduct.getMerchantId().concat("_").concat(merchantProduct.getProductId())), JSON.toJSONString(merchantProduct));
            }
        } catch (Exception e) {
            log.info("==================【根据商户ID与产品ID查询商户产品】==================【获取异常】", e);
        }
        log.info("==================【根据商户ID与产品ID查询商户产品】==================【商户产品信息】 merchantProduct: {}", JSON.toJSONString(merchantProduct));
        return merchantProduct;
    }

    /**
     * 根据通道ID查询通道信息
     *
     * @param channelId 通道ID
     */
    @Override
    public Channel getChannelById(String channelId) {
        Channel channel = null;
        try {
            channel = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channelId)), Channel.class);
            if (channel == null) {
                channel = channelMapper.selectByPrimaryKey(channelId);
                if (channel == null) {
                    log.info("==================【根据通道ID查询通道信息】==================【通道对象不存在】 channelId: {}", channelId);
                    return null;
                }
                redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
                redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channel.getChannelCode()), JSON.toJSONString(channel));
            }
        } catch (Exception e) {
            log.info("==================【根据通道ID查询通道信息】==================【获取异常】", e);
        }
        log.info("==================【根据通道ID查询通道信息】==================【通道信息】 channel: {}", JSON.toJSONString(channel));
        return channel;
    }

    /**
     * 根据通道编号获取通道信息
     *
     * @param channelCode 通道code
     */
    @Override
    public Channel getChannelByChannelCode(String channelCode) {
        Channel channel = null;
        try {
            channel = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode)), Channel.class);
            if (channel == null || !channel.getEnabled()) {
                channel = channelMapper.selectByChannelCode(channelCode);
                if (channel == null) {
                    log.info("==================【根据通道编号获取通道信息】==================【通道信息不存在】");
                    throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
                }
                redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
                redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode), JSON.toJSONString(channel));
            }
        } catch (Exception e) {
            log.info("==================【根据通道编号获取通道信息】==================【获取异常】", e);
            throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
        }
        log.info("==================【根据通道编号获取通道信息】==================【通道信息】 channel: {}", JSON.toJSONString(channel));
        return channel;
    }

    /**
     * 根据商户产品ID查询通道银行ID集合信息
     *
     * @param merProId 商户产品ID
     * @return 通道银行ID集合
     */
    @Override
    public List<String> getChaBankIdByMerProId(String merProId) {
        List<String> chaBankIdList = null;
        try {
            chaBankIdList = JSON.parseArray(redisService.get(AsianWalletConstant.MERCHANTCHANNEL_CACHE_KEY.concat("_").concat(merProId)), String.class);
            if (ArrayUtil.isEmpty(chaBankIdList)) {
                chaBankIdList = merchantChannelMapper.selectByMerProId(merProId);
                if (ArrayUtil.isEmpty(chaBankIdList)) {
                    log.info("==================【根据商户产品ID查询通道银行ID集合信息】==================【通道对象不存在】 merProId: {}", merProId);
                    return null;
                }
                redisService.set(AsianWalletConstant.MERCHANTCHANNEL_CACHE_KEY.concat("_").concat(merProId), JSON.toJSONString(chaBankIdList));
            }
        } catch (Exception e) {
            log.info("==================【根据商户产品ID查询通道银行ID集合信息】==================【获取异常】", e);
        }
        log.info("==================【根据商户产品ID查询通道银行ID集合信息】==================【通道银行ID集合信息】 chaBankIdList: {}", JSON.toJSONString(chaBankIdList));
        return chaBankIdList;
    }

    /**
     * 根据通道银行ID查询通道银行
     *
     * @param chaBankId 通道银行ID
     * @return 通道银行
     */
    @Override
    public ChannelBank getChaBankById(String chaBankId) {
        ChannelBank channelBank = null;
        try {
            channelBank = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(chaBankId)), ChannelBank.class);
            if (channelBank == null) {
                channelBank = channelBankMapper.selectByPrimaryKey(chaBankId);
                if (channelBank == null) {
                    log.info("==================【根据通道银行ID查询通道银行】==================【通道银行对象不存在】 chaBankId: {}", chaBankId);
                    return null;
                }
                redisService.set(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(channelBank.getId()), JSON.toJSONString(channelBank));
            }
        } catch (Exception e) {
            log.info("==================【根据通道银行ID查询通道银行】==================【获取异常】", e);
        }
        log.info("==================【根据通道银行ID查询通道银行】==================【通道银行信息】 channelBank: {}", JSON.toJSONString(channelBank));
        return channelBank;
    }

    /**
     * 根据商户编号和币种查询账户
     *
     * @param merchantId 商户号
     * @param currency   币种
     * @return 账户
     */
    @Override
    public Account getAccountByMerchantIdAndCurrency(String merchantId, String currency) {
        Account account = null;
        try {
            account = JSON.parseObject(redisService.get(AsianWalletConstant.ACCOUNT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(currency)), Account.class);
            if (account == null) {
                account = accountMapper.getAccount(merchantId, currency);
                if (account == null) {
                    log.info("==================【根据商户编号和币种查询账户】==================【账户对象不存在】 merchantId: {} | currency: {}", merchantId, currency);
                    return null;
                }
                redisService.set(AsianWalletConstant.ACCOUNT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(currency), JSON.toJSONString(account));
            }
        } catch (Exception e) {
            log.info("==================【根据商户编号和币种查询账户】==================【获取异常】", e);
        }
        log.info("==================【根据商户编号和币种查询账户】==================【账户信息】 account: {}", JSON.toJSONString(account));
        return account;
    }
}

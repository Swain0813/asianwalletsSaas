package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ThDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.trade.channels.th.impl.ThServiceImpl;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private PayTypeMapper payTypeMapper;

    @Autowired
    private MerchantReportMapper merchantReportMapper;

    @Autowired
    private ThServiceImpl thService;

    @Autowired
    private MerchantCardCodeMapper merchantCardCodeMapper;

    /**
     * 根据币种编码获取币种信息
     *
     * @param code
     * @return
     */
    @Override
    public Currency getCurrencyByCode(String code) {
        Currency currency = JSON.parseObject(redisService.get(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(code)), Currency.class);
        if (currency == null) {
            currency = currencyMapper.selectByCurrency(code);
            if (currency == null) {
                log.info("==================【根据币种编码获取币种信息】==================【币种信息不存在】 code: {}", code);
                //当前币种不支持
                throw new BusinessException(EResultEnum.PRODUCT_CURRENCY_NO_SUPPORT.getCode());
            }
            redisService.set(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(code), JSON.toJSONString(currency));
        }
        log.info("==================【根据币种编码获取币种信息】==================【币种信息】 currency: {}", JSON.toJSONString(currency));
        return currency;
    }

    /**
     * 根据商户编号获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 密钥
     */
    @Override
    public Attestation getAttestationByMerchantId(String merchantId) {
        Attestation attestation = JSON.parseObject(redisService.get(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat(merchantId)), Attestation.class);
        if (attestation == null) {
            attestation = attestationMapper.selectByMerchantId(merchantId);
            if (attestation == null) {
                log.info("==================【根据商户ID获取密钥对象】==================【密钥对象不存在】 merchantId: {}", merchantId);
                //密钥不存在
                throw new BusinessException(EResultEnum.SECRET_IS_NOT_EXIST.getCode());
            }
            redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(merchantId), JSON.toJSONString(attestation));
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
        ExchangeRate exchangeRate = JSON.parseObject(redisService.get(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(localCurrency).concat("_").concat(foreignCurrency)), ExchangeRate.class);
        try {
            if (exchangeRate == null || !exchangeRate.getEnabled()) {
                exchangeRate = exchangeRateMapper.selectByLocalCurrencyAndForeignCurrency(localCurrency, foreignCurrency);
                if (exchangeRate == null) {
                    log.info("==================【根据本币与外币获取汇率对象】==================【汇率对象不存在】 localCurrency: {} | foreignCurrency: {}", localCurrency, foreignCurrency);
                    //外面调用此方法的自己在外面判断
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
        Institution institution = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institutionId)), Institution.class);
        if (institution == null) {
            institution = institutionMapper.selectByPrimaryKey(institutionId);
            if (institution == null) {
                log.info("==================【根据机构ID获取机构】==================【机构对象不存在】 institutionId: {}", institutionId);
                //机构信息不存在
                throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
            }
            if (!institution.getEnabled()) {
                log.info("-----------------【根据机构ID获取机构】--------------【机构已被禁用】");
                //机构已禁用
                throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
            }
            redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
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
        InstitutionRequestParameters institutionRequestParameters = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionId).concat("_") + tradeDirection), InstitutionRequestParameters.class);
        if (institutionRequestParameters == null) {
            institutionRequestParameters = institutionRequestParametersMapper.selectByInstitutionIdAndTradeDirection(institutionId, tradeDirection);
            if (institutionRequestParameters == null) {
                log.info("==================【根据机构ID与交易方向查询获取机构请求参数】==================【机构请求参数对象不存在】 institutionId: {} | tradeDirection: {}", institutionId, tradeDirection);
                //机构请求参数信息不存在
                throw new BusinessException(EResultEnum.INSTITUTION_REQUST_PARA_IS_NOT_EXIST.getCode());
            }
            redisService.set(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionRequestParameters.getInstitutionCode()).concat("_") + institutionRequestParameters.getTradeDirection(),
                    JSON.toJSONString(institutionRequestParameters));
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
        Merchant merchant = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchantId)), Merchant.class);
        if (merchant == null) {
            merchant = merchantMapper.selectByPrimaryKey(merchantId);
            if (merchant == null) {
                log.info("==================【根据商户ID获取商户】==================【商户对象不存在】 merchantId: {}", merchantId);
                //商户不存在
                throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
            }
            if (!merchant.getEnabled()) {
                log.info("===========【根据商户ID获取商户】==========【商户已禁用】");
                //商户被禁用
                throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
            }
            redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
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
        Product product = JSON.parseObject(redisService.get(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_") + productCode), Product.class);
        if (product == null) {
            product = productMapper.selectByProductCode(productCode);
            if (product == null) {
                log.info("==================【根据产品编码获取产品】==================【产品对象不存在】 productCode: {}", productCode);
                //产品信息不存在
                throw new BusinessException(EResultEnum.PRODUCT_DOES_NOT_EXIST.getCode());
            }
            if (!product.getEnabled()) {
                log.info("==================【根据产品编码获取产品】==================【产品信息已禁用】");
                //获取产品信息异常
                throw new BusinessException(EResultEnum.GET_PRODUCT_INFO_ERROR.getCode());
            }
            redisService.set(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_") + productCode, JSON.toJSONString(product));
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
        MerchantProduct merchantProduct = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANTPRODUCT_CACHE_KEY.concat("_").concat(merchantId.concat("_").concat(productId))), MerchantProduct.class);
        if (merchantProduct == null) {
            merchantProduct = merchantProductMapper.selectByMerchantIdAndProductId(merchantId, productId);
            if (merchantProduct == null) {
                log.info("==================【根据商户ID与产品ID查询商户产品】==================【商户产品对象不存在】 merchantId: {} | productId: {}", merchantId, productId);
                throw new BusinessException(EResultEnum.MERCHANT_PRODUCT_DOES_NOT_EXIST.getCode());
            }
            if (!merchantProduct.getEnabled()) {
                log.info("==================【根据商户ID与产品ID查询商户产品】==================【商户产品信息已禁用】");
                throw new BusinessException(EResultEnum.MERCHANT_PRODUCT_IS_DISABLED.getCode());
            }
            redisService.set(AsianWalletConstant.MERCHANTPRODUCT_CACHE_KEY.concat("_").concat(merchantProduct.getMerchantId().concat("_").concat(merchantProduct.getProductId())), JSON.toJSONString(merchantProduct));
        }
        log.info("==================【根据商户ID与产品ID查询商户产品】==================【商户产品信息】 merchantProduct: {}", JSON.toJSONString(merchantProduct));
        return merchantProduct;
    }

    /**
     * 根据通道ID查询通道信息【此方法未查询到会返回NULL】
     *
     * @param channelId 通道ID
     */
    @Override
    public Channel getChannelById(String channelId) {
        Channel channel = null;
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
        Channel channel = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode)), Channel.class);
        if (channel == null || !channel.getEnabled()) {
            channel = channelMapper.selectByChannelCode(channelCode);
            if (channel == null) {
                log.info("==================【根据通道编号获取通道信息】==================【通道信息不存在】");
                throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
            }
            if (!channel.getEnabled()) {
                log.info("==================【根据通道编号获取通道信息】==================【通道信息已禁用】");
                throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
            }
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode), JSON.toJSONString(channel));
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
        List<String> chaBankIdList = JSON.parseArray(redisService.get(AsianWalletConstant.MERCHANTCHANNEL_CACHE_KEY.concat("_").concat(merProId)), String.class);
        if (ArrayUtil.isEmpty(chaBankIdList)) {
            chaBankIdList = merchantChannelMapper.selectByMerProId(merProId);
            if (ArrayUtil.isEmpty(chaBankIdList)) {
                log.info("==================【根据商户产品ID查询通道银行ID集合信息】==================【通道对象不存在】 merProId: {}", merProId);
                throw new BusinessException(EResultEnum.CHANNEL_BANK_DOES_NOT_EXIST.getCode());
            }
            redisService.set(AsianWalletConstant.MERCHANTCHANNEL_CACHE_KEY.concat("_").concat(merProId), JSON.toJSONString(chaBankIdList));
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
        ChannelBank channelBank = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(chaBankId)), ChannelBank.class);
        if (channelBank == null) {
            channelBank = channelBankMapper.selectByPrimaryKey(chaBankId);
            if (channelBank == null) {
                log.info("==================【根据通道银行ID查询通道银行】==================【通道银行对象不存在】 chaBankId: {}", chaBankId);
                throw new BusinessException(EResultEnum.CHANNEL_BANK_DOES_NOT_EXIST.getCode());
            }
            redisService.set(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(channelBank.getId()), JSON.toJSONString(channelBank));
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
        Account account = JSON.parseObject(redisService.get(AsianWalletConstant.ACCOUNT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(currency)), Account.class);
        if (account == null) {
            account = accountMapper.getAccount(merchantId, currency);
            if (account == null) {
                log.info("==================【根据商户编号和币种查询账户】==================【账户对象不存在】 merchantId: {} | currency: {}", merchantId, currency);
                return null;
            }
            redisService.set(AsianWalletConstant.ACCOUNT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(currency), JSON.toJSONString(account));
        }
        log.info("==================【根据商户编号和币种查询账户】==================【账户信息】 account: {}", JSON.toJSONString(account));
        return account;
    }

    /**
     * 获取支付方式.
     *
     * @param extend1  extend1 id 查询
     * @param language 语言
     * @return the payType by extend-1 and language
     */
    @Override
    public PayType getPayTypeByExtend1AndLanguage(String extend1, String language) {
        PayType payType = JSON.parseObject(redisService.get(AsianWalletConstant.PAY_TYPE_CACHE_KEY.concat("_").concat(extend1).concat("_").concat(language)), PayType.class);
        if (payType == null) {
            payType = payTypeMapper.selectByExtend1AndLanguage(extend1, language);
            if (payType == null) {
                log.info("==================【根据extend1编号和语言查询支付方式】==================【payType不存在】 merchantId: {} | currency: {}", extend1, language);
                return null;
            }
            redisService.set(AsianWalletConstant.PAY_TYPE_CACHE_KEY.concat("_").concat(extend1).concat("_").concat(language), JSON.toJSONString(payType));
        }
        log.info("==================【根据extend1编号和语言查询支付方式】==================【支付方式】 payType: {}", JSON.toJSONString(payType));
        return payType;
    }


    /**
     * 根据商户编号以及通道编号获取商户报备信息
     *
     * @param merchantId
     * @param channelCode
     * @return
     */
    @Override
    public MerchantReport getMerchantReport(String merchantId, String channelCode) {
        MerchantReport merchantReport = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_REPORT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(channelCode)), MerchantReport.class);
        if (merchantReport == null) {
            merchantReport = merchantReportMapper.selectByChannelCodeAndMerchantId(merchantId, channelCode);
            if (merchantReport == null) {
                log.info("==================【根据商户编号和通道编号获取商户报备信息】==================【商户报备信息不存在】 merchantId: {} | channelCode: {}", merchantId, channelCode);
                return null;
            }
            redisService.set(AsianWalletConstant.MERCHANT_REPORT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(channelCode), JSON.toJSONString(merchantReport));
        }
        log.info("==================【根据商户编号和通道编号获取商户报备信息】==================【商户报备信息】 account: {}", JSON.toJSONString(merchantReport));

        return merchantReport;
    }

    /**
     * 通华签到获取62域
     *
     * @param terminalId 设备号
     * @param merchantId 报备里的二级商户号 2020年6月22日产品确认
     * @param channel
     * @return
     */
    @Override
    public String getThKey(String terminalId, String merchantId, Channel channel) {
        String institutionId = channel.getChannelMerchantId();
        String channelCode = channel.getChannelCode();
        log.info("++++++++++++++++++++++商户获取62域缓存信息开始++++++++++++++++++++++");
        String key = redisService.get(AsianWalletConstant.Th_SIGN_CACHE_KEY.
                concat("_").concat(institutionId).concat("_").concat(merchantId).concat("_").concat(terminalId));
        if (StringUtils.isEmpty(key)) {
            log.info("++++++++++++++++++++++商户获取62域缓存信息 缓存不存在 调用通华ThSign签到接口++++++++++++++++++++++");
            String timeStamp = System.currentTimeMillis() + "";
            ISO8583DTO iso8583DTO = new ISO8583DTO();
            iso8583DTO.setMessageType("0800");
            iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(6, 12));
            //机构号
            iso8583DTO.setAcquiringInstitutionIdentificationCode_32(institutionId);
            //终端号
            iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
            //商户号
            iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
            iso8583DTO.setReservedPrivate_60("50" + timeStamp.substring(6, 12) + "003");
            iso8583DTO.setReservedPrivate_63("001");
            ThDTO thDTO = new ThDTO();
            thDTO.setIso8583DTO(iso8583DTO);
            thDTO.setChannel(channel);
            BaseResponse baseResponse = thService.thSign(thDTO);
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), ISO8583DTO.class);
            key = iso8583VO.getReservedPrivate_62();
            log.info("++++++++++++++++++++++商户获取62域缓存信息++++++++++++++++++++++ iso8583VO:{}", JSON.toJSONString(iso8583VO));
            redisService.set(AsianWalletConstant.Th_SIGN_CACHE_KEY.
                    concat("_").concat(institutionId).concat("_").concat(merchantId).concat("_").concat(terminalId), key);

        }
        log.info("++++++++++++++++++++++商户获取62域缓存信息完成++++++++++++++++++++++");
        return key;
    }

    /**
     * 获取商户码牌信息
     *
     * @param id
     * @return
     */
    @Override
    public MerchantCardCode getMerchantCardCode(String id) {
        MerchantCardCode merchantCardCode = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CARD_CODE.concat("_").concat(id)), MerchantCardCode.class);
        if (merchantCardCode == null) {
            merchantCardCode = merchantCardCodeMapper.selectById(id);
            if (merchantCardCode == null) {
                log.info("================交易服务==【根据码牌id查询商户码牌信息】==================【码牌信息对象不存在】 id: {}", id);
                throw new BusinessException(EResultEnum.CODE_CARD_INFORMATION_DOES_NOT_EXIST.getCode());
            }
            redisService.set(AsianWalletConstant.MERCHANT_CARD_CODE.concat("_").concat(merchantCardCode.getId()), JSON.toJSONString(merchantCardCode));
        }
        return merchantCardCode;
    }
}

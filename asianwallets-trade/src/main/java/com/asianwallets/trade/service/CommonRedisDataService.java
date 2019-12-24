package com.asianwallets.trade.service;

import com.asianwallets.common.entity.*;

import java.util.List;

/**
 * 通用获取数据接口
 */
public interface CommonRedisDataService {

    /**
     * 根据币种编码获取币种
     *
     * @param code 币种编码
     * @return 币种
     */
    Currency getCurrencyByCode(String code);

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 密钥
     */
    Attestation getAttestationByMerchantId(String merchantId);

    /**
     * 根据本币与外币获取汇率
     *
     * @param localCurrency   本币
     * @param foreignCurrency 外币
     * @return 汇率
     */
    ExchangeRate getExchangeRateByCurrency(String localCurrency, String foreignCurrency);

    /**
     * 根据机构ID获取机构
     *
     * @param institutionId 机构ID
     * @return 机构
     */
    Institution getInstitutionById(String institutionId);

    /**
     * 根据机构ID与交易方向查询获取机构请求参数
     *
     * @param institutionId  机构ID
     * @param tradeDirection 交易方向
     * @return 机构
     */
    InstitutionRequestParameters getInstitutionRequestByIdAndDirection(String institutionId, Byte tradeDirection);

    /**
     * 根据商户ID获取商户
     *
     * @param merchantId 商户ID
     * @return 商户
     */
    Merchant getMerchantById(String merchantId);

    /**
     * 根据产品编码获取产品
     *
     * @param productCode 产品编码
     * @return 产品
     */
    Product getProductByCode(Integer productCode);

    /**
     * 根据商户ID与产品ID查询商户产品
     *
     * @param merchantId 商户ID
     * @param productId  产品ID
     * @return 商户产品
     */
    MerchantProduct getMerProByMerIdAndProId(String merchantId, String productId);

    /**
     * 根据通道ID查询通道信息
     *
     * @param channelId 通道ID
     * @return 通道
     */
    Channel getChannelById(String channelId);

    /**
     * 根据通道code从redis获取通道信息
     *
     * @param channelCode 通道code
     */
    Channel getChannelByChannelCode(String channelCode);

    /**
     * 根据商户产品ID查询通道银行ID集合信息
     *
     * @param merProId 商户产品ID
     * @return 通道银行ID集合
     */
    List<String> getChaBankIdByMerProId(String merProId);

    /**
     * 根据通道银行ID查询通道银行
     *
     * @param chaBankId 通道银行ID
     * @return 通道银行
     */
    ChannelBank getChaBankById(String chaBankId);

    /**
     * 根据商户编号和币种查询账户
     *
     * @param merchantId 商户号
     * @param currency   币种
     * @return 账户
     */
    Account getAccountByMerchantIdAndCurrency(String merchantId, String currency);
}

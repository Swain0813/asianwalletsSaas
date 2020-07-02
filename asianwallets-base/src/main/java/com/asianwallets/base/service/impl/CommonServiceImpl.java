package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.InstitutionMapper;
import com.asianwallets.base.dao.MerchantCardCodeMapper;
import com.asianwallets.base.dao.MerchantMapper;
import com.asianwallets.base.service.CommonService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantCardCode;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 共同模块的实现类
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private MerchantCardCodeMapper merchantCardCodeMapper;

    /**
     * 根据机构code获取机构名称
     *
     * @param institutionCode
     * @return
     */
    @Override
    public Institution getInstitutionInfo(String institutionCode) {
        //查询机构信息,先从redis获取
        Institution institution = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institutionCode)), Institution.class);
        if (institution == null) {
            //redis不存在,从数据库获取
            institution = institutionMapper.selectByCode(institutionCode);
            if (institution == null) {
                //机构信息不存在
                throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
            }
            if(!institution.getEnabled()){
                log.info("***********基础服务************机构已被禁用**************************");
                throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
        }
        return institution;
    }

    /**
     * 根据商户编号获取商户信息
     * @param merchantId
     * @return
     */
    @Override
    public Merchant getMerchant(String merchantId) {
        //获取商户信息
        Merchant merchant = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchantId)), Merchant.class);
        if (merchant == null) {
            //redis不存在,从数据库获取
            merchant = merchantMapper.getMerchant(merchantId);
            if (merchant == null) {
                //商户信息不存在
                throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
            }
            if(!merchant.getEnabled()){
               log.info("******************基础服务******商户已被禁用*************************");
                throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
        }
        return merchant;
    }


    /**
     * 缓存获取通道信息
     * @param channelId
     * @return
     */
    public Channel getChannelById(String channelId) {
        Channel channel = null;
        channel = JSON.parseObject(redisService.get(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channelId)), Channel.class);
        if (channel == null) {
            channel = channelMapper.selectByPrimaryKey(channelId);
            if (channel == null) {
                log.info("==================基础服务【根据通道ID查询通道信息】==================【通道对象不存在】 channelId: {}", channelId);
                return null;
            }
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channel.getChannelCode()), JSON.toJSONString(channel));
        }
        log.info("==================基础服务【根据通道ID查询通道信息】==================【通道信息】 channel: {}", JSON.toJSONString(channel));
        return channel;
    }

    /**
     * 根据静态码编号获取静态码信息
     * @param id
     * @return
     */
    @Override
    public MerchantCardCode getMerchantCardCode(String id) {
        //查询静态码信息,先从redis获取
        MerchantCardCode merchantCardCode = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CARD_CODE.concat("_").concat(id)), MerchantCardCode.class);
        if (merchantCardCode == null) {
            //redis不存在,从数据库获取
            merchantCardCode = merchantCardCodeMapper.getMerchantCardCode(id);
            if (merchantCardCode == null) {
                //静态码信息不存在
                throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
            }
            if(!merchantCardCode.getEnabled()){
                log.info("****************基础服务*******静态码信息已被禁用**************************");
                throw new BusinessException(EResultEnum.MERCHANT_CARD_CODE_IS_ENABLE.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.MERCHANT_CARD_CODE.concat("_").concat(id), JSON.toJSONString(merchantCardCode));
        }
        return merchantCardCode;
    }
}

package com.asianwallets.permissions.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.dao.ChannelMapper;
import com.asianwallets.permissions.dao.InstitutionMapper;
import com.asianwallets.permissions.dao.MerchantMapper;
import com.asianwallets.permissions.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限服务共同模块的实现类
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private ChannelMapper channelMapper;

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
                log.info("**************————————*********机构已被禁用**************************");
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
               log.info("**************………………**********商户已被禁用*************************");
                throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
        }
        return merchant;
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
                log.info("===========*****=======【根据通道编号获取通道信息】=======***===========【通道信息不存在】");
                throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
            }
            if (!channel.getEnabled()) {
                log.info("======***============【根据通道编号获取通道信息】====**==============【通道信息已禁用】");
                throw new BusinessException(EResultEnum.GET_CHANNEL_INFO_ERROR.getCode());
            }
            redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channelCode), JSON.toJSONString(channel));
        }
        log.info("==================【根据通道编号获取通道信息】==================【通道信息】 channel: {}", JSON.toJSONString(channel));
        return channel;
    }
}

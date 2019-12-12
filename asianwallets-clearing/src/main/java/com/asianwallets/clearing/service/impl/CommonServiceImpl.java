package com.asianwallets.clearing.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.clearing.dao.MerchantMapper;
import com.asianwallets.clearing.service.CommonService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 共通方法的实现
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisService redisService;


    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 获得机构信息从redis里获取
     * @param merchantId
     * @return
     */
    @Override
    public Merchant getMerchantInfo(String merchantId) {
        //查询机构信息,先从redis获取
        Merchant merchant = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchantId)), Merchant.class);
        if (merchant == null) {
            //redis不存在,从数据库获取
            merchant = merchantMapper.selectByMerchantId(merchantId);
            if (merchant == null || !merchant.getEnabled()) {
                log.info("-----------------清结算服务商户信息不存在 -----------------  merchantId :{}", merchantId);
                //商户信息不存在
                throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
        }
        return merchant;
    }
}

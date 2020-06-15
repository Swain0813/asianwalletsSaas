package com.asianwallets.rights.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.rights.dao.AttestationMapper;
import com.asianwallets.rights.dao.InstitutionMapper;
import com.asianwallets.rights.dao.MerchantMapper;
import com.asianwallets.rights.service.CommonRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 共通从redis获取基础数据的实现类
 */
@Service
@Slf4j
public class CommonRedisServiceImpl implements CommonRedisService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private AttestationMapper attestationMapper;

    @Autowired
    private MerchantMapper merchantMapper;


    /**
     * 根据机构编号获取机构信息
     *
     * @param institutionCode
     * @return
     */
    @Override
    public Institution getInstitutionInfo(String institutionCode) {
        //先从redis获取
        Institution institution = JSON.parseObject(redisService.get(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institutionCode)), Institution.class);
        if (institution == null) {
            //redis不存在,从数据库获取
            institution = institutionMapper.selectByInstitutionCode(institutionCode);
            if (institution == null) {
                log.info("-----------------权益系统根据机构编号获取机构信息 -----------------  institutionCode:{}", institutionCode);
                //机构信息不存在
                throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
            }
            if (!institution.getEnabled()) {
                log.info("-----------------权益系统根据机构编号已禁用 -----------------  institutionCode:{}", institutionCode);
                throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
        }
        log.info("================== CommonRedisService getInstitutionInfo =================== institution: {}", JSON.toJSONString(institution));
        return institution;
    }

    /**
     * 获取机构的公钥
     *
     * @param institutionCode 机构号
     * @return Attestation
     */
    @Override
    public Attestation getAttestationInfo(String institutionCode) {
        Attestation attestation = JSON.parseObject(redisService.get(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(institutionCode)), Attestation.class);
        if (attestation == null) {
            attestation = attestationMapper.selectByInstitutionCode(institutionCode);
            if (attestation == null) {
                /*
                用户未在机构后台上传RSA时，通过机构号查询为空，此时通过平台生成的数据使用 priKey与pubKey置为空
                * */
                attestation = attestationMapper.selectByInstitutionCode("PF_" + institutionCode);
                attestation.setPrikey("");
                attestation.setPubkey("");
            }
            if (attestation == null) {
                log.info("-----------------【权益系统】根据机构编号获取机构秘钥信息 信息不存在 -----------------  institutionCode:{}", institutionCode);
                throw new BusinessException(EResultEnum.SECRET_IS_NOT_EXIST.getCode());
            }
            redisService.get(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(JSON.toJSONString(attestation)));
        }
        return attestation;
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
                log.info("************************商户已被禁用*************************");
                throw new BusinessException(EResultEnum.MERCHANT_IS_DISABLED.getCode());
            }
            //同步redis
            redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
        }
        return merchant;
    }

}
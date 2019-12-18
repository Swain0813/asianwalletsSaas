package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.AttestationMapper;
import com.asianwallets.base.service.AttestationService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.vo.AttestationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shenxinran
 * @Date: 2019/2/15 19:34
 * @Description: 验签
 */
@Slf4j
@Service
@Transactional
public class AttestationServiceImpl extends BaseServiceImpl<Attestation> implements AttestationService {

    @Autowired
    private AttestationMapper attestationMapper;

    @Autowired
    private RedisService redisService;

    /**
     * 生成RSA公私钥
     *
     * @return
     */
    @Override
    public Map getRSA() {
        Map<String, String> map;
        try {
            map = RSAUtils.initKey();
        } catch (Exception e) {
            log.info("---------生成RSA公私钥错误---------");
            throw new BusinessException(EResultEnum.KEY_GENERATION_FAILED.getCode());
        }

        return map;
    }

    /**
     * 分页查询公钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public List<AttestationVO> selectKeyInfo(AttestationDTO attestationDTO) {
        return attestationMapper.selectKeyInfo(attestationDTO);
    }

    /**
     * 更新密钥信息
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public int updateKeyInfo(AttestationDTO attestationDTO) {
        if (StringUtils.isBlank(attestationDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        int num;
        Attestation attestation = attestationMapper.selectByPrimaryKey(attestationDTO.getId());
        if (attestation == null) {
            throw new BusinessException(EResultEnum.SECRET_IS_NOT_EXIST.getCode());
        }
        //设置空值，防止被修改
        attestation.setPrikey(null);
        attestation.setPubkey(null);
        attestation.setMd5key(null);
        //仅可修改商户公钥
        attestation.setMerPubkey(attestationDTO.getMerPubkey());
        attestation.setUpdateTime(new Date());
        num = attestationMapper.updateByPrimaryKeySelective(attestation);
        try {
            //更新密钥信息后添加的redis里
            redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(attestation.getMerchantId()), JSON.toJSONString(attestation));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }

}

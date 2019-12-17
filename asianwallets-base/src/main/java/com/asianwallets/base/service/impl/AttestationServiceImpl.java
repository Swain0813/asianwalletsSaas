package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.vo.AttestationVO;
import com.asianwallets.institution.dao.AttestationMapper;
import com.asianwallets.institution.service.AttestationService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * 添加密钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public int addKey(AttestationDTO attestationDTO) {
        int num;
        //机构编号不能为空
        if (StringUtils.isEmpty(attestationDTO.getInstitutionCode())) {
            throw new BusinessException(EResultEnum.INSTITUTIONCODE_IS_NULL.getCode());
        }
        //公钥不能为空
        if (StringUtils.isEmpty(attestationDTO.getPubkey())) {
            throw new BusinessException(EResultEnum.ATTESTATION_PUBKEY_IS_NULL.getCode());
        }
        //校验是否重复
        if (attestationMapper.selectByInstitutionCode(attestationDTO.getInstitutionCode()) > 0 || attestationMapper.selectByPubKey(attestationDTO.getPubkey()) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Attestation attestation = new Attestation();
        BeanUtils.copyProperties(attestationDTO, attestation);
        attestation.setId(IDS.uuid2());//id
        attestation.setEnabled(true);
        attestation.setType((byte) 0);
        attestation.setCreateTime(new Date());
        num = attestationMapper.insertSelective(attestation);
        try {
            //更新密钥信息后添加的redis里
            redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(attestationDTO.getInstitutionCode()), JSON.toJSONString(attestation));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }

    /**
     * 分页查询公钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public List<AttestationVO> selectKeyInfo(AttestationDTO attestationDTO) {
        List<AttestationVO> attestationVOS = new ArrayList<>();
        if (!StringUtils.isBlank(attestationDTO.getInstitutionCode())) {
            attestationVOS = attestationMapper.selectKeyInfo(attestationDTO);
        }
        if (attestationVOS.size() == 0) {
            attestationVOS.add(attestationMapper.selectPlatformPub("PF_" + attestationDTO.getInstitutionCode()));
        }

        return attestationVOS;
    }


    /**
     * 查询密钥的所有信息 包含平台的私钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public PageInfo<Attestation> pageAllKeyInfo(AttestationDTO attestationDTO) {
        return new PageInfo<>(attestationMapper.pageAllKeyInfo(attestationDTO));
    }

    /**
     * 更新密钥信息
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public int updateKeyInfo(AttestationDTO attestationDTO) {
        int num;
        Attestation attestation = attestationMapper.selectByPrimaryKey(attestationDTO.getId());
        if (attestation == null) {
            throw new BusinessException(EResultEnum.SECRET_IS_NOT_EXIST.getCode());
        }
        BeanUtils.copyProperties(attestationDTO, attestation);
        attestation.setUpdateTime(new Date());
        num = attestationMapper.updateByPrimaryKeySelective(attestation);
        try {
            //更新密钥信息后添加的redis里
            redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(attestationDTO.getInstitutionCode()), JSON.toJSONString(attestation));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }

    /**
     * 启用禁用密钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public int banKeyInfo(AttestationDTO attestationDTO) {
        int num;
        if (StringUtils.isEmpty(attestationDTO.getId())) {
            throw new BusinessException(EResultEnum.ATTESTATION_ID_IS_NULL.getCode());
        }
        if (attestationDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.ENABLE_IS_NULL.getCode());
        }
        Attestation attestation = attestationMapper.selectByPrimaryKey(attestationDTO.getId());
        if (attestation == null) {
            throw new BusinessException(EResultEnum.SECRET_IS_NOT_EXIST.getCode());
        }
        BeanUtils.copyProperties(attestationDTO, attestation);
        attestation.setUpdateTime(new Date());
        num = attestationMapper.updateByPrimaryKeySelective(attestation);
        try {
            //更新密钥信息后添加的redis里
            redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_").concat(attestationDTO.getInstitutionCode()), JSON.toJSONString(attestation));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }


}

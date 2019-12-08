package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.PayTypeMapper;
import com.asianwallets.base.service.PayTypeService;
import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.entity.PayType;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.PayTypeVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.asianwallets.common.utils.ReflexClazzUtils.getNullPropertyNames;

/**
 * 支付方式
 */

@Service
@Transactional
public class PayTypeServiceImpl implements PayTypeService {

    @Autowired
    private PayTypeMapper payTypeMapper;


    /**
     * 新增支付方式
     *
     * @param payTypeDTO
     * @return
     */
    @Override
    public int addPayType(PayTypeDTO payTypeDTO) {
        if (StringUtils.isBlank(payTypeDTO.getName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (payTypeMapper.selectByName(payTypeDTO) != null) {
            throw new BusinessException(EResultEnum.PAYMENTMODE_EXIST.getCode());
        }
        if (StringUtils.isBlank(payTypeDTO.getId())) {
            //第一次新增
            PayType payType = new PayType();
            BeanUtils.copyProperties(payTypeDTO, payType);
            payType.setCreateTime(new Date());
            payType.setEnabled(true);
            payType.setId(IDS.uniqueID().toString());
            //对外ID
            payType.setExtend1(IDS.uniqueID().toString());
            return payTypeMapper.insert(payType);
        } else {
            List<PayType> payTypeList = payTypeMapper.selectByExtend1(payTypeDTO.getId());
            if (payTypeList.size() > 0) {
                if (payTypeList.stream().anyMatch(p -> p.getLanguage().equals(payTypeDTO.getLanguage()))) {
                    throw new BusinessException(EResultEnum.LANGUAGE_EXIST.getCode());
                }
                //新增语言
                PayType payType = new PayType();
                BeanUtils.copyProperties(payTypeDTO, payType);
                payType.setCreateTime(new Date());
                payType.setEnabled(true);
                payType.setId(IDS.uniqueID().toString());
                //对外ID
                payType.setExtend1(payTypeList.get(0).getExtend1());
                return payTypeMapper.insert(payType);
            } else {
                throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
            }
        }

    }

    /**
     * 修改支付方式
     *
     * @param payTypeDTO
     * @return
     */
    @Override
    public int updatePayType(PayTypeDTO payTypeDTO) {
        if (StringUtils.isBlank(payTypeDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        PayType payType = payTypeMapper.selectByExtend1AndLanguage(payTypeDTO.getId(), payTypeDTO.getLanguage());
        if (payType == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        if (payTypeMapper.selectByName(payTypeDTO) != null) {
            throw new BusinessException(EResultEnum.PAYMENTMODE_EXIST.getCode());
        }
        String id = payType.getId();
        BeanUtils.copyProperties(payTypeDTO, payType, getNullPropertyNames(payTypeDTO));
        payType.setUpdateTime(new Date());
        payType.setId(id);
        payType.setExtend1(null);
        return payTypeMapper.updateByPrimaryKeySelective(payType);
    }

    /**
     * 分页查询
     *
     * @param payTypeDTO
     * @return
     */
    @Override
    public PageInfo<PayTypeVO> pagePayType(PayTypeDTO payTypeDTO) {
        return new PageInfo<>(payTypeMapper.pagePayType(payTypeDTO));
    }

    /**
     * 禁用支付方式
     *
     * @param payTypeDTO
     * @return
     */
    @Override
    public int banPayType(PayTypeDTO payTypeDTO) {
        if (StringUtils.isBlank(payTypeDTO.getId()) || payTypeDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        PayType payType = payTypeMapper.selectByExtend1AndLanguage(payTypeDTO.getId(), payTypeDTO.getLanguage());
        if (payType == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        String id = payType.getId();
        payType.setEnabled(payTypeDTO.getEnabled());
        payType.setModifier(payTypeDTO.getModifier());
        payType.setUpdateTime(new Date());
        payType.setId(id);
        return payTypeMapper.updateByPrimaryKeySelective(payType);
    }

    /**
     * 查询所有支付方式
     *
     * @param language
     * @return
     */
    @Override
    public List<PayTypeVO> inquireAllPaytype(String language) {
        return payTypeMapper.inquireAllPaytype(language);
    }
}

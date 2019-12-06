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
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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
        if (!StringUtils.isBlank(payTypeDTO.getCnName()) || !StringUtils.isBlank(payTypeDTO.getEnName())) {
            if (payTypeMapper.selectByCnOrEnName(payTypeDTO) == null) {
                PayType payType = new PayType();
                BeanUtils.copyProperties(payTypeDTO, payType);
                payType.setCreateTime(new Date());
                payType.setEnabled(true);
                payType.setId(IDS.uniqueID().toString());
                return payTypeMapper.insert(payType);
            } else {
                throw new BusinessException(EResultEnum.PAYMENTMODE_EXIST.getCode());
            }
        } else {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
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
        PayType payType = payTypeMapper.selectByPrimaryKey(payTypeDTO.getId());
        if (payType == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        BeanUtils.copyProperties(payTypeDTO, payType, getNullPropertyNames(payTypeDTO));
        payType.setUpdateTime(new Date());
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
        List<PayType> payTypes = payTypeMapper.pagePayType(payTypeDTO);
        List<PayTypeVO> payTypeVOS = new ArrayList<>();
        for (PayType payType : payTypes) {
            PayTypeVO payTypeVO = new PayTypeVO();
            if (payTypeDTO.getLanguage().equals("zh-cn")) {
                BeanUtils.copyProperties(payType, payTypeVO);
                payTypeVO.setName(payType.getCnName());
                payTypeVOS.add(payTypeVO);
            } else {
                BeanUtils.copyProperties(payType, payTypeVO);
                payTypeVO.setName(payType.getEnName());
                payTypeVOS.add(payTypeVO);
            }
        }
        return new PageInfo<>(payTypeVOS);
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
        PayType payType = new PayType();
        payType.setId(payTypeDTO.getId());
        payType.setUpdateTime(new Date());
        payType.setEnabled(payTypeDTO.getEnabled());
        payType.setModifier(payTypeDTO.getModifier());
        return payTypeMapper.updateByPrimaryKeySelective(payType);
    }

    /**
     * 查询所有支付方式
     *
     * @return
     */
    @Override
    public List<PayType> inquireAllPaytype() {
        Example example = new Example(PayType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("enabled", true);
        return payTypeMapper.selectByExample(example);
    }
}

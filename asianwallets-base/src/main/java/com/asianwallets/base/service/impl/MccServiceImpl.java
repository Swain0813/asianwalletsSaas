package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.MccMapper;
import com.asianwallets.base.service.MccService;
import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.MccVO;
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
 * mcc
 */

@Service
@Transactional
public class MccServiceImpl implements MccService {

    @Autowired
    private MccMapper mccMapper;


    /**
     * 新增MCC
     *
     * @param mccDTO
     * @return
     */
    @Override
    public int addMcc(MccDTO mccDTO) {
        if (StringUtils.isBlank(mccDTO.getName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (mccMapper.selectByName(mccDTO) != null) {
            throw new BusinessException(EResultEnum.MCC_EXIST.getCode());
        }
        if (StringUtils.isBlank(mccDTO.getId())) {
            //第一次新增
            Mcc mcc = new Mcc();
            BeanUtils.copyProperties(mccDTO, mcc);
            mcc.setCreateTime(new Date());
            mcc.setEnabled(true);
            mcc.setId(IDS.uniqueID().toString());
            //对外ID
            mcc.setExtend1(IDS.uniqueID().toString());
            return mccMapper.insert(mcc);
        } else {
            List<Mcc> mccList = mccMapper.selectByExtend1(mccDTO.getId());
            if (mccList.size() > 0) {
                if (mccList.stream().anyMatch(p -> p.getLanguage().equals(mccDTO.getLanguage()))) {
                    throw new BusinessException(EResultEnum.LANGUAGE_EXIST.getCode());
                }
                //新增语言
                Mcc mcc = new Mcc();
                BeanUtils.copyProperties(mccDTO, mcc);
                mcc.setCreateTime(new Date());
                mcc.setCode(mccList.get(0).getCode());
                mcc.setEnabled(true);
                mcc.setId(IDS.uniqueID().toString());
                //对外ID
                mcc.setExtend1(mccList.get(0).getExtend1());
                return mccMapper.insert(mcc);
            } else {
                throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
            }

        }

    }

    /**
     * 修改MCC
     *
     * @param mccDTO
     * @return
     */
    @Override
    public int updateMcc(MccDTO mccDTO) {
        if (StringUtils.isBlank(mccDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Mcc mcc = mccMapper.selectByExtend1AndLanguage(mccDTO.getId(), mccDTO.getLanguage());
        if (mcc == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        if (mccMapper.selectByName(mccDTO) != null || mccMapper.selectByCode(mccDTO) != null) {
            throw new BusinessException(EResultEnum.MCC_EXIST.getCode());
        }
        if (!StringUtils.isBlank(mccDTO.getCode()) && !mcc.getCode().equals(mccDTO.getCode())) {
            //修改所有CODE
            mccMapper.updateById(mccDTO);
        }
        String id = mcc.getId();
        BeanUtils.copyProperties(mccDTO, mcc, getNullPropertyNames(mccDTO));
        mcc.setExtend1(null);
        mcc.setCode(null);
        mcc.setUpdateTime(new Date());
        mcc.setId(id);
        return mccMapper.updateByPrimaryKeySelective(mcc);
    }

    /**
     * 分页查询
     *
     * @param mccDTO
     * @return
     */
    @Override
    public PageInfo<MccVO> pageMcc(MccDTO mccDTO) {
        return new PageInfo<>(mccMapper.pageMcc(mccDTO));
    }

    /**
     * 禁用MCC
     *
     * @param mccDTO
     * @return
     */
    @Override
    public int banMcc(MccDTO mccDTO) {
        if (StringUtils.isBlank(mccDTO.getId()) || mccDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Mcc mcc = mccMapper.selectByExtend1AndLanguage(mccDTO.getId(), mccDTO.getLanguage());
        if (mcc == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        String id = mcc.getId();
        mcc.setEnabled(mccDTO.getEnabled());
        mcc.setModifier(mccDTO.getModifier());
        mcc.setUpdateTime(new Date());
        mcc.setId(id);
        return mccMapper.updateByPrimaryKeySelective(mcc);
    }

    /**
     * 查询所有MCC
     *
     * @param language
     * @return
     */
    @Override
    public List<MccVO> inquireAllMcc(String language) {
        return mccMapper.inquireAllMcc(language);
    }

    /**
     * 导入MCC
     *
     * @param list
     * @return
     */
    @Override
    public int importMcc(List<Mcc> list) {
        return mccMapper.insertList(list);
    }

    /**
     * 导出
     *
     * @param mccDto
     * @return
     */
    @Override
    public List<MccVO> exportMcc(MccDTO mccDto) {
        List<MccVO> mccVOS = mccMapper.pageMcc(mccDto);
        for (MccVO mccVO : mccVOS) {
            if (mccVO.getEnabled()) {
                mccVO.setEnabledStr("启用");
            } else {
                mccVO.setEnabledStr("禁用");
            }
        }
        return mccVOS;
    }
}

package com.asianwallets.base.service.impl;

import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.institution.dao.LanguageMapper;
import com.asianwallets.institution.entity.Language;
import com.asianwallets.institution.service.LanguageService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/1/29 14:25
 * @Description: 语种管理业务类
 */
@Service
@Transactional
public class LanguageServiceImpl extends BaseServiceImpl<Language> implements LanguageService {

    @Autowired
    private LanguageMapper languageMapper;

    /**
     * 添加语种
     *
     * @param languageDTO
     * @return
     */
    @Override
    public int addLanguage(LanguageDTO languageDTO) {
        Language language = new Language();
        BeanUtils.copyProperties(languageDTO, language);
        if (languageMapper.findLanguage(language.getLangCode(), language.getLangName()) > 0) {
            throw new BusinessException(EResultEnum.LANGUAGE_EXIST.getCode());
        }
        language.setEnabled(true);
        language.setCreateTime(new Date());
        return languageMapper.insertSelective(language);
    }

    /**
     * 修改语种
     *
     * @param languageDTO
     * @return
     */
    @Override
    public int updateLanguage(LanguageDTO languageDTO) {
        if (languageMapper.findLanguage(languageDTO.getLangCode(), languageDTO.getLangName()) > 0) {
            throw new BusinessException(EResultEnum.LANGUAGE_EXIST.getCode());
        }
        Language language = new Language();
        BeanUtils.copyProperties(languageDTO, language);
        language.setUpdateTime(new Date());
        return languageMapper.updateByPrimaryKeySelective(language);
    }

    /**
     * 分页查询语种信息
     *
     * @param languageDTO
     * @return
     */
    @Override
    public PageInfo<Language> pageFindLanguage(LanguageDTO languageDTO) {
        return new PageInfo<>(languageMapper.pageFindLanguage(languageDTO));
    }

    /**
     * 依据ID 查询语言信息
     *
     * @param id
     * @return
     */
    @Override
    public Language getLanguageInfo(String id) {
        return languageMapper.selectByPrimaryKey(id);
    }

    /**
     * 启用禁用 语种
     *
     * @param modifier
     * @param languageID
     * @param enabled
     * @return
     */
    @Override
    public int banLanguage(String modifier, String languageID, Boolean enabled) {
        Language language = new Language();
        language.setId(languageID);
        language.setUpdateTime(new Date());
        language.setEnabled(enabled);
        language.setModifier(modifier);
        return languageMapper.updateByPrimaryKeySelective(language);
    }
}

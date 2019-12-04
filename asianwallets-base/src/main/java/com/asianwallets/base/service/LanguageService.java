package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.entity.Language;
import com.github.pagehelper.PageInfo;

/**
 * 语种管理
 */
public interface LanguageService extends BaseService<Language> {

    /**
     * 添加语种
     *
     * @param languageDTO
     * @return
     */
    int addLanguage(LanguageDTO languageDTO);

    /**
     * 修改语种
     *
     * @param languageDTO
     * @return
     */
    int updateLanguage(LanguageDTO languageDTO);

    /**
     * 分页查询语种信息
     *
     * @param languageDTO
     * @return
     */
    PageInfo<Language> pageFindLanguage(LanguageDTO languageDTO);

    /**
     * 依据ID 查询语言信息
     *
     * @param id
     * @return
     */
    Language getLanguageInfo(String id);

    /**
     * 启用禁用 语种
     *
     * @param modifier
     * @param languageID
     * @param enabled
     * @return
     */
    int banLanguage(String modifier, String languageID, Boolean enabled);
}

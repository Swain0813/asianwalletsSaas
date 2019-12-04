package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.entity.Language;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageMapper extends BaseMapper<Language> {


    /**
     * 查找语种
     *
     * @param langCode
     * @param langName
     * @return
     */
    int findLanguage(@Param("langCode") String langCode, @Param("langName") String langName);

    /**
     * 分页查询语种
     *
     * @param languageDTO
     * @return
     */
    List<Language> pageFindLanguage(LanguageDTO languageDTO);


}
package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DictionaryInfoAllDTO;
import com.asianwallets.common.dto.DictionaryInfoDTO;
import com.asianwallets.common.entity.Dictionary;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryMapper extends BaseMapper<Dictionary> {


    /**
     * 检查code与name是否重复
     *
     * @param code
     * @param name
     * @return
     */
    int checkValue(@Param("code") String code, @Param("name") String name, @Param("dictypeCode") String dictypeCode, @Param("language") String language);

    /**
     * 分页功能的条件查询
     *
     * @param dictionaryInfoDTO
     * @return
     */
    List<Dictionary> pageDictionaryInfo(DictionaryInfoDTO dictionaryInfoDTO);

    /**
     * 禁用类型为typeId下的所有数据
     *
     * @param modifierName
     * @param typeId
     * @param enabled
     * @return
     */
    int banDictionaryWithType(@Param("modifierName") String modifierName, @Param("typeId") String typeId, @Param("enabled") boolean enabled);


    /**
     * 依据name与language查找对应的code
     *
     * @param type
     * @param language
     * @return
     */
    String getNameAndLanguage(@Param("type") String type, @Param("language") String language);

    /**
     * 根据类型与语种查询最新的code
     *
     * @param dictypeCode
     * @param language
     * @return
     */
    String getLatestCodeBydictypeCodeAndLanguage(@Param("dictypeCode") String dictypeCode, @Param("language") String language);

    /**
     * 检查不同语言的code是否重复
     *
     * @param code
     * @param language
     * @return
     */
    int selectDicCodeAndLanguage(@Param("code") String code, @Param("language") String language);

    /**
     * 依据CODE 查询数据
     *
     * @return
     */
    Dictionary selectByCode(@Param("code") String code);

    /**
     * 查询所有数据字典信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    List<Dictionary> pageDictionaryInfos(DictionaryInfoAllDTO dictionaryInfoDTO);

}
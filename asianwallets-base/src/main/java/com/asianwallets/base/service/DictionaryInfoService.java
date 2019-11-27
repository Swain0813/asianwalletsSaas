package com.asianwallets.base.service;


import com.asianwallets.common.dto.DictionaryInfoAllDTO;
import com.asianwallets.common.dto.DictionaryInfoDTO;
import com.asianwallets.common.entity.Dictionary;
import com.github.pagehelper.PageInfo;

/**
 * @author shenxinran
 * 字典类型与数据管理业务层
 */
public interface DictionaryInfoService {

    /**
     * 添加信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    int addDictionaryInfo(DictionaryInfoDTO dictionaryInfoDTO);

    /**
     * 启用禁用字典类型
     *
     * @param modifierName
     * @param typeId
     * @param enabled
     * @return
     */
    int banDictionaryType(String modifierName, String typeId, Boolean enabled);

    /**
     * 启用禁用字典数据
     *
     * @param modifierName
     * @param id
     * @param enabled
     * @return
     */
    int banDictionary(String modifierName, String id, Boolean enabled);

    /**
     * 更新字典索引类型
     *
     * @param dictionaryInfoDTO@return
     */
    int updateDictionaryType(DictionaryInfoDTO dictionaryInfoDTO);

    /**
     * 更新字典数据
     *
     * @param dictionaryInfoDTO
     * @return
     */
    int updateDictionary(DictionaryInfoDTO dictionaryInfoDTO);

    /**
     * 查询类型信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    PageInfo<Dictionary> pageDicTypeInfo(DictionaryInfoDTO dictionaryInfoDTO);

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    Dictionary getDictionaryInfo(String id);

    /**
     * 新增语言
     *
     * @param dictionaryInfoDTO
     * @return
     */
    int addOtherLanguage(DictionaryInfoDTO dictionaryInfoDTO);


    /**
     * 查询全部数据字典信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    PageInfo<Dictionary> pageDictionaryInfos(DictionaryInfoAllDTO dictionaryInfoDTO);
}

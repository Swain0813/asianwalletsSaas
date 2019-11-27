package com.asianwallets.base.dao;


import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.DictionaryType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryTypeMapper extends BaseMapper<DictionaryType> {


    /**
     * 查找 dicCode是否存在
     *
     * @param dictypeCode
     * @return 对应code的名称
     */
    String findDicTypeCode(@Param("dictypeCode") String dictypeCode);

    /**
     * 通过value查询code
     *
     * @param value
     * @return
     */
    String findDicTypeCodeByValue(@Param("value") String value);

    /**
     * 查询共有多少条
     *
     * @return
     */
    String findCount();

    /**
     * 禁用类型
     *
     * @param modifierName
     * @param typeId
     * @param enabled
     * @return
     */
    int banDictionaryType(@Param("modifierName") String modifierName, @Param("typeId") String typeId, @Param("enabled") boolean enabled);


}
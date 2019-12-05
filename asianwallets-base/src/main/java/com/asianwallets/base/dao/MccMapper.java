package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Mcc;
import org.springframework.stereotype.Repository;

/**
 * MCC数据
 */
@Repository
public interface MccMapper extends BaseMapper<Mcc> {
    int deleteByPrimaryKey(String id);

    int insert(Mcc record);

    int insertSelective(Mcc record);

    Mcc selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Mcc record);

    int updateByPrimaryKey(Mcc record);
}
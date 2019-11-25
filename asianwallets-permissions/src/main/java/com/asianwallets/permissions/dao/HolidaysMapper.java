package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Holidays;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidaysMapper extends BaseMapper<Holidays> {


    /**
     * 查询重复个数
     *
     * @param holidays
     * @return
     */
    int findDuplicatesCount(Holidays holidays);
}
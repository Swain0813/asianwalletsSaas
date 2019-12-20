package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Holidays;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidaysMapper extends BaseMapper<Holidays> {

    /**
     * 根据日期与国家查询节假日信息
     *
     * @param date    日期
     * @param country 国家
     * @return 节假日
     */
    Holidays selectByDateAndCountry(@Param("date") String date, @Param("country") String country);
}
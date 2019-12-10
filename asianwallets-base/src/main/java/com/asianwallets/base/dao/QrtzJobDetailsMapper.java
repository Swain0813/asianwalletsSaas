package com.asianwallets.base.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface QrtzJobDetailsMapper {


    @Select("select count(1) from qrtz_job_details where JOB_GROUP = #{key}")
    int getCountByInsProId(@Param("key") String key);

}

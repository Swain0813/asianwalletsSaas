package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsSysConst;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface TcsSysConstMapper extends BaseMapper<TcsSysConst> {


    @Select("select value from tcs_sys_const where `key` = #{key}")
    String selectUrlByKey(String key);

}

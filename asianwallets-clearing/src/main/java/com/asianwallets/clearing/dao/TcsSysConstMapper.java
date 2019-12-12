package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsSysConst;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface TcsSysConstMapper  extends BaseMapper<TcsSysConst> {


    @Select("select t.value from tcs_sys_const t where t.key = 'CSAPI_MD5key'")
    String getCSAPI_MD5Key();
}
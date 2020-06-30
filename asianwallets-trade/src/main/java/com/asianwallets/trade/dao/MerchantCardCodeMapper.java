package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.MerchantCardCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantCardCodeMapper extends BaseMapper<MerchantCardCode> {


    MerchantCardCode selectById(@Param("id") String id);
}
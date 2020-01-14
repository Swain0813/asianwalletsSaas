package com.asianwallets.base.dao;
import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.entity.SettleControl;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettleControlMapper extends  BaseMapper<SettleControl> {

    /**
     * 根据账户id查询账户设置信息
     * @param accountId
     * @return
     */
    SettleControl selectByAccountId(@Param("accountId") String accountId);

}

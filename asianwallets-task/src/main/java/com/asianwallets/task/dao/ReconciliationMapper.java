package com.asianwallets.task.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Reconciliation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 调账表的数据层
 */
@Repository
public interface ReconciliationMapper extends BaseMapper<Reconciliation> {

    /**
     * 根据id修改调账单状态
     * @param id
     * @param status
     * @param name
     * @param remark1
     * @return
     */
    @Update("update reconciliation set status = #{status} ,remark1 = #{remark1},update_time=NOW(),modifier=#{name} where id = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") int status, @Param("name") String name, @Param("remark1") String remark1);
}

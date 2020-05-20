package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Institution;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 机构表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-22
 */
@Repository
public interface InstitutionMapper extends BaseMapper<Institution> {

    /**
     * 根据机构编号获取机构信息
     * @param code
     * @return
     */
    Institution selectByCode(@Param("code") String code);
}

package com.asianwallets.base.dao;
import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.entity.InstitutionAudit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 机构审核表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface InstitutionAuditMapper extends  BaseMapper<InstitutionAudit> {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询机构审核信息
     **/
    List<InstitutionAudit>  pageFindInstitutionAudit(InstitutionDTO institutionDTO);



    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate getInstitutionInfoAudit
     * @return
     **/
    InstitutionAudit getInstitutionInfoAudit(@Param("id") String id, @Param("language") String language);
}

package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.entity.CheckAccountAudit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckAccountAuditMapper extends BaseMapper<CheckAccountAudit> {

    /**
     * 分页查询对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccountAudit> pageAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO);


    /**
     * 导出对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccountAudit> exportAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO);

}
package com.asianwallets.base.service;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.entity.CheckAccount;
import com.asianwallets.common.entity.CheckAccountAudit;
import com.asianwallets.common.entity.CheckAccountLog;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 对账业务接口
 */
public interface AccountCheckService {

    /**
     * 分页查询对账管理
     * @param searchAccountCheckDTO
     * @return
     */
    PageInfo<CheckAccountLog> pageAccountCheckLog(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 导入通道对账单
     * @param file
     * @return
     */
    Object channelAccountCheck(String username,MultipartFile file);

    /**
     * 分页查询对账管理详情
     * @param searchAccountCheckDTO
     * @return
     */
    PageInfo<CheckAccount> pageAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     *导出对账管理详情
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccount> exportAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 差错处理
     * @param checkAccountId
     * @param remark
     * @return
     */
    int updateCheckAccount(String checkAccountId, String remark);

    /**
     * 分页查询对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    PageInfo<CheckAccountAudit> pageAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 导出对账管理复核详情
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccountAudit> exportAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 差错复核
     *
     * @param
     * @return
     */
    int auditCheckAccount(String checkAccountId,Boolean enable,String remark);
}

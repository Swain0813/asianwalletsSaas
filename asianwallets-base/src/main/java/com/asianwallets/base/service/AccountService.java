package com.asianwallets.base.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.vo.AccountListVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 账户表 服务类
 */
public interface AccountService extends BaseService<Account> {
    /**
     *分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    PageInfo<AccountListVO> pageFindAccount(AccountSearchDTO accountSearchDTO);

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    List<AccountListVO> exportAccountList(AccountSearchDTO accountSearchDTO);
}

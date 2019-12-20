package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.AccountMapper;
import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.vo.AccountListVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 账户表 服务实现类
 */
@Service
@Transactional
public class AccountServiceImpl extends BaseServiceImpl<Account> implements AccountService {


    @Autowired
    private AccountMapper accountMapper;

    /**
     * 分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public PageInfo<AccountListVO> pageFindAccount(AccountSearchDTO accountSearchDTO) {
        //查询账户信息
        List<AccountListVO> accountListVOS = accountMapper.pageFindAccount(accountSearchDTO);
        return new PageInfo<AccountListVO>(accountListVOS);
    }

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<AccountListVO> exportAccountList(AccountSearchDTO accountSearchDTO) {
        return accountMapper.pageFindAccount(accountSearchDTO);
    }

}

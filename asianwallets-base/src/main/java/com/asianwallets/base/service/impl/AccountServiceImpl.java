package com.asianwallets.base.service.impl;

import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 账户表 服务实现类
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Service
@Transactional
public class AccountServiceImpl extends BaseServiceImpl<Account> implements AccountService {

}

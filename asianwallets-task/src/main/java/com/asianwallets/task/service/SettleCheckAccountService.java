package com.asianwallets.task.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.entity.SettleCheckAccount;
import java.util.Date;

/**
 * 结算对账单定时生成
 */
public interface SettleCheckAccountService extends BaseService<SettleCheckAccount> {

    /**
     * 结算对账单定时生成
     *
     * @return
     */
    int settleAccountCheck(Date time);


}

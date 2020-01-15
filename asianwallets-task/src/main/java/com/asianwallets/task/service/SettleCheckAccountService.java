package com.asianwallets.task.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.entity.SettleCheckAccountDetail;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 机构结算单表 服务类
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
public interface SettleCheckAccountService extends BaseService<SettleCheckAccount> {

    /**
     * 机构结算信息对账
     *
     * @return
     */
    int settleAccountCheck(Date time);


}

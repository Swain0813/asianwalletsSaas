package com.asianwallets.base.service;

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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询机构结算对账
     **/
    PageInfo<SettleCheckAccount> pageSettleAccountCheck(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询机构结算对账详情
     **/
    PageInfo<SettleCheckAccountDetail> pageSettleAccountCheckDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 导出机构结算对账
     **/
    Map<String, Object> exportSettleAccountCheck(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO);

}

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
 * 结算对账管理
 */
public interface SettleCheckAccountService extends BaseService<SettleCheckAccount> {

    /**
     * 商户结算信息对账
     *
     * @return
     */
    int settleAccountCheck(Date time);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询商户结算对账
     **/
    PageInfo<SettleCheckAccount> pageSettleAccountCheck(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询商户结算对账详情
     **/
    PageInfo<SettleCheckAccountDetail> pageSettleAccountCheckDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 导出商户结算对账
     **/
    Map<String, Object> exportSettleAccountCheck(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO);

}

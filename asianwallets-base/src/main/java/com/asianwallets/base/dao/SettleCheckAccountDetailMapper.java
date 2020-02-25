package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.entity.SettleCheckAccountDetail;
import com.asianwallets.common.vo.ExportSettleCheckAccountVO;
import com.asianwallets.common.vo.PageSettleCheckAccountDetailVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 机构结算单详细表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Repository
public interface SettleCheckAccountDetailMapper extends  BaseMapper<SettleCheckAccountDetail> {
    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询机构结算对账详情
     **/
    List<PageSettleCheckAccountDetailVO> pageSettleAccountCheckDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 导出机构结算对账详情
     **/
    List<ExportSettleCheckAccountVO> exportSettleAccountCheckDetail(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO);
}

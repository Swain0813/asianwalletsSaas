package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.vo.ClearAccountVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TmMerChTvAcctBalanceMapper extends BaseMapper<TmMerChTvAcctBalance> {

    /**
     * 查询清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<ClearAccountVO> pageClearBalanceLogs(ClearSearchDTO clearSearchDTO);

    /**
     * 导出清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<ClearAccountVO> exportClearBalanceLogs(ClearSearchDTO clearSearchDTO);

    /**
     * 查询账户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalance> pageAccountBalanceLogs(AccountSearchDTO accountSearchDTO);

    /**
     * 导出账户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalance> exportAccountBalanceLogs(AccountSearchDTO accountSearchDTO);

}
package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.AccountSearchExportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.*;
import com.asianwallets.permissions.feign.base.AccountFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 账户信息feign端的实现类
 */
@Component
public class AccountFeignImpl implements AccountFeign {


    /**
     * 分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public BaseResponse pageFindAccount(AccountSearchDTO accountSearchDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    @Override
    public  BaseResponse pageClearLogs(@RequestBody @ApiParam AccountSearchDTO clearSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    @Override
    public List<TmMerChTvAcctBalanceVO> exportClearLogs(@RequestBody @ApiParam AccountSearchExportDTO clearSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询结算户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    @Override
    public BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出结算户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<TmMerChTvAcctBalanceVO> exportSettleLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询冻结余额流水详情
     * @param frozenMarginInfoDTO
     * @return
     */
    @Override
    public BaseResponse pageFrozenLogs(@RequestBody @ApiParam AccountSearchDTO frozenMarginInfoDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出冻结余额流水详情
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<TmMerChTvAcctBalanceVO> exportFrozenLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<MerchantBalanceVO> exportMerchantBalance(OrdersDTO ordersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerchantBalance(OrdersDTO ordersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

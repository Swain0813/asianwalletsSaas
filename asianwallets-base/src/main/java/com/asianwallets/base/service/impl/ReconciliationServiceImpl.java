package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.AccountMapper;
import com.asianwallets.base.dao.ReconciliationMapper;
import com.asianwallets.base.service.ClearingService;
import com.asianwallets.base.service.CommonService;
import com.asianwallets.base.service.ReconciliationService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 调账功能的实现类
 */
@Transactional
@Service
@Slf4j
public class ReconciliationServiceImpl implements ReconciliationService {

    @Autowired
    private CommonService commonService;


    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    /**
     * 分页查询调账单
     * @param reconciliationDTO
     * @return
     */
    @Override
    public PageInfo<Reconciliation> pageReconciliation(ReconciliationDTO reconciliationDTO) {
        List<Reconciliation> reconciliations = reconciliationMapper.pageReconciliation(reconciliationDTO);
        reconciliations.forEach(r -> {
            if (r.getStatus() == TradeConstant.UNFREEZE_SUCCESS || r.getStatus() == TradeConstant.UNFREEZE_WAIT || r.getStatus() == TradeConstant.UNFREEZE_FALID) {
                r.setAmount(r.getAmount().negate());
            }
        });
        return new PageInfo<Reconciliation>(reconciliations);
    }

    /**
     * 分页审核调账单
     * @param reconciliationDTO
     * @return
     */
    @Override
    public PageInfo<Reconciliation> pageReviewReconciliation(ReconciliationDTO reconciliationDTO) {
        List<Reconciliation> reconciliations = reconciliationMapper.pageReviewReconciliation(reconciliationDTO);
        reconciliations.forEach(r -> {
            if (r.getStatus() == TradeConstant.UNFREEZE_SUCCESS || r.getStatus() == TradeConstant.UNFREEZE_WAIT || r.getStatus() == TradeConstant.UNFREEZE_FALID) {
                r.setAmount(r.getAmount().negate());
            }
        });
        return new PageInfo<Reconciliation>(reconciliations);
    }

    /**
     * 资金变动审核
     * @param name
     * @param reconciliationId
     * @param enabled
     * @param remark
     * @return
     */
    @Override
    public String auditReconciliation(String name, String reconciliationId, boolean enabled, String remark) {
        Reconciliation reconciliation = reconciliationMapper.selectByPrimaryKey(reconciliationId);
        if (reconciliation == null) {
            //调账记录不存在
            throw new BusinessException(EResultEnum.TIAOZHANG_RECORD_IS_NOT_EXIST.getCode());
        }

        if (TradeConstant.RECONCILIATION_WAIT != reconciliation.getStatus() && TradeConstant.UNFREEZE_WAIT != reconciliation.getStatus() &&
                TradeConstant.FREEZE_WAIT != reconciliation.getStatus()) {
            //调账状态不合法
            throw new BusinessException(EResultEnum.FUNDS_STATUS_IS_ILLEGAL.getCode());
        }
        Merchant merchant = commonService.getMerchant(reconciliation.getMerchantId());
        //判断币种
        Account account = accountMapper.getAccount(merchant.getId(), reconciliation.getCurrency());
        if (account == null) {
            //当前商户不存在该币种的账户
            throw new BusinessException(EResultEnum.MERCHANT_ACCOUNT_CURRENCY_IS_NOT_EXIST.getCode());
        }
        //机构账户对应的币种已禁用
        if (!account.getEnabled()) {
            //当前商户该币种的账户已禁用
            throw new BusinessException(EResultEnum.MERCHANT_ACCOUNT_CURRENCY_IS_DISABLE.getCode());
        }
        //判断审核状态  资金变动类型 1-调账 2-资金冻结 3-资金解冻
        if (!enabled && reconciliation.getChangeType() == TradeConstant.TRANSFER) {
            //更新调账记录表
            reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_FALID, name, remark);
            return "审核失败成功";
        }
        if (!enabled && reconciliation.getChangeType() == TradeConstant.FUND_FREEZING) {
            //更新调账记录表
            reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.FREEZE_FALID, name, remark);
            return "审核失败成功";
        }
        if (!enabled && reconciliation.getChangeType() == TradeConstant.THAWING_FUNDS) {
            //更新调账记录表
            reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.UNFREEZE_FALID, name, remark);
            return "审核失败成功";
        }
        //冻结
        if (reconciliation.getReconciliationType() == AsianWalletConstant.FREEZE) {
            //判断为冻结时
            if (!reconciliation.getFreezeType().equals(TradeConstant.RESERVATION_FREEZE)) {
                //不为预约时
                //判断余额
                if (reconciliation.getAmount().compareTo(account.getSettleBalance().subtract(account.getFreezeBalance())) == 1) {
                    throw new BusinessException(EResultEnum.BANLANCE_NOT_FOOL.getCode());
                }
            }
            FinancialFreezeDTO ffd = new FinancialFreezeDTO(reconciliation, account);
            BaseResponse response = clearingService.freezingFunds(ffd);
            if (response.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                //冻结成功
                //更新调账记录表
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.FREEZE_SUCCESS, name, remark);
            } else {//请求失败
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.FREEZE_FALID, name, remark);
                return "冻结失败";
            }
            return null;
        }

        //解冻
        if (reconciliation.getReconciliationType() == AsianWalletConstant.UNFREEZE) {
            //判断冻结账户余额
            if (reconciliation.getAmount().compareTo(account.getFreezeBalance()) == 1) {
                throw new BusinessException(EResultEnum.INSUFFICIENT_FROZEN_ACCOUNT_BALANCE.getCode());
            }
            FinancialFreezeDTO ffd = new FinancialFreezeDTO(reconciliation, account);
            BaseResponse response = clearingService.freezingFunds(ffd);
            if (response.getCode().equals(TradeConstant.CLEARING_SUCCESS)){
                //解冻成功
                //更新调账记录表
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.UNFREEZE_SUCCESS, name, remark);
            } else {//请求失败
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.UNFREEZE_FALID, name, remark);
                return "解冻失败";
            }
            return null;
        }

        //调账
        //判断余额
        if (reconciliation.getReconciliationType() == AsianWalletConstant.RECONCILIATION_OUT && reconciliation.getAmount().compareTo(account.getSettleBalance().subtract(account.getFreezeBalance())) == 1) {
            throw new BusinessException(EResultEnum.BANLANCE_NOT_FOOL.getCode());
        }
        FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
        //调用清结算的资金变动接口
        BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
        if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
            //上报清结算成功的场合
            //更新调账记录表
            reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS, name, remark);
        }else {
           //上报清结算失败的场合
            reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_FALID, name, remark);
            return "调账失败";
        }
        return null;
    }


    /**
     * 资金变动操作
     * @param name
     * @param reconOperDTO
     * @return
     */
    @Override
    public String doReconciliation(String name, ReconOperDTO reconOperDTO) {
        //获取商户信息
        Merchant merchant = commonService.getMerchant(reconOperDTO.getMerchantId());
        Account account = accountMapper.getAccount(merchant.getId(), reconOperDTO.getCurrency());
        if (account == null) {
            //商户账户对应的币种不存在
            throw new BusinessException(EResultEnum.MERCHANT_ACCOUNT_CURRENCY_IS_NOT_EXIST.getCode());
        }
        if (!account.getEnabled()) {
            //当前商户该币种的账户已禁用
            throw new BusinessException(EResultEnum.MERCHANT_ACCOUNT_CURRENCY_IS_DISABLE.getCode());
        }
        //入账账户类型不能为空
        if(reconOperDTO.getAccountType()==null ||reconOperDTO.getAccountType()==0){
            //入账账户类型不能为空
            throw new BusinessException(EResultEnum.ACCOUNT_TYPE_IS_NULL.getCode());
        }
        if (reconOperDTO.getChangeType() == TradeConstant.TRANSFER && AsianWalletConstant.RECONCILIATION_IN == reconOperDTO.getType()) {
            //调入
            return doReconciliationIn(name, merchant, reconOperDTO);
        } else if (reconOperDTO.getChangeType() == TradeConstant.TRANSFER && AsianWalletConstant.RECONCILIATION_OUT == reconOperDTO.getType()) {
            //调出
            //判断余额
            if (reconOperDTO.getAmount().compareTo(account.getSettleBalance().subtract(account.getFreezeBalance())) == 1) {
                throw new BusinessException(EResultEnum.BANLANCE_NOT_FOOL.getCode());
            }
            return doReconciliationOut(name, merchant, reconOperDTO);

        } else if (reconOperDTO.getChangeType() == TradeConstant.FUND_FREEZING && AsianWalletConstant.FREEZE == reconOperDTO.getType()) {
            //冻结
            if (!reconOperDTO.getFreezeType().equals(TradeConstant.RESERVATION_FREEZE)) {//不为预约时
                //判断余额
                if (reconOperDTO.getAmount().compareTo(account.getSettleBalance().subtract(account.getFreezeBalance())) == 1) {
                    throw new BusinessException(EResultEnum.BANLANCE_NOT_FOOL.getCode());
                }
            }
            return doFreeze(name, merchant, reconOperDTO, account.getId());
        } else if (reconOperDTO.getChangeType() == TradeConstant.THAWING_FUNDS && AsianWalletConstant.UNFREEZE == reconOperDTO.getType()) {
            //解冻
            //判断冻结户余额
            if (reconOperDTO.getAmount().compareTo(account.getFreezeBalance()) == 1) {
                throw new BusinessException(EResultEnum.BANLANCE_NOT_FOOL.getCode());
            }
            return doUnFreeze(name, merchant, reconOperDTO, account.getId());
        }
        return null;
    }

    /**
     * 调入
     * @param name
     * @param merchant
     * @param reconOperDTO
     * @return
     */
    private String doReconciliationIn(String name, Merchant merchant, ReconOperDTO reconOperDTO) {
        Reconciliation reconciliation = createrOrder(AsianWalletConstant.RECONCILIATION_IN, name, merchant, reconOperDTO, null);
        if (reconciliationMapper.insert(reconciliation) > 0) {
            return "success";
        } else {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     * 调出
     * @param name
     * @param merchant
     * @param reconOperDTO
     * @return
     */
    private String doReconciliationOut(String name, Merchant merchant, ReconOperDTO reconOperDTO) {
        Reconciliation reconciliation = createrOrder(AsianWalletConstant.RECONCILIATION_OUT, name, merchant, reconOperDTO, null);
        if (reconciliationMapper.insert(reconciliation) > 0) {
            return "success";
        } else {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     *解冻
     * @param name
     * @param merchant
     * @param reconOperDTO
     * @param accountId
     * @return
     */
    private String doUnFreeze(String name, Merchant merchant, ReconOperDTO reconOperDTO, String accountId) {
        Reconciliation reconciliation = createrOrder(AsianWalletConstant.UNFREEZE, name, merchant, reconOperDTO, accountId);
        if (reconciliationMapper.insert(reconciliation) > 0) {
            return "success";
        } else {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     *冻结
     * @param name
     * @param merchant
     * @param reconOperDTO
     * @param accountId
     * @return
     */
    private String doFreeze(String name, Merchant merchant, ReconOperDTO reconOperDTO, String accountId) {
        Reconciliation reconciliation = createrOrder(AsianWalletConstant.FREEZE, name, merchant, reconOperDTO, accountId);
        if (reconciliationMapper.insert(reconciliation) > 0) {
            return "success";
        } else {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     * 创建调账表的调账单
     * @param type
     * @param name
     * @param merchant
     * @param reconOperDTO
     * @param accountId
     * @return
     */
    private Reconciliation createrOrder(int type, String name, Merchant merchant, ReconOperDTO reconOperDTO, String accountId) {
        //添加调账单
        Reconciliation reconciliation = new Reconciliation();
        if (StringUtils.isEmpty(reconOperDTO.getId())) {
            reconciliation.setId("T" + IDS.uniqueID());
        } else {
            reconciliation.setId(reconOperDTO.getId());
        }
        //根据商户编号获取机构信息
        Institution institution = commonService.getInstitutionInfo(merchant.getInstitutionId());
        reconciliation.setInstitutionId(institution.getId());
        reconciliation.setInstitutionName(institution.getCnName());
        reconciliation.setCreator(name);
        reconciliation.setMerchantOrderId(reconciliation.getId());
        //当变动类型为冻结或者解冻时
        reconciliation.setAccountId(reconOperDTO.getChangeType() != TradeConstant.TRANSFER ? accountId : null);
        reconciliation.setCreateTime(new Date());
        reconciliation.setAmount(reconOperDTO.getChangeType() == TradeConstant.THAWING_FUNDS ? reconOperDTO.getAmount().negate() : reconOperDTO.getAmount());
        //资金变动类型 1-调账 2-资金冻结 3-资金解冻
        reconciliation.setChangeType(reconOperDTO.getChangeType());
        //冻结类型 1-冻结 2-预约冻结
        reconciliation.setFreezeType(reconOperDTO.getFreezeType());
        //入账类型 1-清算户 2-结算户 3-冻结户 这里只用到结算户和冻结户
        reconciliation.setAccountType(reconOperDTO.getAccountType());
        reconciliation.setAmount(reconOperDTO.getChangeType() == TradeConstant.THAWING_FUNDS ? reconOperDTO.getAmount().negate() : reconOperDTO.getAmount());
        reconciliation.setCurrency(reconOperDTO.getCurrency());
        reconciliation.setMerchantId(merchant.getId());
        reconciliation.setMerchantName(merchant.getCnName());
        reconciliation.setReconciliationType(type);
        if (reconOperDTO.getChangeType().equals(TradeConstant.TRANSFER)) {
            //待调账
            reconciliation.setStatus(TradeConstant.RECONCILIATION_WAIT);
        } else if (reconOperDTO.getChangeType().equals(TradeConstant.FUND_FREEZING)) {
            //待冻结
            reconciliation.setStatus(TradeConstant.FREEZE_WAIT);
        } else if (reconOperDTO.getChangeType().equals(TradeConstant.THAWING_FUNDS)) {
            //待解冻
            reconciliation.setStatus(TradeConstant.UNFREEZE_WAIT);
        }
        //调账原因
        reconciliation.setRemark(reconOperDTO.getRemark());
        return reconciliation;
    }

    /**
     * 查询商户可用余额
     * @param searchAvaBalDTO
     * @return
     */
    @Override
    public String getAvailableBalance(SearchAvaBalDTO searchAvaBalDTO) {
        BigDecimal avaBal = null;
        if (searchAvaBalDTO.getType().equals(TradeConstant.FROZEN_FUND)) { //冻结金查询
            avaBal = reconciliationMapper.selectFreezeBalance(searchAvaBalDTO);
        } else {
            avaBal = reconciliationMapper.selectAvailableBalance(searchAvaBalDTO);//可用余额查询
        }
        if (StringUtils.isEmpty(avaBal)) {
            throw new BusinessException(EResultEnum.ACCOUNT_IS_NOT_EXIST.getCode());//账户异常
        }
        return avaBal.toString();
    }
}

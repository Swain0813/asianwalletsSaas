package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.AccountMapper;
import com.asianwallets.base.dao.BankCardMapper;
import com.asianwallets.base.dao.SettleOrderMapper;
import com.asianwallets.base.service.ClearingService;
import com.asianwallets.base.service.ReconciliationService;
import com.asianwallets.base.service.SettleOrderService;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.BankCard;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.RabbitMassage;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.WithdrawalVO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 结算交易
 */
@Transactional
@Service
@Slf4j
public class SettleOrderServiceImpl implements SettleOrderService {

    @Autowired
    private SettleOrderMapper settleOrderMapper;

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private AccountMapper accountMapper;


    @Autowired
    private BankCardMapper bankCardMapper;

    @Autowired
    private ClearingService clearingService;

    /**
     * 结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrder(settleOrderDTO));

    }

    /**
     * 结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrderDetail(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrderDetail(settleOrderDTO));

    }

    /**
     * 结算导出
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO) {
        return settleOrderMapper.exportSettleOrderInfo(settleOrderDTO);
    }

    /**
     * 结算审核
     * @param reviewSettleDTO
     * @return
     */
    @Override
    public int reviewSettlement(ReviewSettleDTO reviewSettleDTO) {
        log.info("------------结算审核开始------------settleOrderDTO:{}", JSON.toJSON(reviewSettleDTO));
        if (StringUtils.isEmpty(reviewSettleDTO.getReviewStatus())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        List<ReviewSettleInfoDTO> rsInfos = reviewSettleDTO.getReviewSettleInfoDTOS();
        ArrayList<SettleOrder> soList = new ArrayList<>();
        BigDecimal allFee = BigDecimal.ZERO;
        for (ReviewSettleInfoDTO rsInfo : rsInfos) {
            //判断汇率是否填写
            if (StringUtils.isEmpty(rsInfo.getRate())) {
                throw new BusinessException(EResultEnum.RATE_IS_NULL.getCode());
            }
            SettleOrder sOrder = settleOrderMapper.selectByPrimaryKey(rsInfo.getId());
            //判断是否为结算中
            if (sOrder == null || !sOrder.getTradeStatus().equals(TradeConstant.AUDIT_WAIT)) {
                throw new BusinessException(EResultEnum.TRADE_STATUS_IS_ERROR.getCode());
            }
            sOrder.setModifier(reviewSettleDTO.getModifier());//修改人
            sOrder.setUpdateTime(new Date());//更新时间
            sOrder.setTradeFee(reviewSettleDTO.getTradeFee());//批次交易手续费
            sOrder.setRemark(reviewSettleDTO.getRemark());//备注
            sOrder.setRate(rsInfo.getRate());//汇率
            sOrder.setSettleChannel(reviewSettleDTO.getSettleChannel());//结算通道
            //手续费币种
            if (StringUtils.isEmpty(reviewSettleDTO.getFeeCurrency())) {
                sOrder.setFeeCurrency(sOrder.getTxncurrency());
            } else {
                sOrder.setFeeCurrency(reviewSettleDTO.getFeeCurrency());
            }
            sOrder.setSettleAmount(sOrder.getTxnamount().multiply(rsInfo.getRate()));//单条记录的计费
            allFee = allFee.add(sOrder.getSettleAmount());//总金额
            soList.add(sOrder);
        }
        final BigDecimal allFees = reviewSettleDTO.getTotalSettleAmount();//总结算金额
        //金额为负
        if (allFees.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(EResultEnum.SETTLEMENT_AMOUNT_TOO_SMALL.getCode());
        }
        soList.forEach(so -> so.setTotalSettleAmount(allFees));
        int tag = 0;
        if (String.valueOf(reviewSettleDTO.getReviewStatus()).equals(String.valueOf(TradeConstant.AUDIT_FAIL))) {
            log.info("------------审核失败------------settleOrderList:{}", JSON.toJSON(soList));
            tag = auditFailure(reviewSettleDTO, soList);

        } else if (String.valueOf(reviewSettleDTO.getReviewStatus()).equals(String.valueOf(TradeConstant.AUDIT_SUCCESS))) {
            log.info("------------审核成功------------");
            soList.forEach(s -> s.setTradeStatus(TradeConstant.AUDIT_SUCCESS));//设置成功状态
            log.info("------------审核成功 更新参数------------settleOrderList:{}", JSON.toJSON(soList));
            for (SettleOrder settleOrder : soList) {
                tag += settleOrderMapper.updateByPrimaryKey(settleOrder);
            }
            if (tag != soList.size()) {
                log.info("-------------更新失败 后台数据未更新完全-------------");
                throw new BusinessException(EResultEnum.UPDATE_FAILED.getCode());
            }
        }
        return tag;
    }

    /**
     * 失败情况下的处理
     *
     * @param settleOrderDTO
     * @param list
     * @return
     */
    private int auditFailure(ReviewSettleDTO settleOrderDTO, List<SettleOrder> list) {
        list.forEach(s -> s.setTradeStatus(TradeConstant.AUDIT_FAIL));//设置失败状态
        int tag = 0;
        for (SettleOrder settleOrder : list) {
            ReconOperDTO reconOperDTO = new ReconOperDTO();
            reconOperDTO.setId("T" + IDS.uniqueID());
            reconOperDTO.setAmount(settleOrder.getTxnamount());
            reconOperDTO.setMerchantId(settleOrder.getMerchantId());
            reconOperDTO.setCurrency(settleOrder.getTxncurrency());
            reconOperDTO.setType(1);//调入
            reconOperDTO.setAccountType(TradeConstant.OTHER_ACCOUNT);//其他账户
            reconOperDTO.setChangeType(TradeConstant.TRANSFER);//资金变动类型
            reconOperDTO.setRemark("结算审核失败，系统自动调账");
            log.info("------------审核失败 调账开始------------reconOperDTO:{}", JSON.toJSON(reconOperDTO));
            //调账
            String flag = reconciliationService.doReconciliation(settleOrderDTO.getModifier(), reconOperDTO);
            if (flag.equals("success")) {
                log.info("------------审核失败 过审开始------------settleOrderDTO:{}", JSON.toJSON(settleOrderDTO));
                //过审
                reconciliationService.auditReconciliation(settleOrderDTO.getModifier(), reconOperDTO.getId(), true, "结算审核失败，系统自动调账，系统自动审核");
            }
            tag += settleOrderMapper.updateByPrimaryKey(settleOrder);
        }
        if (list.size() != tag) {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        return tag;
    }

    /**
     * 手动提款
     * @param withdrawalDTO
     * @param userName
     */
    @Override
    public String withdrawal(WithdrawalDTO withdrawalDTO, String userName) {
        List<WithdrawalBankDTO> withdrawalBankDTOS = withdrawalDTO.getWithdrawalBankDTOS();
        HashMap<String, String> map = new HashMap<>();
        withdrawalBankDTOS.forEach(v -> {
            if (!map.containsKey(v.getBankCodeCurrency())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");//根据年月日时分秒毫秒生成批次号
                map.put(v.getBankCodeCurrency(), "P" + sdf.format(new Date().getTime() + (int) (Math.random() * 100)));
            }
        });
        for (WithdrawalBankDTO withdrawalBankDTO : withdrawalBankDTOS) {
            //获取账户表中(结算账户余额-冻结账户余额)大于最小起结金额的数据
            WithdrawalVO withdrawalVO = accountMapper.getAccountByWithdrawal(withdrawalBankDTO.getMerchantId(), withdrawalBankDTO.getCurrency());
            if (StringUtils.isEmpty(withdrawalVO)) {
                log.info("-------------手动提款时对应accountId的账户为空,结算账户金额-冻结金额小于最小起结金额 账户ID:{}--------------", withdrawalBankDTO);
                throw new BusinessException(EResultEnum.INSUFFICIENT_MINIMUM_AMOUNT.getCode());
            }
            //最小起结金额大于提款金额
            if (withdrawalVO.getMinSettleAmount().compareTo(withdrawalBankDTO.getAmount()) > 0) {
                log.info("-------------提现小于最小起结金额 账户ID:{}--------------", withdrawalBankDTO);
                throw new BusinessException(EResultEnum.INSUFFICIENT_MINIMUM_AMOUNT.getCode());
            }
            if (withdrawalVO.getAvailableBalance().compareTo(withdrawalBankDTO.getAmount()) < 0) {
                log.info("-------------手动提款时对应accountId的账户为空,结算账户金额-冻结金额小于提款金额 账户ID:{}--------------", withdrawalBankDTO);
                throw new BusinessException(EResultEnum.INSUFFICIENT_WITHDRAWAL_AMOUNT.getCode());
            }
            //获取银行卡信息
            BankCard bankCard = bankCardMapper.getBankCard(withdrawalBankDTO.getMerchantId(),withdrawalBankDTO.getBankCodeCurrency());
            if (bankCard != null && bankCard.getBankAccountCode() != null && bankCard.getBankCurrency() != null) {
                //机构结算表的数据的设置
                SettleOrder settleOrder = new SettleOrder();
                //结算交易的流水号
                settleOrder.setId("J" + IDS.uniqueID());
                //批次号
                settleOrder.setBatchNo(map.get(withdrawalBankDTO.getBankCodeCurrency()));
                //机构编号
                settleOrder.setInstitutionId(withdrawalVO.getInstitutionId());
                //机构名称
                settleOrder.setInstitutionName(withdrawalVO.getInstitutionName());
                settleOrder.setMerchantId(withdrawalVO.getMerchantId());
                settleOrder.setMerchantName(withdrawalVO.getMerchantName());
                //交易币种
                settleOrder.setTxncurrency(withdrawalVO.getCurrency());
                //结算金额 交易金额即结算金额-冻结金额
                settleOrder.setTxnamount(withdrawalBankDTO.getAmount());
                //结算账户即银行卡账号
                settleOrder.setAccountCode(bankCard.getBankAccountCode());
                //账户名即开户名称
                settleOrder.setAccountName(bankCard.getAccountName());
                //银行名称即开户行名称
                settleOrder.setBankName(bankCard.getBankName());
                settleOrder.setSwiftCode(bankCard.getSwiftCode());//Swift Code
                settleOrder.setIban(bankCard.getIban());//Iban
                //收款人地址
                settleOrder.setReceiverAddress(bankCard.getReceiverAddress());
                settleOrder.setBankCode(bankCard.getBankCode());//bank code
                settleOrder.setBankCurrency(bankCard.getBankCurrency());//结算币种
                settleOrder.setBankCodeCurrency(bankCard.getBankCurrency());//银行卡币种
                //中间行相关字段
                settleOrder.setIntermediaryBankCode(bankCard.getIntermediaryBankCode());//中间行银行编码
                settleOrder.setIntermediaryBankName(bankCard.getIntermediaryBankName());//中间行银行名称
                settleOrder.setIntermediaryBankAddress(bankCard.getIntermediaryBankAddress());//中间行银行地址
                settleOrder.setIntermediaryBankAccountNo(bankCard.getIntermediaryBankAccountNo());//中间行银行账户
                settleOrder.setIntermediaryBankCountry(bankCard.getIntermediaryBankCountry());//中间行银行城市
                settleOrder.setIntermediaryOtherCode(bankCard.getIntermediaryOtherCode());//中间行银行其他code
                settleOrder.setTradeStatus(AsianWalletConstant.SETTLING);//结算中
                settleOrder.setSettleType(AsianWalletConstant.SETTLE_ACCORD);//手动结算
                settleOrder.setCreateTime(new Date());//创建时间
                settleOrder.setCreator(userName);//创建人
                //将机构结算金额通过调账调出去上报清结算
                FundChangeDTO fundChangeDTO = new FundChangeDTO(settleOrder);
                //应结算日期
                fundChangeDTO.setShouldDealtime(DateToolUtils.formatTimestamp.format(new Date()));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //上报清结算成功的场合,插入数据到机构结算表
                    settleOrderMapper.insert(settleOrder);
                    return "提款成功";
                }else {
                    log.info("************上报清结算失败**************************");
                    return "提款失败";
                }
            } else {
                log.info("手动提款结算交易对应的银行卡信息不存在：merchantId={},accountCode={},currency={}", withdrawalVO.getMerchantId(), withdrawalVO.getAccountCode(), withdrawalVO.getCurrency());
                throw new BusinessException(EResultEnum.BILLING_CORRESPONDING_BANK_CARD_DOES_NOT_EXIST.getCode());
            }
        }
        return "提款成功";
    }

}

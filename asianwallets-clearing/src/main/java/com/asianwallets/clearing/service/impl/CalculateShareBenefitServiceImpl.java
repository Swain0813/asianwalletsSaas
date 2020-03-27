package com.asianwallets.clearing.service.impl;

import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.AccountMapper;
import com.asianwallets.clearing.dao.ShareBenefitLogsMapper;
import com.asianwallets.clearing.dao.TcsStFlowMapper;
import com.asianwallets.clearing.dao.TmMerChTvAcctBalanceMapper;
import com.asianwallets.clearing.service.CalculateShareBenefitService;
import com.asianwallets.clearing.utils.ComDoubleUtil;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.ShareBenefitLogs;
import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.utils.IDS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-06 13:50
 **/
@Slf4j
@Service
public class CalculateShareBenefitServiceImpl implements CalculateShareBenefitService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ShareBenefitLogsMapper shareBenefitLogsMapper;

    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/8/23
     * @Descripate 根据流程梳理要求优化的以组为单位分润并提交事物
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateShareForMerchantGroup2(String agentCode, String currency, List<ShareBenefitLogs> list) {
        String key = Const.Redis.CLEARING_KEY + "_" + agentCode + "_" + currency;
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("***************** get lock success key :【{}】 ************** ", key);
            try {
                log.info("**************** calculateShareForMerchantGroup2 单组分润 **************# 开始执行代理商户号为:{}，币种为:{}的待分润数据,时间:{}", agentCode, currency, new Date());
                if (StringUtils.isEmpty(agentCode) || list == null || list.size() == 0 || StringUtils.isEmpty(currency)) {
                    log.info("**************** calculateShareForMerchantGroup2 单组分润 **************# 执行代理商户号为:{}，币种为:{}的数据过程输入参数为空，时间：", agentCode, currency, new Date());
                    return;
                }
                Account account = accountMapper.selectByMerchantIdAndCurrency(agentCode, currency);
                //代理商分润为负数的时候出现挂账现象等待下一笔分润累计，所以代理商账户的结算户可能为负，不用判断
                if (account == null) {
                    log.info("**************** calculateShareForMerchantGroup2 单组分润 **************代理商户:{},币种：{} 不存在，不结算", agentCode, currency);
                    return;
                }
                //期初结算余额
                double beforeBalance = account.getSettleBalance().doubleValue();
                log.info("**************** calculateShareForMerchantGroup2 单组分润 **************# 代理商户为:{}，币种为:{}的期初结算余额为:{}", agentCode, currency, beforeBalance);
                //期末余额
                double afterBalance = 0.0D;
                //账户变动流水
                List<TmMerChTvAcctBalance> list1 = new ArrayList<>();
                //结算账户流水
                List<TcsStFlow> list3 = new ArrayList<>();
                int num = 0;
                for (ShareBenefitLogs sl : list) {
                    afterBalance = ComDoubleUtil.addBySize(beforeBalance, sl.getShareBenefit().doubleValue(), 2);
                    beforeBalance = afterBalance;
                }
                if (afterBalance < 0.0D) {
                    log.info("*************** calculateSharebenefit 单调分润 ************** 结算户余额为负 afterBalance", afterBalance);
                    return;
                }

                beforeBalance = account.getSettleBalance().doubleValue();
                afterBalance = 0.0D;
                for (ShareBenefitLogs sl : list) {
                    if (sl == null || StringUtils.isEmpty(sl.getAgentId()) || StringUtils.isEmpty(sl.getTradeCurrency())
                            || StringUtils.isEmpty(sl.getShareBenefit())) {
                        log.info("*************** calculateSharebenefit 单调分润 ************** 接收处理数据，分润流水信息不全 流水号 ：{}", sl.getId());
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                    if (shareBenefitLogsMapper.selectCountbyIdAndIsShare(sl.getId(), TradeConstant.SHARE_BENEFIT_WAIT) == 0) {
                        log.info("*************** calculateSharebenefit 单调分润 ************** 编号为：{} 流水确认不通过", sl.getId());
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                    log.info("*************** calculateSharebenefit 单调分润 **************,准备执行编号为：{} 分润 ：{} 的待流水记录，期初余额：{}", sl.getId(), sl.getShareBenefit(), beforeBalance);
                    afterBalance = ComDoubleUtil.addBySize(beforeBalance, sl.getShareBenefit().doubleValue(), 2);
                    log.info("*************** calculateSharebenefit 单调分润 **************,准备执行编号为：{} 分润 ：{} 的待流水记录，期末余额：{}", sl.getId(), sl.getShareBenefit(), afterBalance);


                    //插入结算户流水 --- 已结算记录
                    TcsStFlow tcsStFlow = new TcsStFlow();
                    tcsStFlow.setSTFlow("SF" + IDS.uniqueID());
                    tcsStFlow.setRefcnceFlow(sl.getOrderId());
                    tcsStFlow.setTradetype("SP");
                    tcsStFlow.setMerchantid(sl.getAgentId());
                    tcsStFlow.setMerOrderNo(sl.getId());
                    tcsStFlow.setTxncurrency(sl.getTradeCurrency());
                    tcsStFlow.setTxnamount(sl.getShareBenefit().doubleValue());
                    tcsStFlow.setFee(0.0D);
                    tcsStFlow.setRefundOrderFee(0.0D);
                    tcsStFlow.setRefundOrderFeeCurrency(sl.getTradeCurrency());
                    tcsStFlow.setFeecurrency(sl.getTradeCurrency());
                    tcsStFlow.setChannelCost(0.0D);
                    tcsStFlow.setChannelcostcurrency(sl.getTradeCurrency());
                    tcsStFlow.setRevokemount(0.0D);
                    tcsStFlow.setBusinessType(sl.getOrderType());
                    tcsStFlow.setBalancetype(1);
                    tcsStFlow.setAccountNo(account.getId());
                    tcsStFlow.setSTstate(2);
                    tcsStFlow.setShouldSTtime(new Date());
                    tcsStFlow.setActualSTtime(new Date());
                    tcsStFlow.setSysorderid(sl.getOrderId());
                    tcsStFlow.setAddDatetime(new Date());
                    tcsStFlow.setSltamount(sl.getShareBenefit().doubleValue());
                    tcsStFlow.setSltcurrency(sl.getTradeCurrency());
                    tcsStFlow.setTxnexrate(0.0D);
                    tcsStFlow.setGatewayFee(0.0D);
                    tcsStFlow.setNeedClear(1);
                    if (sl.getShareBenefit().doubleValue() < 0.0D) {
                        tcsStFlow.setRemark("挂账");
                    }
                    list3.add(tcsStFlow);

                    //插入商户账户流水表（结算户）--分润流水
                    TmMerChTvAcctBalance tma = new TmMerChTvAcctBalance();
                    tma.setMerchantOrderId(sl.getId());
                    tma.setInstitutionId(account.getInstitutionId());
                    tma.setInstitutionName(account.getInstitutionName());
                    tma.setMerchantName(account.getMerchantName());
                    tma.setFlow("MV" + IDS.uniqueID());
                    tma.setMerchantid(sl.getAgentId());
                    tma.setVaccounId(account.getId());
                    tma.setType(2);
                    tma.setBussinesstype(sl.getOrderType());
                    tma.setBalancetype(1);
                    tma.setCurrency(sl.getTradeCurrency());
                    tma.setReferenceflow(sl.getOrderId());
                    tma.setTradetype("SP");
                    tma.setTxnamount(sl.getTradeAmount().doubleValue());
                    tma.setSltamount(sl.getTradeAmount().doubleValue());
                    tma.setSltcurrency(sl.getTradeCurrency());
                    tma.setIncome(sl.getShareBenefit().doubleValue());
                    tma.setOutcome(0.0D);
                    tma.setFee(0.0D);
                    tma.setRefundOrderFee(0.0D);
                    tma.setBalance(beforeBalance);
                    tma.setAfterbalance(afterBalance);
                    tma.setSysAddDate(new Date());
                    tma.setBalanceTimestamp(new Date());
                    tma.setSltexrate(0.0D);
                    tma.setGatewayFee(0.0D);
                    if (sl.getShareBenefit().doubleValue() < 0.0D) {
                        tma.setRemark("挂账");
                    }
                    list1.add(tma);
                    beforeBalance = afterBalance;
                    //更新分润记录表变成已分润
                    num += shareBenefitLogsMapper.updateByIsShare(sl.getId(), TradeConstant.SHARE_BENEFIT_SUCCESS);
                }
                log.info("**************** calculateShareForMerchantGroup2 单组分润 **************# 代理商户为:{}，币种为:{}的期末结算余额为:{}", agentCode, currency, afterBalance);
                int result3 = tcsStFlowMapper.insertList(list3);
                int result = tmMerChTvAcctBalanceMapper.insertList(list1);
                account.setSettleBalance(new BigDecimal(afterBalance));
                int result1 = accountMapper.updateSPAMTByPrimaryKey(account);
                if (result != list1.size() || num != list.size() || result1 == 0 || result3 != list3.size()) {
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }
            } catch (Exception e) {
                log.error("**************** calculateShareForMerchantGroup2 单组分润 ************** 代理商户号：{} ， 币种 ：{} ，异常：{}", agentCode, currency, e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
        }
    }
}

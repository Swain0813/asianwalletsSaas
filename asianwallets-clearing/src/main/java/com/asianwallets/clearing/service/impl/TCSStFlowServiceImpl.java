package com.asianwallets.clearing.service.impl;

import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.AccountMapper;
import com.asianwallets.clearing.dao.TcsCtFlowMapper;
import com.asianwallets.clearing.dao.TcsStFlowMapper;
import com.asianwallets.clearing.dao.TmMerChTvAcctBalanceMapper;
import com.asianwallets.clearing.service.TCSStFlowService;
import com.asianwallets.clearing.utils.ComDoubleUtil;
import com.asianwallets.clearing.utils.DateUtil;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.TcsCtFlow;
import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.clearing.IntoAndOutMerhtAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 结算服务
 * @author: YangXu
 * @create: 2019-07-25 15:03
 **/
@Slf4j
@Service
public class TCSStFlowServiceImpl implements TCSStFlowService {


    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TcsCtFlowMapper tcsCtFlowMapper;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 清算账户的资金变动处理方法，主要包含插入清算表一条待清算数据,允许立即撤销（在未结算未清算的情况下做RV）
     **/
    @Transactional
    @Override
    public BaseResponse IntoAndOutMerhtSTAccount2(IntoAndOutMerhtAccountRequest ioma) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(Const.CSCode.CODE_CS0009);
        baseResponse.setMsg(Const.CSCode.MSG_CS0009);
        TcsStFlow stf = new TcsStFlow();
        stf.setSTFlow("ST" + IDS.uniqueID());
        stf.setAddDatetime(new Date());
        stf.setBalancetype(ioma.getBalancetype());
        stf.setBusinessType(1);
        stf.setChannelCost(ioma.getChannelCost());
        stf.setFee(ioma.getFee());
        stf.setMerchantid(ioma.getMerchantid());
        stf.setMerOrderNo(ioma.getMerOrderNo());
        stf.setRefcnceFlow(ioma.getRefcnceFlow());
        Date sholddtime = DateUtil.parse(ioma.getShouldDealtime(), "yyyy-MM-dd HH:mm:ss");
        log.debug("*************** 结算 IntoAndOutMerhtCLAccount2 **************** sholddtime:{}", sholddtime);
        stf.setShouldSTtime(sholddtime);
        stf.setSTstate(1);//表示待结算
        stf.setSysorderid(ioma.getSysorderid());
        stf.setTradetype(ioma.getTradetype());
        stf.setTxnamount(ioma.getTxnamount());
        stf.setTxncurrency(ioma.getTxncurrency());
        stf.setTxndesc(ioma.getTxndesc());
        stf.setTxnexrate(ioma.getTxnexrate());
        stf.setRemark(ioma.getRemark());
        stf.setSltamount(ioma.getSltamount());
        stf.setSltcurrency(ioma.getSltcurrency());
        stf.setFeecurrency(ioma.getFeecurrency());
        stf.setChannelcostcurrency(ioma.getChannelCostcurrency());
        stf.setGatewayFee(ioma.getGatewayFee());
        //是否需要处理清除  1不需要，2需要
        stf.setNeedClear(1);
        stf.setRefundOrderFee(ioma.getRefundOrderFee());
        stf.setRefundOrderFeeCurrency(ioma.getRefundOrderFeeCurrency());
        //基础处理
        String key = Const.Redis.CLEARING_KEY + "_" + stf.getMerchantid() + "_" + stf.getSltcurrency();
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("***************** get lock success key :【{}】 ************** 流水号 ：{} ", key, ioma.getRefcnceFlow());
            try {
                String mbuaccountId = null;
                String vaccounId = null;
                int result = 0;
                int Fre_result = 0;//修改商户账户冻结资金字段结果
                int Fre_result2 = 0;//插入冻结记录表结果
                double afterbalance = 0.0;//变动后
                double balance = 0.0;//变动前
                if (stf == null || !(stf.getBalancetype() == 1 || stf.getBalancetype() == 2)) {
                    log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** 待结算的数据为空");
                    return baseResponse;
                }
                /**
                 * 结算户资金变动处理，如果只正常资金那么就插入一条待结算的结算记录。 如果是冻结资金，
                 * 就先更新账户冻结资金金额，插入冻结资金流水，再插入一条待结算的结算记录
                 */
                Account mva01 = accountMapper.selectByMerchantIdAndCurrency(stf.getMerchantid(), stf.getTxncurrency());
                log.info("---------getVersion----------:{}", mva01.getVersion());
                if (mva01 == null || mva01.getId() == null || mva01.getId().equals("")) {
                    log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** 查询商户结算账户信息异常");
                    return baseResponse;
                }

                //查询结算算表中未结算算的金额
                BigDecimal unSettleAmount = tcsStFlowMapper.getUnSettleAmount(stf.getMerchantid(), stf.getTxncurrency());
                unSettleAmount = unSettleAmount == null ? BigDecimal.ZERO : unSettleAmount;
                //结算表中未结算的金额+结算户的资金-冻结户+交易金额
                double secondMoney = ComDoubleUtil.addBySize(mva01.getSettleBalance().doubleValue(), unSettleAmount.doubleValue(), 2);
                double totalMoney = ComDoubleUtil.subBySize(secondMoney, mva01.getFreezeBalance().doubleValue(), 2);
                double outMoney = ComDoubleUtil.addBySize(totalMoney, stf.getTxnamount() - stf.getFee() + stf.getRefundOrderFee(), 2);
                if (outMoney < 0) {
                    log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** 结算算户资金必须大于等于0才能操作，结束时间：{}", new Date());
                    return baseResponse;
                }
                // 获得结算账户的相关编号
                vaccounId = mva01.getId();
                stf.setAccountNo(vaccounId);// 设置商户的结算虚拟账户编号
                /*
                 * 判断是否是冻结资金，如果是冻结资金入待结算表前需要先冻结资金并且记录冻结操作；
                 * 资金为正表示入账，资金为负表示出账
                 */
                if (stf.getBalancetype() == 2) {
                    //表示冻结资金
                    //第一步，更新账户冻结资金金额
                    Account mvafrz = new Account();
                    mvafrz.setCurrency(stf.getSltcurrency());// 结算币种
                    mvafrz.setMerchantId(stf.getMerchantid());// 交易商户号
                    mvafrz.setEnabled(true);
                    mvafrz.setFreezeBalance(new BigDecimal(-1 * (stf.getTxnamount() - stf.getFee() + stf.getRefundOrderFee())));//加冻结 还要加上手续费
                    balance = mva01.getFreezeBalance().doubleValue();//变动前就是刚查询出来的
                    afterbalance = mva01.getFreezeBalance().doubleValue() + (-1 * (stf.getTxnamount() - stf.getFee() + stf.getRefundOrderFee()));//变动后就是
                    mvafrz.setId(vaccounId);
                    mvafrz.setUpdateTime(new Date());
                    mvafrz.setVersion(mva01.getVersion());
                    Fre_result = accountMapper.updateFrozenBalance(mvafrz);
                    //第二步，插入一条冻结资金记录
                    if (Fre_result == 0) {
                        log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** #更新冻结资金失败");
                        throw new BusinessException(EResultEnum.ERROR.getCode());
                    }
                    //冻结资金流水记录
                    TmMerChTvAcctBalance mab = new TmMerChTvAcctBalance();
                    mab.setFlow("MV" + IDS.uniqueID());
                    mab.setAfterbalance(afterbalance);
                    mab.setMerchantid(stf.getMerchantid());
                    mab.setOrganId(stf.getOrganId());//所属机构号
                    mab.setMbuaccountId(mbuaccountId);
                    mab.setVaccounId(vaccounId);
                    mab.setBalance(balance);
                    mab.setBalanceTimestamp(new Date());
                    mab.setSysAddDate(new Date());
                    mab.setBalancetype(stf.getBalancetype());
                    mab.setBussinesstype(stf.getBusinessType());
                    mab.setCurrency(stf.getTxncurrency());
                    mab.setFee(stf.getFee());//冻结资金中不显示手续费
                    mab.setIncome(-1 * (stf.getTxnamount() - stf.getFee() + stf.getRefundOrderFee()));
                    mab.setOutcome(Double.parseDouble("0"));
                    mab.setReferenceflow(stf.getRefcnceFlow());
                    mab.setTradetype(stf.getTradetype());
                    mab.setTxnamount(-1 * stf.getTxnamount());
                    mab.setType(3);//冻结户
                    mab.setGatewayFee(Double.parseDouble("0"));//20170615添加的网关状态手续费
                    mab.setSltcurrency(stf.getSltcurrency());
                    mab.setSltexrate(Double.parseDouble("1"));
                    mab.setSltamount(-1 * stf.getTxnamount());//结算资金
                    mab.setRefundOrderFee(stf.getRefundOrderFee()); //退还手续费
                    Fre_result2 = tmMerChTvAcctBalanceMapper.insertSelective(mab);
                }
                // 插入结算表
                result = tcsStFlowMapper.insert(stf);
                if ((stf.getBalancetype() == 1 && result > 0) || (stf.getBalancetype() == 2 && result > 0 && Fre_result2 > 0)) {
                    baseResponse.setCode(Const.Code.OK);
                    baseResponse.setMsg(Const.Code.OK_MSG);
                } else {
                    log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** 插入商户结算账户资金变动流水信息异常");
                    throw new BusinessException(EResultEnum.ERROR.getCode());
                }
            } catch (Exception ex) {
                log.info("*************** 结算 IntoAndOutMerhtCLAccount2 **************** Exception：{}", ex);
                throw new BusinessException(EResultEnum.ERROR.getCode());
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
            return baseResponse;
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
            return baseResponse;
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/29
     * @Descripate 根据流程梳理要求优化的以组为单位结算并提交事物
     **/
    @Override
    @Transactional
    public void SettlementForMerchantGroup2(String merchantid, String sltcurrency, List<TcsStFlow> list) {
        String key = Const.Redis.CLEARING_KEY + "_" + merchantid + "_" + sltcurrency;
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("***************** get lock success key :【{}】 ************** ", key);
            try {
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************# 开始执行商户号为:{}，币种为:{}的待结算数据,时间:{}", merchantid, sltcurrency, new Date());
                if (StringUtils.isEmpty(merchantid) || list == null || list.size() == 0 || StringUtils.isEmpty(sltcurrency)) {
                    log.info("**************** SettlementForMerchantGroup2 单组结算 **************# 执行商户号为:{}，币种为:{}的数据过程输入参数为空，时间：", merchantid, sltcurrency, new Date());
                    return;
                }
                Account account = accountMapper.selectByMerchantIdAndCurrency(merchantid, sltcurrency);
                if (account == null || account.getSettleBalance().doubleValue() < 0 || account.getFreezeBalance().doubleValue() < 0) {
                    log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的账户资金为负数，不结算", merchantid, sltcurrency);
                    return;
                }
                //判断此批次的总金额是否大于等于0，满足条件才能进行结算
                double totalTxnAmt = 0.0;//此批次的总交易金额
                double totalFee = 0.0;//此批次的总手续费
                double totalRefundFee = 0.0;//此批次的总退还手续费
                double totalFreAmt = 0.0; //此批次已冻结的总金额
                for (TcsStFlow validateST : list) {
                    totalTxnAmt += validateST.getTxnamount();
                    totalFee += validateST.getFee();
                    totalRefundFee += validateST.getRefundOrderFee();

                    //若是冻结资金，交易金额 - 手续费 + 退还手续费
                    if (validateST.getBalancetype() == 2) {
                        totalFreAmt = totalFreAmt + (validateST.getTxnamount() - validateST.getFee() +  validateST.getRefundOrderFee());
                    }
                }
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的此批次总交易金额：{}", merchantid, sltcurrency, totalTxnAmt);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的此批次总手续费：{}", merchantid, sltcurrency, totalFee);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的此批次总退还手续费：{}", merchantid, sltcurrency, totalRefundFee);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的此批次里冻结资金的总金额 ：{}", merchantid, sltcurrency, totalFreAmt);
                //此批次的总收入
                double incomeAmt1 = ComDoubleUtil.subBySize(totalTxnAmt, totalFee, 2);
                double incomeAmt = ComDoubleUtil.addBySize(incomeAmt1,totalRefundFee,2);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} 的此批次总交易金额减总手续费 incomeAmt：{}", merchantid, sltcurrency, incomeAmt);
                //add by ysl 追加结算金额+此批次的总收入如果小于0则下一个批次结算
                //outMoney=结算金额+此批次的总收入
                double outMoney = ComDoubleUtil.addBySize(account.getSettleBalance().doubleValue(), incomeAmt, 2);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} outMoney：{}", merchantid, sltcurrency, outMoney);
                //outMoney1 = outMoney - 冻结金额
                double outMoney1 = ComDoubleUtil.subBySize(outMoney, account.getFreezeBalance().doubleValue(), 2);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{} outMoney1：{}", merchantid, sltcurrency, outMoney1);
                //计算批次总金额时，已经把冻结资金类型的 纪录的金额减去，上一部减去冻结金额相当于减去两次，所以要加上
                double resultOutMoney = ComDoubleUtil.addBySize(outMoney1, -1 * totalFreAmt, 2);
                log.info("**************** SettlementForMerchantGroup2 单组结算 **************商户:{},币种：{}  resultOutMoney：{}", merchantid, sltcurrency, resultOutMoney);
                log.info("==========此批次的总收入==========" + resultOutMoney);
                if (resultOutMoney < 0) {
                    log.info("*********** SettlementForMerchantGroup2 单组结算 ********** 商户:{},币种：{}此批次总收入为负数，全部回滚下次金额满足条件再结算", merchantid, sltcurrency);
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }


                for (TcsStFlow st : list) {
                    //单个执行结算处理
                    BaseResponse dm = this.SettlementBase3(st);
                    if (dm == null || !dm.getCode().equals(Const.Code.OK)) {
                        log.info("**************** ClearForGroupBatch 批次清算 **************# 结束执行key为【{}】的待清算数据,结果：{}", key, dm.getCode());
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                }
            } catch (Exception e) {
                log.error("**************** SettlementForMerchantGroup2 单组结算 ************** 商户号：{} ， 币种 ：{} ，异常：{}", merchantid, sltcurrency, e);
                throw new BusinessException(EResultEnum.ERROR.getCode());
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


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/29
     * @Descripate 结算以批次优化建立此方法，不带事物提交，事物提交放在组中处理
     **/
    public BaseResponse SettlementBase3(TcsStFlow record) {
        BaseResponse message = new BaseResponse();
        message.setCode(Const.Code.FAILED);
        message.setMsg(Const.Code.FAILED_MSG);
        try {
            if (record == null || record.getMerchantid() == null || record.getMerchantid().equals("") || record.getSTFlow() == null || record.getSTFlow().equals("")) {
                log.info("*************** SettlementBase3 结算基础方法 ************** 接收处理数据，关键信息不全");
                return message;
            }
            // 第一步需要确认当前需要结算的交易是否存在；
            TcsStFlow st = tcsStFlowMapper.selectByPrimaryKey(record.getSTFlow());
            if (st == null || record.getSTFlow() == null || record.getSTFlow().equals("") || st.getBalancetype() == 0 || st.getBalancetype() >= 3 || st.getSTstate() == 2) {
                log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{} 交易确认不通过", record.getSTFlow());
                return message;
            }
            log.info("*************** SettlementBase3 结算基础方法 **************确认商户的结算交易存在,准备执行编号为：{} 的待结算记录", record.getSTFlow());
            double afterbalance = 0.0;//正常资金变动后
            double balance = 0.0;//正常资金变动前
            double fr_afterbalance = 0.0;//冻结资金变动后
            double fr_balance = 0.0;//冻结资金变动前
            String mbuaccountId = null;
            String vaccounId = null;
            int result = 0;
            int result1 = 0;
            int result1_fr = 0;
            int result2 = 0;
            int result3 = 0;
            int result4 = 0;

            //更新商户账户资金(结算户资金)
            Account mva01 = accountMapper.selectByMerchantIdAndCurrency(st.getMerchantid(), st.getSltcurrency());
            log.info("---------getVersion----------:{}", mva01.getVersion());
            balance = mva01.getSettleBalance().doubleValue();//结算账户资金
            fr_balance = mva01.getFreezeBalance().doubleValue();//冻结账户资金
            log.info("*************** SettlementBase3 结算基础方法 ************** 变动前结算户:{},结算资金：{}，冻结资金：{}", mva01.getId(), balance, fr_balance);
            vaccounId = mva01.getId();//虚拟账户编号
            Account mva = new Account();//账户更新条件实体类
            if (st.getBalancetype() == 1) {//正常资金
                mva.setFreezeBalance(new BigDecimal(0.0));
                mva.setSettleBalance(new BigDecimal(st.getTxnamount() - st.getFee() + st.getRefundOrderFee()));
                balance = mva01.getSettleBalance().doubleValue();//结算账户资金
                fr_balance = mva01.getFreezeBalance().doubleValue();//冻结账户资金
            } else if (st.getBalancetype() == 2) {//冻结资金 如果是冻结资金，需要同步处理冻结资金和正常资金
                mva.setFreezeBalance(new BigDecimal(st.getTxnamount()-st.getFee()+st.getRefundOrderFee()));//去冻结资金(资金带负号进来的)
                mva.setSettleBalance(new BigDecimal(st.getTxnamount() - st.getFee() +st.getRefundOrderFee()));//账户正常资金
                balance = mva01.getSettleBalance().doubleValue();
                fr_balance = mva01.getFreezeBalance().doubleValue();//冻结资金变动前
            }
            mva.setId(mva01.getId());
            mva.setUpdateTime(new Date());
            mva.setVersion(mva01.getVersion());
            //更新账户资金
            result2 = accountMapper.updateSTAMTByPrimaryKey(mva);
            if (result2 == 0) {
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return message;
            }
            //查询变动后结算户资金
            Account mva1 = accountMapper.selectByMerchantIdAndCurrency(st.getMerchantid(), st.getSltcurrency());
            afterbalance = mva1.getSettleBalance().doubleValue();//结算账户资金
            fr_afterbalance = mva1.getFreezeBalance().doubleValue();//冻结账户资金
            log.info("*************** SettlementBase3 结算基础方法 ************** 变动后结算户:{},结算资金：{}，冻结资金：{}", mva01.getId(), afterbalance, fr_balance);
            if (st.getBalancetype() == 1) {//正常资金
                afterbalance = mva1.getSettleBalance().doubleValue();//结算账户资金
                fr_afterbalance = mva1.getFreezeBalance().doubleValue();//冻结账户资金
            } else if (st.getBalancetype() == 2) {////冻结资金
                afterbalance = mva1.getSettleBalance().doubleValue();//结算账户资金
                fr_afterbalance = mva1.getFreezeBalance().doubleValue();//冻结账户资金
                //如果是冻结资金需要在插入正常资金前将冻结资金解冻
                //插入商户账户流水表（结算户）--冻结资金解冻流水
                TmMerChTvAcctBalance mab_fr = new TmMerChTvAcctBalance();
                mab_fr.setFlow("MV" + IDS.uniqueID());
                mab_fr.setAfterbalance(fr_afterbalance);
                mab_fr.setOrganId(st.getOrganId());//所属机构编号
                mab_fr.setMerchantid(st.getMerchantid());
                mab_fr.setMbuaccountId(mbuaccountId);
                mab_fr.setVaccounId(vaccounId);
                mab_fr.setBalance(fr_balance);
                mab_fr.setBalanceTimestamp(new Date());
                mab_fr.setSysAddDate(new Date());
                mab_fr.setBalancetype(st.getBalancetype());
                mab_fr.setBussinesstype(st.getBusinessType());
                mab_fr.setCurrency(st.getTxncurrency());
                mab_fr.setFee(Double.parseDouble("0"));//冻结资金不计手续费
                mab_fr.setGatewayFee(Double.parseDouble("0"));
                mab_fr.setSltcurrency(st.getSltcurrency());
                mab_fr.setSltexrate(st.getTxnexrate());
                //解冻资金全部都是从出账
                mab_fr.setIncome(Double.parseDouble("0"));
                mab_fr.setOutcome(-1 * st.getTxnamount()+st.getFee()-st.getRefundOrderFee());
                mab_fr.setTxnamount(-1 * st.getTxnamount()+st.getFee()-st.getRefundOrderFee());
                mab_fr.setSltamount(-1 * st.getTxnamount()+st.getFee()-st.getRefundOrderFee());
                mab_fr.setReferenceflow(st.getRefcnceFlow());
                mab_fr.setTradetype(st.getTradetype());
                mab_fr.setType(3);// type 3 冻结账户
                mab_fr.setRemark("解冻资金" + "/" + st.getRemark());
                mab_fr.setRefundOrderFee(Double.parseDouble("0"));
                //插入账户流水记录
                result1_fr = tmMerChTvAcctBalanceMapper.insertSelective(mab_fr);
                if (result1_fr < 1) {
                    log.info("*************** SettlementBase3 结算基础方法 **************:商户的结算过程中解冻资金异常-_-！**,编号为：{}", record.getSTFlow());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
            }
            //插入商户账户流水表（结算户）--正常资金
            TmMerChTvAcctBalance mab = new TmMerChTvAcctBalance();
            mab.setFlow("MV" + IDS.uniqueID());
            mab.setAfterbalance(afterbalance);
            mab.setOrganId(st.getOrganId());//所属机构编号
            mab.setMerchantid(st.getMerchantid());
            mab.setMbuaccountId(mbuaccountId);
            mab.setVaccounId(vaccounId);
            mab.setBalance(balance);
            mab.setBalanceTimestamp(new Date());
            mab.setSysAddDate(new Date());
            mab.setBalancetype(1);//正常资金流水记录
            mab.setBussinesstype(st.getBusinessType());
            mab.setCurrency(st.getTxncurrency());
            if (st.getBalancetype() == 1) {
                mab.setFee(st.getFee());
            } else {
                //冻结资金手续费还要扣的
                mab.setFee(st.getFee());
            }
            mab.setGatewayFee(st.getGatewayFee());
            //20170511新增数据
            mab.setSltcurrency(st.getSltcurrency());
            mab.setSltexrate(st.getTxnexrate());
            mab.setRefundOrderFee(st.getRefundOrderFee());
            //要判断接口传入的交易金额是正数还是负数
            if (st.getSltamount() >= 0) {
                //表示收入
                mab.setIncome(st.getSltamount() - st.getFee() + st.getRefundOrderFee());
                mab.setOutcome(Double.parseDouble("0"));
                mab.setTxnamount(st.getTxnamount());
                mab.setSltamount(st.getSltamount());
                //冻结资金需要特殊处理
            } else {
                //小于0表示支出
                mab.setIncome(Double.parseDouble("0"));
                mab.setOutcome(-1 * st.getSltamount() + st.getFee() - st.getRefundOrderFee());
                mab.setTxnamount(-1 * st.getTxnamount());
                mab.setSltamount(-1 * st.getTxnamount());
            }
            mab.setReferenceflow(st.getRefcnceFlow());
            mab.setTradetype(st.getTradetype());
            mab.setType(2);
            mab.setRemark(st.getRemark());
            //插入账户流水记录
            result1 = tmMerChTvAcctBalanceMapper.insertSelective(mab);
            if (result1 == 0) {
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return message;
            }
            // 更新清算户资金变动（当前业务逻辑下为待结算并且还是正常资金的去掉调账AA,提款WD以外都是从清算表中过来的数据，所以需要回去再处理清算资金）
            if ((st.getSltamount() > 0 && st.getNeedClear() == 2) || (st.getNeedClear() == 2 && st.getTxnamount() < 0 && st.getTradetype().equals(TradeConstant.RV))) {
                //待结算表中的未结算正常资金都是从清算表中过来的，所以还得回去处理清算账户资金和流水
                //RV撤销时 清算时已经减去了清算户的余额,生成一条ST记录，上面结算时结算账户减去了撤销金额，清算户要把清算金额再加回来
                /**
                 * 处理清算资金之前得看看这个订单是否有清算资金撤销的情况
                 */
                TcsCtFlow ctflow = new TcsCtFlow();
                ctflow.setOrganId(st.getOrganId());
                ctflow.setMerchantid(st.getMerchantid());
                ctflow.setMerOrderNo(st.getMerOrderNo());
                ctflow.setSltcurrency(st.getSltcurrency());
                ctflow.setSysorderid(st.getSysorderid());

                Double leftmoney = tcsCtFlowMapper.getCLLeftMoney(ctflow);//获取清算表中金额
                if (leftmoney == null || leftmoney.doubleValue() < 0) {
                //if (leftmoney == null) {
                    log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{}的结算流水 查询订单清算户剩余资金异常，或者查询处理过程中异常", record.getSTFlow());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
                Account mvacl01 = accountMapper.selectByMerchantIdAndCurrency(st.getMerchantid(), st.getSltcurrency());
                double balance2 = mvacl01.getClearBalance().doubleValue();
                double fr_balance2 = mvacl01.getFreezeBalance().doubleValue();
                log.info("*************** SettlementBase3 结算基础方法 **************:变动前对应清算户:{},清算资金：{},冻结资金：{}", mvacl01.getId(), balance2, fr_balance2);
                mvacl01.setClearBalance(new BigDecimal(-1 * leftmoney));
                //冻结户资金不变动所有设为0 sql：freeze_balance = freeze_balance+#{freezeBalance,jdbcType=DECIMAL},
                mvacl01.setFreezeBalance(new BigDecimal(0.0));
                log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{} 减去原有清算户中资金: {} 的结算流水", record.getSTFlow(), leftmoney);
                result3 = accountMapper.updateCTAMTByPrimaryKey(mvacl01);
                if (result3 == 0) {
                    log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{} 结算后返回处理对应的清算户资金异常" + record.getSTFlow());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
                //清算户更新后立马查询变动后的清算户情况
                Account mvacl02 = accountMapper.selectByMerchantIdAndCurrency(st.getMerchantid(), st.getSltcurrency());
                double balance3 = mvacl02.getClearBalance().doubleValue();
                double fr_balance3 = mvacl02.getFreezeBalance().doubleValue();
                log.info("*************** SettlementBase3 结算基础方法 **************:变动后对应清算户:{},清算资金：{},冻结资金：{}", mvacl01.getId(), balance3, fr_balance3);
                //插入商户账户流水表(清算流水)
                TmMerChTvAcctBalance mab1 = new TmMerChTvAcctBalance();
                mab1.setFlow("MV" + IDS.uniqueID());
                mab1.setAfterbalance(balance3);
                mab1.setOrganId(st.getOrganId());
                mab1.setMerchantid(st.getMerchantid());
                mab1.setVaccounId(mvacl01.getId());
                mab1.setBalance(balance2);
                mab1.setBalanceTimestamp(new Date());
                mab1.setSysAddDate(new Date());
                mab1.setBalancetype(st.getBalancetype());
                mab1.setBussinesstype(st.getBusinessType());
                mab1.setCurrency(st.getTxncurrency());
                mab1.setFee(Double.parseDouble("0"));//清算流水中手续费为0
                mab1.setRefundOrderFee(Double.parseDouble("0"));
                mab1.setGatewayFee(Double.parseDouble("0"));
                if (st.getTxnamount() < 0 && st.getTradetype().equals(TradeConstant.RV)) {
                    mab1.setIncome(-1 * leftmoney);
                    mab1.setOutcome(Double.parseDouble("0"));
                } else if (st.getSltamount() > 0 && st.getNeedClear() == 2) {
                    mab1.setIncome(Double.parseDouble("0"));
                    mab1.setOutcome(leftmoney);//结算后处理清算资金都是支出
                }

                mab1.setReferenceflow(st.getRefcnceFlow());
                mab1.setTradetype(st.getTradetype());
                mab1.setType(1);
                //资金流水中用收入和支出表示正负，所以金额全部要改成正
                if (st.getSltamount() < 0) {
                    //表示结算记录中为负数
                    mab1.setTxnamount(-1 * st.getTxnamount());
                    mab1.setSltamount(-1 * st.getSltamount());//结算金额
                } else {
                    //表示结算记录中为正数
                    mab1.setTxnamount(st.getTxnamount());
                    mab1.setSltamount(st.getSltamount());//结算金额
                }
                mab1.setSltcurrency(st.getSltcurrency());//结算币种
                mab1.setSltexrate(st.getTxnexrate());//结算汇率
                mab1.setRemark(st.getRemark());
                result4 = tmMerChTvAcctBalanceMapper.insertSelective(mab1);
                if (result4 <= 0) {
                    log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{} 结算后返回处理对应的清算户资金,插入资金流水记录异常", record.getSTFlow());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
            }
            //在更新结算数据状态
            // 需要更新结算流水表：
            st.setSTstate(2);
            st.setActualSTtime(new Date());
            result = tcsStFlowMapper.updateByPrimaryKeySelective(st);
            if (result1 <= 0 || result2 <= 0 || result <= 0) {
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return message;
            } else {
                message.setCode(Const.Code.OK);
                message.setMsg(Const.Code.OK_MSG);
            }
            log.info("*************** SettlementBase3 结算基础方法 ************** 编号为：{} 的结算流水执行完成", record.getSTFlow());
        } catch (Exception e) {
            log.error("**************** SettlementBase3 结算基础方法 ************** 商户号：{} ， 币种 ：{} ，异常：{}", record.getMerchantid(), record.getTxncurrency(), e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            return message;
        }
    }


}

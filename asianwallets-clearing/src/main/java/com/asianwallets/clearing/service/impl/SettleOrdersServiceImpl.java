package com.asianwallets.clearing.service.impl;
import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.*;
import com.asianwallets.clearing.service.SettleOrdersService;
import com.asianwallets.clearing.utils.ComDoubleUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.utils.DateToolUtils;
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
 * 定时跑批自动提款功能
 */
@Slf4j
@Service
public class SettleOrdersServiceImpl implements SettleOrdersService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BankCardMapper bankCardMapper;

    @Autowired
    private SettleOrderMapper settleOrderMapper;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    /**
     * 自动提款功能
     */
    @Override
    @Transactional
    public void getSettleOrders(String merchantId, String currency, List<Account> lists){
        String key = Const.Redis.CLEARING_KEY + "_" + merchantId + "_" + currency;
        log.info("************自动提款功能 CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("*****************自动提款功能 get lock success key :【{}】 ************** ", key);
            try {
                log.info("**************** getSettleOrders 单组自动提款 **************# 开始执行商户编号为:{}，币种为:{}的待提款的数据,时间:{}", merchantId, currency, new Date());
                if (StringUtils.isEmpty(merchantId) || lists == null || lists.size() == 0 || StringUtils.isEmpty(currency)) {
                    log.info("**************** getSettleOrders 单组自动 **************# 执行商户编号为:{}，币种为:{}的数据过程输入参数为空，时间：", merchantId, currency, new Date());
                    return;
                }
                //根据商户编号和币种查询账户信息
                Account account = accountMapper.selectByMerchantIdAndCurrency(merchantId,currency);
                if(account==null){
                    log.info("*************自动提款功能 getSettleOrders*************商户编号:{},币种：{} 不存在，不能进行自动提款结算",merchantId,currency);
                }
                double beforeBalance = account.getSettleBalance().doubleValue();//期初结算余额
                log.info("**************** 自动提款功能 getSettleOrders **************# 商户编号:{}，币种为:{}的期初结算余额为:{}", merchantId,currency,beforeBalance);
                double afterBalance = 0.0D; //期末余额
                List<TmMerChTvAcctBalance> tmMerChTvAcctBalanceLists = new ArrayList<>();//账户变动流水
                List<SettleOrder> settleOrderLists = new ArrayList<>();//账户变动流水
                List<TcsStFlow> tcsStFlowLists = new ArrayList<>();//结算账户流水
                for(Account list:lists){
                    if (list == null || StringUtils.isEmpty(list.getMerchantId()) || StringUtils.isEmpty(list.getCurrency())
                            || StringUtils.isEmpty(list.getFreezeBalance()) || StringUtils.isEmpty(list.getFreezeBalance())
                            || list.getSettleBalance().subtract(list.getFreezeBalance()).compareTo(BigDecimal.ZERO)==-1) {
                        log.info("*************** 自动提款功能 getSettleOrders ************** 接收处理数据，结算金额-冻结金额小于0或者必要的参数为空*********商户编号为:{},币种为:{}",merchantId,currency);
                        return;
                    }
                    //结算金额
                    BigDecimal outMoney = list.getSettleBalance().subtract(list.getFreezeBalance());//自动提现金额即结算金额-冻结金额
                    //获取银行卡信息
                    BankCard bankCard = bankCardMapper.getBankCard(list.getMerchantId(),list.getCurrency());
                    if(bankCard!=null && bankCard.getBankAccountCode()!=null && bankCard.getBankCurrency()!=null){
                        //根据商户编号和银行卡code获取结算表中当日第一条数据的批次号
                        String batchNo = settleOrderMapper.getBatchNo(list.getInstitutionId(),bankCard.getBankCode());
                        //结算表的数据的设置
                        SettleOrder settleOrder = new SettleOrder();
                        settleOrder.setId("J"+ IDS.uniqueID());//结算交易的流水号
                        if(StringUtils.isEmpty(batchNo)){
                            //根据年月日时分秒毫秒生成批次号
                            settleOrder.setBatchNo("P".concat(DateToolUtils.currentTime()));//批次号
                        }else {//非空的场合
                            settleOrder.setBatchNo(batchNo);//批次号
                        }
                        //机构编号
                        settleOrder.setInstitutionId(list.getInstitutionId());
                        //机构名称
                        settleOrder.setInstitutionName(list.getInstitutionName());
                        //商户编号
                        settleOrder.setMerchantId(list.getMerchantId());
                        //商户名称
                        settleOrder.setMerchantName(list.getMerchantName());
                        //交易币种
                        settleOrder.setTxncurrency(list.getCurrency());
                        //结算金额即结算金额-冻结金额
                        settleOrder.setTxnamount(outMoney);
                        //结算账户即银行卡账号
                        settleOrder.setAccountCode(bankCard.getBankAccountCode());
                        //账户名即开户名称
                        settleOrder.setAccountName(bankCard.getAccountName());
                        //银行名称即开户行名称
                        settleOrder.setBankName(bankCard.getBankName());
                        //收款人地址
                        settleOrder.setReceiverAddress(bankCard.getReceiverAddress());
                        //Swift Code
                        settleOrder.setSwiftCode(bankCard.getSwiftCode());
                        //Iban
                        settleOrder.setIban(bankCard.getIban());
                        //bank code
                        settleOrder.setBankCode(bankCard.getBankCode());
                        //结算币种
                        settleOrder.setBankCurrency(bankCard.getBankCurrency());
                        //银行卡币种
                        settleOrder.setBankCodeCurrency(bankCard.getBankCurrency());
                        //中间行相关字段
                        settleOrder.setIntermediaryBankCode(bankCard.getIntermediaryBankCode());//中间行银行编码
                        settleOrder.setIntermediaryBankName(bankCard.getIntermediaryBankName());//中间行银行名称
                        settleOrder.setIntermediaryBankAddress(bankCard.getIntermediaryBankAddress());//中间行银行地址
                        settleOrder.setIntermediaryBankAccountNo(bankCard.getIntermediaryBankAccountNo());//中间行银行账户
                        settleOrder.setIntermediaryBankCountry(bankCard.getIntermediaryBankCountry());//中间行银行城市
                        settleOrder.setIntermediaryOtherCode(bankCard.getIntermediaryOtherCode()); //中间行银行其他code
                        //结算中
                        settleOrder.setTradeStatus(AsianWalletConstant.SETTLING);
                        //自动结算
                        settleOrder.setSettleType(AsianWalletConstant.SETTLE_AUTO);
                        //创建时间
                        settleOrder.setCreateTime(new Date());
                        //创建人
                        settleOrder.setCreator("定时跑批生成结算交易任务");
                        settleOrderLists.add(settleOrder);
                        log.info("*************** getSettleOrders 自动提款前 **************,准备执行商户编号为：{} 自动提款金额 ：{} 的待结算记录，期初余额：{}", list.getMerchantId(),outMoney,beforeBalance);
                        //期初余额-结算金额
                        afterBalance = ComDoubleUtil.subBySize(beforeBalance,outMoney.doubleValue(),2);
                        log.info("*************** getSettleOrders 自动提款后 **************,准备执行商户编号为：{} 自动提款金额 ：{} 的待结算记录，期末余额：{}", list.getMerchantId(), outMoney, afterBalance);
                        //插入结算户流水 --- 已结算记录
                        TcsStFlow tcsStFlow = new TcsStFlow();
                        tcsStFlow.setSTFlow("SF"+ IDS.uniqueID());
                        tcsStFlow.setRefcnceFlow(settleOrder.getId());
                        tcsStFlow.setTradetype(TradeConstant.WD);
                        tcsStFlow.setMerchantid(settleOrder.getMerchantId());
                        tcsStFlow.setMerOrderNo(settleOrder.getId());
                        tcsStFlow.setTxncurrency(settleOrder.getTxncurrency());
                        tcsStFlow.setTxnamount(-1*settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        tcsStFlow.setFee(0.0D);
                        tcsStFlow.setFeecurrency(settleOrder.getTxncurrency());
                        tcsStFlow.setRefundOrderFeeCurrency(settleOrder.getTxncurrency());
                        tcsStFlow.setRefundOrderFee(0.0D);
                        tcsStFlow.setChannelCost(0.0D);
                        tcsStFlow.setChannelcostcurrency(settleOrder.getTxncurrency());
                        tcsStFlow.setRevokemount(0.0D);
                        //1正常资金
                        tcsStFlow.setBalancetype(1);
                        tcsStFlow.setAccountNo(account.getId());
                        //结算状态 2已结算
                        tcsStFlow.setSTstate(2);
                        tcsStFlow.setBusinessType(1);
                        tcsStFlow.setShouldSTtime(new Date());
                        tcsStFlow.setActualSTtime(new Date());
                        tcsStFlow.setSysorderid(settleOrder.getId());
                        tcsStFlow.setAddDatetime(new Date());
                        tcsStFlow.setSltamount(-1*settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        tcsStFlow.setSltcurrency(settleOrder.getTxncurrency());
                        tcsStFlow.setTxnexrate(0.0D);
                        tcsStFlow.setGatewayFee(0.0D);
                        //是否需要处理清除 清算表 不需要
                        tcsStFlow.setNeedClear(1);
                        tcsStFlow.setTxndesc("提款");
                        tcsStFlow.setRemark("自动提款生成结算交易");
                        tcsStFlowLists.add(tcsStFlow);

                        //插入商户账户流水表（结算户）--提款记录
                        TmMerChTvAcctBalance tma = new TmMerChTvAcctBalance();
                        tma.setFlow("MV" + IDS.uniqueID());
                        tma.setMerchantid(settleOrder.getMerchantId());
                        tma.setMerchantName(settleOrder.getMerchantName());
                        tma.setInstitutionId(settleOrder.getInstitutionId());
                        tma.setInstitutionName(settleOrder.getInstitutionName());
                        tma.setMerchantOrderId(settleOrder.getId());
                        tma.setVaccounId(account.getId());
                        //结算户
                        tma.setType(2);
                        //1正常资金
                        tma.setBalancetype(1);
                        tma.setBussinesstype(1);
                        tma.setCurrency(settleOrder.getTxncurrency());
                        tma.setReferenceflow(settleOrder.getId());
                        tma.setTradetype(TradeConstant.WD);
                        tma.setTxnamount(settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        tma.setSltamount(settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        tma.setSltcurrency(settleOrder.getTxncurrency());
                        tma.setIncome(0.0D);
                        tma.setOutcome(settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        tma.setFee(0.0D);
                        //原账户余额
                        tma.setBalance(beforeBalance);
                        //变动后账户余额
                        tma.setAfterbalance(afterBalance);
                        tma.setSltexrate(Double.parseDouble("1"));
                        tma.setSysAddDate(new Date());
                        tma.setBalanceTimestamp(new Date());
                        tma.setGatewayFee(0.0D);
                        tma.setRemark("自动提款生成结算交易");
                        tmMerChTvAcctBalanceLists.add(tma);
                        beforeBalance = afterBalance;
                    }else{
                        log.info("结算交易对应的银行卡信息不存在：merchantId={},accountCode={},currency={}",list.getMerchantId(),list.getAccountCode(),list.getCurrency());
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                }
                //数据库操作
                //变更后的账户信息
                account.setId(account.getId());
                account.setVersion(account.getVersion());
                account.setSettleBalance(new BigDecimal(afterBalance));
                account.setUpdateTime(new Date());
                account.setRemark("定时任务自动提款更新");
                int result2 =  tcsStFlowMapper.insertList(tcsStFlowLists);
                int result3 = tmMerChTvAcctBalanceMapper.insertList(tmMerChTvAcctBalanceLists);
                int result4 = settleOrderMapper.insertList(settleOrderLists);
                int result = accountMapper.updateAccountByPrimaryKey(account);
                if(result2!=tcsStFlowLists.size() || result3!=tmMerChTvAcctBalanceLists.size() || result==0 || result4!=settleOrderLists.size()){
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }
            }catch (Exception e){
                log.error("**************** getSettleOrders 单组自动提款功能 ************** 商户编号：{} ， 币种 ：{} ，异常：{}", merchantId, currency, e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* 自动提款功能 release lock failed ******************** ：{} ", key);
                }
                log.info("********************* 自动提款功能 release lock success ******************** : {}", key);
            }
        }else {
            log.info("********************* 自动提款功能 get lock failed ******************** : {} : " + key);
        }
    }
}

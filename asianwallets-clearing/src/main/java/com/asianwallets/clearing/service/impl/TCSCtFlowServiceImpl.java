package com.asianwallets.clearing.service.impl;
import cn.hutool.core.date.DateUtil;
import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.AccountMapper;
import com.asianwallets.clearing.dao.TcsCtFlowMapper;
import com.asianwallets.clearing.dao.TcsStFlowMapper;
import com.asianwallets.clearing.dao.TmMerChTvAcctBalanceMapper;
import com.asianwallets.clearing.service.TCSCtFlowService;
import com.asianwallets.clearing.utils.ComDoubleUtil;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 清算服务
 * @author: YangXu
 * @create: 2019-07-25 15:03
 **/
@Slf4j
@Service
public class TCSCtFlowServiceImpl implements TCSCtFlowService {

    @Autowired
    private TcsCtFlowMapper tcsCtFlowMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 结算账户资金变动处理，主要操作包含插入结算流水，后续操作交由清结算定时任务来处理，尽量避免同步操作导致并发锁表的可能性
     **/
    @Transactional
    @Override
    public BaseResponse IntoAndOutMerhtSTAccount2(IntoAndOutMerhtAccountRequest ioma) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(Const.CSCode.CODE_CS0009);
        baseResponse.setMsg(Const.CSCode.MSG_CS0009);
        TcsCtFlow ctf = new TcsCtFlow();
        ctf.setCTFlow("CT"+IDS.uniqueID());
        ctf.setBalancetype(ioma.getBalancetype());
        ctf.setBusinessType(1);
        ctf.setChannelCost(ioma.getChannelCost());
        ctf.setCTstate(1);//待清算
        ctf.setFee(ioma.getFee());
        ctf.setMerchantid(ioma.getMerchantid());
        ctf.setMerOrderNo(ioma.getMerOrderNo());
        ctf.setRefcnceFlow(ioma.getRefcnceFlow());
        Date sholddtime = DateUtil.parse(ioma.getShouldDealtime(), "yyyy-MM-dd HH:mm:ss");
        log.debug("*************** 清算 IntoAndOutMerhtCLAccount2 **************** sholddtime:{}" , sholddtime);
        ctf.setShouldCTtime(sholddtime);
        ctf.setSysorderid(ioma.getSysorderid());
        ctf.setTradetype(ioma.getTradetype());
        ctf.setTxnamount(ioma.getTxnamount());
        ctf.setTxncurrency(ioma.getTxncurrency());
        ctf.setAddDatetime(new Date());
        ctf.setTxndesc(ioma.getTxndesc());
        ctf.setTxnexrate(ioma.getTxnexrate());
        ctf.setRemark(ioma.getRemark());
        ctf.setSltamount(ioma.getSltamount());
        ctf.setSltcurrency(ioma.getSltcurrency());
        ctf.setFeecurrency(ioma.getFeecurrency());
        ctf.setChannelcostcurrency(ioma.getChannelCostcurrency());
        ctf.setGatewayFee(ioma.getGatewayFee());
        ctf.setRefundOrderFee(ioma.getRefundOrderFee());
        ctf.setRefundOrderFeeCurrency(ioma.getRefundOrderFeeCurrency());
        try {
            if (ctf == null || ctf.getSltcurrency() == null || ctf.getTxncurrency() == null || ctf.getMerchantid() == null || ctf.getBusinessType() == 0) {
                //输入参数为空，待清算的数据为空
                log.info("*************** 清算 IntoAndOutMerhtCLAccount2 **************** 待清算的数据为空，结束时间：{}", new Date());
                return baseResponse;
            }
            //清算户资金必须大于等于0才能操作
            Account account = accountMapper.selectByMerchantIdAndCurrency(ctf.getMerchantid(),ctf.getTxncurrency());
            //查询清算表中未清算的金额
            BigDecimal unClearAmount =tcsCtFlowMapper.getUnClearAmount(ctf.getMerchantid(),ctf.getTxncurrency());
            unClearAmount = unClearAmount == null ? BigDecimal.ZERO : unClearAmount;
            //清算户资金+清算表中未清算的金额+交易金额  清算这边的手续费不算了，都统一到结算那边算
            double clearMoney = ComDoubleUtil.addBySize(account.getClearBalance().doubleValue(),unClearAmount.doubleValue(),2);
            double outMoney = ComDoubleUtil.addBySize(clearMoney,ctf.getTxnamount()-ctf.getFee()+ctf.getRefundOrderFee(), 2);
            if(outMoney<0){
                log.info("*************** 清算 IntoAndOutMerhtCLAccount2 **************** 清算户资金必须大于等于0才能操作，结束时间：{}", new Date());
                return baseResponse;
            }
            int result = tcsCtFlowMapper.insertSelective(ctf);
            if (result > 0) {
                baseResponse.setCode(Const.Code.OK);
                baseResponse.setMsg(Const.Code.OK_MSG);
            } else {
                //插入商户待清算记录信息异常
                log.info("*************** 清算 IntoAndOutMerhtCLAccount2 **************** 插入商户待清算记录信息异常，结束时间：{}", new Date());
                throw new BusinessException(EResultEnum.ERROR.getCode());
            }
        } catch (Exception e) {
            log.info("*************** 清算 IntoAndOutMerhtCLAccount2 **************** Exception：{}", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            return baseResponse;
        }
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 以商户编号，币种，业务类型，待清算分组list进行一组批次清算方法
     **/
    @Override
    @Transactional
    public BaseResponse ClearForMerchantGroup(String mid, String currency, int businessType, List<TcsCtFlow> list) {
        log.info("**************** ClearForMerchantGroup 单组清算 **************#开始，时间：" + new Date());
        BaseResponse message = new BaseResponse();
        message.setCode(Const.Code.FAILED);
        message.setMsg(Const.Code.FAILED_MSG);
        int allsize = 0;//总条数
        int successize = 0;//成功条数
        String key = Const.Redis.CLEARING_KEY + "_" + mid + "_" + currency;
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("***************** get lock success key :【{}】 ************** ");
            try {
                if (mid == null || mid.equals("") || currency == null || currency.equals("") || businessType == 0 || list == null || list.size() == 0) {
                    log.info("**************** ClearForMerchantGroup 单组清算 ************** 输入参数为空，结束，时间：{}", new Date());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
                allsize = list.size();//总记录数
                //先查询商户账户是否存在
                Account mcl = new Account();
                mcl.setCurrency(currency);//结算币种
                mcl.setMerchantId(mid);//交易商户号
                mcl.setEnabled(true);
                Account ma2cl = accountMapper.selectOne(mcl);//账户查询
                if (ma2cl == null) {
                    //查询不到清算账户
                    log.info("**************** ClearForMerchantGroup 单组清算 ************** mid :【{}】,currency :【{}】 输入参数查询不到对应的清算账户信息，时间：{}", mid, currency, new Date());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
                //先要保证开始执行前结算户不为负数
                if (ma2cl.getSettleBalance() == null || ma2cl.getFreezeBalance() == null || (ma2cl.getSettleBalance().doubleValue() < 0 || ma2cl.getFreezeBalance().doubleValue() < 0)) {
                    //结算户为负数
                    log.info("**************** ClearForMerchantGroup 单组清算 ************** mid :【{}】,currency :【{}】输入参数查询结算户为负数，时间 : {}", mid, currency, new Date());
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
                /***************************循环清算单条数据 START *************************************/
                for (TcsCtFlow ct : list) {
                    //先查询清算记录是否存在
                    TcsCtFlow clexit = tcsCtFlowMapper.selectOne(ct);
                    if (clexit == null) {
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 单号：{} 验证清算记录不存在，时间：{}", ct.getCTFlow(), new Date());
                        break;
                    }
                    //再查询一下当前的清算户信息
                    Account ma2clnow = accountMapper.selectByPrimaryKey(ma2cl.getId());
                    log.info("---------getVersion----------:{}",ma2clnow.getVersion());
                    if (ma2clnow == null) {
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 单号：{} 验证清算户信息不存在，时间：{}", ct.getCTFlow(), new Date());
                        break;
                    }
                    log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 清算户变动前金额 :{}，时间：{}", ma2cl.getClearBalance(), new Date());
                    double afterbalance = 0.0;//变动后余额
                    double balance = 0.0;//变动前余额
                    //先更新清算账户；
                    Account mva = new Account();
                    balance = ma2clnow.getClearBalance().doubleValue();//变动前余额
                    afterbalance = balance + ct.getTxnamount();//变动后余额
                    mva.setClearBalance(new BigDecimal(ct.getTxnamount()));//清算表中的金额
                    mva.setId(ma2clnow.getId());
                    mva.setUpdateTime(new Date());
                    mva.setVersion(ma2clnow.getVersion());
                    int rows = accountMapper.updateCTAMTByPrimaryKey(mva);
                    if (rows != 1) {
                        //表示更新清算户失败，
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 单号：{} 更新清算户失败 ，时间：", ct.getCTFlow(), new Date());
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return message;
                    }
                    //再次查询变动后的清算户资金
                    Account ma2clafter = accountMapper.selectByPrimaryKey(ma2cl.getId());
                    log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 清算户变动后金额 :{}，时间：", ma2clafter.getClearBalance(), new Date());
                    log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 单号：{} 更新清算户成功，继续，时间：{}", ct.getCTFlow(), new Date());
                    //插入清算流水；
                    TmMerChTvAcctBalance mab = new TmMerChTvAcctBalance();
                    mab.setFlow("MV" + IDS.uniqueID());
                    mab.setAfterbalance(afterbalance);
                    mab.setOrganId(ct.getOrganId());
                    mab.setMerchantid(ct.getMerchantid());
                    mab.setMbuaccountId(null);
                    mab.setSysAddDate(new Date());
                    mab.setVaccounId(ma2cl.getId());//清算户编号
                    mab.setBalance(balance);
                    mab.setBalanceTimestamp(new Date());
                    mab.setBalancetype(ct.getBalancetype());
                    mab.setBussinesstype(ct.getBusinessType());
                    mab.setCurrency(ct.getTxncurrency());//交易币种
                    mab.setFee(Double.parseDouble("0"));//清算流水中手续费为0
                    mab.setRefundOrderFee(Double.parseDouble("0"));//清算流水中手续费为0
                    mab.setGatewayFee(Double.parseDouble("0"));//清算流水中网关手续费为0，不做计算
                    mab.setSltcurrency(ct.getSltcurrency());//结算币种
                    mab.setSltexrate(ct.getTxnexrate());//结算汇率
                    //退还收单手续费
                    mab.setRefundOrderFee(Double.parseDouble("0"));
                    //要判断接口传入的交易金额是正数还是负数
                    if (ct.getTxnamount() >= 0) {
                        //表示收入，
                        mab.setIncome(ct.getTxnamount());
                        mab.setOutcome(Double.parseDouble("0"));
                        mab.setTxnamount(ct.getTxnamount());
                        mab.setSltamount(ct.getSltamount());
                    } else {
                        //小于0表示支出，有收入和支出那么所有的都要在流失里面都显示为正
                        mab.setIncome(Double.parseDouble("0"));
                        mab.setTxnamount(-1 * ct.getTxnamount());
                        mab.setOutcome(-1 * ct.getTxnamount());
                        mab.setSltamount(-1 * ct.getSltamount());
                    }
                    mab.setReferenceflow(ct.getRefcnceFlow());
                    mab.setRemark(ct.getRemark());
                    mab.setTradetype(ct.getTradetype());
                    //清算账户
                    mab.setType(1);
                    //插入账户资金变动流水
                    int rows2 = tmMerChTvAcctBalanceMapper.insertSelective(mab);
                    if (rows2 != 1) {
                        //表示插入账户资金变动流水失败，
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 ************** 单号：{} 插入账户资金变动流水失败，时间：{}", ct.getCTFlow(), new Date());
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return message;
                    }
                    //插入待结算流水；
                    //第四步：往stflow表中添加一条待结算数据
                    TcsStFlow st = new TcsStFlow();
                    st.setSTFlow("SF" + IDS.uniqueID());
                    st.setAccountNo(ma2cl.getId());
                    st.setFee(ct.getFee());
                    //网关状态手续费
                    st.setGatewayFee(ct.getGatewayFee());
                    st.setChannelCost(ct.getChannelCost());
                    //所属机构编号
                    st.setOrganId(ct.getOrganId());
                    st.setMerchantid(ct.getMerchantid());
                    st.setSysorderid(ct.getSysorderid());
                    st.setMerOrderNo(ct.getMerOrderNo());
                    st.setRefcnceFlow(ct.getRefcnceFlow());
                    st.setShouldSTtime(ct.getShouldCTtime());
                    st.setRefundOrderFee(ct.getRefundOrderFee());
                    st.setRefundOrderFeeCurrency(ct.getRefundOrderFeeCurrency());
                    st.setSTstate(1);
                    if (ct.getTxnamount() >= 0) {
                        if(st.getTradetype().equals("RA")){
                            st.setTradetype("RA");//撤销失败的场合需要调账到清算表
                        }else{
                            st.setTradetype("ST");//此时的交易类型要转换成结算：
                        }
                        st.setNeedClear(2);//需要处理清算资金
                    } else {
                        st.setTradetype("RV");//此时的交易类型要转换成结算：
                        st.setNeedClear(2);//需要处理清算资金
                    }
                    st.setTxnamount(ct.getTxnamount());
                    st.setTxncurrency(ct.getTxncurrency());
                    st.setAddDatetime(new Date());
                    st.setBusinessType(ct.getBusinessType());
                    st.setBalancetype(ct.getBalancetype());
                    st.setSltamount(ct.getSltamount());
                    st.setSltcurrency(ct.getSltcurrency());
                    st.setFeecurrency(ct.getFeecurrency());
                    st.setChannelcostcurrency(ct.getChannelcostcurrency());
                    st.setTxnexrate(ct.getTxnexrate());

                    TcsStFlow st2 = new TcsStFlow();
                    st2.setTradetype(ct.getTradetype());
                    st2.setMerchantid(ct.getMerchantid());
                    st2.setMerOrderNo(ct.getMerOrderNo());
                    st2.setRefcnceFlow(ct.getRefcnceFlow());
                    st2.setSysorderid(ct.getSysorderid());
                    List<TcsStFlow> listst = tcsStFlowMapper.select(st2);
                    if (listst != null && listst.size() > 0) {
                        //表示记录存在
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 **************  单号：{} 待插入的ST记录已经存在，时间：{}", ct.getCTFlow(), new Date());
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return message;
                    }
                    //记录不存在
                    int rows3 = tcsStFlowMapper.insertSelective(st);
                    log.info("**************** ClearForMerchantGroup 循环清算单条数据 **************  单号：{} 插入待结算流水记录完成", ct.getCTFlow());
                    // 需要更新清算流水表：
                    ct.setCTstate(2);
                    ct.setActualCTtime(new Date());
                    //更新清算记录；
                    int rows4 = tcsCtFlowMapper.updateByPrimaryKeySelective(ct);
                    if (rows3 == 1 && rows4 == 1) {
                        successize++;//成功数加1
                    } else {
                        log.info("**************** ClearForMerchantGroup 循环清算单条数据 **************  单号：{} 异常显示", ct.getCTFlow());
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return message;
                    }
                }

                /***************************循环清算单条数据 END *************************************/
                //判断总共成功的条数和总记录数是否相等，相等就全部提交事物，不等就全部回滚事物 successize/allsize
                if (allsize > 0 && successize > 0 && allsize == successize) {
                    //整个批次处理成功
                    log.info("**************** ClearForMerchantGroup 单组清算 ************** #整个批次处理成功，结束，时间：{}", new Date());
                    log.info("**************** ClearForMerchantGroup 单组清算********* 本次清算跑批的总条数是:{}",allsize);
                    message.setCode(Const.Code.OK);
                    message.setMsg(Const.Code.OK_MSG);
                } else {
                    //整个批次处理失败
                    log.info("**************** ClearForMerchantGroup 单组清算 ************** 整个批次处理失败，结束，时间：{}", new Date());
                    message.setCode(Const.Code.SELECT_FAILED);
                    message.setMsg(Const.Code.SELECT_FAILED_MSG + ":整个批次处理失败");
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return message;
                }
            } catch (Exception e) {
                log.error("****************  ClearForGroupBatch 批次清算 ************** 商户号：{} ， 币种 ：{} ，异常：{}",mid,currency,e);
                throw new BusinessException(EResultEnum.ERROR.getCode());
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
            return message;
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
            return message;
        }
    }
}

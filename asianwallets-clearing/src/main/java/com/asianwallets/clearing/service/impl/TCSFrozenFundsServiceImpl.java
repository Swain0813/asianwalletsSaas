package com.asianwallets.clearing.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.AccountMapper;
import com.asianwallets.clearing.dao.TmMerChTvAcctBalanceMapper;
import com.asianwallets.clearing.service.TCSFrozenFundsService;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-08-01 17:22
 **/
@Service
@Slf4j
public class TCSFrozenFundsServiceImpl implements TCSFrozenFundsService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 清结算系统冻结/解冻处理方法
     **/
    @Override
    @Transactional
    public BaseResponse frozenFundsLogs(FinancialFreezeDTO ffl) {
        log.info("********************* frozenFundsLogs *********************** #开始时间：{}", new Date());
        BaseResponse message = new BaseResponse();
        message.setCode(Const.Code.FAILED);// 默认失败
        //获得商户查询的请求参数,并且验证
        String key = Const.Redis.CLEARING_KEY + "_" + ffl.getMerchantId() + "_" + ffl.getTxncurrency();
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            log.info("***************** get lock success key :【{}】 ************** 流水号 ：{} ", key, ffl.getMerOrderNo());
            try {
                Account mva = accountMapper.selectByMerchantIdAndCurrency(ffl.getMerchantId(), ffl.getTxncurrency());
                ffl.setMvaccountId(mva.getId());
                log.info("---------getVersion----------:{}",mva.getVersion());
                if (mva == null) {
                    //虚拟账户不存在
                    log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #虚拟账户不存在，验证不通过,时间：{}", new Date());
                    message.setCode(Const.Code.CODE_MvaccountIdIllegal);
                    message.setMsg(Const.Code.MSG_MvaccountIdIllegal);
                    return message;
                }
                if (mva == null && mva.getId() == null && mva.getMerchantId() == null && !(mva.getVersion() > 0) && ffl == null && ffl.getMvaccountId() == null) {
                    //请求参数为空
                    message.setCode(Const.Code.CODE_NoParameter);
                    message.setMsg(Const.Code.MSG_NoParameter);
                    log.info("********************* frozenFundsLogs *********************** #请求参数为空,时间：{}", new Date());
                    return message;
                }
                //商户账户存在的情况下，需要判断本次操作是否会导致账户为负
                if (!(mva.getFreezeBalance().doubleValue() + ffl.getTxnamount() >= 0)) {
                    log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #本次操作后会导致冻结账户资金为负,时间：{}", new Date());
                    message.setCode(Const.Code.CODE_LackOfFunds);
                    message.setMsg(Const.Code.CODE_LackOfFunds + ":本次操作是否会导致账户为负");
                    return message;
                }
                /*
                 * 参数OK的情况下需要同时做冻结资金更新和插入冻结记录表2个操作
                 * 要么同是成功，要么同是失败
                 */
                //更新结算户冻结资金
                log.info("***************** frozenFundsLogs ****************# 账户：{} 变动前冻结账户余额 ：{}", ffl.getMvaccountId(), mva.getFreezeBalance().doubleValue());
                if (ffl.getState() == 1 && mva.getSettleBalance().doubleValue() < ffl.getTxnamount()) {
                    log.info("********************* frozenFundsLogs *********************** #结算账户资金金额不足,时间：{}", new Date());
                    return message;
                }
                if (ffl.getState() == 2 && mva.getFreezeBalance().doubleValue() < ffl.getTxnamount()) {
                    log.info("********************* frozenFundsLogs *********************** #冻结账户资金金额不足,时间：{}", new Date());
                    return message;
                }
                double balance = mva.getFreezeBalance().doubleValue();
                double afterBalance = mva.getFreezeBalance().doubleValue() + ffl.getTxnamount();
                //设置冻结资金
                mva.setFreezeBalance(new BigDecimal(ffl.getTxnamount()));
                int rows1 = accountMapper.updateFrozenBalance(mva);
                log.info("***************** frozenFundsLogs ****************# 账户：{} 变动后冻结账户余额 ：{}", ffl.getMvaccountId(), afterBalance);
                //冻结资金流水记录
                TmMerChTvAcctBalance mab = new TmMerChTvAcctBalance();
                mab.setFlow("MV" + IDS.uniqueID());
                mab.setAfterbalance(afterBalance);
                mab.setMerchantid(ffl.getMerchantId());
                mab.setMerchantName(mva.getMerchantName());
                mab.setInstitutionId(mva.getInstitutionId());
                mab.setInstitutionName(mva.getInstitutionName());
                mab.setMerchantOrderId(ffl.getMerOrderNo());
                mab.setOrganId(ffl.getId());
                mab.setMbuaccountId(null);
                mab.setVaccounId(ffl.getMvaccountId());
                mab.setBalance(balance);
                mab.setBalanceTimestamp(new Date());
                mab.setSysAddDate(new Date());
                mab.setBalancetype(2);
                mab.setBussinesstype(1);
                mab.setCurrency(ffl.getTxncurrency());
                mab.setFee(Double.parseDouble("0"));
                mab.setRefundOrderFee(Double.parseDouble("0"));
                mab.setReferenceflow(ffl.getMerOrderNo());
                //冻结户
                mab.setType(3);
                mab.setGatewayFee(Double.parseDouble("0"));
                mab.setSltcurrency(ffl.getTxncurrency());
                mab.setSltexrate(Double.parseDouble("1"));
                mab.setRemark(ffl.getDesc());
                int rows2 = 0;
                if (ffl.getState() == 1) {
                    log.info("*******************进入加冻结，插入结算记录的虚拟账户编号*********************:" + ffl.getMvaccountId());
                    //加冻结，插入结算记录
                    //冻结
                    mab.setTradetype("FZ");
                    mab.setIncome(ffl.getTxnamount());
                    mab.setOutcome(Double.parseDouble("0"));
                    mab.setTxnamount(ffl.getTxnamount());
                    //结算资金
                    mab.setSltamount(ffl.getTxnamount());
                } else {
                    log.info("*******************进入解冻结，更新结算记录的虚拟账户编号*********************:" + ffl.getMvaccountId());
                    //解冻
                    mab.setTradetype("TW");
                    mab.setIncome(Double.parseDouble("0"));
                    mab.setOutcome(-1 * ffl.getTxnamount());
                    mab.setTxnamount(-1 * ffl.getTxnamount());
                    //结算资金
                    mab.setSltamount(-1 * ffl.getTxnamount());
                }
                //插入账户余额变动记录表
                rows2 = tmMerChTvAcctBalanceMapper.insertSelective(mab);
                log.info("******************* mva:{}", JSON.toJSON(mva));
                if (rows1 == 1 && rows2 == 1) {
                    //成功
                    message.setCode(Const.Code.OK);
                    message.setMsg(Const.Code.OK_MSG);
                    log.info("********************* frozenFundsLogs *********************** #所有处理都成功,时间：{}", new Date());
                } else {
                    //异常需要抛出错误并且回滚
                    log.info("********************* frozenFundsLogs *********************** 异常需要抛出错误并且回滚,时间：{}", new Date());
                    throw new BusinessException(EResultEnum.ERROR.getCode());
                }
            } catch (Exception e) {
                log.info("******************* frozenFundsLogs ******************** Exception：{} ", e);
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

package com.asianwallets.clearing;
import com.asianwallets.clearing.service.*;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClearingApplicationTests {
    @Autowired
    private IntoAccountService intoAccountService;

    @Autowired
    private ClearService clearService;

    @Autowired
    private SettleService settleService;

    @Autowired
    private ShareBenefitService shareBenefitService;

    @Autowired
    private FrozenFundsService frozenFundsService;


    @Test
    public void test1(){
        //clearService.ClearForGroupBatch();
        //settleService.SettlementForBatch();
        shareBenefitService.ShareBenefitForBatch();
        //new RedisLockThread().start();
        //new RedisLockThread3().start();    //RF RV
        //new RedisLockThread1().start();  //加冻结
        //new RedisLockThread2().start();  //解冻结
    }

    /**
     * 收单1
     */
    class RedisLockThread extends Thread {
        @Override
        public void run() {
            FundChangeDTO fundChangeDTO = new FundChangeDTO();
            fundChangeDTO.setMerchantid("M201912062672");
            fundChangeDTO.setIsclear(1);//1：清算 2结算
            fundChangeDTO.setBalancetype(1);//资金类型
            fundChangeDTO.setRefcnceFlow("1");//交易流水号
            fundChangeDTO.setSysorderid("1");//系统订单号
            fundChangeDTO.setMerOrderNo("1");//机构订单号
            fundChangeDTO.setFee(1.00);//手续费
            fundChangeDTO.setTxnamount(100.00);//交易金额
            fundChangeDTO.setSltamount(100.00);//结算金额
            fundChangeDTO.setTradetype(TradeConstant.NT);//收单
            fundChangeDTO.setTxncurrency("SGD");//订单币种
            fundChangeDTO.setSltcurrency("SGD");//结算币种
            fundChangeDTO.setFeecurrency("SGD");//手续费币种
            fundChangeDTO.setChannelCostcurrency("SGD");//通道成本币种
            fundChangeDTO.setShouldDealtime(DateToolUtils.formatTimestamp.format(new Date()));//应结算日期
            fundChangeDTO.setChannelCost(0.00);//交易通道成本
            fundChangeDTO.setTxndesc("test");//测试
            fundChangeDTO.setTxnexrate(1.00000);//保留五位小数
            fundChangeDTO.setRemark("demo清结算接口测试");
            fundChangeDTO.setGatewayFee(0.00);//交易状态手续费

            FundChangeDTO fundChangeDTO1 = new FundChangeDTO();
            BeanUtils.copyProperties(fundChangeDTO, fundChangeDTO1);
            fundChangeDTO1.setIsclear(1);//1：清算 2结算
            fundChangeDTO1.setBalancetype(1);//资金类型
            fundChangeDTO1.setRefcnceFlow("2");//交易流水号
            fundChangeDTO1.setSysorderid("2");//系统订单号
            fundChangeDTO1.setMerOrderNo("2");//机构订单号
            fundChangeDTO1.setFee(1.00);//手续费
            fundChangeDTO1.setTxnamount(100.00);//交易金额
            fundChangeDTO1.setSltamount(100.00);
            fundChangeDTO1.setTradetype(TradeConstant.NT);//收单
            fundChangeDTO1.setTxncurrency("THB");//订单币种
            fundChangeDTO1.setSltcurrency("THB");//结算币种
            fundChangeDTO1.setFeecurrency("THB");//手续费币种
            fundChangeDTO1.setChannelCostcurrency("THB");//通道成本币种
            List<FundChangeDTO> list = new ArrayList<>();
            list.add(fundChangeDTO1); //thb 101
            list.add(fundChangeDTO);  //sgd 101

            for (FundChangeDTO fundChange : list) {
                intoAccountService.intoAndOutMerhtAccount(fundChange);
            }
        }
    }


    /**
     * 冻结
     **/
    class RedisLockThread1 extends Thread {
        @Override
        public void run() {

            FinancialFreezeDTO csFrozenFundsRequest = new FinancialFreezeDTO();
            csFrozenFundsRequest.setMerchantId("M201912062672");
            csFrozenFundsRequest.setMerOrderNo("1");
            csFrozenFundsRequest.setTxncurrency("SGD");
            csFrozenFundsRequest.setTxnamount(100.00);
            csFrozenFundsRequest.setState(1);
            frozenFundsService.CSFrozenFunds(csFrozenFundsRequest);


        }
    }
    /**
     * 解冻结
     **/
    class RedisLockThread2 extends Thread {
        @Override
        public void run() {

            FinancialFreezeDTO csFrozenFundsRequest = new FinancialFreezeDTO();
            csFrozenFundsRequest.setMerchantId("M201912062672");
            csFrozenFundsRequest.setMerOrderNo("1");
            csFrozenFundsRequest.setTxncurrency("SGD");
            csFrozenFundsRequest.setTxnamount(-100.00);
            csFrozenFundsRequest.setState(2);
            frozenFundsService.CSFrozenFunds(csFrozenFundsRequest);


        }
    }

    /**
     * @Author YangXu
     * @Date 2019/12/16
     * @Descripate 退款
     * @return
     **/
    class RedisLockThread3 extends Thread {
        @Override
        public void run() {
            FundChangeDTO fundChangeDTO = new FundChangeDTO();
            fundChangeDTO.setMerchantid("M201912062672");
            fundChangeDTO.setIsclear(2);//1：清算 2结算
            fundChangeDTO.setBalancetype(1);//资金类型
            fundChangeDTO.setRefcnceFlow("3");//交易流水号
            fundChangeDTO.setSysorderid("3");//系统订单号
            fundChangeDTO.setMerOrderNo("3");//机构订单号
            fundChangeDTO.setFee(1.00);//手续费
            fundChangeDTO.setTxnamount(-100.00);//交易金额
            fundChangeDTO.setSltamount(-100.00);//结算金额
            fundChangeDTO.setTradetype(TradeConstant.RF);//收单
            fundChangeDTO.setTxncurrency("SGD");//订单币种
            fundChangeDTO.setSltcurrency("SGD");//结算币种
            fundChangeDTO.setFeecurrency("SGD");//手续费币种
            fundChangeDTO.setChannelCostcurrency("SGD");//通道成本币种
            fundChangeDTO.setShouldDealtime(DateToolUtils.formatTimestamp.format(new Date()));//应结算日期
            fundChangeDTO.setChannelCost(0.00);//交易通道成本
            fundChangeDTO.setTxndesc("test");//测试
            fundChangeDTO.setTxnexrate(1.00000);//保留五位小数
            fundChangeDTO.setRemark("demo清结算接口测试");
            fundChangeDTO.setGatewayFee(0.00);//交易状态手续费
            fundChangeDTO.setRefundOrderFee(1);
            fundChangeDTO.setRefundOrderFeeCurrency("SGD");

            //FundChangeDTO fundChangeDTO1 = new FundChangeDTO();
            //BeanUtils.copyProperties(fundChangeDTO, fundChangeDTO1);
            //fundChangeDTO1.setIsclear(2);//1：清算 2结算
            //fundChangeDTO1.setBalancetype(1);//资金类型
            //fundChangeDTO1.setRefcnceFlow("4");//交易流水号
            //fundChangeDTO1.setSysorderid("4");//系统订单号
            //fundChangeDTO1.setMerOrderNo("4");//机构订单号
            //fundChangeDTO1.setFee(1.00);//手续费
            //fundChangeDTO1.setTxnamount(-100.00);//交易金额
            //fundChangeDTO1.setSltamount(-100.00);
            //fundChangeDTO1.setTradetype(TradeConstant.RF);//收单
            //fundChangeDTO1.setTxncurrency("THB");//订单币种
            //fundChangeDTO1.setSltcurrency("THB");//结算币种
            //fundChangeDTO1.setFeecurrency("THB");//手续费币种
            //fundChangeDTO1.setChannelCostcurrency("THB");//通道成本币种
            //fundChangeDTO.setRefundOrderFee(1);
            //fundChangeDTO.setRefundOrderFeeCurrency("THB");

            FundChangeDTO fundChangeDTO1 = new FundChangeDTO();
            BeanUtils.copyProperties(fundChangeDTO, fundChangeDTO1);
            fundChangeDTO1.setIsclear(1);//1：清算 2结算
            fundChangeDTO1.setBalancetype(1);//资金类型
            fundChangeDTO1.setRefcnceFlow("4");//交易流水号
            fundChangeDTO1.setSysorderid("4");//系统订单号
            fundChangeDTO1.setMerOrderNo("4");//机构订单号
            fundChangeDTO1.setFee(1.00);//手续费
            fundChangeDTO1.setTxnamount(-100.00);//交易金额
            fundChangeDTO1.setSltamount(-100.00);
            fundChangeDTO1.setTradetype(TradeConstant.RV);//收单
            fundChangeDTO1.setTxncurrency("THB");//订单币种
            fundChangeDTO1.setSltcurrency("THB");//结算币种
            fundChangeDTO1.setFeecurrency("THB");//手续费币种
            fundChangeDTO1.setChannelCostcurrency("THB");//通道成本币种
            fundChangeDTO.setRefundOrderFee(1);
            fundChangeDTO.setRefundOrderFeeCurrency("THB");

            List<FundChangeDTO> list = new ArrayList<>();
            list.add(fundChangeDTO1); //thb 101
            list.add(fundChangeDTO);  //sgd 101

            for (FundChangeDTO fundChange : list) {
                intoAccountService.intoAndOutMerhtAccount(fundChange);
            }
        }
    }

}

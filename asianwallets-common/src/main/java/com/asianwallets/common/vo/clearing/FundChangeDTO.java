package com.asianwallets.common.vo.clearing;;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.utils.DateToolUtils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资金变动输入参数
 */
@Data
public class FundChangeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //商户号
    public String merchantid;

    //清结算类型，1：清算，2结算
    public int isclear;

    /**
     * 参考流水号，所属业务表,eg:退款表记录flow,提款表记录flow
     */
    public String refcnceFlow;

    //交易类型
    public String tradetype;

    //商户订单号
    public String merOrderNo;

    //交易币种
    public String txncurrency;

    //交易金额
    public Double txnamount;

    //交易描述
    public String txndesc;

    //交易汇率
    public double txnexrate;

    //备注
    public String remark;

    //结算金额
    public double sltamount;

    //结算币种
    public String sltcurrency;

    //手续费币种
    public String feecurrency;

    //退还手续费币种
    public String refundOrderFeeCurrency;

    //退还手续费
    public double refundOrderFee;

    //通道成本币种
    public String channelCostcurrency;

    //交易状态手续费
    public double gatewayFee;

    //清算状态
    public int state;

    //应该清算时间
    public String shouldDealtime;

    //实际清算时间
    public Date actualCTtime;

    /**
     * 系统订单号,eg:tb_pgw_order中的sysorderflow，
     * sysorderid与refcnceFlow可以相同，只要可以反向定位出
     * 流水原来所属的交易即可，比如退款：可以输入：原订单编号和本次退款编号
     */
    public String sysorderid;

    /**
     * 手续费
     */
    public Double fee;

    /**
     * 渠道成本
     */
    public Double channelCost;

    /**
     * 资金类型
     * 1：正常资金，2：冻结资金
     */
    public int balancetype;

    //签名信息
    public String signMsg;

    //应答code
    public String respCode;

    //应答消息
    public String respMsg;


    public FundChangeDTO() {
    }


    /**
     * 退款用
     *
     * @param tradetype
     * @param orderRefund
     */
    public FundChangeDTO(String tradetype, OrderRefund orderRefund) {
        this.merchantid = orderRefund.getMerchantId();
        this.refcnceFlow = orderRefund.getId();
        //交易类型 NT：收单，RF：退款，RV：撤销，WD：提款，AA:调账，TA:转账
        this.tradetype = tradetype;
        //商户订单号
        this.merOrderNo = orderRefund.getMerchantOrderId();
        //订单币种
        this.txncurrency = orderRefund.getOrderCurrency();
        this.txnamount = -1 * orderRefund.getOrderAmount().doubleValue();
        //原订单id
        this.sysorderid = orderRefund.getOrderId();
        if (tradetype.equals(TradeConstant.RF)) {
            //退款
            this.balancetype = TradeConstant.FROZEN_FUND;
            //结算
            this.isclear = TradeConstant.SETTLE;
        } else if (tradetype.equals(TradeConstant.RV)) {
            //撤销
            //正常资金
            this.balancetype = TradeConstant.NORMAL_FUND;
            //清算
            this.isclear = TradeConstant.CLEARING;
        } else if (tradetype.equals(TradeConstant.AA)) {
            //调账
            //正常资金
            this.balancetype = TradeConstant.NORMAL_FUND;
            //清算
            this.isclear = TradeConstant.SETTLE;
        } else if (tradetype.equals(TradeConstant.RA)) {
            //调账
            //正常资金
            this.balancetype = TradeConstant.NORMAL_FUND;
            //清算
            this.isclear = TradeConstant.CLEARING;
        }
        this.txndesc = "退款";
        //汇率,2位
        this.txnexrate = orderRefund.getExchangeRate().doubleValue();
        if (orderRefund.getRemark() != null) {
            this.remark = orderRefund.getRemark();
        } else {
            this.remark = "";
        }
        //结算币种
        this.sltcurrency = orderRefund.getOrderCurrency();
        this.sltamount = -1 * orderRefund.getOrderAmount().doubleValue();
        //网关手续费
        this.gatewayFee = 0.00;
        //手续费,2位
        if (orderRefund.getFeePayer() == 1) {
            this.fee = orderRefund.getRefundFee().doubleValue();
        } else {
            this.fee = 0.00;
        }
        //手续费币种
        this.feecurrency = orderRefund.getOrderCurrency();
        //退还手续费,2位
        if (orderRefund.getFeePayer() == 1) {
            this.refundOrderFee = orderRefund.getRefundOrderFee().doubleValue();
        } else {
            this.refundOrderFee = 0.00;
        }
        //退还手续费币种
        this.refundOrderFeeCurrency = orderRefund.getOrderCurrency();
        //通道成本 2位
        this.channelCost = orderRefund.getChannelFee().doubleValue();
        this.channelCostcurrency = orderRefund.getTradeCurrency();
        this.shouldDealtime = orderRefund.getProductSettleCycle();
    }

    /**
     * 调账用
     *
     * @param reconciliation
     */
    public FundChangeDTO(Reconciliation reconciliation) {
        this.merchantid = reconciliation.getMerchantId();
        this.refcnceFlow = reconciliation.getId();
        //交易类型 NT：收单，RF：退款，RV：撤销，WD：提款，AA:调账，TA:转账
        if (reconciliation.getAccountType() == 1) {
            this.tradetype = TradeConstant.RA;
        } else if (reconciliation.getAccountType() == 2) {
            this.tradetype = TradeConstant.AA;
        }
        //商户订单号
        this.merOrderNo = reconciliation.getMerchantOrderId();
        //订单币种
        this.txncurrency = reconciliation.getCurrency();
        if (reconciliation.getReconciliationType() == 1) {
            //调入
            this.txnamount = reconciliation.getAmount().doubleValue();
            this.sltamount = reconciliation.getAmount().doubleValue();
        } else {
            //调出
            this.txnamount = -1 * reconciliation.getAmount().doubleValue();
            this.sltamount = -1 * reconciliation.getAmount().doubleValue();
        }
        //原订单id
        this.sysorderid = reconciliation.getOrderId();
        if (reconciliation.getAccountType() == 1) {
            this.balancetype = TradeConstant.NORMAL_FUND;
            this.isclear = TradeConstant.CLEARING;
        } else if (reconciliation.getAccountType() == 2) {
            this.balancetype = TradeConstant.NORMAL_FUND;
            this.isclear = TradeConstant.SETTLE;
        }
        this.txndesc = "调账";
        this.remark = reconciliation.getRemark();
        //结算币种
        this.sltcurrency = reconciliation.getCurrency();
        this.txnexrate = 1;
        //网关手续费
        this.gatewayFee = 0.00;
        //手续费,2位
        this.fee = 0.00;
        //手续费币种
        this.feecurrency = reconciliation.getCurrency();
        //退还手续费,2位
        this.refundOrderFee = 0.00;
        //退还手续费币种
        this.refundOrderFeeCurrency = reconciliation.getCurrency();
        //通道成本 2位
        this.channelCost = 0.00;
        this.channelCostcurrency = reconciliation.getCurrency();
        this.shouldDealtime = DateToolUtils.formatTimestamp.format(new Date());


    }

    /**
     * 收单用
     *
     * @param orders
     * @param tradeType
     */
    public FundChangeDTO(Orders orders, String tradeType) {
        //商户号
        this.merchantid = orders.getMerchantId();
        //是否清算
        this.isclear = TradeConstant.CLEARING;
        //交易流水号
        this.refcnceFlow = orders.getId();
        //交易类型 NT：收单，RF：退款，RV：撤销，WD：提款，AA:调账，TA:转账
        this.tradetype = tradeType;
        this.merOrderNo = orders.getMerchantOrderId();//商户订单号
        this.txncurrency = orders.getOrderCurrency();//订单币种
        //订单金额,2位
        this.txnamount = orders.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.sysorderid = orders.getId();//订单id
        //手续费,2位
        this.fee = orders.getFee().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //通道成本 2位
        this.channelCost = 0.00;
        //资金类型,正常资金
        this.balancetype = TradeConstant.NORMAL_FUND;
        //商品描述
        this.txndesc = orders.getProductDescription();
        //汇率
        this.txnexrate = orders.getExchangeRate().setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.remark = orders.getRemark();//备注
        this.sltcurrency = orders.getOrderCurrency();//结算币种
        //结算金额
        this.sltamount = orders.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.feecurrency = orders.getOrderCurrency();//手续费币种
        this.channelCostcurrency = orders.getTradeCurrency();//通道成本币种
        this.gatewayFee = 0.00;//网关手续费
        this.shouldDealtime = orders.getProductSettleCycle();//应结算日期
        //退还手续费,2位
        this.refundOrderFee = 0.00;
        //退还手续费币种
        this.refundOrderFeeCurrency = orders.getOrderCurrency();
    }

    /**
     * 提款用
     * @param settleOrder
     */
    public FundChangeDTO(SettleOrder settleOrder) {
        this.merchantid = settleOrder.getMerchantId();//商户号
        this.refcnceFlow = settleOrder.getId();//业务流水号
        this.tradetype = TradeConstant.WD;//交易类型 WD：提款
        this.merOrderNo = settleOrder.getId();//结算交易的流水号
        this.txncurrency = settleOrder.getTxncurrency();//交易币种
        this.txnamount = -1 * settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.sltamount = -1 * settleOrder.getTxnamount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.sysorderid =settleOrder.getId();//结算交易的流水号
        this.balancetype = TradeConstant.FROZEN_FUND;//冻结资金
        this.isclear = TradeConstant.SETTLE;//是否清算
        this.txndesc = "提款";//交易描述
        this.txnexrate = 1;//没有汇率的场合
        this.remark = settleOrder.getRemark();//备注
        this.sltcurrency = settleOrder.getTxncurrency();//结算币种
        this.channelCostcurrency = settleOrder.getTxncurrency();//通道成本币种
        this.gatewayFee = 0.00;//网关手续费
        this.fee = 0.00;//手续费,2位
        this.feecurrency = settleOrder.getTxncurrency();//手续费币种
        this.channelCost = 0.00;//通道成本 2位
        this.shouldDealtime =DateToolUtils.formatTimestamp.format(new Date());
    }
}

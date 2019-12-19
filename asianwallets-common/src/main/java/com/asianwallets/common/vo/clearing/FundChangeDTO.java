package com.asianwallets.common.vo.clearing;

import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.OrderRefund;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author RyanCai
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
        //机构订单号
        this.merOrderNo = orderRefund.getMerchantOrderId();
        //订单币种
        this.txncurrency = orderRefund.getOrderCurrency();
        this.txnamount = -1 * orderRefund.getOrderAmount().doubleValue();
        //原订单id
        this.sysorderid = orderRefund.getOrderId();
        if (tradetype.equals(TradeConstant.RF)) {
            //退款
            this.balancetype = TradeConstant.NORMAL_FUND;
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
        this.txndesc = "";
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
        //通道成本币种
        this.channelCostcurrency = orderRefund.getOrderCurrency();
        //网关手续费
        this.gatewayFee = 0.00;
        //手续费,2位
        this.fee = orderRefund.getRefundFee().doubleValue();
        //手续费币种
        this.feecurrency = orderRefund.getOrderCurrency();
        //退还手续费,2位
        this.refundOrderFee = orderRefund.getRefundOrderFee().doubleValue();
        //退还手续费币种
        this.refundOrderFeeCurrency = orderRefund.getOrderCurrency();
        //通道成本 2位
        this.channelCost = 0.00;
    }

}

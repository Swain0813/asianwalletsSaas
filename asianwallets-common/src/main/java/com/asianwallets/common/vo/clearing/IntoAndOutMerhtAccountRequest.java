package com.asianwallets.common.vo.clearing;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author RyanCai
 *
 */
@Data
public class IntoAndOutMerhtAccountRequest implements Serializable{
	private static final long serialVersionUID = 1L;

	private String version;

	private int inputCharset;

	private int language;

	private String merchantid;//商户号

    private int isclear;//清结算类型，1：清算，2结算

    /**
     * 参考流水号，所属业务表,eg:退款表记录flow,提款表记录flow
     */
    private String refcnceFlow;

    private String tradetype;//交易类型

    private String merOrderNo;//商户订单号

    private String txncurrency;//交易币种

    private Double txnamount;//交易金额

    private String txndesc;//交易描述

    private double txnexrate;//交易汇率

    private String remark;//备注

    private double sltamount;//结算金额

    private String sltcurrency;//结算币种

    private String feecurrency;//手续费币种

    private String refundOrderFeeCurrency;//退还手续费币种

    private double refundOrderFee;//退还手续费

    private String channelCostcurrency;//通道成本币种

    private double gatewayFee;//交易状态手续费

    private int state;//清算状态

    private String shouldDealtime;//应该清算时间

    private Date actualCTtime;//实际清算时间

    /**
     * 系统订单号,eg:tb_pgw_order中的sysorderflow，
     * sysorderid与refcnceFlow可以相同，只要可以反向定位出
     * 流水原来所属的交易即可，比如退款：可以输入：原订单编号和本次退款编号
     */
    private String sysorderid;

    /**
     * 手续费
     */
    private Double fee;

    /**
     * 渠道成本
     */
    private Double channelCost;

    /**
     * 资金类型
     * 1：正常资金，2：冻结资金
     */
    private int balancetype;

    private String signMsg;//签名信息

    private String respCode;//应答code

    private String respMsg;//应答消息



}

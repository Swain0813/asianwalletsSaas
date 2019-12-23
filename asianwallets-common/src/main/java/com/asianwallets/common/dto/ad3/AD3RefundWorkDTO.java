package com.asianwallets.common.dto.ad3;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.MD5Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: AD3退款接口业务参数
 * @author: YangXu
 * @create: 2019-03-14 10:37
 **/
@Data
@ApiModel(value = "AD3退款接口业务参数", description = "AD3退款接口业务参数")
public class AD3RefundWorkDTO {

    @ApiModelProperty(value = "终端编号")
    private String terminalId ;

    @ApiModelProperty(value = "操作员ID，字符串")
    private String operatorId;

    @ApiModelProperty(value = "交易密码")
    private String tradePwd;

    @ApiModelProperty(value = "终端系统订单号，唯一")
    private String merOrderNo;

    @ApiModelProperty(value = "终端提交过来的退款流水号")
    private String outRefundId;

    @ApiModelProperty(value = "交易系统订单流水号")
    private String sysOrderNo;

    @ApiModelProperty(value = "固定14位 格式yyyyMMddHHmmss")
    private String refundOrdertime;

    @ApiModelProperty(value = "退款金额 保留二位小数")
    private String refundAmount;

    @ApiModelProperty(value = "退款来源")
    private String source;

    @ApiModelProperty(value = "备注1")
    private String ext1;

    /**
     * 调AD3退款用
     * @param terminalId
     * @param operatorId
     * @param tradePassword
     * @param orderRefund
     */
    public AD3RefundWorkDTO(String terminalId, String operatorId, String tradePassword, OrderRefund orderRefund) {
        this.terminalId = terminalId ;//终端编号
        this.operatorId = operatorId ;//操作员ID
        this.tradePwd = MD5Util.getMD5String(tradePassword).toLowerCase();//交易密码
        this.merOrderNo = orderRefund.getOrderId();//终端系统订单号
        this.outRefundId = orderRefund.getId();//终端提交过来的退款流水号
        this.sysOrderNo = orderRefund.getChannelNumber();//交易系统订单流水号
        this.refundOrdertime = DateToolUtils.toString(new Date(), "yyyyMMddHHmmss");//退款时间
        this.refundAmount = String.valueOf(orderRefund.getTradeAmount());//退款金额
        this.source = "1";//如:1，pos请求过来必须传1
        if(orderRefund.getRemark()!=null){//不为空的场合，直接从退款信息里获取
         this.ext1 = orderRefund.getRemark();
        }else {
            this.ext1 = "";//备注
        }
    }

    /**
     * AD3直接退款----撤销用
     * @param terminalId
     * @param operatorId
     * @param tradePassword
     * @param orders
     */
    public AD3RefundWorkDTO(String terminalId, String operatorId, String tradePassword, Orders orders) {
        this.terminalId = terminalId ;//终端编号
        this.operatorId = operatorId ;//操作员ID
        this.tradePwd = MD5Util.getMD5String(tradePassword).toLowerCase();//交易密码
        this.merOrderNo = orders.getId();//终端系统订单号
        this.outRefundId = orders.getId();//终端提交过来的退款流水号
        this.sysOrderNo = orders.getChannelNumber();//交易系统订单流水号
        this.refundOrdertime = DateToolUtils.toString(new Date(), "yyyyMMddHHmmss");//退款时间
        this.refundAmount = String.valueOf(orders.getTradeAmount());//退款金额
        this.source = "1";//如:1，pos请求过来必须传1
        if(orders.getRemark()!=null){//不为空的场合，直接从退款信息里获取
            this.ext1 = orders.getRemark();
        }else {
            this.ext1 = "";//备注
        }
    }
}

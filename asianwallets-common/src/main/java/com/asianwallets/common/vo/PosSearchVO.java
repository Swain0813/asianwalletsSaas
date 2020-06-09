package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel(value = "pos机交易打印查询输出实体", description = "pos机交易打印查询输出实体")
public class PosSearchVO {

    @ApiModelProperty(value = "订单号")
    private String id;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;//1-订单 2-退款单

    @ApiModelProperty(value = "支付方式")
    private String payMethod;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单笔数")
    private int count;

    @ApiModelProperty(value = "预授权订单的备注")
    private String preRemark;
}

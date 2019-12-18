package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/5/13 16:59
 * @Description: 线上查询订单信息输出实体
 */
@Data
@ApiModel(value = "线上查询订单信息输出实体", description = "线上查询订单信息输出实体")
public class OnlineOrdersInfoVO {

    @ApiModelProperty(value = "订单id")
    private String referenceNo;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "机构订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易状态")
    private Byte txnstatus;

    @ApiModelProperty(value = "机构上传订单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date orderTime;

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date txnTime;

    @ApiModelProperty(value = "创建时间")
    @JsonIgnore
    public Date createTime;

    @ApiModelProperty(value = "备注1")
    private String remark1;

    @ApiModelProperty(value = "备注2")
    private String remark2;

    @ApiModelProperty(value = "备注3")
    private String remark3;

}

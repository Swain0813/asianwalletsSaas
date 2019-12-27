package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "结算流水表", description = "结算流水表")
public class ClearAccountVO {

    @ApiModelProperty(value = "交易流水号")
    private String refcnceFlow;

    @ApiModelProperty(value = "交易类型")
    private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算

    @ApiModelProperty(value = "交易币种")
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")
    private Double txnamount;

    @ApiModelProperty(value = "交易手续费")
    private Double fee;

    @ApiModelProperty(value = "手续费币种")
    private String feecurrency;

    @ApiModelProperty(value = "系统添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addDatetime;

    @ApiModelProperty(value = "备注")
    private String remark;



}
package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;


@Data
@ApiModel(value = "结算审核输入实体", description = "结算审核输入实体")
public class ReviewSettleDTO {

    @ApiModelProperty(value = "批次结算审核list")
    List<ReviewSettleInfoDTO> reviewSettleInfoDTOS;

    @ApiModelProperty(value = "结算通道")
    private String settleChannel;

    @ApiModelProperty(value = "审核状态")//审核状态： 2-审核成功 3-审核失败
    private Byte reviewStatus;

    @ApiModelProperty(value = "批次交易手续费")
    private BigDecimal tradeFee;

    @ApiModelProperty(value = "结算金额")
    private BigDecimal totalSettleAmount;

    @ApiModelProperty(value = "手续费币种")
    private String feeCurrency;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "交易密码")
    private String tradePwd;

    @ApiModelProperty(value = "更改者")
    private String modifier;
}




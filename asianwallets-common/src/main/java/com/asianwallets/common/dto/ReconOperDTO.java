package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "调账操作的输入参数", description = "调账操作的输入参数")
public class ReconOperDTO {
    @ApiModelProperty(value = "id")
    private String id;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "调账类型")
    private Integer type; //1,调入，2,调出 3,冻结 4,解冻

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "币种")
    private String currency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "调账金额")
    private BigDecimal amount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "调账原因")
    private String remark;

    @ApiModelProperty(value = "入账类型 1-冻结户 2-保证金户 ")
    private Integer accountType;

    @ApiModelProperty(value = "冻结类型 1-冻结 2-预约冻结")
    private Integer freezeType;

    @ApiModelProperty(value = "资金变动类型 1-调账 2-资金冻结 3-资金解冻")
    private Byte changeType;


    public ReconOperDTO() {
    }
}

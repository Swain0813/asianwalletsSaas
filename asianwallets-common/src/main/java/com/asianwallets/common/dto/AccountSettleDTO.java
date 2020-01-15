package com.asianwallets.common.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel(value = "提款设置输入参数", description = "提款设置输入参数")
public class AccountSettleDTO {

    @NotNull(message = "50002")
    private String accountId;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "自动结算结算开关")//1-开 0-关 默认是0
    private Boolean settleSwitch;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "最小起结金额")
    private BigDecimal minSettleAmount;
}

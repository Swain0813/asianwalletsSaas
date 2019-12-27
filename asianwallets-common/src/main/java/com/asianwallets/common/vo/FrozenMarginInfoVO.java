package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel(value = "账户查询冻结与保证金输出参数", description = "账户查询冻结与保证金信息输出参数")
public class FrozenMarginInfoVO {

    @ApiModelProperty(value = "流水号")
    private String id;

    @ApiModelProperty(value = "币种")
    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "状态")// 5冻结成功 8解冻成功
    private int status;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    @ApiModelProperty(value = "备注")
    private String remark;


}

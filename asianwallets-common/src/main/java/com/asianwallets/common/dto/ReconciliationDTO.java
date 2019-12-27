package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;


@Data
@ApiModel(value = "调账查询输入实体", description = "调账查询输入实体")
public class ReconciliationDTO extends BasePageHelper {

    @ApiModelProperty(value = "调账订单号")
    private String id;

    @ApiModelProperty(value = "变动类型 1调账 2资金冻结 3资金解冻")
    private Integer changeType;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "状态")//调账状态 1-待调账 2-调账成功 3-调账失败 4-待冻结 5-冻结成功 6-冻结失败, 7-待解冻 8-解冻成功 9-解冻失败
    private String status;

    @ApiModelProperty(value = "调账金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "调账原因")
    private String remark;

}

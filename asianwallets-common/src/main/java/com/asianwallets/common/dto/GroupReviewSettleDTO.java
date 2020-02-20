package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "集团商户结算审核输入实体", description = "集团商户结算审核输入实体")
public class GroupReviewSettleDTO {

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "审核状态 1-审核成功 2-审核失败")//审核状态： 2-审核成功 3-审核失败
    private Byte reviewStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "更改者")
    private String modifier;
}




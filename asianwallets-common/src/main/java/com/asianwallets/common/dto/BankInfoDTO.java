package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-05 14:16
 **/

@Data
@ApiModel(value = "通道输入实体", description = "通道输入实体")
public class BankInfoDTO {

    @ApiModelProperty(value = "银行id")
    String bankId;
}

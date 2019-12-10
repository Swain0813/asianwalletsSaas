package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-01-30 10:12
 **/
@Data
@ApiModel(value = "审核商户产品实体", description = "审核商户产品实体")
public class AuaditProductDTO {


    @ApiModelProperty(value = "审核产品")
    public Boolean enabled;

    @ApiModelProperty(value = "备注")
    public String remarks;

    @ApiModelProperty(value = "商户产品id集合")
    public List<String> merProId;

}

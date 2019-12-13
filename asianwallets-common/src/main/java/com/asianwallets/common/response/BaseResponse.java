package com.asianwallets.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通用返回实体", description = "通用返回实体")
public class BaseResponse {

    @ApiModelProperty(value = "响应码")
    private String code;

    @ApiModelProperty(value = "响应信息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private Object data;

}



package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/5/29 16:08
 * @Description: enets回调浏览器输入实体
 */
@Data
@ApiModel(value = "enets回调浏览器输入实体", description = "enets回调浏览器输入实体")
public class EnetsOutCallbackDTO {

    @ApiModelProperty(value = "默认为1")
    private String ss;

    @ApiModelProperty(value = "")
    private EnetsCallbackDTO msg;

}

package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Pos机登录返回输出参数", description = "Pos机登录返回输出参数")
public class RedisSysUserVO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("机构编号")
    private String institutionId;

    @ApiModelProperty("机构名称")
    private String institutionName;

    @ApiModelProperty("交易密码")
    private String tradePassword;


}

package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AD3登陆接口业务参数实体", description = "AD3登陆接口业务参数实体")
public class LoginBizContentDTO {

    @ApiModelProperty(value = "1.登录 2.登出")
    private String type;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "密码 登录必填，登出可不填")
    private String password;

    @ApiModelProperty(value = "终端imei编号")
    private String imei;

    public LoginBizContentDTO() {
    }

    public LoginBizContentDTO(String type, Channel channel) {
        this.type = type;
        this.imei = channel.getExtend1();
        this.operatorId = channel.getExtend2();
        this.password = channel.getExtend3();
    }
}

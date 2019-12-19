package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CSB扫码支付接口输出实体", description = "CSB扫码支付接口输出实体")
public class AD3LoginVO {

    @ApiModelProperty(value = "返回结果")//T000表示成功，其余见响应码列表
    private String respCode;

    @ApiModelProperty(value = "返回结果描述")//success,成功或者错误提示
    private String respMsg;

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "token")
    private String token;
}

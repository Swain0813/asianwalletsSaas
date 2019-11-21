package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *操作日志的输入参数
 */
@Data
@ApiModel(value = "操作输入参数的实体", description = "操作日志输入参数的实体")
public class OperationLogDTO extends BasePageHelper {

    @ApiModelProperty(value = "操作日志id")
    private String id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户操作ip")
    private String operationIp;

    @ApiModelProperty(value = "操作类型")
    private Byte operationType;

    @ApiModelProperty(value = "功能点")
    private String functionPoint;

    @ApiModelProperty(value = "操作内容")
    private String operationContext;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "备注")
    private String remark;

}

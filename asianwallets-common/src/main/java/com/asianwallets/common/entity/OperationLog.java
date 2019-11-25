package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 操作日志表
 */
@Data
@Entity
@Table(name = "operation_log")
@ApiModel(value = "操作日志", description = "操作日志")
public class OperationLog extends BaseEntity {

    @ApiModelProperty(value = "用户名")
    @Column(name = "user_name")
    private String userName;

    @ApiModelProperty(value = "用户操作ip")
    @Column(name = "operation_ip")
    private String operationIp;

    @ApiModelProperty(value = "操作类型")
    @Column(name = "operation_type")
    private Byte operationType;

    @ApiModelProperty(value = "功能点")
    @Column(name = "function_point")
    private String functionPoint;

    @ApiModelProperty(value = "操作内容")
    @Column(name = "operation_context")
    private String operationContext;
}
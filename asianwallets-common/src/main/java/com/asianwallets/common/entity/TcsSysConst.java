package com.asianwallets.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tcs_sys_const")
@ApiModel(value = "结算常量表", description = "结算常量表")
public class TcsSysConst implements Serializable {

    @Id
    @ApiModelProperty(value = "结算流水号")
    @Column(name = "key")
    private String key;

    @ApiModelProperty(value = "")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(value = "")
    @Column(name = "systemType")
    private Integer systemType;

    @ApiModelProperty(value = "")
    @Column(name = "value")
    private String value;


}
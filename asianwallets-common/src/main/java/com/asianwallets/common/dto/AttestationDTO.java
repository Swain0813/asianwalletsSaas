package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/2/18 10:05
 * @Description: 密钥输入实体
 */
@Data
@ApiModel(value = "密钥信息输入实体", description = "密钥信息输入实体")
public class AttestationDTO {

    @ApiModelProperty(value = "uuid")
    private String id;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户公钥")
    private String merPubkey;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

}

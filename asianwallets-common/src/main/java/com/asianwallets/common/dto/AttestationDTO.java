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

    @ApiModelProperty(value = "机构code")
    private String institutionCode;

    @ApiModelProperty(value = "公钥")
    private String pubkey;

    @ApiModelProperty(value = "私钥")
    private String prikey;

    @ApiModelProperty(value = "签名")
    private String md5key;

    /*@ApiModelProperty(value = "类型")//0-机构 1-机构绑定的平台(新增机构时会产生)  3-平台(仅用于收银台)
    private Byte type;*/

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

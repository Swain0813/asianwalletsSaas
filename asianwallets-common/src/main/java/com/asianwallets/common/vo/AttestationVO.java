package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/2/18 10:05
 * @Description: 密钥输出实体
 */
@Data
@ApiModel(value = "密钥输出实体", description = "密钥输出实体")
public class AttestationVO {

    @ApiModelProperty(value = "uuid")
    private String id;

    @ApiModelProperty(value = "机构ID")
    private String institutionId;

    @ApiModelProperty(value = "商户ID")
    private String merchantId;

    @ApiModelProperty(value = "商户公钥")
    private String merchantPubkey;

    @ApiModelProperty(value = "平台公钥")
    private String platformPubkey;

    @ApiModelProperty(value = "签名")
    private String md5key;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

}

package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.util.List;


@Data
@ApiModel(value = "机构产品通道输出实体", description = "机构产品通道输出实体")
public class InstitutionProductChannelVO {

    @ApiModelProperty(value = "机构id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "产品简称")
    private String productAbbrev;

    @ApiModelProperty(value = "产品详情logo")
    private String productDetailsLogo;

    @ApiModelProperty(value = "产品打印logo")
    private String productPrintLogo;

    @ApiModelProperty(value = "产品图片")
    private String productImg;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "支付方式ID")
    private String payTypeId;

    @ApiModelProperty(value = "交易方向")
    private Byte transType;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "机构通道集合")
    private List<InstitutionChannelVO> institutionChannelVOList;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

}

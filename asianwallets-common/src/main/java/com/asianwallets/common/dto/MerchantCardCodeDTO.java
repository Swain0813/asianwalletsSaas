package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商户码牌输入实体", description = "商户码牌输入实体")
public class MerchantCardCodeDTO extends BasePageHelper {

    @ApiModelProperty(value = "静态码编号")
    private String id;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "店铺编号")
    private String shopId;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "静态码")
    private String qrcodeUrl;

    @ApiModelProperty(value = "静态码状态")
    private Boolean enabled;

    @ApiModelProperty(value = "创建开始时间")
    private String startDate;

    @ApiModelProperty(value = "创建结束时间")
    private String endDate;

    @ApiModelProperty(value = "ext1")
    private String ext1;

    @ApiModelProperty(value = "ext2")
    private String ext2;

    @ApiModelProperty(value = "ext3")
    private String ext3;

    @ApiModelProperty(value = "ext4")
    private String ext4;

    @ApiModelProperty(value = "ext5")
    private String ext5;

    @ApiModelProperty(value = "ext6")
    private String ext6;

    @ApiModelProperty(value = "ext7")
    private String ext7;
}

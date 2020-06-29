package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name="merchant_card_code")
@ApiModel(value = "商户聚合码表", description = "商户聚合码表")
public class MerchantCardCode extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "店铺编号")
    @Column(name = "shop_id")
    private String shopId;

    @ApiModelProperty(value = "店铺名称")
    @Column(name = "shop_name")
    private String shopName;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "静态码")
    @Column(name = "qrcode_url")
    private String qrcodeUrl;

    @ApiModelProperty(value = "静态码状态")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "ext1")
    @Column(name = "ext1")
    private String ext1;

    @ApiModelProperty(value = "ext2")
    @Column(name = "ext2")
    private String ext2;

    @ApiModelProperty(value = "ext3")
    @Column(name = "ext3")
    private String ext3;

    @ApiModelProperty(value = "ext4")
    @Column(name = "ext4")
    private String ext4;

    @ApiModelProperty(value = "ext5")
    @Column(name = "ext5")
    private String ext5;

    @ApiModelProperty(value = "ext6")
    @Column(name = "ext6")
    private String ext6;

    @ApiModelProperty(value = "ext7")
    @Column(name = "ext7")
    private String ext7;
}
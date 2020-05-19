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
@Table(name = "institution_product")
@ApiModel(value = "机构产品", description = "机构产品")
public class InstitutionProduct extends BaseEntity {

    @ApiModelProperty(value = "机构id")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "产品id")
    @Column(name = "product_id")
    private String productId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "产品简称")
    @Column(name = "product_abbrev")
    private String productAbbrev;

    @ApiModelProperty(value = "产品详情logo")
    @Column(name ="product_details_logo")
    private String productDetailsLogo;

    @ApiModelProperty(value = "产品打印logo")
    @Column(name ="product_print_logo")
    private String productPrintLogo;

    @ApiModelProperty(value = "产品图片")
    @Column(name = "product_img")
    private String productImg;

}

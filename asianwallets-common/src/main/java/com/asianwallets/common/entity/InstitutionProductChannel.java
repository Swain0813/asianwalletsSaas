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
@Table(name = "institution_product_channel")
@ApiModel(value = "机构产品通道", description = "机构产品通道")
public class InstitutionProductChannel extends BaseEntity {

	@ApiModelProperty(value = "机构id")
	@Column(name = "institution_id")
    private String institutionId;

	@ApiModelProperty(value = "产品id")
	@Column(name = "product_id")
    private String productId;

	@ApiModelProperty(value = "通道id")
	@Column(name = "channel_id")
    private String channelId;

	@ApiModelProperty(value = "机构名称")
	@Column(name = "institution_name")
    private String institutionName;

	@ApiModelProperty(value = "产品简称")
	@Column(name = "product_abbrev")
    private String productAbbrev;

}

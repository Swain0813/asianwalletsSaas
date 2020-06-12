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
@Table(name =  "ota_channel")
@ApiModel(value = "ota平台管理表", description = "ota平台管理表")
public class OtaChannel extends BaseEntity {

	@ApiModelProperty(value = "OTA平台名称")
	@Column(name ="system_name")
	private String systemName;

	@ApiModelProperty(value = "是否支持撤销")
	@Column(name ="cancel_default")
	private Boolean cancelDefault;

	@ApiModelProperty(value = "撤销url")
	@Column(name ="cancel_url")
	private String cancelUrl;

	@ApiModelProperty(value = "上报url")
	@Column(name ="report_url")
	private String reportUrl;

	@ApiModelProperty(value = "是否可以核销")
	@Column(name ="verification_default")
	private Boolean verificationDefault;

	@ApiModelProperty(value = "核销url")
	@Column(name ="verification_url")
	private String verificationUrl;

	@ApiModelProperty(value = "平台logo")
	@Column(name ="system_img")
	private String systemImg;

	@ApiModelProperty(value = "enabled")
	@Column(name ="enabled")
	private Boolean enabled;

	@ApiModelProperty(value = "ext1")
	@Column(name ="ext1")
	private String ext1;

	@ApiModelProperty(value = "ext2")
	@Column(name ="ext2")
	private String ext2;

	@ApiModelProperty(value = "ext3")
	@Column(name ="ext3")
	private String ext3;

	@ApiModelProperty(value = "ext4")
	@Column(name ="ext4")
	private String ext4;

	@ApiModelProperty(value = "ext5")
	@Column(name ="ext5")
	private String ext5;

	@ApiModelProperty(value = "ext6")
	@Column(name ="ext6")
	private String ext6;

	@ApiModelProperty(value = "ext7")
	@Column(name ="ext7")
	private String ext7;


}

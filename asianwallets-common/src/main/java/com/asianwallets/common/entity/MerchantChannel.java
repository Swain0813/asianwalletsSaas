package com.asianwallets.common.entity;

import java.io.Serializable;
import java.util.Date;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Data
@Entity
@Table(name =  "merchant_channel")
public class MerchantChannel extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 商户产品id
     */
	@ApiModelProperty(value = "商户产品id")
	@Column(name ="mer_pro_id")
	private String merProId;
    /**
     * 通道id
     */
	@ApiModelProperty(value = "通道银行中间表id")
	@Column(name ="cha_ban_id")
	private String chaBanId;
    /**
     * 启用禁用
     */
	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;
    /**
     * 排序
     */
	@ApiModelProperty(value = "排序")
	@Column(name ="sort")
	private String sort;


}

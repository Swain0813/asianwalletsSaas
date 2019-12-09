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
@Table(name = "product_channel")
@ApiModel(value = "产品通道中间表", description = "产品通道中间表")
public class ProductChannel extends BaseEntity {

    @ApiModelProperty(value = "产品ID")
    @Column(name = "product_id")
    private String productId;

    @ApiModelProperty(value = "通道ID")
    @Column(name = "channel_id")
    private String channelId;

}
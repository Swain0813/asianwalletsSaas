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
@Table(name = "channel_bank")
@ApiModel(value = "通道银行", description = "通道银行")
public class ChannelBank extends BaseEntity {

    @ApiModelProperty(value = "通道id")
    @Column(name = "channel_id")
    private String channelId;

    @ApiModelProperty(value = "银行id")
    @Column(name = "bank_id")
    private String bankId;

    @ApiModelProperty(value = "enabled")
    @Column(name = "enabled")
    private Boolean enabled;
}
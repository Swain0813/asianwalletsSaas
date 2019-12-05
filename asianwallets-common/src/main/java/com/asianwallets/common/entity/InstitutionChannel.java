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
@Table(name = "institution_channel")
@ApiModel(value = "机构通道", description = "机构通道")
public class InstitutionChannel extends BaseEntity {

    @ApiModelProperty(value = "机构id")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "通道id")
    @Column(name = "channel_id")
    private String channelId;

}

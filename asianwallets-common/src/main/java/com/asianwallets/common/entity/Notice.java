package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 公告表的实体类
 */
@Data
@Entity
@Table(name = "notice")
@ApiModel(value = "公告", description = "公告")
public class Notice extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "公告类别")
    @Column(name = "category")
    private String category;

    @ApiModelProperty(value = "公告标题")
    @Column(name = "title")
    private String title;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "公告排序")
    @Column(name = "rank")
    private int rank;

    @ApiModelProperty(value = "公告生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    @ApiModelProperty(value = "公告生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_date")
    private Date endDate;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "公告内容")
    @Column(name = "context")
    private String context;
}
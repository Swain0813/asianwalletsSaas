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

@Data
@Entity
@Table(name = "holidays")
@ApiModel(value = "节假日", description = "节假日")
public class Holidays extends BaseEntity {

    @ApiModelProperty(value = "国家编号")
    @Column(name = "country_code")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Column(name = "country_name")
    private String countryName;

    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "date")
    private Date date;

    @ApiModelProperty(value = "节假日名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;
}
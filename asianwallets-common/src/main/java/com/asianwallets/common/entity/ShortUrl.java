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
@Table(name = "short_url")
@ApiModel(value = "短链接", description = "短链接")
public class ShortUrl extends BaseEntity {

    @ApiModelProperty(value = "长链接")
    @Column(name = "url")
    private String url;

    @ApiModelProperty(value = "短链接")
    @Column(name = "short_url")
    private String shortUrl;

}
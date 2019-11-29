package com.asianwallets.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 功能描述:(实体基础类)
 * @createTime 2018年6月29日 上午10:55:17
 * @copyright: 上海众哈网络技术有限公司
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -6153048148601751741L;

    @Id
    @Column(name = "id")
    @ApiModelProperty(hidden = true)
    @GeneratedValue(generator="UUID")
    public String id;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    @Column(name = "creator")
    private String creator;

    @ApiModelProperty(value = "更改者")
    @Column(name = "modifier")
    private String modifier;

    @ApiModelProperty(value = "备注")
    @Column(name = "remark")
    private String remark;


}

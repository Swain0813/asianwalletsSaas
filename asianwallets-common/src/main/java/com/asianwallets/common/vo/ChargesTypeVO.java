package com.asianwallets.common.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author shenxinran
 * @Date: 2019/1/25 14:23
 * @Description: 费率管理输出实体
 */
@Data
public class ChargesTypeVO {
    private String id;

    private String rateType;

    private String rateTypeName;

    private BigDecimal guaranteedAmount;

    private BigDecimal cappingAmount;

    private BigDecimal addedValue;

    private String feePayer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String creator;

    private String modifier;

    private String remark;

    private Boolean enabled;

}

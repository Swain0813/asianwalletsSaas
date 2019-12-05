package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 产品实体
 * @author: YangXu
 * @create: 2019-03-04 14:49
 **/
@Data
@ApiModel(value = "ProductVO", description = "产品输出实体")
public class ProductVO {

    @ApiModelProperty(value = "产品id")
    private String id;

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "交易类型")
    private Byte transType;

    @ApiModelProperty(value = "支付类型")
    private String payType;

    @ApiModelProperty(value = "支付类型名称")
    private String payTypeName;

    @ApiModelProperty(value = "交易场景")
    private Byte tradeDirection;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "单笔交易限额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal limitAmount;

    @ApiModelProperty(value = "日累计交易笔数")
    private Integer dailyTradingCount;

    @ApiModelProperty(value = "日累计交易总额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyTotalAmount;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "产品图标")
    private String productImg;

    @ApiModelProperty(value = "产品详情logo")
    private String productDetailsLogo;

    @ApiModelProperty(value = "产品打印logo")
    private String productPrintLogo;

}

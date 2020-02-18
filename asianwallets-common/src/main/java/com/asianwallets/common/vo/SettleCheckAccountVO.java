package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 机构结算单表
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Data
public class SettleCheckAccountVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    public String id;

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

    /**
     * 商户编号
     */
    @ApiModelProperty(value = "商户编号")
    private String merchantId;
    /**
     * 结算时间
     */
    @ApiModelProperty(value = "结算时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "count")
    private Integer count;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;
    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;
    /**
     * 退还收单手续费
     */
    @ApiModelProperty(value = "退还收单手续费")
    private BigDecimal refundOrderFee;
    /**
     * 期初金额
     */
    @ApiModelProperty(value = "期初金额")
    private BigDecimal initialAmount;
    /**
     * 期末金额
     */
    @ApiModelProperty(value = "期末金额")
    private BigDecimal finalAmount;

    /**
     * 商户名
     */
    @ApiModelProperty(value = "商户名")
    private String merchantName;

}

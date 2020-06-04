package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "通道对账详情导出表", description = "通道对账详情导出表")
public class CheckAccountVO {

    @ApiModelProperty(value = "机构编号")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "平台订单id")
    private String uOrderId;

    @ApiModelProperty(value = "平台通道流水号")
    private String uChannelNumber;

    @ApiModelProperty(value = "上游订单id")
    private String cOrderId;

    @ApiModelProperty(value = "上游通道流水号")
    private String cChannelNumber;

    @ApiModelProperty(value = "交易类型")
    private Integer tradeType;

    @ApiModelProperty(value = "平台交易金额")
    private BigDecimal uTradeAmount;

    @ApiModelProperty(value = "平台交易币种")
    private String uTradeCurrency;

    @ApiModelProperty(value = "平台手续费")
    private BigDecimal uFee;

    @ApiModelProperty(value = "平台订单状态")
    private Byte uStatus;

    @ApiModelProperty(value = "系统订单状态")
    private Byte cStatus;

    @ApiModelProperty(value = "上游交易金额")
    private BigDecimal cTradeAmount;

    @ApiModelProperty(value = "上游交易币种")
    private String cTradeCurrency;

    @ApiModelProperty(value = "上游手续费")
    private BigDecimal cFee;

    @ApiModelProperty(value = "错误类型")
    private Integer errorType; //1,待对账；2，差错处理 3，补单  4，对账成功

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tradeTime;

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

    @ApiModelProperty(value = "对账说明")
    private String remark;

    @ApiModelProperty(value = "差错处理说明")
    private String remark1;

}
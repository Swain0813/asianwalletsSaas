package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-10 17:24
 **/
@Data
public class MerchantProductExportVO {


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "交易场景")
    private Integer tradeDirection;

    @ApiModelProperty(value = "产品名称")
    private String productAbbrev;

    @ApiModelProperty(value = "费率类型 (1-单笔费率,2-单笔定额)")
    private String rateType;

    @ApiModelProperty(value = "费率")
    private BigDecimal rate;

    @ApiModelProperty(value = "费率最小值")
    private BigDecimal minTate;

    @ApiModelProperty(value = "费率最大值")
    private BigDecimal maxTate;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;

    @ApiModelProperty(value = "状态")
    private Boolean enabled;

    @ApiModelProperty(value = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectTime;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;




}

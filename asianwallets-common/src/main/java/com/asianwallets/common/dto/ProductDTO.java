package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description: 产品DTO
 * @author: YangXu
 * @create: 2019-12-05 11:16
 **/
@Data
@ApiModel(value = "产品实体", description = "产品实体")
public class ProductDTO extends BasePageHelper {

    @ApiModelProperty(value = "产品id")
    private String productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;
    /**
     * 产品详情logo
     */
    @ApiModelProperty(value = "产品详情logo")
    private String productDetailsLogo;
    /**
     * 产品打印logo
     */
    @ApiModelProperty(value = "产品打印logo")
    private String productPrintLogo;
    /**
     * 产品图标
     */
    @ApiModelProperty(value = "产品图标")
    private String productImg;
    /**
     * 交易类型（1-收、2-付）
     */
    @ApiModelProperty(value = "交易类型（1-收、2-付）")
    private Byte transType;
    /**
     * 交易场景：1-线上 2-线下
     */
    @ApiModelProperty(value = "交易场景：1-线上 2-线下")
    private Byte tradeDirection;
    /**
     * 支付方式(银联，网银，...）
     */
    @ApiModelProperty(value = "支付方式(银联，网银，...）")
    private String payType;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "起始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}

package com.asianwallets.common.dto;


import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 订单输入实体
 * @author: XuWenQi
 * @create: 2019-02-12 15:41
 **/
@Data
@ApiModel(value = "订单输入实体", description = "订单输入实体")
public class MockOrdersDTO extends BasePageHelper {

    @ApiModelProperty(value = "商户订单ID")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "交易开始时间")
    private String startDate;

    @ApiModelProperty(value = "交易结束时间")
    private String endDate;

}
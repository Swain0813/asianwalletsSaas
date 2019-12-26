package com.asianwallets.trade.vo;

import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "机构关联信息输出实体", description = "机构关联信息输出实体")
public class OnlineMerchantVO {

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "中文名称")
    private String cnName;

    @ApiModelProperty(value = "禁用启用")
    private Boolean enabled;

    @ApiModelProperty(value = "订单信息")
    private Orders orders;

    @ApiModelProperty(value = "产品")
    private List<OnlineProductVO> productList;
}
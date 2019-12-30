package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shenxinran
 * @Date: 2019/3/19 16:58
 * @Description: 线上订单查询实体
 */
@Data
@ApiModel(value = "线上订单查询实体", description = "线上订单查询实体")
public class OnlineOrderQueryDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "机构订单号")
    private String orderNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "页码")
    public int pageNum;

    @ApiModelProperty(value = "每页条数")
    public int pageSize;

}

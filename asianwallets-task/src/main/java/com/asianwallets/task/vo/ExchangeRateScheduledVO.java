package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "汇率爬虫输出实体", description = "汇率爬虫输出实体")
public class ExchangeRateScheduledVO {

    @ApiModelProperty(value = "买入汇率")
    private String buyRate;

    @ApiModelProperty(value = "卖出汇率")
    private String saleRate;

    @ApiModelProperty(value = "发布时间")
    private String usingTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    public ExchangeRateScheduledVO() {
    }

    public ExchangeRateScheduledVO(String buyRate, String saleRate, String usingTime) {
        this.buyRate = buyRate;
        this.saleRate = saleRate;
        this.usingTime = usingTime;
    }

}

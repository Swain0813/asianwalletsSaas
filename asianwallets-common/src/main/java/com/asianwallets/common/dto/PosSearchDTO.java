package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "pos机交易打印查询实体", description = "pos机交易打印查询实体")
public class PosSearchDTO {

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "交易类型")//1-收  2-付
    private String type;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "币种")
    private String orderCurrency;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}

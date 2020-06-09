package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查询对账实体", description = "查询对账实体")
public class SearchAccountCheckDTO extends BasePageHelper {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "渠道编号")
    private String channelCode;

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "系统订单号")
    private String orderId;

    @ApiModelProperty(value = "通道订单号")
    private String channelNumber;

    @ApiModelProperty(value = "机构编号")
    private String institutionCode;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "对账状态")
    private int errorType;

    @ApiModelProperty(value = "创建者")
    private String creator;
}

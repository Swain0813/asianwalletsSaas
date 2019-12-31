package com.asianwallets.trade.dto;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shenxinran
 * @Date: 2019/3/19 16:58
 * @Description: AD3线上订单查询实体
 */
@Data
@ApiModel(value = "AD3线上订单查询实体", description = "AD3线上订单查询实体")
public class AD3OnlineOrderQueryDTO {

    @ApiModelProperty(value = "固定值v1.0")
    private String version = "v1.0";

    @ApiModelProperty(value = "开放给商户的唯一编号")
    private String merchantId;

    @ApiModelProperty(value = "收单时商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "收单时商户上送的订单时间，格式yyyyMMddHHmmss")
    private String merorderDatetime;

    @ApiModelProperty(value = "商户加密证书方式")//1为使用平台提供的密钥 2为使用自己生成的密钥
    private String merchantSignType;

    @ApiModelProperty(value = "签名")
    private String signMsg;


    public AD3OnlineOrderQueryDTO(Orders orders, String merchantOnlineCode) {
        this.merchantId = merchantOnlineCode;
        this.merOrderNo = orders.getId();
        this.merorderDatetime = DateUtil.format(orders.getReportChannelTime(), "yyyyMMddHHmmss");
        this.merchantSignType = "2";//1为使用平台提供的密钥 2为使用自己生成的密钥
    }
}

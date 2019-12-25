package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AD3线下BSC支付接口公共参数实体", description = "AD3线下BSC支付接口公共参数实体")
public class AD3BSCScanPayDTO {

    @ApiModelProperty(value = "版本号")//固定v1.0
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "CSB扫码业务参数")
    private BSCScanBizContentDTO bizContent;

    @ApiModelProperty(value = "签名")
    private String signMsg;

    //以下是非必填参数
    @ApiModelProperty(value = "通道")
    private Channel channel;

    @ApiModelProperty(value = "订单")
    private Orders orders;

    public AD3BSCScanPayDTO() {
    }

    public AD3BSCScanPayDTO(Orders orders, Channel channel) {
        this.inputCharset = AD3Constant.CHARSET_UTF_8;//编码
        this.language = AD3Constant.LANGUAGE_CN;//语言
        this.merchantId = channel.getChannelMerchantId();//商户号
        this.orders = orders;
        this.channel = channel;
    }
}

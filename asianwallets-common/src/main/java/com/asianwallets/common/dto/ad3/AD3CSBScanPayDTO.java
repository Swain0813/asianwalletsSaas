package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.constant.AD3Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AD3线下CSB支付接口公共参数实体", description = "AD3线下CSB支付接口公共参数实体")
public class AD3CSBScanPayDTO {

    @ApiModelProperty(value = "版本号")//固定v1.0
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "CSB扫码业务参数")
    private CSBScanBizContentDTO bizContent;

    @ApiModelProperty(value = "签名")
    private String signMsg;

    public AD3CSBScanPayDTO() {
    }

    public AD3CSBScanPayDTO(String merchantId) {
        this.inputCharset = AD3Constant.CHARSET_UTF_8;//编码
        this.language = AD3Constant.LANGUAGE_CN;//语言
        this.merchantId = merchantId;//商户号
    }
}

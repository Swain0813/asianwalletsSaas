package com.asianwallets.common.dto.ad3;
import com.asianwallets.common.constant.AD3Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: AD3线下退款接口实体
 * @author: YangXu
 * @create: 2019-03-14 10:33
 **/
@Data
@ApiModel(value = "AD3线下退款接口实体", description = "AD3线下退款接口实体")
public class AD3RefundDTO {

    @ApiModelProperty(value = "版本号")//固定v1.0
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "业务参数实体")
    private AD3RefundWorkDTO bizContent;

    @ApiModelProperty(value = "签名")
    private String signMsg;


    public AD3RefundDTO(String merchantCode) {
        this.inputCharset = AD3Constant.CHARSET_UTF_8;
        this.language = AD3Constant.LANGUAGE_CN;
        this.merchantId = merchantCode;
    }
}

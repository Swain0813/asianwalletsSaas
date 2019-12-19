package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.constant.AD3Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AD3登陆接口参数实体", description = "AD3登陆接口参数实体")
public class AD3LoginDTO {

    @ApiModelProperty(value = "版本号")//固定v1.0
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "AD3登陆接口业务参数实体")
    private LoginBizContentDTO bizContent;

    public AD3LoginDTO() {
    }

    public AD3LoginDTO(String merchantId, LoginBizContentDTO bizContent) {
        this.inputCharset = AD3Constant.CHARSET_UTF_8;
        this.language = AD3Constant.LANGUAGE_CN;
        this.merchantId = merchantId;
        this.bizContent = bizContent;
    }
}

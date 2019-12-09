package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 银行导出VO
 * @author: XuWenQi
 * @create: 2019-08-26 10:57
 **/
@Data
@ApiModel(value = "银行导出VO", description = "银行实银行导出VO体")
public class ExportBankVO {

    @ApiModelProperty(value = "银行编号")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行国家")
    private String bankCountry;

    @ApiModelProperty(value = "币种")
    private String bankCurrency;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建者")
    private String creator;
}

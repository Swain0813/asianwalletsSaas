package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-02 16:03
 **/
@Data
@ApiModel(value = "通道关联信息输出实体", description = "通道关联信息输出实体")
public class BankReleVantVO {

    @ApiModelProperty(value = "id")
    private String bankId;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行国家")
    private String bankCountry;

    @ApiModelProperty(value = "币种")
    private String bankCurrency;

    @ApiModelProperty(value = "银行机构编号")
    private String bankIssuerId;

    @ApiModelProperty(value = "银行图片")
    private String bankImg;
}

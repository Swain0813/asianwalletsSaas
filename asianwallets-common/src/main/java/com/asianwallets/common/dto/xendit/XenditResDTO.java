package com.asianwallets.common.dto.xendit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: Xendit通道支付资金返回实体
 * @author: YangXu
 * @create: 2019-06-19 14:16
 **/
@Data
@ApiModel(value = "Xendit通道支付资金返回实体", description = "Xendit通道支付资金返回实体")
public class XenditResDTO {

    @ApiModelProperty(value = "")
    private String user_id;

    @ApiModelProperty(value = "")
    private String external_id;

    @ApiModelProperty(value = "")
    private BigDecimal amount;

    @ApiModelProperty(value = "")
    private String bank_code;

    @ApiModelProperty(value = "")
    private String account_holder_name;

    @ApiModelProperty(value = "")
    private String disbursement_description;

    @ApiModelProperty(value = "")
    private String status;

    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "")
    private String[] email_to;

    @ApiModelProperty(value = "")
    private String[] email_cc;

    @ApiModelProperty(value = "")
    private String[] email_bcc;

}

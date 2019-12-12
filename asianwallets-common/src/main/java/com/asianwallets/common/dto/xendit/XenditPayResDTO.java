package com.asianwallets.common.dto.xendit;

import com.asianwallets.common.entity.AvailableBanks;
import com.asianwallets.common.entity.AvailableRetailOutlets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: Xendit通道收单返回实体
 * @author: YangXu
 * @create: 2019-06-20 15:16
 **/
@Data
@ApiModel(value = "Xendit通道收单返回实体", description = "Xendit通道收单返回实体")
public class XenditPayResDTO {

    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "")
    private String user_id;

    @ApiModelProperty(value = "")
    private String external_id;

    @ApiModelProperty(value = "")
    private String status;

    @ApiModelProperty(value = "")
    private String merchant_name;

    @ApiModelProperty(value = "")
    private String merchant_profile_picture_url;

    @ApiModelProperty(value = "")
    private BigDecimal amount;

    @ApiModelProperty(value = "")
    private String payer_email;

    @ApiModelProperty(value = "")
    private String description;

    @ApiModelProperty(value = "")
    private String invoice_url;

    @ApiModelProperty(value = "")
    private Date expiry_date;

    @ApiModelProperty(value = "")
    private List<AvailableBanks> available_banks;

    @ApiModelProperty(value = "")
    private List<AvailableRetailOutlets> available_retail_outlets;

    @ApiModelProperty(value = "")
    private Boolean should_exclude_credit_card;

    @ApiModelProperty(value = "")
    private Boolean should_send_email;

    @ApiModelProperty(value = "")
    private Date created;

    @ApiModelProperty(value = "")
    private Date updated;


}

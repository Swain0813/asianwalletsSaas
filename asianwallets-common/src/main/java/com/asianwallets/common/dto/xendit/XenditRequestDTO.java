package com.asianwallets.common.dto.xendit;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: XuWenQi
 * @create: 2019-06-19 11:20
 **/

@Data
@ApiModel(value = "Xendit通道请求实体", description = "Xendit通道请求实体")
public class XenditRequestDTO {

    @ApiModelProperty(value = "订单id")
    private String external_id;

    @ApiModelProperty(value = "银行code")
    private String bank_code;

    @ApiModelProperty(value = "帐户持有人的姓名")
    private String account_holder_name;

    @ApiModelProperty(value = "目的地银行帐号")
    private String account_number;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "")
    private String[] email_to;

    @ApiModelProperty(value = "")
    private String[] email_cc;

    @ApiModelProperty(value = "")
    private String[] email_bcc;


    public XenditRequestDTO() {
    }

    public XenditRequestDTO(Orders orders, Channel channel) {
        this.external_id = orders.getId();
        this.bank_code = channel.getIssuerId();
        this.account_holder_name = "swain";
        this.account_number = channel.getChannelMerchantId();
        this.description = orders.getGoodsDescription();
        this.amount = orders.getTradeAmount();
        this.description = orders.getGoodsDescription();
        String[] s = new String[1];
        s[0] = orders.getDraweeEmail();
        this.email_to = s;
        this.email_cc = s;
        this.email_bcc = s;
    }
}

package com.asianwallets.common.dto.xendit;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

/**
 * @author: XuWenQi
 * @create: 2019-06-19 11:20
 **/

@Data
@ApiModel(value = "Xendit通道请求实体", description = "Xendit通道请求实体")
public class XenditPayRequestDTO {

    @ApiModelProperty(value = "订单id")
    private String external_id;

    @ApiModelProperty(value = "邮箱")
    private String payer_email;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "是否需要发送邮件通知")
    private Boolean should_send_email;

    @ApiModelProperty(value = "虚拟账户id")
    private String callback_virtual_account_id;

    @ApiModelProperty(value = "最终用户为了在发票到期之前支付发票的持续时间")
    private Integer invoice_duration;

    @ApiModelProperty(value = "在成功支付发票后，最终用户将被重定向到的URL")
    private String success_redirect_url;

    @ApiModelProperty(value = "最终用户将在此发票到期时重定向到的URL")
    private String failure_redirect_url;

    @ApiModelProperty(value = "您帐户中可用的付款渠道选择")//如果预计此特定发票中的所有付款渠道均可用，请将此字段留空
    private String[] payment_methods;

    public XenditPayRequestDTO() {
    }

    public XenditPayRequestDTO(Orders orders, Channel channel, String success_redirect_url, String failure_redirect_url) {
        this.external_id = orders.getId();
        this.payer_email = orders.getPayerEmail();
        this.description = StringUtils.isEmpty(orders.getProductName()) ? "商品描述" : orders.getProductName();
        this.amount = orders.getChannelAmount();
        this.callback_virtual_account_id = channel.getChannelMerchantId();
        this.should_send_email = false;
        this.invoice_duration = null;
        this.success_redirect_url = success_redirect_url;
        this.failure_redirect_url = failure_redirect_url;
        this.payment_methods = new String[]{orders.getIssuerId()};
    }
}

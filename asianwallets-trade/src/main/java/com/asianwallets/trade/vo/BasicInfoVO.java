package com.asianwallets.trade.vo;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.entity.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "交易基础信息实体", description = "交易基础信息实体")
public class BasicInfoVO {

    @ApiModelProperty("商户")
    private Merchant merchant;

    @ApiModelProperty("产品")
    private Product product;

    @ApiModelProperty("商户产品")
    private MerchantProduct merchantProduct;

    @ApiModelProperty("通道")
    private Channel channel;

    @ApiModelProperty("银行名称")
    private String bankName;
}

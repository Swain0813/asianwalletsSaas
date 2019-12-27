package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shenxinran
 * @Date: 2019/4/1 11:35
 * @Description: 收银台URL输出信息
 */
@Data
public class CheckoutCounterURLVO {

    @ApiModelProperty(value = "收银台URL")
    public String checkoutCounterURL;
}

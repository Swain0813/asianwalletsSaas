package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;


@Data
@ApiModel(value = "结算审核输入List实体", description = "结算审核输入List实体")
public class ReviewSettleInfoDTO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;
}

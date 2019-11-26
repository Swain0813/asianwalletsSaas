package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel(value = "汇率输入实体", description = "汇率输入实体")
public class ExchangeRateDTO extends BasePageHelper {

    @NotNull(message = "40003")
    @ApiModelProperty(value = "本位币种")
    private String localCurrency;

    @NotNull(message = "40004")
    @ApiModelProperty(value = "目标币种")
    private String foreignCurrency;

    @NotNull(message = "40005")
    @ApiModelProperty(value = "买入汇率")
    private BigDecimal buyRate;

    @ApiModelProperty(value = "汇率id")
    private String id;

    @ApiModelProperty(value = "卖出汇率")
    private BigDecimal saleRate;

    @ApiModelProperty(value = "中间价")
    private BigDecimal middleRate;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "起始发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startUsingTime;

    @ApiModelProperty(value = "结束发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endUsingTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}

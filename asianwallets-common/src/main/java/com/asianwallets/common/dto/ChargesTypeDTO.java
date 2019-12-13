package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 费率管理输入实体
 * @author: shenxinran
 **/
@Data
@ApiModel(value = "ChargesTypeDTO", description = "费率管理输入实体")
public class ChargesTypeDTO extends BasePageHelper {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "费率类型")
    private String rateType;

    @ApiModelProperty(value = "保底金额,默认0")
    private BigDecimal guaranteedAmount;

    @ApiModelProperty(value = "封顶金额,默认99999999")
    private BigDecimal cappingAmount;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addedValue;

    @ApiModelProperty(value = "付款方")
    @NotNull(message = "付款方不能为空")
    private String feePayer;

    @ApiModelProperty(value = "创建时间")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty("语言")
    private String language;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

}

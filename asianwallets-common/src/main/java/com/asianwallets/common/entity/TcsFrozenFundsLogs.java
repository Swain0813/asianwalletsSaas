package com.asianwallets.common.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "tcs_frozenfundslogs")
@ApiModel(value = "系统冻结资金记录", description = "系统冻结资金记录")
public class TcsFrozenFundsLogs{

    @ApiModelProperty(value = "编号")
    @Column(name = "id")
    private String id;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "organId")
    private String organId;

    @ApiModelProperty(value = "商户号")
    @Column(name = "merchantId")
    private String merchantId;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merOrderNo")
    private String merOrderNo;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "txncurrency")
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "txnamount")
    private Double txnamount;

    @ApiModelProperty(value = "虚拟户编号")
    @Column(name = "mvaccountId")
    private String mvaccountId;

    @ApiModelProperty(value = "业务类型要和业务账户类型一致")
    @Column(name = "businessType")
    private Integer businessType;

    @ApiModelProperty(value = "状态")
    @Column(name = "state")
    private Integer state;//0待冻结,1加冻结,2解冻结,3-冻结失败

    @ApiModelProperty(value = "冻结时间")
    @Column(name = "frozenDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date frozenDatetime;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "updateDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDatetime;

    @ApiModelProperty(value = "解冻时间")
    @Column(name = "unfreezeDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date unfreezeDatetime;

    @ApiModelProperty(value = "冻结备注")
    @Column(name = "frozenDesc")
    private String frozenDesc;

    @ApiModelProperty(value = "解冻备注")
    @Column(name = "unfrozenDesc")
    private String unfrozenDesc;


}

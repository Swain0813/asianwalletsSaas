package com.asianwallets.common.vo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-28 14:07
 **/
@Data
public class MerchantExportVO {

    @ApiModelProperty(value = "创建时间")
    public Date createTime;


    @ApiModelProperty(value = "商户编号")
    public String id;

    @ApiModelProperty(value = "商户名称")
    private String cnName;

    @ApiModelProperty(value = "机构号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String ext5;

    @ApiModelProperty(value = "商户类型")
    private String merchantType;


    @ApiModelProperty(value = "集团主账户")
    private String groupMasterAccount;


    @ApiModelProperty(value = "代理商编号")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    private String ext6;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "地区")
    private String region;

    @ApiModelProperty(value = "拓展销售")
    private String developSales;


    @ApiModelProperty(value = "mcc")
    private String mcc;


    @ApiModelProperty(value = "公司注册号")
    private String companyRegistNumber;

    @ApiModelProperty(value = "法人姓名")
    private String legalName;


    @ApiModelProperty(value = "商户地址")
    private String institutionAdress;

    @ApiModelProperty(value = "商户电话")
    private String institutionPhone;

    @ApiModelProperty(value = "商户联系人")
    private String contactPhone;

    @ApiModelProperty(value = "商户邮箱")
    private String merchantEmail;


    @ApiModelProperty(value = "商户URL")
    private String merchantWebUrl;

    @ApiModelProperty(value = "审核状态")//1-待审核 2-审核通过 3-审核不通过
    private Byte auditStatus;

    @ApiModelProperty(value = "审核备注")
    private String remark;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

}

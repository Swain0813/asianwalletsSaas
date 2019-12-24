package com.asianwallets.common.dto.ad3;


import com.asianwallets.common.constant.AD3Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/3/5 11:08
 * @Description: AD3查询订单状态接口业务参数实体
 */
@Data
@ApiModel(value = "AD3查询订单状态接口业务参数实体", description = "AD3查询订单状态接口业务参数实体")
public class QueryOneOrderBizContentDTO {

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "查询类型")//1交易订单查询，2退款订单查询
    private Integer type;

    @ApiModelProperty(value = "商户系统订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "商户退款订单编号")//type=2时必填
    private String merchantRefundId;

    @ApiModelProperty(value = "业务类型")//1人名币业务 2跨境业务
    private String bussinesType;

    public QueryOneOrderBizContentDTO() {
    }

    public QueryOneOrderBizContentDTO(String terminalId, String operatorId, Integer type, String merOrderNo, String merchantRefundId) {
        this.terminalId = terminalId;//终端编号
        this.operatorId = operatorId;//操作员id
        this.type = type;//订单类型
        this.merOrderNo = merOrderNo;//订单编号
        this.merchantRefundId = merchantRefundId;//退款订单编号
        this.bussinesType = AD3Constant.BUSINESS_OUT;//业务类型,固定境外
    }
}

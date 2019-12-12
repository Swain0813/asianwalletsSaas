package com.asianwallets.common.dto.enets;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description: enets线下撤销订单请求实体
 * @author: XuWenQi
 * @create: 2019-06-13 13:45
 **/
@Data
@ApiModel(value = "enets线下撤销订单请求实体", description = "enets线下撤销订单请求实体")
public class EnetsCancelOrderDTO {

    private EnetsSMRequestDTO requestJsonDate;

    private String institutionOrderId;

    private String apiSecret;

    private String apiKeyId;

    private String reqIp;

    private String orderId;


    public EnetsCancelOrderDTO() {
    }

    public EnetsCancelOrderDTO(EnetsSMRequestDTO enetsSMRequestDTO, String institutionOrderId, String reqIp, String orderId) {
        this.requestJsonDate = enetsSMRequestDTO;
        this.apiSecret = "21296dd3-5bf6-4dfc-b8a2-03fbcc213b7b";
        this.apiKeyId = "b027dacd-1c13-4916-8b93-38fae6be2f80";
        this.institutionOrderId = institutionOrderId;
        this.reqIp = reqIp;
        this.orderId = orderId;
    }
}

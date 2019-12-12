package com.asianwallets.common.dto.doku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-12 11:35
 **/
@Data
@ApiModel(value = "DOKU请求实体", description = "DOKU请求实体")
public class DOKUReqDTO {

    @ApiModelProperty(value = "")
    public String key;

    @ApiModelProperty(value = "")
    public DOKURequestDTO dokuRequestDTO;

    @ApiModelProperty(value = "")
    public DOKURefundDTO dokuRefundDTO;
}

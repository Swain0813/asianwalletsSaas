package com.asianwallets.common.dto.qfpay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-18 14:44
 **/
@Data
@ApiModel(value = "QfPayRefundSerDTO返回实体", description = "QfPayRefundSerDTO返回实体")
public class QfPayRefundSerDTO {

    @ApiModelProperty(value = "成功: 200；失败: 400")
    public Integer code;

    @ApiModelProperty(value = "返回信息")
    public String message;

    @ApiModelProperty(value = "")
    public List<QfResDTO> data;


}

package com.asianwallets.common.dto.qfpay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-13 16:02
 **/
@Data
@ApiModel(value = "QfCSBResDTO返回实体", description = "QfCSBResDTO返回实体")
public class QfResDataDTO {


    @ApiModelProperty(value = "成功: 200；失败: 400")
    public Integer code;

    @ApiModelProperty(value = "返回信息")
    public String message;

    @ApiModelProperty(value = "")
    public QfResDTO data;




}

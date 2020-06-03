package com.asianwallets.common.dto.upi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 17:39
 **/
@Data
@ApiModel(value = "下载对账DTO", description = "下载对账DTO")
public class UpiDownDTO {

    @ApiModelProperty(value = "String")
    private String version;

    @ApiModelProperty(value = "交易代码")
    private String trade_code;

    @ApiModelProperty(value = "商户号")
    private String agencyId;

    @ApiModelProperty(value = "终端号")
    private String terminal_no;

    @ApiModelProperty(value = "对账日期YYYYMMDD")
    private String settle_date;

    //交易对账文件：TRAN（默认）；退款对账文件：REFUND；第二版本的交易对账文件：TRAN_V2；第二版本的退款对账文件：REFUND_V2；
    @ApiModelProperty(value = "对账文件类型")
    private String file_type;

}

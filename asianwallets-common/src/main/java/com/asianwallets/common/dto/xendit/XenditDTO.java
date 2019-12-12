package com.asianwallets.common.dto.xendit;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-19 13:46
 **/

@Data
@ApiModel(value = "Xendit通道请求实体", description = "Xendit通道请求实体")
public class XenditDTO {

    @ApiModelProperty(value = "支付资金实体")
    private XenditRequestDTO xenditRequestDTO;

    @ApiModelProperty(value = "收单实体")
    private XenditPayRequestDTO xenditPayRequestDTO;

    //------------------------------------
    @ApiModelProperty(value = "机构订单号")
    private String institutionOrderId;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public XenditDTO() {
    }

    public XenditDTO(XenditRequestDTO xenditRequestDTO, String institutionOrderId, String md5KeyStr, String reqIp, String tradeCurrency, Channel channel) {
        this.xenditRequestDTO = xenditRequestDTO;
        this.institutionOrderId = institutionOrderId;
        this.md5KeyStr = md5KeyStr;
        this.reqIp = reqIp;
        this.tradeCurrency = tradeCurrency;
        this.channel = channel;
    }


    public XenditDTO(XenditPayRequestDTO xenditPayRequestDTO, String institutionOrderId, String md5KeyStr, String reqIp, String tradeCurrency, Channel channel) {
        this.xenditPayRequestDTO = xenditPayRequestDTO;
        this.institutionOrderId = institutionOrderId;
        this.md5KeyStr = md5KeyStr;
        this.reqIp = reqIp;
        this.tradeCurrency = tradeCurrency;
        this.channel = channel;
    }
}

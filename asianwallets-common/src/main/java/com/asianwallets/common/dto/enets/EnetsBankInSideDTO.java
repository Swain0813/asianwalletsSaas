package com.asianwallets.common.dto.enets;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: XuWenQi
 * @create: 2019-06-03 10:30
 **/

@Data
@ApiModel(value = "enets网银通道请求实体", description = "enets网银通道请求实体")
public class EnetsBankInSideDTO {

    @ApiModelProperty(value = "交易类型  默认为1")
    private String ss = "1";

    @ApiModelProperty(value = "请求实体")
    private String msg;

    public EnetsBankInSideDTO() {
    }

    public EnetsBankInSideDTO(EnetsBankCoreDTO msg) {
        this.msg = JSONObject.toJSONString(msg);
    }
}

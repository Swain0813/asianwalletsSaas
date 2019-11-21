package com.asianwallets.common.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-03-20 17:10
 **/
@Data
public class HttpResponse {

    public JSONObject jsonObject;
    public Integer httpStatus;

}

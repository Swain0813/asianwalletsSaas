package com.asianwallets.trade.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import com.asianwallets.trade.dto.AD3OnlineCallbackDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
@Slf4j
@Api(description = "线上回调接口")
@RequestMapping("/onlineCallback")
public class OnlineCallbackController extends BaseController {

    @Autowired
    private Ad3Service ad3Service;

    @Autowired
    private NextPosService nextPosService;

    @ApiOperation(value = "ad3线上服务器回调接口")
    @PostMapping("/ad3ServerCallback")
    public String callback(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------线上网关回调接口参数为空----------------");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        AD3OnlineCallbackDTO ad3OnlineCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), AD3OnlineCallbackDTO.class);
        log.info("--------------------------------线上AD3服务器回调接口--------------------------------AD3OnlineCallbackDTO:{}", JSON.toJSON(ad3OnlineCallbackDTO));
//        ad3Service.callback(ad3OnlineCallbackDTO)
        return null;
    }

    @ApiOperation(value = "NextPos回调")
    @PostMapping("/nextPosCallback")
    public void nextPosCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (ArrayUtil.isEmpty(parameterMap)) {
            log.info("=================【NextPos回调】=================【回调参数记录为空】");
            return;
        }
        log.info("================【NextPos回调】================【回调参数记录】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, Object> paramMap = new HashMap<>();
        Set<String> keySet = parameterMap.keySet();
        for (String key : keySet) {
            paramMap.put(key, parameterMap.get(key)[0]);
        }
        nextPosService.nextPosCallback(paramMap, response);
    }
}

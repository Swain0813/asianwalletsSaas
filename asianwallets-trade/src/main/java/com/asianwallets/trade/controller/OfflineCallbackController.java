package com.asianwallets.trade.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.megapay.NextPosCallbackDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import com.asianwallets.trade.dto.AD3OfflineCallbackDTO;
import com.asianwallets.trade.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@RestController
@Slf4j
@Api(description = "线下回调接口")
@RequestMapping("/offlineCallback")
public class OfflineCallbackController extends BaseController {

    @Autowired
    private Ad3Service ad3Service;

    @Autowired
    private NextPosService nextPosService;

    @ApiOperation(value = "ad3线下服务器回调接口")
    @PostMapping("/ad3ServerCallback")
    public String ad3ServerCallback(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (ArrayUtil.isEmpty(parameterMap)) {
            log.info("=================【AD3线下回调接口信息记录】=================【回调参数记录为空】");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        log.info("=================【AD3线下回调接口信息记录】=================【回调参数记录】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, String> paramMap = new HashMap<>();
        Set<String> keySet = parameterMap.keySet();
        for (String key : keySet) {
            paramMap.put(key, parameterMap.get(key)[0]);
        }
        AD3OfflineCallbackDTO ad3OfflineCallbackDTO = JSON.parseObject(JSON.toJSONString(paramMap), AD3OfflineCallbackDTO.class);
        log.info("=================【AD3线下回调接口信息记录】=================【JSON解析后的回调参数记录】 ad3OfflineCallbackDTO:{}", JSON.toJSONString(ad3OfflineCallbackDTO));
        return ad3Service.ad3ServerCallback(ad3OfflineCallbackDTO);
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

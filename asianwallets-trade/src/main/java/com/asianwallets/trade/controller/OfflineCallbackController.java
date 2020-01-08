package com.asianwallets.trade.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.channels.alipay.AlipayService;
import com.asianwallets.trade.channels.enets.EnetsService;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import com.asianwallets.trade.channels.wechat.WechantService;
import com.asianwallets.trade.dto.AD3OfflineCallbackDTO;
import com.asianwallets.common.dto.ArtificialDTO;
import com.asianwallets.trade.dto.EnetsPosCallbackDTO;
import com.asianwallets.trade.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    private EnetsService enetsService;

    @Autowired
    private NextPosService nextPosService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private WechantService wechantService;

    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "ad3线下服务器回调接口")
    @PostMapping("/ad3CsbServerCallback")
    public String ad3OfflineCsbServerCallback(HttpServletRequest request) {
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
        return ad3Service.ad3OfflineCsbServerCallback(ad3OfflineCallbackDTO);
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

    @ApiOperation(value = "eNetsCSB服务器回调")
    @PostMapping("/eNetsCsbCallback")
    public ResponseEntity<Void> eNetsCsbCallback(HttpServletRequest request, HttpServletResponse response) {
        //用流的方式接收数据
        StringBuilder jsonResMsg = new StringBuilder();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonResMsg.append(line);
            }
        } catch (IOException e) {
            log.info("================【eNets线下Csb回调】================【读取回调参数异常】", e);
            return null;
        }
        String strResMsg = String.valueOf(jsonResMsg);
        log.info("================【eNets线下Csb回调】================【回调参数记录】 strResMsg: {}", JSON.toJSONString(strResMsg));
        JSONObject apiResMsgObj = JSONObject.fromObject(strResMsg);
        String stan = apiResMsgObj.getString("stan");
        String retrieval_ref = apiResMsgObj.getString("retrieval_ref");
        //结算币种
        String txn_identifier = apiResMsgObj.getString("txn_identifier");
        String response_code = apiResMsgObj.getString("response_code");
        EnetsPosCallbackDTO enetsPosCallbackDTO = new EnetsPosCallbackDTO(stan, retrieval_ref, txn_identifier, response_code);
        log.info("================【eNets线下Csb回调】================【JSON解析后的参数记录】 enetsPosCallbackDTO: {}", JSON.toJSONString(enetsPosCallbackDTO));
        return enetsService.eNetsCsbCallback(enetsPosCallbackDTO, response);
    }

    @ApiOperation(value = "aliPayCSB扫码服务器回调")
    @PostMapping("/aliPayCsbServerCallback")
    public void aliPayCsbServerCallback(HttpServletRequest request, HttpServletResponse response) {
        alipayService.aliPayCsbServerCallback(request, response);
    }

    @ApiOperation(value = "wechat扫码服务器回调")
    @PostMapping("/wechatCsbServerCallback")
    public void wechatCsbServerCallback(HttpServletRequest request, HttpServletResponse response) {
        wechantService.wechatCsbServerCallback(request, response);
    }

    @ApiOperation(value = "人工回调")
    @PostMapping("/artificialCallback")
    public BaseResponse artificialCallback(@RequestBody @ApiParam ArtificialDTO artificialDTO) {
        log.info("================【人工回调】================【输入参数记录】 artificialDTO: {}", JSON.toJSONString(artificialDTO));
        //artificialDTO.setUserName(getSysUserVO().getUsername());
        return ResultUtil.success(commonService.artificialCallback(artificialDTO));
    }
}

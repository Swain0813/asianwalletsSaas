package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.EnetsService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: enets
 * @author: YangXu
 * @create: 2019-06-03 11:40
 **/
@Controller
@Api(description = "ENETS")
@RequestMapping("/enets")
public class EnetsController extends BaseController {

    @Autowired
    private EnetsService enetsService;

    @ApiOperation(value = "eNets线上收单接口")
    @PostMapping("eNetsDebitPay")
    @ResponseBody
    public BaseResponse eNetsDebitPay(@RequestBody @ApiParam @Valid EnetsBankRequestDTO enetsBankRequestDTO) {
        return enetsService.eNetsDebitPay(enetsBankRequestDTO);
    }

    @ApiOperation(value = "eNets跳转页面接口")
    @PostMapping("sendToeNetsDebitPay")
    @CrossOrigin
    public ModelAndView sendToeNetsDebitPay(HttpServletRequest httpServletRequest) {
        ModelAndView model = new ModelAndView();
        String txnReq = httpServletRequest.getParameter("txnReq");
        String keyId = httpServletRequest.getParameter("keyId");
        String hmac = httpServletRequest.getParameter("hmac");
        Map<String, String> smap = new HashMap<String, String>();//用来签名使用的map
        smap.put("txnReq", txnReq);
        smap.put("keyId", keyId);
        smap.put("hmac", hmac);
        smap.put("payUrl", httpServletRequest.getParameter("payUrl"));

        model.addObject("map", smap);
        model.addObject("hmac", hmac);
        model.addObject("keyId", keyId);
        model.addObject("txnReq", txnReq);
        model.setViewName("sendEnets");
        return model;
    }

    @ApiOperation(value = "eNets线下收单接口")
    @PostMapping("NPSQRCodePay")
    @ResponseBody
    public BaseResponse NPSQRCodePay(@RequestBody @ApiParam @Valid EnetsOffLineRequestDTO enetsOffLineRequestDTO) {
        return enetsService.NPSQRCodePay(enetsOffLineRequestDTO);
    }

}

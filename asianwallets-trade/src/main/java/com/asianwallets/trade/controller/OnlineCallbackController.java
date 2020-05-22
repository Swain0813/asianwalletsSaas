package com.asianwallets.trade.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.channels.alipay.AlipayService;
import com.asianwallets.trade.channels.doku.DokuService;
import com.asianwallets.trade.channels.eghl.EGHLService;
import com.asianwallets.trade.channels.enets.EnetsService;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.channels.megaPay.MegaPayService;
import com.asianwallets.trade.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private EnetsService enetsService;

    @Autowired
    private EGHLService eghlService;

    @Autowired
    private MegaPayService megaPayService;

    @Autowired
    private Help2PayService help2PayService;

    @Autowired
    private DokuService dokuService;

    @Autowired
    private AlipayService alipayService;

    @ApiOperation(value = "ad3线上服务器回调接口")
    @PostMapping("/ad3OnlineServerCallback")
    public String ad3OnlineServerCallback(HttpServletRequest request) {
        log.info("------------------【线上网关Server回调】回调开始----------------");
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------【线上网关Server回调】接口参数为空----------------");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        AD3OnlineCallbackDTO ad3OnlineCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), AD3OnlineCallbackDTO.class);
        log.info("--------------------------------【线上网关Server回调】参数记录--------------------------------AD3OnlineCallbackDTO:{}", JSON.toJSON(ad3OnlineCallbackDTO));
        return ad3Service.ad3OnlineServerCallback(ad3OnlineCallbackDTO);
    }

    @ApiOperation(value = "ad3线上浏览器回调接口")
    @PostMapping("/ad3OnlineBrowserCallback")
    @CrossOrigin
    public void ad3OnlineBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("------------------【AD3线上网关Browser回调】回调开始----------------");
        //AD3 支付宝与微信无浏览器回调
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------【AD3线上网关Browser回调】参数为空----------------");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        AD3OnlineCallbackDTO aavo = JSON.parseObject(JSON.toJSONString(dtoMap), AD3OnlineCallbackDTO.class);
        log.info("------------------【AD3线上网关Browser回调】参数记录----------------AD3OnlineCallbackDTO:{}", JSON.toJSON(aavo));
        ad3Service.ad3OnlineBrowserCallback(aavo, response);
    }

    @ApiOperation(value = "enets网银浏览器回调")
    @PostMapping("/eNetsBankBrowserCallback")
    public void eNetsBankBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        //页面返回处理
        Object message = request.getParameter("message");//contains TxnRes message
        String txnRes = String.valueOf(message);
        try {
            txnRes = URLDecoder.decode(txnRes + "", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("--------------------------------enets网银浏览器回调发生异常--------------------------------", e);
        }
        log.info("--------------------------------enets网银浏览器回调接口信息记录--------------------------------enets网银浏览器回调接口回调参数记录 txnRes:{}", txnRes);
        EnetsCallbackDTO enetsCallbackDTO = null;
        //返回结果数据结构不一致的特殊处理
        if (txnRes.contains("ss")) {
            EnetsOutCallbackDTO enetsOutCallbackDTO = JSON.parseObject(txnRes, EnetsOutCallbackDTO.class);
            enetsCallbackDTO = enetsOutCallbackDTO.getMsg();
        } else {
            enetsCallbackDTO = JSON.parseObject(txnRes, EnetsCallbackDTO.class);
        }
        if (enetsCallbackDTO == null) {
            log.info("--------------------------------enets网银浏览器回调接口信息记录--------------------------------回调参数为空");
            return;
        }
        //获取签名
        String hmac = request.getParameter("hmac");
        enetsCallbackDTO.setHmac(hmac);//签名
        log.info("--------------------------------enets网银浏览器回调接口信息记录--------------------------------enets网银浏览器回调接口回调参数记录 enetsCallbackDTO:{}", JSON.toJSON(enetsCallbackDTO));
        enetsService.eNetsBankBrowserCallback(enetsCallbackDTO, txnRes, response);
    }

    @ApiOperation(value = "enets网银服务器回调")
    @PostMapping(value = "/eNetsBankServerCallback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> eNetsBankServerCallback(@RequestBody String txnRes, HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = JSONObject.fromObject(txnRes);
        log.info("=================【eNets网银服务器回调接口信息记录】=================【回调参数记录】 json: {}", JSON.toJSON(json));
        String msg = json.getString("msg");
        EnetsCallbackDTO enetsCallbackDTO = JSON.parseObject(msg, EnetsCallbackDTO.class);
        String hmac = request.getHeader("hmac");
        enetsCallbackDTO.setHmac(hmac);//签名
        log.info("=================【eNets网银服务器回调接口信息记录】=================【JSON格式化回调参数记录】 enetsCallbackDTO:{}", JSON.toJSON(enetsCallbackDTO));
        if (enetsCallbackDTO == null) {
            log.info("=================【eNets网银服务器回调接口信息记录】=================【回调参数为空】");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        return enetsService.eNetsBankServerCallback(enetsCallbackDTO, txnRes, response);
    }

    @ApiOperation(value = "enets线上扫码服务器回调")
    @PostMapping(value = "/eNetsQrCodeServerCallback")
    public ResponseEntity<Void> eNetsQrCodeServerCallback(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer jsonResMsg = null;
        BufferedReader reader = null;
        //用流的方式接收数据
        try {
            jsonResMsg = new StringBuffer();
            String line = null;
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonResMsg.append(line);
            }
        } catch (Exception e) {
            log.info("=================【eNets线上扫码服务器回调】=================【读取流数据发生错误】", e);
        }
        String strResMsg = jsonResMsg.toString();
        log.info("=================【eNets线上扫码服务器回调】=================【回调参数记录】 strResMsg:{}", strResMsg);
        JSONObject apiResMsgObj = JSONObject.fromObject(strResMsg);
        JSONObject msg = apiResMsgObj.getJSONObject("msg");
        EnetsCallbackDTO enetsCallbackDTO = JSON.parseObject(String.valueOf(msg), EnetsCallbackDTO.class);
        String hmac = request.getHeader("hmac");
        log.info("=================【eNets线上扫码服务器回调】=================【回调签名参数记录】 hmac: {}", hmac);
        enetsCallbackDTO.setHmac(hmac);//签名
        log.info("=================【eNets线上扫码服务器回调】=================【son化后的回调参数记录】 enetsCallbackDTO:{}", JSON.toJSON(enetsCallbackDTO));
        if (enetsCallbackDTO == null) {
            log.info("=================【eNets线上扫码服务器回调】=================【json化后的回调参数为空】");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("=================【eNets线上扫码服务器回调】=================【流关闭异常】", e);
            }
        }
        return enetsService.eNetsQrCodeServerCallback(enetsCallbackDTO, strResMsg, response);
    }

    @ApiOperation(value = "help2Pay浏览器回调")
    @PostMapping("/help2PayBrowserCallback")
    public void help2PayBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("--------------------------------help2Pay浏览器回调接口信息记录-------------------------------回调参数记录 parameterMap:{}", JSON.toJSON(parameterMap));
        if (parameterMap.size() == 0) {
            log.info("------------------help2Pay浏览器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        Help2PayCallbackDTO help2PayCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), Help2PayCallbackDTO.class);
        log.info("--------------------------------help2Pay浏览器回调接口信息记录-------------------------------JSON解析后的参数记录 help2PayCallbackDTO:{}", JSON.toJSON(help2PayCallbackDTO));
        help2PayService.help2PayBrowserCallback(help2PayCallbackDTO, response);
    }

    @ApiOperation(value = "help2Pay服务器回调")
    @PostMapping("/help2PayServerCallback")
    public void help2PayServerCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("--------------------------------help2Pay服务器回调接口信息记录-------------------------------回调参数记录 parameterMap:{}", JSON.toJSON(parameterMap));
        if (parameterMap.size() == 0) {
            log.info("------------------help2Pay服务器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        Help2PayCallbackDTO help2PayCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), Help2PayCallbackDTO.class);
        log.info("--------------------------------help2Pay服务器回调接口信息记录-------------------------------JSON解析后的回调参数记录 help2PayCallbackDTO:{}", JSON.toJSON(help2PayCallbackDTO));
        help2PayService.help2PayServerCallback(help2PayCallbackDTO, response);
    }


    @ApiOperation(value = "enets线上扫码浏览器回调")
    @PostMapping("/eNetsQrCodeBrowserCallback")
    public void eNetsQrCodeBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        //页面返回处理
        Object message = request.getParameter("message");//contains TxnRes message
        String txnRes = String.valueOf(message);
        try {
            txnRes = URLDecoder.decode(txnRes + "", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("-----------------------enets线上扫码浏览器回调发生异常-----------------------", e);
        }
        log.info("--------------------------------enets线上扫码浏览器回调接口信息记录--------------------------------回调参数记录 txnRes:{}", txnRes);
        EnetsCallbackDTO enetsCallbackDTO = null;
        //返回结果数据结构不一致的特殊处理
        if (txnRes.contains("ss")) {
            EnetsOutCallbackDTO enetsOutCallbackDTO = JSON.parseObject(txnRes, EnetsOutCallbackDTO.class);
            enetsCallbackDTO = enetsOutCallbackDTO.getMsg();
        } else {
            enetsCallbackDTO = JSON.parseObject(txnRes, EnetsCallbackDTO.class);
        }
        if (enetsCallbackDTO == null) {
            log.info("--------------------------------enets线上扫码浏览器回调接口信息记录--------------------------------回调参数为空");
            return;
        }
        //获取签名
        String hmac = request.getParameter("hmac");
        enetsCallbackDTO.setHmac(hmac);//签名
        log.info("--------------------------------enets线上扫码浏览器回调接口信息记录--------------------------------json化后的回调参数记录 enetsCallbackDTO:{}", JSON.toJSON(enetsCallbackDTO));
        enetsService.eNetsQrCodeBrowserCallback(enetsCallbackDTO, txnRes, response);
    }

    @ApiOperation(value = "EGHL回调服务器")
    @PostMapping("/eghlServerCallback")
    public void eghlServerCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------EGHL回调服务器参数为空----------------");
            return;
        }
        log.info("--------------------------------EGHL回调服务器接口信息记录--------------------------------参数记录 parameterMap:{}", JSON.toJSON(parameterMap));
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        EghlBrowserCallbackDTO eghlBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), EghlBrowserCallbackDTO.class);
        log.info("--------------------------------EGHL回调服务器接口信息记录--------------------------------JSON解析后的参数记录 eghlBrowserCallbackDTO:{}", JSON.toJSON(eghlBrowserCallbackDTO));
        eghlService.eghlServerCallback(eghlBrowserCallbackDTO, response);
    }

    @ApiOperation(value = "EGHL回调浏览器")
    @PostMapping("/eghlBrowserCallback")
    public void eghlBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------EGHL回调浏览器接口信息记录----------------回调参数为空");
            return;
        }
        log.info("--------------------------------EGHL回调浏览器接口信息记录--------------------------------参数记录 parameterMap:{}", JSON.toJSON(parameterMap));
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        EghlBrowserCallbackDTO eghlBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), EghlBrowserCallbackDTO.class);
        log.info("--------------------------------EGHL回调浏览器接口信息记录--------------------------------EGHL回调浏览器回调参数记录 eghlBrowserCallbackDTO:{}", JSON.toJSON(eghlBrowserCallbackDTO));
        eghlService.eghlBrowserCallback(eghlBrowserCallbackDTO, response);
    }

    @ApiOperation(value = "megaPayTHB服务器回调")
    @PostMapping("/megaPayThbServerCallback")
    public void megaPayThbServerCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------megaPayTHB服务器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        log.info("--------------------------------megaPayTHB服务器回调接口信息记录--------------------------------megaPayTHB服务器回调接口回调参数记录 dtoMap:{}", JSON.toJSON(dtoMap));
        MegaPayServerCallbackDTO megaPayServerCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), MegaPayServerCallbackDTO.class);
        log.info("--------------------------------megaPayTHB服务器回调接口信息记录--------------------------------JSON解析后的回调参数记录 megaPayServerCallbackDTO:{}", JSON.toJSON(megaPayServerCallbackDTO));
        megaPayService.megaPayThbServerCallback(megaPayServerCallbackDTO, request, response);
    }

    @ApiOperation(value = "megaPayTHB浏览器回调")
    @PostMapping("/megaPayThbBrowserCallback")
    public void megaPayThbBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------megaPayTHB浏览器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        log.info("--------------------------------megaPayTHB浏览器回调接口信息记录--------------------------------megaPayTHB浏览器回调接口回调参数记录 dtoMap:{}", JSON.toJSON(dtoMap));
        MegaPayBrowserCallbackDTO megaPayBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), MegaPayBrowserCallbackDTO.class);
        log.info("--------------------------------megaPayTHB浏览器回调接口信息记录--------------------------------json解析后的回调参数记录 megaPayBrowserCallbackDTO:{}", JSON.toJSON(megaPayBrowserCallbackDTO));
        megaPayService.megaPayThbBrowserCallback(megaPayBrowserCallbackDTO, response);
    }

    @ApiOperation(value = "megaPayIDR服务器回调")
    @PostMapping("/megaPayIdrServerCallback")
    public void megaPayIdrServerCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------megaPayIDR服务器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        log.info("--------------------------------megaPayIDR服务器回调接口信息记录--------------------------------megaPayIDR服务器回调接口回调参数记录 dtoMap:{}", JSON.toJSON(dtoMap));
        MegaPayIDRServerCallbackDTO megaPayIDRServerCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), MegaPayIDRServerCallbackDTO.class);
        log.info("--------------------------------megaPayIDR服务器回调接口信息记录--------------------------------json解析后的回调参数记录 dtoMap:{}", JSON.toJSON(megaPayIDRServerCallbackDTO));
        megaPayService.megaPayIdrServerCallback(megaPayIDRServerCallbackDTO, request, response);
    }

    @ApiOperation(value = "megaPayIDR浏览器回调")
    @PostMapping("/megaPayIdrBrowserCallback")
    public void megaPayIdrBrowserCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() == 0) {
            log.info("------------------megaPayIDR浏览器回调接口信息记录----------------回调参数为空");
            return;
        }
        HashMap<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        log.info("--------------------------------megaPayIDR浏览器回调接口信息记录--------------------------------megaPayIDR浏览器回调接口回调参数记录 dtoMap:{}", JSON.toJSON(dtoMap));
        MegaPayIDRBrowserCallbackDTO megaPayIDRBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), MegaPayIDRBrowserCallbackDTO.class);
        log.info("--------------------------------megaPayIDR浏览器回调接口信息记录--------------------------------json解析后回调参数记录 megaPayIDRBrowserCallbackDTO:{}", JSON.toJSON(megaPayIDRBrowserCallbackDTO));
        megaPayService.megaPayIdrBrowserCallback(megaPayIDRBrowserCallbackDTO, response);
    }

    @ApiOperation(value = "DOKU通道服务器回调")
    @PostMapping("/dokuServerCallback")
    public void dokuServerCallback(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("==============【DOKU服务器回调接口】==============【回调参数】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        DokuServerCallbackDTO dokuServerCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), DokuServerCallbackDTO.class);
        log.info("==============【DOKU服务器回调接口】==============【JSON解析后的回调参数记录】 dokuServerCallbackDTO:{}", JSON.toJSONString(dokuServerCallbackDTO));
        dokuService.dokuServerCallback(dokuServerCallbackDTO);
    }

    @ApiOperation(value = "DOKU通道浏览器回调")
    @PostMapping("/dokuBrowserCallback")
    public void dokuBrowserCallback(HttpServletRequest request,HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("==============【DOKU浏览器回调接口】==============【回调参数】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        DokuBrowserCallbackDTO dokuBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), DokuBrowserCallbackDTO.class);
        log.info("==============【DOKU浏览器回调接口】==============【JSON解析后的回调参数记录】 dokuBrowserCallbackDTO:{}", JSON.toJSONString(dokuBrowserCallbackDTO));
        dokuService.dokuBrowserCallback(dokuBrowserCallbackDTO,response);
    }

    @ApiOperation(value = "Alipay线上通道服务器回调")
    @PostMapping("/alipayServerCallback")
    public void alipayServerCallback(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("==============【Alipay线上通道服务器回调】==============【回调参数】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        AplipayServerCallbackDTO  aplipayServerCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), AplipayServerCallbackDTO.class);
        log.info("==============【Alipay线上通道服务器回调】==============【JSON解析后的回调参数记录】 dokuServerCallbackDTO:{}", JSON.toJSONString(aplipayServerCallbackDTO));
        alipayService.aplipayServerCallback(aplipayServerCallbackDTO,dtoMap);
    }

    @ApiOperation(value = "Alipay线上通道浏览器回调")
    @PostMapping("/alipayBrowserCallback")
    public void alipayBrowserCallback(HttpServletRequest request,HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("==============【Alipay线上通道浏览器回调】==============【回调参数】 parameterMap:{}", JSON.toJSONString(parameterMap));
        Map<String, String> dtoMap = new HashMap<>();
        Set<String> set = parameterMap.keySet();
        for (String key : set) {
            dtoMap.put(key, parameterMap.get(key)[0]);
        }
        AplipayBrowserCallbackDTO aplipayBrowserCallbackDTO = JSON.parseObject(JSON.toJSONString(dtoMap), AplipayBrowserCallbackDTO.class);
        log.info("************【Alipay线上通道浏览器回调】***********【JSON解析后的回调参数记录】 dokuBrowserCallbackDTO:{}", JSON.toJSONString(aplipayBrowserCallbackDTO));
        alipayService.aplipayBrowserCallback(aplipayBrowserCallbackDTO,response);
    }
}

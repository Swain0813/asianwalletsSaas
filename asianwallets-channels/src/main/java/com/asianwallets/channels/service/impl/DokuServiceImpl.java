package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.DokuService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-12 10:36
 **/

@Service
@Slf4j
@Transactional
public class DokuServiceImpl implements DokuService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private ChannelsConfig channelsConfig;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/12
     * @Descripate doku收单接口
     **/
    @Override
    public BaseResponse payMent(DOKUReqDTO dokuReqDTO) {
        log.info("=================【DOKU网银收单接口】=================【请求参数记录】 dokuReqDTO: {}", JSON.toJSONString(dokuReqDTO.getDokuRequestDTO()));
        int num = channelsOrderMapper.selectCountById(dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT());
        } else {
            co = new ChannelsOrder();
        }
        co.setTradeCurrency(dokuReqDTO.getDokuRequestDTO().getCURRENCY());
        co.setTradeAmount(new BigDecimal(dokuReqDTO.getDokuRequestDTO().getAMOUNT()));
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        co.setMd5KeyStr(dokuReqDTO.getKey());
        co.setId(dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT());
        co.setOrderType(AD3Constant.TRADE_ORDER);
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        /*********** 加签方法 *************/
        String clearText = dokuReqDTO.getDokuRequestDTO().getAMOUNT() + dokuReqDTO.getDokuRequestDTO().getMALLID() + dokuReqDTO.getKey()
                + dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT();
        log.info("=================【DOKU网银收单接口】=================【签名前的明文】 clearText: {}", clearText);
        String words = addSign(clearText);
        dokuReqDTO.getDokuRequestDTO().setWORDS(words);
        log.info("=================【DOKU网银收单接口】=================【请求接口参数记录】 URL: {} | dokuRequestDTO: {}", channelsConfig.getDokuPayUrl(), JSON.toJSONString(dokuReqDTO.getDokuRequestDTO()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html>\n");
        stringBuilder.append("<html>\n");
        stringBuilder.append("<head>\n");
        stringBuilder.append("<title>ASIAN WALLET</title>\n");
        stringBuilder.append("</head>\n");
        stringBuilder.append("<body>\n");
        stringBuilder.append("<form method=\"post\" id=\"frmid\" name=\"SendForm\" action=\"").append(channelsConfig.getDokuPayUrl()).append("\">\n");
        stringBuilder.append("<input type='hidden' name='AMOUNT' value='").append(dokuReqDTO.getDokuRequestDTO().getAMOUNT()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='BASKET' value='").append(dokuReqDTO.getDokuRequestDTO().getBASKET()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='CHAINMERCHANT' value='").append(dokuReqDTO.getDokuRequestDTO().getCHAINMERCHANT()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='CURRENCY' value='").append(dokuReqDTO.getDokuRequestDTO().getCURRENCY()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='EMAIL' value='").append(dokuReqDTO.getDokuRequestDTO().getEMAIL()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='MALLID' value='").append(dokuReqDTO.getDokuRequestDTO().getMALLID()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='NAME' value='").append(dokuReqDTO.getDokuRequestDTO().getNAME()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='PAYMENTCHANNEL' value='").append(dokuReqDTO.getDokuRequestDTO().getPAYMENTCHANNEL()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='PURCHASEAMOUNT' value='").append(dokuReqDTO.getDokuRequestDTO().getPURCHASEAMOUNT()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='PURCHASECURRENCY' value='").append(dokuReqDTO.getDokuRequestDTO().getCURRENCY()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='REQUESTDATETIME' value='").append(dokuReqDTO.getDokuRequestDTO().getREQUESTDATETIME()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='SESSIONID' value='").append(dokuReqDTO.getDokuRequestDTO().getSESSIONID()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='TRANSIDMERCHANT' value='").append(dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT()).append("'/>\n");
        stringBuilder.append("<input type='hidden' name='WORDS' value='").append(dokuReqDTO.getDokuRequestDTO().getWORDS()).append("'/>\n");
        stringBuilder.append("</form>\n");
        stringBuilder.append("</body>\n");
        stringBuilder.append("\t<script type=\"text/javascript\">\n" +
                "\t\n" +
                "\twindow.onload=function(){\n" +
                "      \t\t  var form=document.getElementById(\"frmid\");\n" +
                "      \t\t  form.submit();\n" +
                "    };\n</script>");
        stringBuilder.append("</html>\n");
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(stringBuilder.toString());
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        log.info("=================【DOKU网银收单接口】=================【返回参数】 baseResponse: {}", JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/13
     * @Descripate 检查交易状态
     **/
    @Override
    public BaseResponse checkStatus(DOKUReqDTO dokuReqDTO) {
        BaseResponse response = new BaseResponse();
        /*********** 加签方法 *************/
        //币种是选填，如果币种不为空需要需要拼接币种
        String str = null;
        if (StringUtils.isEmpty(dokuReqDTO.getDokuRequestDTO().getCURRENCY())) {
            str = dokuReqDTO.getDokuRequestDTO().getMALLID() + dokuReqDTO.getKey() + dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT();
        } else {
            str = dokuReqDTO.getDokuRequestDTO().getMALLID() + dokuReqDTO.getKey() + dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT() + dokuReqDTO.getDokuRequestDTO().getCURRENCY();
        }
        String encod = addSign(str);
        dokuReqDTO.getDokuRequestDTO().setWORDS(encod);
        log.info("----------------- doku查询接口----------------- DokuRequestDTO:{}", JSON.toJSONString(dokuReqDTO.getDokuRequestDTO()));
        int status = 0;
        String body = null;
        Map<String, Object> map = BeanToMapUtil.beanToMap(dokuReqDTO.getDokuRequestDTO());
        try {
            cn.hutool.http.HttpResponse execute = HttpRequest.post(channelsConfig.getCheckUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(map)
                    .timeout(20000)
                    .execute();
            status = execute.getStatus();
            body = execute.body();
            log.info("----------------- doku查询接口----------------- status:{},body:{}", status, body);
            if (AsianWalletConstant.HTTP_SUCCESS_STATUS != status || org.springframework.util.StringUtils.isEmpty(body)) {
                response.setCode(TradeConstant.HTTP_FAIL);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return response;
            }
            Map<String, String> reponseMap = XMLUtil.xmlToMap(body, "UTF-8");
            if ("0000".equals(reponseMap.get("RESPONSECODE"))) {
                response.setCode(TradeConstant.HTTP_SUCCESS);
                response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                response.setData(reponseMap);
            } else {
                response.setCode(TradeConstant.HTTP_SUCCESS);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                response.setData(reponseMap);
                return response;
            }

        } catch (Exception e) {
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        return response;

    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/13
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(DOKUReqDTO dokuReqDTO) {
        BaseResponse response = new BaseResponse();

        /*********** 加签方法 *************/
        String str = null;
        if (StringUtils.isEmpty(dokuReqDTO.getDokuRequestDTO().getCURRENCY())) {
            str = dokuReqDTO.getDokuRefundDTO().getAMOUNT() + dokuReqDTO.getDokuRefundDTO().getMALLID() + dokuReqDTO.getKey() + dokuReqDTO.getDokuRefundDTO().getREFIDMERCHANT() + dokuReqDTO.getDokuRefundDTO().getSESSIONID();
        } else {
            str = dokuReqDTO.getDokuRefundDTO().getAMOUNT() + dokuReqDTO.getDokuRefundDTO().getMALLID() + dokuReqDTO.getKey() + dokuReqDTO.getDokuRefundDTO().getREFIDMERCHANT() + dokuReqDTO.getDokuRefundDTO().getSESSIONID() + dokuReqDTO.getDokuRefundDTO().getCURRENCY();
        }
        String encod = addSign(str);
        dokuReqDTO.getDokuRequestDTO().setWORDS(encod);


        log.info("----------------- doku退款接口----------------- DokuRefundDTO:{}", JSON.toJSONString(dokuReqDTO.getDokuRefundDTO()));
        Map<String, Object> map = BeanToMapUtil.beanToMap(dokuReqDTO.getDokuRefundDTO());
        int status = 0;
        String body = null;
        try {
            cn.hutool.http.HttpResponse execute = HttpRequest.post(channelsConfig.getDokuRefundUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(map)
                    .timeout(20000)
                    .execute();
            status = execute.getStatus();
            body = execute.body();

            log.info("----------------- doku退款接口返回----------------- status:{},body :{}", status, body);
            if (AsianWalletConstant.HTTP_SUCCESS_STATUS != status || org.springframework.util.StringUtils.isEmpty(body)) {
                response.setCode(TradeConstant.HTTP_FAIL);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return response;
            }
            Map<String, String> reponseMap = XMLUtil.xmlToMap(body, "UTF-8");
            if ("0000".equals(reponseMap.get("RESPONSECODE"))) {
                response.setCode(TradeConstant.HTTP_SUCCESS);
                response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                response.setData(reponseMap);
            } else {
                response.setCode(TradeConstant.HTTP_SUCCESS);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                response.setData(reponseMap);
                return response;
            }

        } catch (Exception e) {
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/13
     * @Descripate 加签方法
     **/
    public String addSign(String str) {
        byte[] hashvalue = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(str.getBytes("UTF-8"));
            hashvalue = messageDigest.digest();
        } catch (Exception e) {
            log.info("=========== dokuRequestDTO加签异常 ================");
        }
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < hashvalue.length; i++) {
            temp = Integer.toHexString(hashvalue[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        String encod = stringBuffer.toString();
        return encod;
    }
}

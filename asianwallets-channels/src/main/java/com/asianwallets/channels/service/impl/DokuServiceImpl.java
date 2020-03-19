package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.DokuService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.security.MessageDigest;
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


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/12
     * @Descripate doku收单接口
     **/
    @Override
    public BaseResponse payMent(DOKUReqDTO dokuReqDTO) {


        /*********** 加签方法 *************/
        String str = dokuReqDTO.getDokuRequestDTO().getAMOUNT() + dokuReqDTO.getDokuRequestDTO().getMALLID() + dokuReqDTO.getKey() + dokuReqDTO.getDokuRequestDTO().getTRANSIDMERCHANT();
        String encod = addSign(str);
        dokuReqDTO.getDokuRequestDTO().setWORDS(encod);

        log.info("----------------- doku收单接口----------------- DokuRequestDTO:{}", JSON.toJSONString(dokuReqDTO.getDokuRequestDTO()));
        BaseResponse response = new BaseResponse();
        int status = 0;
        String body = null;
        Map<String, Object> map = BeanToMapUtil.beanToMap(dokuReqDTO.getDokuRequestDTO());
        try {
            cn.hutool.http.HttpResponse execute = HttpRequest.post("")
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(map)
                    .timeout(20000)
                    .execute();
            status = execute.getStatus();
            body = execute.body();
        } catch (Exception e) {
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        log.info("----------------- doku收单接口返回----------------- status:{},body:{}", status, body);
        if (AsianWalletConstant.HTTP_SUCCESS_STATUS != status || org.springframework.util.StringUtils.isEmpty(body)) {
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        response.setCode(TradeConstant.HTTP_SUCCESS);
        response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        response.setData(body);
        return response;
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
            cn.hutool.http.HttpResponse execute = HttpRequest.post("")
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
            cn.hutool.http.HttpResponse execute = HttpRequest.post("")
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

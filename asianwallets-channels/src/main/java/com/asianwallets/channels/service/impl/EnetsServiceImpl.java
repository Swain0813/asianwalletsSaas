package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.EnetsService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.HTTPUtil;
import com.asianwallets.common.utils.SignatureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: enets
 * @author: YangXu
 * @create: 2019-06-03 11:42
 **/
@Service
@Slf4j
@Transactional
public class EnetsServiceImpl implements EnetsService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eNets网银收单接口
     **/
    @Override
    public BaseResponse eNetsDebitPay(EnetsBankRequestDTO enetsBankRequestDTO) {
        log.info("-----------------eNets网银收单接口信息记录-----------------请求参数记录 enetsBankRequestDTO:{}", JSON.toJSONString(enetsBankRequestDTO));
        JSONObject jsonObject = JSONObject.parseObject(enetsBankRequestDTO.getTxnReq());
        JSONObject msg = JSONObject.parseObject(jsonObject.get("msg").toString());

        int num = channelsOrderMapper.selectCountById(msg.get("merchantTxnRef").toString());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(msg.get("merchantTxnRef").toString());
        } else {
            co = new ChannelsOrder();
        }
        co.setMerchantOrderId(enetsBankRequestDTO.getInstitutionOrderId());
        co.setTradeCurrency(msg.get("currencyCode").toString());
        co.setTradeAmount(new BigDecimal(msg.get("txnAmount").toString()));
        co.setReqIp(msg.get("ipAddress").toString());
        //co.setDraweeName(eghlRequestDTO.getCustName());
        //co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        co.setBrowserUrl(msg.get("b2sTxnEndURL").toString());
        co.setServerUrl(msg.get("s2sTxnEndURL").toString());
        //co.setDraweePhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        //co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        co.setMd5KeyStr(enetsBankRequestDTO.getMd5KeyStr());
        co.setId(msg.get("merchantTxnRef").toString());
        co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
        co.setRemark("enets网银收单交易金额需要放大100倍上送给上游通道");
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        BaseResponse response = new BaseResponse();
        enetsBankRequestDTO.setPayUrl(enetsBankRequestDTO.getChannel().getPayUrl());
        enetsBankRequestDTO.setJumpUrl(enetsBankRequestDTO.getChannel().getPayUrl());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE html>\n");
        stringBuffer.append("<html>\n");
        stringBuffer.append("<head>\n");
        stringBuffer.append("<title>ASIAN WALLET</title>\n");
        stringBuffer.append("</head>\n");
        stringBuffer.append("<body>\n");
        stringBuffer.append("<form method=\"post\" id=\"frmid\" name=\"SendForm\" action=\"" + enetsBankRequestDTO.getChannel().getPayUrl() + "\">\n");
        stringBuffer.append("<input type='hidden' name='keyId' value='" + enetsBankRequestDTO.getKeyId() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='hmac' value='" + enetsBankRequestDTO.getHmac() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='txnReq' value='" + enetsBankRequestDTO.getTxnReq() + "'/>\n");
        stringBuffer.append("</form>\n");
        stringBuffer.append("</body>\n");
        //stringBuffer.append("</html>\n");
        stringBuffer.append("\t<script type=\"text/javascript\">\n" +
                "\t\n" +
                "\twindow.onload=function(){\n" +
                "      \t\t  var form=document.getElementById(\"frmid\");\n" +
                "      \t\t  form.submit();\n" +
                "    };\n</script>");
        stringBuffer.append("</html>\n");
        response.setData(stringBuffer.toString());
        //response.setData(enetsBankRequestDTO);
        log.info("-----------------eNets网银收单接口信息记录-----------------enetsBankRequestDTO:{}", stringBuffer.toString());
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eNets线下收单接口
     **/
    @Override
    public BaseResponse NPSQRCodePay(EnetsOffLineRequestDTO enetsOffLineRequestDTO) {

        int num = channelsOrderMapper.selectCountById(enetsOffLineRequestDTO.getOrderId());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(enetsOffLineRequestDTO.getOrderId());
        } else {
            co = new ChannelsOrder();
        }
        co.setMerchantOrderId(enetsOffLineRequestDTO.getInstitutionOrderId());
        co.setTradeCurrency(enetsOffLineRequestDTO.getRequestJsonDate().getNpxData().getSourceCurrency());
        co.setTradeAmount(new BigDecimal(enetsOffLineRequestDTO.getRequestJsonDate().getTargetAmount()));
        co.setReqIp(enetsOffLineRequestDTO.getReqIp());
        co.setServerUrl(enetsOffLineRequestDTO.getRequestJsonDate().getCommunicationData().get(0).getCommunicationDestination());
        co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        co.setId(enetsOffLineRequestDTO.getOrderId());
        co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
        co.setRemark("eNets线下收单交易金额需要放大100倍上送给上游通道");

        BaseResponse baseResponse = new BaseResponse();
        log.info("-----------------eNets线下收单接口-----------------enetsOffLineRequestDTO:{}", JSON.toJSON(enetsOffLineRequestDTO));
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJsonDate = null;
        try {
            requestJsonDate = objectMapper.writeValueAsString(enetsOffLineRequestDTO.getRequestJsonDate());
        } catch (JsonProcessingException e) {
            log.error("********************eNets线下收单发生异常**********************", e.getMessage());
        }

        String sign = SignatureUtil.calculateSignature(requestJsonDate + enetsOffLineRequestDTO.getApiSecret());

        System.setProperty("https.protocols", "TLSv1.2");
        HTTPUtil httpUtil = new HTTPUtil();
        log.info("-----------------eNets线下收单 httpUtil 请求 -----------------requestJsonDate:{}，sign ：{}", requestJsonDate, sign);
        String responseString = httpUtil.postRequest(enetsOffLineRequestDTO.getChannel().getPayUrl(), requestJsonDate, httpUtil.generateJsonHeaders(sign, enetsOffLineRequestDTO.getApiKeyId()));
        log.info("-----------------eNets线下收单 httpUtil 返回-----------------responseString:{}", responseString);

        net.sf.json.JSONObject rejson = net.sf.json.JSONObject.fromObject(responseString);
        String return_mti = (String) rejson.get("mti");
        String return_txn_identifier = (String) rejson.get("txn_identifier");
        String return_process_code = (String) rejson.get("process_code");
        String return_amount = (String) rejson.get("amount");
        String return_stan = (String) rejson.get("stan");
        String return_transaction_time = (String) rejson.get("transaction_time");
        String return_transaction_date = (String) rejson.get("transaction_date");
        String return_entry_mode = (String) rejson.get("entry_mode");
        String return_condition_code = (String) rejson.get("condition_code");
        String return_institution_code = (String) rejson.get("institution_code");
        String return_response_code = (String) rejson.get("response_code");
        String return_host_tid = (String) rejson.get("host_tid");
        String return_qr_code = (String) rejson.get("qr_code");

        //信息落地到中间表
        co.setRemark1(enetsOffLineRequestDTO.getRequestJsonDate().getRetrievalRef());
        co.setRemark2(return_stan);
        co.setRemark3(return_txn_identifier);

        if ("0210".equals(return_mti)
                && "990000".equals(return_process_code)
                && enetsOffLineRequestDTO.getRequestJsonDate().getStan().equals(return_stan)
                && enetsOffLineRequestDTO.getRequestJsonDate().getTransactionTime().equals(return_transaction_time)
                && enetsOffLineRequestDTO.getRequestJsonDate().getTransactionDate().equals(return_transaction_date)
                && "000".equals(return_entry_mode)
                && "85".equals(return_condition_code)
                && enetsOffLineRequestDTO.getRequestJsonDate().getInstitutionCode().equals(return_institution_code)) {

            if ("00".equals(return_response_code)) {//响应编码  00-一次成功的交易
                baseResponse.setCode("200");
                baseResponse.setMsg("success");
                baseResponse.setData(return_qr_code);
            } else {
                baseResponse.setMsg("fail");
                baseResponse.setCode("302");
            }
        } else {
            //验证返回信息不通过
            log.info("-----------------eNets线下收单验证信息不通过-----------------enetsOffLineRequestDTO:{}", JSON.toJSON(enetsOffLineRequestDTO));
            baseResponse.setMsg("fail");
            baseResponse.setCode("302");
        }
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }
        return baseResponse;
    }
}

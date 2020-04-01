package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.EnetsService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
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

    @Autowired
    private ChannelsConfig channelsConfig;

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
        enetsBankRequestDTO.setPayUrl(channelsConfig.getENetsDebitUrl());
        enetsBankRequestDTO.setJumpUrl(channelsConfig.getENetsJumpUrl());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE html>\n");
        stringBuffer.append("<html>\n");
        stringBuffer.append("<head>\n");
        stringBuffer.append("<title>ASIAN WALLET</title>\n");
        stringBuffer.append("</head>\n");
        stringBuffer.append("<body>\n");
        stringBuffer.append("<form method=\"post\" id=\"frmid\" name=\"SendForm\" action=\"" + channelsConfig.getENetsJumpUrl() + "\">\n");
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
        response.setCode(TradeConstant.HTTP_SUCCESS);
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
    public BaseResponse eNetsPosCSBPay(EnetsOffLineRequestDTO enetsOffLineRequestDTO) {
        log.info("=================【Enets线下CSB】=================【请求参数】 enetsOffLineRequestDTO: {}", JSON.toJSONString(enetsOffLineRequestDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = enetsOffLineRequestDTO.getOrders();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setId(orders.getId());
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(enetsOffLineRequestDTO.getRequestJsonDate().getNpxData().getSourceCurrency());
            channelsOrder.setTradeAmount(new BigDecimal(enetsOffLineRequestDTO.getRequestJsonDate().getTargetAmount()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setServerUrl(enetsOffLineRequestDTO.getRequestJsonDate().getCommunicationData().get(0).getCommunicationDestination());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrder.setRemark("eNets线下收单交易金额需要放大100倍上送给上游通道");
            ObjectMapper objectMapper = new ObjectMapper();
            String requestJsonDate = null;
            try {
                requestJsonDate = objectMapper.writeValueAsString(enetsOffLineRequestDTO.getRequestJsonDate());
            } catch (JsonProcessingException e) {
                log.info("=================【Enets线下CSB】=================【json解析异常】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //生成签名
            String clearText = requestJsonDate + enetsOffLineRequestDTO.getApiSecret();
            log.info("=================【Enets线下CSB】=================【签名前的明文】 clearText: {}", clearText);
            String sign = SignatureUtil.calculateSignature(clearText);
            log.info("=================【Enets线下CSB】=================【签名后的密文】 sign: {}", sign);
            //System.setProperty("https.protocols", "TLSv1.2");
            HTTPUtil httpUtil = new HTTPUtil();
            log.info("=================【Enets线下CSB】=================【请求Enets-CSB请求参数】 requestJsonDate: {}", requestJsonDate);
            String responseString = httpUtil.postRequest(channelsConfig.getENetsPOSUrl(), requestJsonDate, httpUtil.generateJsonHeaders(sign, enetsOffLineRequestDTO.getApiKeyId()));
            log.info("=================【Enets线下CSB】=================【请求Enets-CSB响应参数】 responseString: {}", JSON.toJSONString(responseString));
            JSONObject jsonObject = JSON.parseObject(responseString);
            String return_mti = jsonObject.getString("mti");
            String return_txn_identifier = jsonObject.getString("txn_identifier");
            String return_process_code = jsonObject.getString("process_code");
            String return_amount = jsonObject.getString("amount");
            String return_stan = jsonObject.getString("stan");
            String return_transaction_time = jsonObject.getString("transaction_time");
            String return_transaction_date = jsonObject.getString("transaction_date");
            String return_entry_mode = jsonObject.getString("entry_mode");
            String return_condition_code = jsonObject.getString("condition_code");
            String return_institution_code = jsonObject.getString("institution_code");
            String return_response_code = jsonObject.getString("response_code");
            String return_host_tid = jsonObject.getString("host_tid");
            String return_qr_code = jsonObject.getString("qr_code");
            try {
                //信息落地到中间表
                channelsOrder.setRemark1(enetsOffLineRequestDTO.getRequestJsonDate().getRetrievalRef());
                channelsOrder.setRemark2(return_stan);
                channelsOrder.setRemark3(return_txn_identifier);
                channelsOrderMapper.updateByPrimaryKeySelective(channelsOrder);
            } catch (Exception e) {
                log.info("=================【Enets线下CSB】=================【更新通道订单表异常】", e);
            }
            if ("0210".equals(return_mti) && "990000".equals(return_process_code) && enetsOffLineRequestDTO.getRequestJsonDate().getStan().equals(return_stan)
                    && enetsOffLineRequestDTO.getRequestJsonDate().getTransactionTime().equals(return_transaction_time)
                    && enetsOffLineRequestDTO.getRequestJsonDate().getTransactionDate().equals(return_transaction_date)
                    && "000".equals(return_entry_mode) && "85".equals(return_condition_code)
                    && enetsOffLineRequestDTO.getRequestJsonDate().getInstitutionCode().equals(return_institution_code)) {
                if ("00".equals(return_response_code)) {
                    //响应成功
                    baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                    baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                    baseResponse.setData(return_qr_code);
                } else {
                    baseResponse.setCode(TradeConstant.HTTP_FAIL);
                    baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                }
            } else {
                log.info("=================【Enets线下CSB】=================【eNets线下收单验证信息不通过】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            log.info("=================【Enets线下CSB】=================【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }
}

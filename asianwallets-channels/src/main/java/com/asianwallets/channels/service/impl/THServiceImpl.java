package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.THService;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-07 11:01
 **/
@Slf4j
@Service
public class THServiceImpl implements THService {

    @Autowired
    private ChannelsConfig channelsConfig;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * 通华签到,获取Mac密钥明文
     *
     * @return
     */
    @Override
    public BaseResponse thSignIn(ISO8583DTO iso8583DTO) {
        log.info("===============【通华签到】===============【请求参数】 iso8583DTO:{}", JSON.toJSONString(iso8583DTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //通华提供的主密钥
        String primaryKey = "38D57B7C1979CF7910677DE5BB6A56DF";
        //商户号
        String merchNum = "852999958120501";
        //终端号
        String terminalNum = "00018644";
        //机构号,左边填充0
        String institutionNum = "000000008600005";
        //业务类型
        String businessTypes = "00000000";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(iso8583DTO, null);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华签到】===============【请求报文参数】 sendMsg:{}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华签到】===============【返回报文参数】 result:{}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华签到】===============【返回参数】 iso8583VO:{}", JSON.toJSONString(iso8583VO));
            if ("00".equals(iso8583VO.getResponseCode_39())) {
                String domain62 = iso8583VO.getReservedPrivate_62();
                String cipherText = domain62.substring(40, 56);
                String clearKey = EcbDesUtil.decode3DEA(primaryKey, cipherText).toUpperCase();
                log.info("===============【通华签到】===============【密钥明文】 clearKey: {}", clearKey);
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(iso8583VO);
            } else {
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                baseResponse.setData(iso8583VO);
            }
        } catch (Exception e) {
            log.info("===============【通华签到】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 通华CSB
     *
     * @param thDTO 通华DTO
     * @return
     */
    @Override
    public BaseResponse thCSB(ThDTO thDTO) {
        log.info("===============【通华CSB】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //商户号
        String merchNum = thDTO.getChannel().getChannelMerchantId();
        //终端号
        String terminalNum = thDTO.getChannel().getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getExtend2();
        //加密key
        String key = thDTO.getChannel().getMd5KeyStr();
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华CSB】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华CSB】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华CSB】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            if ("00".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                String additionalData_46 = iso8583VO.getAdditionalData_46();
                String[] split = additionalData_46.split("02");
                String codeUrl = NumberStringUtil.hexStr2Str(split[5]);
                log.info("===============【通华CSB】===============【解析二维码URL】 codeUrl: {}", codeUrl);
                baseResponse.setData(iso8583VO);
            } else {
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                baseResponse.setData(iso8583VO);
            }
        } catch (Exception e) {
            log.info("===============【通华CSB】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 通华BSC
     *
     * @param thDTO 通华DTO
     * @return
     */
    @Override
    public BaseResponse thBSC(ThDTO thDTO) {
        log.info("===============【通华BSC】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //商户号
        String merchNum = thDTO.getChannel().getChannelMerchantId();
        //终端号
        String terminalNum = thDTO.getChannel().getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getExtend2();
        //加密key
        String key = thDTO.getChannel().getMd5KeyStr();
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华BSC】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华BSC】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华BSC】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            if ("00".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(iso8583VO);
            } else {
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                baseResponse.setData(iso8583VO);
            }
        } catch (Exception e) {
            log.info("===============【通华BSC】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华退款
     **/
    @Override
    public BaseResponse thRefund(ThDTO thDTO) {


        //ChannelsOrder co = new ChannelsOrder();
        //co.setMerchantOrderId(aliPayRefundDTO.getPartner_trans_id());
        //co.setTradeCurrency(aliPayRefundDTO.getCurrency());
        //co.setTradeAmount(new BigDecimal(aliPayRefundDTO.getRefund_amount()));
        ////co.setReqIp(msg.get("ipAddress").toString());
        ////co.setDraweeName(eghlRequestDTO.getCustName());
        ////co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        ////co.setBrowserUrl(msg.get("b2sTxnEndURL").toString());
        ////co.setServerUrl(msg.get("s2sTxnEndURL").toString());
        ////co.setDraweePhone(eghlRequestDTO.getCustPhone());
        //co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        ////co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        ////co.setMd5KeyStr(wechaRefundDTO.getApikey());
        //co.setId(aliPayRefundDTO.getPartner_refund_id());
        //co.setOrderType(Byte.valueOf(AD3Constant.REFUND_ORDER));
        //co.setCreateTime(new Date());
        //channelsOrderMapper.insert(co);


        BaseResponse response = new BaseResponse();
        log.info("===============【通华退款接口】===============【请求参数】 iso8583DTO:{}", JSON.toJSONString(thDTO.getIso8583DTO()));
        String tdpu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //商户号
        String merchNum = "852999958120501";
        //终端号
        String terminalNum = "00018644";
        //机构号
        String institutionNum = "000000008600005";
        //业务类型
        String businessTypes = "00000000";
        //加密key
        String key = "1310DAC4FA530D4E";
        try {
            String sendMsg = tdpu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华退款接口】===============【请求报文参数】 sendMsg:{}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华退款接口】===============【返回报文参数】 result:{}", result);
            //解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华退款接口】===============【返回参数】 iso8583DTO1281:{}", JSON.toJSONString(iso8583DTO1281));

            if (iso8583DTO1281.getResponseCode_39().equals("00")) {
                response.setCode("200");
                response.setData(iso8583DTO1281);
                response.setMsg("success");
            } else {
                response.setCode("200");
                response.setData(iso8583DTO1281);
                response.setMsg("fail");
            }
        } catch (Exception e) {
            log.info("===============【通华退款接口】===============【异常】 e:{}", e);
            //请求失败
            response.setCode("300");
            response.setMsg("fail");
            return response;
        }
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/15
     * @Descripate 通华查询接口
     **/
    @Override
    public BaseResponse thQuery(ThDTO thDTO) {
        BaseResponse response = new BaseResponse();
        log.info("===============【通华查询接口】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO.getIso8583DTO()));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //商户号
        String merchNum = thDTO.getChannel().getChannelMerchantId();
        //终端号
        String terminalNum = thDTO.getChannel().getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getExtend2();
        //加密key
        String key = thDTO.getChannel().getMd5KeyStr();
        //业务类型
        String businessTypes = "00000001";
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华查询接口】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华查询接口】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华查询接口】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            if ("00".equals(iso8583VO.getResponseCode_39())) {
                //查询成功
                String[] domain46Query = iso8583VO.getAdditionalData_46().split("02");
                iso8583VO.setAdditionalData_46(domain46Query[4].replace("3",""));
                response.setData(iso8583VO);
                response.setCode(TradeConstant.HTTP_SUCCESS);
                response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            } else {
                //查询失败
                response.setCode("300");
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            log.info("===============【通华查询接口】===============【接口异常】", e);
            //请求失败
            response.setCode("300");
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        return response;
    }

}

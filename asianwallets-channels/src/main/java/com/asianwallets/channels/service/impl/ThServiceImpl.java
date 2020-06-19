package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.MerchantReportMapper;
import com.asianwallets.channels.service.ThService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * The type Th service.
 *
 * @description:
 * @author: YangXu
 * @create: 2020 -05-07 11:01
 */
@Slf4j
@Service
public class ThServiceImpl implements ThService {

    @Autowired
    private ChannelsConfig channelsConfig;

    @Autowired
    private MerchantReportMapper merchantReportMapper;

    @Autowired
    private RedisService redisService;

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
        String primaryKey = channelsConfig.getHexKey();
        //商户号
        String merchNum = iso8583DTO.getCardAcceptorIdentificationCode_42();
        //终端号
        String terminalNum = iso8583DTO.getCardAcceptorTerminalIdentification_41();
        //机构号,左边填充0,总共15位
        String institutionNum = "0000000" + iso8583DTO.getAcquiringInstitutionIdentificationCode_32();
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
                log.info("===============【通华签到】===============【银行卡密钥明文】 签到返回的62域信息: {}", domain62);
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
     * 获取key值
     *
     * @param merchantReport
     * @return
     */
    public String getThKey(MerchantReport merchantReport) {
        String institutionId = merchantReport.getInstitutionId();
        String terminalId = merchantReport.getExtend1();
        String merchantId = merchantReport.getMerchantId();
        String channelCode = merchantReport.getChannelCode();
        log.info("++++++++++++++++++++++商户获取62域缓存信息开始++++++++++++++++++++++");
        String key = JSON.parseObject(redisService.get(AsianWalletConstant.Th_SIGN_CACHE_KEY.
                        concat("_").concat(institutionId).concat("_").concat(merchantId).concat("_").concat(terminalId).concat("_").concat(channelCode)),
                String.class);
        if (StringUtils.isEmpty(key)) {
            log.info("++++++++++++++++++++++商户获取62域缓存信息 缓存不存在 调用通华ThSign签到接口++++++++++++++++++++++");
            String timeStamp = System.currentTimeMillis() + "";
            ISO8583DTO iso8583DTO = new ISO8583DTO();
            iso8583DTO.setMessageType("0800");
            iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(6, 12));
            //机构号
            iso8583DTO.setAcquiringInstitutionIdentificationCode_32(institutionId);
            //终端号
            iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
            //商户号
            iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
            iso8583DTO.setReservedPrivate_60("50" + timeStamp.substring(6, 12) + "003");
            iso8583DTO.setReservedPrivate_63("001");
            BaseResponse baseResponse = thSignIn(iso8583DTO);
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), ISO8583DTO.class);
            key = iso8583VO.getReservedPrivate_62();
            redisService.set(AsianWalletConstant.Th_SIGN_CACHE_KEY.
                    concat("_").concat(institutionId).concat("_").concat(merchantId).concat("_").concat(terminalId).concat("_").concat(channelCode), key);

        }
        log.info("++++++++++++++++++++++商户获取62域缓存信息完成++++++++++++++++++++++");
        //截取并解密 获取key
        String substring = key.substring(40, 56);
        key = Objects.requireNonNull(EcbDesUtil.decode3DEA(channelsConfig.getHexKey(), substring)).toUpperCase();
        return key;
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
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
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
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
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
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
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
        BaseResponse response = new BaseResponse();
        log.info("===============【通华退款接口】===============【请求参数】 iso8583DTO:{}", JSON.toJSONString(thDTO.getIso8583DTO()));
        String tdpu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        //业务类型
        String businessTypes = "00000000";
        //加密key
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
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
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
                iso8583VO.setAdditionalData_46(NumberStringUtil.hexStr2Str(domain46Query[4]));
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

    /**
     * 获取商户报备信息
     *
     * @param merchantId
     * @param channelCode
     * @return
     */
    public MerchantReport getMerchantReport(String merchantId, String channelCode) {
        MerchantReport merchantReport = JSON.parseObject(redisService.get(AsianWalletConstant.MERCHANT_REPORT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(channelCode)), MerchantReport.class);
        if (merchantReport == null) {
            merchantReport = merchantReportMapper.selectByChannelCodeAndMerchantId(merchantId, channelCode);
            if (merchantReport == null) {
                log.info("==================【根据商户编号和通道编号获取商户报备信息】==================【商户报备信息不存在】 merchantId: {} | channelCode: {}", merchantId, channelCode);
                return null;
            }
            redisService.set(AsianWalletConstant.MERCHANT_REPORT_CACHE_KEY.concat("_").concat(merchantId).concat("_").concat(channelCode), JSON.toJSONString(merchantReport));
        }
        log.info("==================【根据商户编号和通道编号获取商户报备信息】==================【商户报备信息】 account: {}", JSON.toJSONString(merchantReport));
        return merchantReport;
    }

    /**
     * 通华线下银行卡消费
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse thBankCard(ThDTO thDTO) {
        log.info("===============【通华线下银行卡】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华线下银行卡】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华线下银行卡】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华线下银行卡】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华线下银行卡】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 通华线下银行卡冲正
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse thBankCardReverse(ThDTO thDTO) {
        log.info("===============【通华线下银行卡冲正】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华线下银行卡冲正】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华线下银行卡冲正】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华线下银行卡冲正】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华线下银行卡冲正】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 通华线下银行卡退款
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse thBankCardRefund(ThDTO thDTO) {
        log.info("===============【通华线下银行卡退款】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华线下银行卡退款】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华线下银行卡退款】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华线下银行卡退款】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华线下银行卡退款】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse thBankCardUndo(ThDTO thDTO) {
        log.info("===============【通华线下银行卡撤销】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华线下银行卡撤销】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华线下银行卡撤销】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华线下银行卡撤销】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华线下银行卡撤销】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }


    /**
     * 预授权
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse preAuth(ThDTO thDTO) {
        log.info("===============【通华预授权】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华预授权】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华预授权】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华预授权】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华预授权】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 预授权冲正
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse preAuthReverse(ThDTO thDTO) {
        log.info("===============【通华预授权冲正】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华预授权冲正】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华预授权冲正】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华预授权冲正】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华预授权冲正】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 预授权撤销
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse preAuthRevoke(ThDTO thDTO) {
        log.info("===============【通华预授权撤销】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华预授权撤销】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华预授权撤销】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华预授权撤销】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华预授权撤销】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 预授权完成
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse preAuthComplete(ThDTO thDTO) {
        log.info("===============【通华预授权完成】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华预授权完成】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华预授权完成】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华预授权完成】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华预授权完成】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * 预授权完成撤销
     *
     * @param thDTO
     * @return
     */
    @Override
    public BaseResponse preAuthCompleteRevoke(ThDTO thDTO) {
        log.info("===============【通华预授权完成撤销】===============【请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
        String tpdu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        MerchantReport merchantReport = getMerchantReport(thDTO.getMerchantId(), thDTO.getChannel().getChannelCode());
        //商户号
        String merchNum = merchantReport.getMerchantId();
        //终端号
        String terminalNum = merchantReport.getExtend1();
        //机构号
        String institutionNum = "0000000" + thDTO.getChannel().getChannelMerchantId();
        //加密key
        String key = getThKey(merchantReport);
        log.info("----------------key----------------key:{}", key);
        //业务类型
        String businessTypes = "00000001";
        BaseResponse baseResponse = new BaseResponse();
        try {
            String sendMsg = tpdu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(thDTO.getIso8583DTO(), key);
            //计算报文长度
            String strHex2 = String.format("%04x", sendMsg.length() / 2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华预授权完成撤销】===============【请求报文参数】 sendMsg: {}", sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华预授权完成撤销】===============【返回报文参数】 result: {}", result);
            //解包
            ISO8583DTO iso8583VO = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华预授权完成撤销】===============【返回参数】 iso8583VO: {}", JSON.toJSONString(iso8583VO));
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(iso8583VO);
        } catch (Exception e) {
            log.info("===============【通华预授权完成撤销】===============【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }
}

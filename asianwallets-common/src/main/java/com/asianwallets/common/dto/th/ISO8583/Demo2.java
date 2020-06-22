package com.asianwallets.common.dto.th.ISO8583;

import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.dto.th.exception.DesCryptionException;
import com.asianwallets.common.utils.IDS;

import java.util.Arrays;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 * 消费接口
 **/
public class Demo2 {

    private static String ip = "58.248.241.169";
    private static String port = "10089";
    private static String institutionId = "000000008600005";
    private static String merchantId = "852999958120501";
    private static String terminalId = "00018644";
    private static String checkValue = "58B815045DC19B19";
    private static String key = "CDB437D90F1F1DFC";
    private static String _2 = "84B6DE23BB7B1776366C11540AA006F8";
    private static String _35 = "40F70D264C74C732288930F068C275179FE28F1D58D2A21E";


    public static void abc() throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);
       /* ISO8583DTO signInDto = new ISO8583DTO();
        signInDto.setMessageType("0200");
        signInDto.setSystemTraceAuditNumber_11(domain11);
        signInDto.setAcquiringInstitutionIdentificationCode_32("08600005");
        signInDto.setCardAcceptorTerminalIdentification_41(terminalId);
        signInDto.setCardAcceptorIdentificationCode_42(merchantId);
        signInDto.setReservedPrivate_60("50000001003");
        signInDto.setReservedPrivate_63("001");
        String isoMsg1 = ISO8583Util.packISO8583DTO(signInDto, null);*/

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("700206");//查询
//        iso8583DTO.setProcessingCode_3("700200");//主扫
//        iso8583DTO.setProcessingCode_3("400101");//被扫
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000100");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("000");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        // iso8583DTO.setAdditionalData_46("5F5206303002300202");//主扫
        iso8583DTO.setAdditionalData_46("5F5221303002020232303230303532303030303030313130363130303137383436350202");//查询
//        String scanCode = "134750495118463486";
//        iso8583DTO.setAdditionalData_46("5F52193030023002" + NumberStringUtil.str2HexStr(scanCode) + "0202");//被扫
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("0100005A");//01000001000000000
        //签到组包
//        String sendMsg1 = "6006090000"
//                + "800100000000"
//                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000000" + merchantId)
//                + isoMsg1;
//        String strHex1 = String.format("%04x", sendMsg1.length() / 2);
//        sendMsg1 = strHex1 + sendMsg1;
//        System.out.println(" ===  签到sendMsg  ====   " + sendMsg1);
//        Map<String, String> respMap1 = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg1));
//        String result1 = respMap1.get("respData");
//        System.out.println(" ====  签到result  ===   " + result1);
//        //解包
//        ISO8583DTO signInVO = ISO8583Util.unpackISO8583DTO(result1);
//        System.out.println("签到结果:" + JSON.toJSONString(signInVO));

       /* //扫码组包
        String isoMsg = ISO8583Util.packISO8583DTO(iso8583DTO, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);
        Map<String, String> respMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));*/
    }

    /**
     * 消费
     *
     * @throws Exception
     */
    public static void bank() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0200");
        dto.setProcessingCode_3("009000");
        //金额
        dto.setAmountOfTransactions_4("000000000900");
        String l = String.valueOf(System.currentTimeMillis());
        dto.setSystemTraceAuditNumber_11(l.substring(7, 13));
       /*
        不必填
        dto.setTimeOfLocalTransaction_12(DateUtil.format(new Date(),"HHmmss"));
        dto.setDateOfLocalTransaction_13(DateUtil.format(new Date(),"MMdd"));
        */
        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("00");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");
        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义 todo
                        l.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2(_2);
        dto.setTrack2Data_35(_35);

        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }


    /**
     * 冲正
     *
     * @throws Exception
     */
    public static void crrect() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0400");
        dto.setProcessingCode_3("009000");
        //金额
        dto.setAmountOfTransactions_4("000000000001");
        // 11域需要和消费相同
        dto.setSystemTraceAuditNumber_11("103533");
       /*
        不必填
        dto.setTimeOfLocalTransaction_12(DateUtil.format(new Date(),"HHmmss"));
        dto.setDateOfLocalTransaction_13(DateUtil.format(new Date(),"MMdd"));
        */
        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("00");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");
        // 冲正新增39 域
        dto.setResponseCode_39("06");
        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 原批次号
                        "009119" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2("C099123C6B0B690A651D3A4A09CDF5DA");
        dto.setTrack2Data_35("D3767BDE76EBF94EC30C73B372EDAFC33C59FFE01A182016");

        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "009119" +
                        //61.2 原交易流水号 11域
                        "103533" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0603";
        dto.setOriginalMessage_61(str61);
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }

    /**
     * 退货
     *
     * @throws Exception
     */
    public static void refund() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0220");
        dto.setProcessingCode_3("200000");
        //金额
        dto.setAmountOfTransactions_4("000000000001");
        // 11域
        dto.setSystemTraceAuditNumber_11("123789");

        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("00");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");

        // 37域 同返回的数据 消费接口未上传
        dto.setRetrievalReferenceNumber_37("000000181308");


        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 交易类型码
                "25" +
                        //60.2 原批次号
                        "157924" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2("C099123C6B0B690A651D3A4A09CDF5DA");
        dto.setTrack2Data_35("D3767BDE76EBF94EC30C73B372EDAFC33C59FFE01A182016");
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "157924" +
                        //61.2 原交易流水号 11域
                        "123964" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0617";
        dto.setOriginalMessage_61(str61);
        dto.setReservedPrivate_63("000");
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }

    /**
     * 撤销
     *
     * @throws Exception
     */
    public static void undo() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0200");
        dto.setProcessingCode_3("200000");
        //金额
        dto.setAmountOfTransactions_4("000000000001");
        // 11域 todo
        dto.setSystemTraceAuditNumber_11("785147");

        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("00");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");

        // 37域 同返回的数据 消费接口未上传 TODO
        dto.setRetrievalReferenceNumber_37("000000181045");


        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 交易类型码
                "23" +
                        //60.2 原批次号
                        "453269" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2("C099123C6B0B690A651D3A4A09CDF5DA");
        dto.setTrack2Data_35("D3767BDE76EBF94EC30C73B372EDAFC33C59FFE01A182016");
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "453269" +
                        //61.2 原交易流水号 11域
                        "785147" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0616";
        dto.setOriginalMessage_61(str61);
        dto.setReservedPrivate_63("000");
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }

    /**
     * 预授权
     *
     * @throws Exception
     */
    public static void preAuth() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        // 0100
        dto.setMessageType("0100");
        dto.setProcessingCode_3("030000");
        //金额
        dto.setAmountOfTransactions_4("000000001000");
        // 11域 todo
        String l = String.valueOf(System.currentTimeMillis());
        dto.setSystemTraceAuditNumber_11(l.substring(7, 13));
        //022 磁条 无pin
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("06");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");

        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 交易类型码
                "10" +
                        //60.2 原批次号
                        l.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2(_2);
        dto.setTrack2Data_35(_35);
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }

    /**
     * 预授权冲正
     *
     * @throws Exception
     */
    public static void preAuthReverse() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0400");
        dto.setProcessingCode_3("030000");
        //金额
        dto.setAmountOfTransactions_4("000000001000");
        // 11域需要和消费相同
        dto.setSystemTraceAuditNumber_11("480419");
        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("06");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");
        // 冲正新增39 域
        dto.setResponseCode_39("06");
        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String l = String.valueOf(System.currentTimeMillis());

        String str60 =
                //60.1 消息类型码
                "10" +
                        //60.2 原批次号 2020年6月22日 原批次号
                        "348041" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2(_2);
        dto.setTrack2Data_35(_35);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "348041" +
                        //61.2 原交易流水号 11域
                        "480419" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0622";
        dto.setOriginalMessage_61(str61);
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));

    }

    /**
     * 预授权撤销
     *
     * @throws Exception
     */
    public static void preAuthRevoke() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0100");
        dto.setProcessingCode_3("200000");
        //金额
        dto.setAmountOfTransactions_4("000000001000");
        // 11域 todo
        dto.setSystemTraceAuditNumber_11("785147");

        //022 磁条
        dto.setPointOfServiceEntryMode_22("022");
        dto.setPointOfServiceConditionMode_25("00");
        //机构号 给的测试数据
        dto.setAcquiringInstitutionIdentificationCode_32("08600005");

        // 38域 同返回的数据 授权标识应答码 TODO
        dto.setAuthorizationIdentificationResponse_38("");
        dto.setCardAcceptorTerminalIdentification_41(terminalId);
        dto.setCardAcceptorIdentificationCode_42(merchantId);
        // 156 人民币币种
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 交易类型码
                "11" +
                        //60.2 原批次号
                        "453269" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡
        dto.setProcessingCode_2(_2);
        dto.setTrack2Data_35(_35);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "453269" +
                        //61.2 原交易流水号 11域
                        "785147" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0616";
        dto.setOriginalMessage_61(str61);
        dto.setReservedPrivate_63("000");
        System.out.println("JSON.toJSONString(dto) = " + JSON.toJSONString(dto));
        String msg = ISO8583Util.packISO8583DTO(dto, key);
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000001" + merchantId)
                + msg;
        String s1 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = s1 + sendMsg;
        System.out.println("请求报文 = " + sendMsg);
        Map<String, String> requestMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));

        String result = requestMap.get("respData");
        System.out.println("返回报文 = " + result);
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println("解析结果:" + JSON.toJSONString(iso8583DTO1281));
    }

    public static void main(String[] args) throws Exception {
//        decode();
//        bank();
//        undo();
//      crrect();
//      refund();
//        preAuth();
//        preAuthReverse();

/*
        String l = String.valueOf(System.currentTimeMillis());
        System.out.println("l.substring(5,11) = " + l.substring(5, 11));
        System.out.println("l.substring(6,12) = " + l.substring(6, 12));
        System.out.println("l.substring(7,13) = " + l.substring(7, 13));*/

    }

    public static void decode() throws DesCryptionException {
        String msg = "6972BD7A34FE6C36C64D03EF0CA5994E9CD241E00773978B72FDEFBF0000000000000000F729CDAC97F8B8A1B7464BE74CDC961676E9D5FEC0F466DB";
        String substring = msg.substring(80, 112);
        String trk = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", substring).toUpperCase();
        System.out.println("trk = " + trk);
        String keyText = msg.substring(40, 56);
        String key = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", keyText).toUpperCase();
        System.out.println("key = " + key);
        String code = "4761340000000019";
        String v35 = "4761340000000019=171210114991787";
        _2 = encrypt(code, trk);
        System.out.println("++++++++++++++++++++++++++");
        _35 = encrypt(v35, trk);
    }

    private static String encrypt(String str, String trk) {
        String newStr;
        if (str.length() % 2 != 0) {
            newStr = str.length() + str + "0";
        } else {
            newStr = str.length() + str;
        }
//        System.out.println("newstr = " + newStr);
        byte[] b = NumberStringUtil.str2Bcd(newStr);
//        System.out.println("NumberStringUtil.bcd2Str(b) = " + NumberStringUtil.bcd2Str(b));
//        System.out.println("b = " + b.length);
//        System.out.println("HexUtil = " + HexUtil.encodeHexStr(b).length());
        String ery = EcbDesUtil.encode3DEA(trk, HexUtil.encodeHexStr(b)).toUpperCase();
        System.out.println("加密 = " + ery);
        String s = EcbDesUtil.decode3DEA(trk, ery).toUpperCase();
        System.out.println("解密 = " + s);
        return ery;
    }

}

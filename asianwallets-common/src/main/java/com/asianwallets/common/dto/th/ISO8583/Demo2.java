package com.asianwallets.common.dto.th.ISO8583;

import cn.hutool.core.codec.BCD;
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
    private static String key = "A85313220A42C71A";
    private static String checkValue = "58B815045DC19B19";

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

    public static void bank() throws Exception {
        ISO8583DTO dto = new ISO8583DTO();
        dto.setMessageType("0200");
        dto.setProcessingCode_3("009000");
        //金额
        dto.setAmountOfTransactions_4("000000000001");
        dto.setSystemTraceAuditNumber_11(IDS.uniqueID().toString().substring(0, 6));
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
                        "000099" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);

        //银行卡

        dto.setProcessingCode_2("CCDA07C694DE6530EE550C01D5281332");
      /*  String bankCode = "6214831211664781";
        String s = NumberStringUtil.str2HexStr(bankCode);
        String strHex2 = String.format("%04x", s.length() / 2).toUpperCase();*/

//        dto.setTrack2Data_35("376214850217415352624102200654300619897080000000");

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

    public static void main(String[] args) throws Exception {
//        bank();
//        abc();
        decode();
    }

    public static void decode() throws DesCryptionException {
       /* String aa = "8962FD8A1CE09D01D79D45C465123111DC59D175C9858D3324606C670000000000000000BDCB26A44A173724EC52D8507CA096F4B730280488D6A311";
        aa.substring(80, 96);
        String trk = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", aa.substring(80, 96)).toUpperCase();
        System.out.println("trk = " + trk);
        String cipherText = aa.substring(40, 56);
        String trk = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", cipherText).toUpperCase();
        System.out.println("trk = " + trk);*/

        /*
        trk = E148273CB8F580CA
        key = A85313220A42C71A
         */

        String code = "6214850217415352";
        String var35 = "6214850217415352=24102200654300619897";
        String newStr = code.length() + code;
        System.out.println("未拼接 = " + newStr);
//        newStr = newStr + "000000";
//        newStr = newStr + "0";
        System.out.println("拼接0 = " + newStr);
        System.out.println("拼接0 = " + newStr.length());
        byte[] bytes = BCD.strToBcd(newStr);
        String trk = "E148273CB8F580CA";
        String ery = EcbDesUtil.encode3DEA(HexUtil.encodeHexStr(trk), HexUtil.encodeHexStr(bytes)).toUpperCase();
        System.out.println("加密 = " + ery);
//        String s9 = DigestUtils.md5Hex(trk).toUpperCase();
//        System.out.println("32位16进制TRK = " + s9);
        String s = EcbDesUtil.decode3DEA(HexUtil.encodeHexStr(trk), ery).toUpperCase();
        System.out.println("解密 = " + s);


    }

    public static String DecimaltoBcd(String str) {
        String b_num = "";
        for (int i = 0; i < str.length(); i++) {

            String b = Integer.toBinaryString(Integer.parseInt(str.valueOf(str.charAt(i))));

            int b_len = 4 - b.length();

            for (int j = 0; j < b_len; j++) {
                b = "0" + b;
            }
            b_num += b;
        }

        return b_num;
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }


}

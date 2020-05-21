package com.asianwallets.common.dto.th.ISO8583;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.utils.IDS;

import java.util.Arrays;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class Demo {

    private static String ip = "58.248.241.169";
    private static String port = "10089";
    private static String institutionId = "000000008600005";
    private static String merchantId = "852999958120501";
    private static String terminalId = "00018644";
    private static String key = "1310DAC4FA530D4E";
    private static String checkValue = "58B815045DC19B19";

    public static void main(String[] args) throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);
        ISO8583DTO signInDto = new ISO8583DTO();
        signInDto.setMessageType("0800");
        signInDto.setSystemTraceAuditNumber_11(domain11);
        signInDto.setAcquiringInstitutionIdentificationCode_32("08600005");
        signInDto.setCardAcceptorTerminalIdentification_41(terminalId);
        signInDto.setCardAcceptorIdentificationCode_42(merchantId);
        signInDto.setReservedPrivate_60("50000001003");
        signInDto.setReservedPrivate_63("001");
        String isoMsg1 = ISO8583Util.packISO8583DTO(signInDto, null);

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

        //扫码组包
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
        System.out.println(Arrays.toString(split));
    }
}

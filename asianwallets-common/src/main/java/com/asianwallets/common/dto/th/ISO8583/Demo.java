package com.asianwallets.common.dto.th.ISO8583;

import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;

import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class Demo {

    private static String ip = "58.248.241.169";
    private static String port = "10089";
    private static String institutionId = "000008600005";
    private static String merchantId = "852999958120501";
    private static String terminalId = "00018644";
    private static String key = "38D57B7C1979CF7910677DE5BB6A56DF";

    public static void main(String[] args) throws Exception {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("009000");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000108");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(IDS.uniqueID().toString().substring(0, 6));
        //受卡方所在地时间HHmmss
        iso8583DTO.setTimeOfLocalTransaction_12(DateToolUtils.getReqTimeHHmmss());
        //受卡方所在地日期MMdd
        iso8583DTO.setDateOfLocalTransaction_13(DateToolUtils.getReqTimeMMdd());
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(institutionId);
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("22000002000600");
        String isoMsg = ISO8583Util.packISO8583DTO(iso8583DTO, key);
        //组包
        String sendMsg = "6006090000"
                + "800100000000"
                + NumberStringUtil.str2HexStr(merchantId + terminalId + institutionId + "00000000" + merchantId)
                + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2);
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  sendMsg  ====   " + sendMsg);
        Map<String, String> respMap = ISO8583Util.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
        System.out.println(iso8583DTO1281.toString());

    }


    //public static void main(String[] args) throws Exception {
    //
    //    ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO("0087600609000080010000000038353239393939353831323035303130303031383634343030303030303030383630303030353030303030303030383532393939393538313230353031303030303030353708000020000100c00012198124110000860000530303031383634343835323939393935383132303530310011500000010030003303031");
    //    System.out.println(iso8583DTO1281.toString());
    //}
}

package com.asianwallets.common.dto.th.ISO8583;

import jdk.nashorn.internal.objects.NativeUint8Array;

import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class demo {


    public static void main(String[] args) {

        String IP = "58.248.241.169";
        String port = "10089";
        String reqCharset = "UTF-8";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11("198124");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("00008600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setReservedPrivate_60("50000001003");
        iso8583DTO.setReservedPrivate_63("001");


        String sendMsg;
        try {
             //组包
            sendMsg = "6006090000"+"800100000000"+NumberStringUtil.str2HexStr("8529999581205010001864400000000860000500000000")
                    +NumberStringUtil.str2HexStr("852999958120501")+ISO8583Util.packISO8583DTO(iso8583DTO);
            String strHex2 = String.format("%04x",sendMsg.length()/2);
            sendMsg = strHex2 + sendMsg;
            System.out.println(" ===  sendMsg  ====   "+sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(IP, port, NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            System.out.println(" ====  result  ===   "+result);
             //解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
            System.out.println(iso8583DTO1281.toString());
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    //public static void main(String[] args) throws Exception {
    //
    //    ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO("0087600609000080010000000038353239393939353831323035303130303031383634343030303030303030383630303030353030303030303030383532393939393538313230353031303030303030353708000020000100c00012198124110000860000530303031383634343835323939393935383132303530310011500000010030003303031");
    //    System.out.println(iso8583DTO1281.toString());
    //}
}

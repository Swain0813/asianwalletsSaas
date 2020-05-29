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
    private static String key = "C5DA676A42D45065";

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

        dto.setProcessingCode_2("44a27bca5d37e4c4cf485489d873b037aa8670484ad985ae");
      /*  String bankCode = "6214831211664781";
        String s = NumberStringUtil.str2HexStr(bankCode);
        String strHex2 = String.format("%04x", s.length() / 2).toUpperCase();*/

//        dto.setTrack2Data_35("44AF840EA48CDE4B0AE3DEDBF618A12D9B723906826F084DDB0523193377DCFE6D3ACCE6164149F8");

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
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));
    }

    public static void main(String[] args) throws Exception {
//        bank();
        decode();
    }

    public static void decode() throws DesCryptionException {
       /* String aa = "00C6600000060980010200000038353239393939353831323035303130303031383634343030303030303030383630303030353030303030303030383532393939393538313230353031303030303030353208100020000102C000161590650808600005303030303031383634343835323939393935383132303530310011506481330030006049B06FC0050F5D8926A7E30C4E42284E1D735367459932C75A7D5AF90000000000000000B7DBD2D43CA94C0A763C6714995523E5892D43360C89FBD80003303031";
        aa.substring(80, 96);
        String trk = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", aa.substring(80, 96)).toUpperCase();
        System.out.println("trk = " + trk);
        String cipherText = aa.substring(40, 56);
        String key = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", cipherText).toUpperCase();
        System.out.println("key = " + key);
        *//*
        trk = BE1FF76FF7796F11
        key = C5DA676A42D45065
         */

        String asc = "6214850217415352";
        String s = EcbDesUtil.encode3DEA(HexUtil.encodeHexStr("BE1FF76FF7796F11"), HexUtil.encodeHexStr(asc)).toUpperCase();
        String s2 = EcbDesUtil.encode3DEA(HexUtil.encodeHexStr("BE1FF76FF7796F11"), HexUtil.encodeHexStr("6214850217415352=24102200654300619897")).toUpperCase();
        System.out.println("35 = " + s2);
        System.out.println("bankCode = " + s);

    }

}

package com.asianwallets.common.dto.th.ISO8583;

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
    private static String key = "04AFFF774377AEDE";
    private static String checkValue = "58B815045DC19B19";

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

        dto.setProcessingCode_2("878524D4814750FD1FB1C1527B4C601D");
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
        /*String aa = "ED2D1554D2B1A86DA94D05D09362396DE0351C3F739495CB3D998C6900000000000000002F5EDE6FB6AA73DB38B42BE1F90EEF0EF690842287D3E2EE";
        // B6AA73DB38B42BE1F90EEF0EF690842287D3E2EE
        aa.substring(80, 96);
        String trk = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", aa.substring(80, 96)).toUpperCase();
        System.out.println("trk = " + trk);
        String cipherText = aa.substring(40, 56);
        String key = EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", cipherText).toUpperCase();
        System.out.println("key = " + key);
        *//*
        trk = 7236BB4ADAD38846
        key = 04AFFF774377AEDE
         */

//
//        String aaa = EcbDesUtil.encode3DEA(HexUtil.encodeHexStr("7236BB4ADAD38846"), HexUtil.encodeHexStr("24102200654300619897")).toUpperCase();
//        System.out.println(aaa);
//        String str = "6214831211664781";

        /* String s = NumberStringUtil.str2HexStr(str);
        System.out.println("s = " + s);
        System.out.println(BCD.strToBcd(NumberStringUtil.stringToAscii(str)));
        String aaa = String.format("%04x", s.length() / 2).toUpperCase();
        System.out.println("aaa = " + aaa+s);*/

        String asc = "6214850217415352";
//        NumberStringUtil.
//        asc = asc.length() + asc;
//        asc = asc + "000000";
        System.out.println("asc = " + asc);
//        byte[] encrypt = SecureUtil.desede(HexUtil.decodeHex("7236BB4ADAD38846")).encrypt(asc);
//        System.out.println("ISOUtil.bytesToHexString(encrypt) = " + ISOUtil.bytesToHexString(encrypt));

     /*   String content = asc;
        byte[] key = HexUtil.decodeHex("7236BB4ADAD38846");
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, key);

        //加密
        byte[] encrypt = des.encrypt(content);
        //解密
        byte[] decrypt = des.decrypt(encrypt);

        //加密为16进制字符串（Hex表示）
        String encryptHex = des.encryptHex(content);
        System.out.println("encryptHex = " + encryptHex);
        //解密为字符串
        String decryptStr = des.decryptStr(encryptHex);*/

        System.out.println(NumberStringUtil.str2HexStr("U2FsdGVkX18ERDOiO5jJ67OVAsbHGu6E6pGEGAS919+42dcvQV870A=="));

        String s = EcbDesUtil.encode3DEA(NumberStringUtil.str2HexStr("7236BB4ADAD38846"), NumberStringUtil.str2HexStr(asc));
        System.out.println("s1 = " + s);
        byte[] bytes1 = DesUtil.doubleDesDecrypt("7236BB4ADAD38846".getBytes(), asc.getBytes());
        System.out.println("s2 = " + ISOUtil.bytesToHexString(bytes1));
//        String s1 = String.format("%04x", asc.length() / 2).toUpperCase();
    }


   /* //加密
    public String getEncryptedCardNo(String str) {
        str = str.length() + str;
        while (str.length() < 32) {
            str += "0";
        }
        byte[] bcdWlen = Util.toBCD(str);
        System.err.println("----str-----" + str);

        try {
            byte[] des = api.calcDES((byte) I_ADK, bcdWlen, 1);
            System.err.println("des---" + HEX.bytesToHex(des));
            return HEX.bytesToHex(des);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }*/
}

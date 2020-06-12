package com.asianwallets.common.dto.upi.iso;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ISO8583Util;
import com.asianwallets.common.dto.th.ISO8583.NumberStringUtil;
import com.asianwallets.common.utils.IDS;

import java.util.Arrays;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-10 14:49
 **/
public class Demo {

    //AW3 商户终端参数: POS商户联调(自带机)
    //商户号: 000000000003421 (测试环境)
    //终端号: 00001903 (测试环境)
    //主密钥索引: 002
    //        210.48.142.168 6000 走网控（主机）
    //        210.48.142.168 7000 走网控（备机）
    //TPDU NII:
    //        006(测试环境)  6000060000
    //        007(生产环境)  6000070000
    private static String ip = "210.48.142.168";
    private static String port = "7000";
    private static String merchantId = "000000000003421";
    private static String terminalId = "00001903";
    private static String key_62 = "C80C9C5AEF671BB7AE63D50DDA0EE5FD1DD7DD2400E0A435AC12A51F50850E45DF0EF070195A1B1E3976E398D4F5A66F0E1A6D8602E218491E186CB1";
    private static String key = "861B7FBD78A6E196";

    public static void main(String[] args) throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        //自定义域
        //iso8583DTO.setReservedPrivate_60("9600000240000");//01000001000000000
        ////iso8583DTO.setReservedPrivate_62("9F0605DF000000039F220101");
        iso8583DTO.setReservedPrivate_60("99000002003");//01000001000000000
        iso8583DTO.setReservedPrivate_62("DF9981805A9559A5F684F3988224B4FC8C45934C20994D27DF777039BC0B9D2D32B097DC4DFD1908875D58EEFBFF44B10D12B8C7512CF94BFFEAF6DDE46CA47CE672DEB961E9360FD4CFC5CB95B1DE02E6E744A169C0F2F4658051A387AC8269EC0ABB38AAA21E80D8046781E4577EB53917C549EDA64A3C06C30691E948848FC72CF4689F0605DF000000039F220101");
        iso8583DTO.setReservedPrivate_63("00185769819");
        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, null);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");

        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
    }
}

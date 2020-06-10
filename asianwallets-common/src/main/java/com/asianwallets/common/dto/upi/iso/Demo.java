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
        iso8583DTO.setReservedPrivate_60("00000002003");//01000001000000000
        iso8583DTO.setReservedPrivate_63("00");
        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, null);
        String sendMsg = "6000060000" +"602110190121"+ isoMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        System.out.println(Arrays.toString(split));
    }
}

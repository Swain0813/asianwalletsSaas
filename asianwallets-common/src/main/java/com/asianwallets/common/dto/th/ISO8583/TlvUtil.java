package com.asianwallets.common.dto.th.ISO8583;

import com.payneteasy.tlv.BerTag;
import com.payneteasy.tlv.BerTlvBuilder;
import com.payneteasy.tlv.HexUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TlvUtil {

    /**
     * 5F52格式
     *
     * @param str 字符串
     * @return
     */
    public static String tlv5f52(String str) {
        BerTlvBuilder berTlvBuilder = new BerTlvBuilder();
        berTlvBuilder.addHex(new BerTag(0x5F52), str);
        byte[] bytes = berTlvBuilder.buildArray();
        //转成Hex码来传输
        String returnStr = "5F" + HexUtil.toHexString(bytes);
        log.info("==========【TlvUtil】==========【5F52】 returnStr: {}", returnStr);
        return returnStr;
    }

}

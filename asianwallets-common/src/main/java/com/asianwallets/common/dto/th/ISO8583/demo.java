package com.asianwallets.common.dto.th.ISO8583;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class demo {


    public static void main(String[] args) {

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11("198124");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setReservedPrivate_60("50000001003");
        iso8583DTO.setReservedPrivate_63("001");


        String sendMsg;
        try {
            // 组包
            sendMsg = "60060900008001000000008529999581205010001864400000000860000500000000852999958120501"+ISO8583Util.packISO8583DTO(iso8583DTO);
            System.out.println(" ===  sendMsg  ====   "+sendMsg);

            String result = ISO8583Util.send8583(sendMsg,"58.248.241.169",10089);

            System.out.println(" ====  result  ===   "+result);
            // 解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
            System.out.println(iso8583DTO1281.toString());
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

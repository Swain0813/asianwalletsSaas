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
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("00008600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setReservedPrivate_60("50000001003");
        iso8583DTO.setReservedPrivate_63("001");


        String sendMsg;
        try {
            // 组包
            sendMsg = "6006090000"+"800100000000"+NumberStringUtil.stringToAscii("85299995812050100018644000000008600005")
                    +"00000000"+NumberStringUtil.stringToAscii("852999958120501")+ISO8583Util.packISO8583DTO(iso8583DTO);
            String strHex2 = String.format("%04x",sendMsg.length()/2);
            sendMsg = strHex2 + sendMsg;
            System.out.println(" ===  sendMsg  ====   "+sendMsg);
            //String result = ISO8583Util.send8583(sendMsg,"58.248.241.169",10089);

            //System.out.println(" ====  result  ===   "+result);
            // 解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(sendMsg);
            System.out.println(iso8583DTO1281.toString());
        } catch (Exception e) {
            System.out.println(e);
        }

        //String s = "600002000080010000000038353239393939353831323030353030303030323735333330303030303030303030303030333030303030303031383532393939393538313230303530303030303030353408000020000100C000121981240852000001303030303237353338353239393939353831323030353000115000000100300003303031";
        //System.out.println(s.length()/2);
        //String strHex2 = String.format("%04x",s.length()/2);
        //System.out.println(strHex2);

        //System.out.println(NumberStringUtil.hexToBinaryString("0333030303030303031383532393939393538313230303530303030303030353"));
    }
}

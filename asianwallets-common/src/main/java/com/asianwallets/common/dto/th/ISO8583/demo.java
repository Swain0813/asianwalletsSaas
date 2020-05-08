package com.asianwallets.common.dto.th.ISO8583;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class demo {


    public static void main(String[] args) {

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0820");
        iso8583DTO.setTransmissionDateAndTime7("0303145843");
        iso8583DTO.setSystemTraceAuditNumber11("000042");
        iso8583DTO.setAcquiringInstitutionDentificationCode32("03090000");

        String sendMsg;
        try {
            // 组包
            sendMsg = ISO8583Util.packISO8583DTO(iso8583DTO);
            System.out.println(sendMsg);

            // 解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(sendMsg);
            System.out.println(iso8583DTO1281.toString());
        } catch (IncorrectLengthException e) {
            System.out.println(e.getMsg());
        } catch (IncorrectMessageException e) {
            System.out.println(e.getMsg());
        }

    }
}

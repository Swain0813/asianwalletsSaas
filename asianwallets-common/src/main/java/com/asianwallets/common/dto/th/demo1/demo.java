package com.asianwallets.common.dto.th.demo1;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class demo {

    public static void main(String[] args) {

        ISO8583DTO128 iso8583DTO128 = new ISO8583DTO128();
        iso8583DTO128.setMessageType("0820");
        iso8583DTO128.setTransmissionDateAndTime7("0303145843");
        iso8583DTO128.setSystemTraceAuditNumber11("000042");
        iso8583DTO128.setAcquiringInstitutionDentificationCode32("03090000");
        iso8583DTO128.setNetworkManagementInformationCode70("301");
        iso8583DTO128.setDestinationInstitutionIdentificationCode100("04025370");

        String sendMsg;
        try {
            // 组包
            sendMsg = ISO8583Util.packISO8583DTO128(iso8583DTO128);
            System.out.println(sendMsg);

            // 解包
            ISO8583DTO128 iso8583DTO1281 = ISO8583Util.unpackISO8583DTO128(sendMsg);
            System.out.println(iso8583DTO1281.toString());
        } catch (IncorrectLengthException e) {
            System.out.println(e.getMsg());
        } catch (IncorrectMessageException e) {
            System.out.println(e.getMsg());
        }

    }
}

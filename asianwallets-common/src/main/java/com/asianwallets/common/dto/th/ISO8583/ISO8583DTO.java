package com.asianwallets.common.dto.th.ISO8583;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:52
 **/

import lombok.Data;

/**
 * 报文交互DTO，128域
 */
@Data
public class ISO8583DTO {

    /**
     * 数据类型
     */
    private String messageType;

    /**
     * 域7交易传输时间(TRANSMISSION-DATE-AND-TIME)
     * n10，10位定长数字字符；
     * 格式：MMDDhhmmss
     * 交易发起方的系统工作日日期和时间。
     */
    @ISO8583Annotation(
            fldIndex = 7, dataFldLength = 10, fldFlag = FldFlag.FIXED
    )
    private String transmissionDateAndTime7;

    /**
     * 域11系统跟踪号(SYSTEM-TRACE-AUDIT-NUMBER)
     * n6，6位定长数字字符
     * 受理方赋予交易的一组数字，用于唯一标识一笔交易的编号。
     */
    @ISO8583Annotation(
            fldIndex = 11, dataFldLength = 6, fldFlag = FldFlag.FIXED
    )
    private String systemTraceAuditNumber11;

    /**
     * 域32受理机构标识码(ACQUIRING-INSTITUTION-DENTIFICATION-CODE)
     * n..12(LLVAR)
     * 2个字节的长度值＋最大12个字节（数字字符）的受理方标识码
     */
    @ISO8583Annotation(
            fldIndex = 32, dataFldLength = 12, fldFlag = FldFlag.UNFIXED_2
    )
    private String acquiringInstitutionDentificationCode32;

    ///**
    // * 域70网络管理信息码 (NETWORK-MANAGEMENT-INFORMATION-CODE)
    // * n3,3位定长数字字符；
    // * 网络业务管理功能码；
    // */
    //@ISO8583Annotation(
    //        fldIndex = 70, dataFldLength = 3, fldFlag = FldFlag.FIXED
    //)
    //private String networkManagementInformationCode70;
    //
    ///**
    // * 域100接收机构标识码(DESTINATION-INSTITUTION-IDENTIFICATION-CODE)
    // * n..12（LLVAR），2个字节的长度值＋最大12个字节（数字字符）的接收方标识码；
    // * 在消息中表示消息接收方机构的标识；
    // */
    //@ISO8583Annotation(
    //        fldIndex = 100, dataFldLength = 12, fldFlag = FldFlag.UNFIXED_2
    //)
    //private String destinationInstitutionIdentificationCode100;

}


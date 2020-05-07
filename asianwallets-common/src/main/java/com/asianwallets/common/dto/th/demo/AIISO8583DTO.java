package com.asianwallets.common.dto.th.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-07 16:43
 **/

import lombok.Data;

/**
 * 报文交互 DTO
 */
@Data
public class AIISO8583DTO {

    /** 2域 */
    @ISO8583Annotation(
            fldIndex = 2, dataFldLength = 19, encodeRule = "BCD", fldFlag = "2"
    )
    private String cardNo02;

    /** 3域 */
    @ISO8583Annotation(
            fldIndex = 3, dataFldLength = 6, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String transProcCode03;

    /** 4域 */
    @ISO8583Annotation(
            fldIndex = 4, dataFldLength = 12, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String transAmt04;

    /** 11域 */
    @ISO8583Annotation(
            fldIndex = 11, dataFldLength = 6, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String sysTrackNo11;

    /** 12域 */
    @ISO8583Annotation(
            fldIndex = 12, dataFldLength = 6, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String locTime12;

    /** 13域 */
    @ISO8583Annotation(
            fldIndex = 13, dataFldLength = 4, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String locDate13;

    /** 14域 */
    @ISO8583Annotation(
            fldIndex = 14, dataFldLength = 4, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String expireDate14;

    /** 15域 */
    @ISO8583Annotation(
            fldIndex = 15, dataFldLength = 4, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String liquidatimeDate15;

    /** 22域 */
    @ISO8583Annotation(
            fldIndex = 22, dataFldLength = 3, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String serviceInputModeCode22;

    /** 23域 */
    @ISO8583Annotation(
            fldIndex = 23, dataFldLength = 3, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String cardSerialNo23;

    /** 25域 */
    @ISO8583Annotation(
            fldIndex = 25, dataFldLength = 2, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String serviceConditionCode25;

    /** 26域 */
    @ISO8583Annotation(
            fldIndex = 26, dataFldLength = 2, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String servicePINAccessCode26;

    /** 32域 */
    @ISO8583Annotation(
            fldIndex = 32, dataFldLength = 11, encodeRule = "BCD", fldFlag = "2",
            defalutValue = ""
    )
    private String acquirerMarkCode32;

    /** 35域 */
    @ISO8583Annotation(
            fldIndex = 35, dataFldLength = 37, encodeRule = "BCD", fldFlag = "2"
    )
    private String track2Data35;

    /** 36域 */
    @ISO8583Annotation(
            fldIndex = 36, dataFldLength = 999, encodeRule = "BCD", fldFlag = "3"
    )
    private String track3Data36;

    /** 37域 */
    @ISO8583Annotation(
            fldIndex = 37, dataFldLength = 12, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String retrievalReferenceNo37;

    /** 38域 */
    @ISO8583Annotation(
            fldIndex = 38, dataFldLength = 6, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String authIdentityRespCode38;

    /** 39域 */
    @ISO8583Annotation(
            fldIndex = 39, dataFldLength = 2, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String respCode39;

    /** 40域 */
    @ISO8583Annotation(
            fldIndex = 40, dataFldLength = 99, encodeRule = "ASCII", fldFlag = "2"
    )
    private String respDesc40;

    /** 41域 */
    @ISO8583Annotation(
            fldIndex = 41, dataFldLength = 18, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String cardAcceptorTerminalID41;

    /** 42域 */
    @ISO8583Annotation(
            fldIndex = 42, dataFldLength = 15, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String cardAcceptorID42;

    /** 44域 */
    @ISO8583Annotation(
            fldIndex = 44, dataFldLength = 25, encodeRule = "ASCII", fldFlag = "2"
    )
    private String additionalRespData44;

    /** 48域 */
    @ISO8583Annotation(
            fldIndex = 48, dataFldLength = 322, encodeRule = "BCD", fldFlag = "3"
    )
    private String additionalDataPrivate48;

    /** 49域 */
    @ISO8583Annotation(
            fldIndex = 49, dataFldLength = 3, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "AFTER", fillChar = "20"
    )
    private String currencyCode49;

    /** 52域 */
    @ISO8583Annotation(
            fldIndex = 52, dataFldLength = 8, encodeRule = "BINARY", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String personalIDCodeData52;

    /** 53域 */
    @ISO8583Annotation(
            fldIndex = 53, dataFldLength = 16, encodeRule = "BCD", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "30"
    )
    private String safeControlInfo53;

    /** 54域 */
    @ISO8583Annotation(
            fldIndex = 54, dataFldLength = 256, encodeRule = "ASCII", fldFlag = "3"
    )
    private String additionalAmt54;

    /** 55域 */
    @ISO8583Annotation(
            fldIndex = 55, dataFldLength = 512, encodeRule = "ASCII", fldFlag = "3"
    )
    private String ICCardDataDomain55;

    /** 57域 */
    @ISO8583Annotation(
            fldIndex = 57, dataFldLength = 999, encodeRule = "ASCII", fldFlag = "3"
    )
    private String fld57Domain57;

    /** 58域 */
    @ISO8583Annotation(
            fldIndex = 58, dataFldLength = 512, encodeRule = "BINARY", fldFlag = "3"
    )
    private String eWalletTransInfo58;

    /** 60域 */
    @ISO8583Annotation(
            fldIndex = 60, dataFldLength = 17, encodeRule = "BCD", fldFlag = "3"
    )
    private String fld60Domain60;

    /** 61域 */
    @ISO8583Annotation(
            fldIndex = 61, dataFldLength = 29, encodeRule = "BCD", fldFlag = "3"
    )
    private String originalInfoDomain61;

    /** 62域 */
    @ISO8583Annotation(
            fldIndex = 62, dataFldLength = 999, encodeRule = "BINARY", fldFlag = "3"
    )
    private String fld62Domain62;

    /** 63域 */
    @ISO8583Annotation(
            fldIndex = 63, dataFldLength = 512, encodeRule = "ASCII", fldFlag = "3"
    )
    private String fld63Domain63;

    /** 64域 */
    @ISO8583Annotation(
            fldIndex = 64, dataFldLength = 8, encodeRule = "ASCII", fldFlag = "1",
            fillRule = "BEFORE", fillChar = "20", defalutValue = "0000000000000000"
    )
    private String mac64;
}

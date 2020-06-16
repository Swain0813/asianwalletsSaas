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
     * 域2 交易处理码(Processing Code)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 2, dataFldLength = 19, type = "BINARY", fldFlag = FldFlag.UNFIXED_2)
    private String processingCode_2;

    /**
     * 域3 交易处理码(Processing Code)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 3, dataFldLength = 6, type = "BCD", fldFlag = FldFlag.FIXED)
    private String processingCode_3;

    /**
     * 域4 交易金额(Amount Of Transactions)
     * 不管是账户资金交易还是积分交易，此域的金额将用于受理方、收单机构之间的结算。变量属性
     * N12，12个字节的定长数字字符域，压缩时用BCD码表示的6个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 4, dataFldLength = 12, type = "BCD", fldFlag = FldFlag.FIXED)
    private String AmountOfTransactions_4;

    /**
     * 域5 小费金额(Amount Of Tips)
     * 该域在消费交易，预授权完成交易，若出现小费，则必须上送。
     */
    @ISO8583Annotation(fldIndex = 5, dataFldLength = 12, type = "BCD", fldFlag = FldFlag.FIXED)
    private String AmountOfTips_5;

    /**
     * 域6 持卡人扣帐金额(Amount Of Cardholder Billing)
     * 变量属性
     * N12，12个字节的定长数字字符域，压缩时用BCD码表示的6个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 6, dataFldLength = 12, type = "BCD", fldFlag = FldFlag.FIXED)
    private String AmountOfCardholderBilling_6;

    /**
     * 域7交易传输时间(TRANSMISSION-DATE-AND-TIME)
     * n10，10位定长数字字符；
     * 格式：MMDDhhmmss
     * 交易发起方的系统工作日日期和时间。
     */
    @ISO8583Annotation(fldIndex = 7, dataFldLength = 10, type = "BCD", fldFlag = FldFlag.FIXED)
    private String transmissionDateAndTime_7;

    /**
     * 域10 持卡人扣帐汇率 (Conversion Rate, Cardholder Billing)
     * 变量属性
     * N8，8个字节的定长数字字符域，压缩时用BCD码表示的4个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 10, dataFldLength = 8, type = "BCD", fldFlag = FldFlag.FIXED)
    private String ConversionRate_10;

    /**
     * 域11 受卡方系统跟踪号(System Trace Audit Number)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域
     */
    @ISO8583Annotation(fldIndex = 11, dataFldLength = 6, type = "BCD", fldFlag = FldFlag.FIXED)
    private String SystemTraceAuditNumber_11;

    /**
     * 域12受卡方所在地时间(Time Of Local Transaction)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域。
     * 格式：hhmmss
     */
    @ISO8583Annotation(fldIndex = 12, dataFldLength = 6, type = "BCD", fldFlag = FldFlag.FIXED)
    private String TimeOfLocalTransaction_12;

    /**
     * 域13受卡方所在地日期(Date Of Local Transaction)
     * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
     * 格式：MMDD
     */
    @ISO8583Annotation(fldIndex = 13, dataFldLength = 4, type = "BCD", fldFlag = FldFlag.FIXED)
    private String DateOfLocalTransaction_13;

    /**
     * 域14卡有效期(Date Of Expired)
     * 变量属性
     * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
     * 格式：YYMM
     */
    @ISO8583Annotation(fldIndex = 14, dataFldLength = 4, type = "BCD", fldFlag = FldFlag.FIXED)
    private String DateOfExpired_14;

    /**
     * 域15清算日期(Date Of Settlement)
     * 变量属性
     * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
     * 格式：MMDD
     */
    @ISO8583Annotation(fldIndex = 15, dataFldLength = 4, type = "BCD", fldFlag = FldFlag.FIXED)
    private String DateOfSettlement_15;

    /**
     * 域22服务点输入方式码(Point Of Service Entry Mode)
     * 变量属性
     * N3，3个字节的定长数字字符域，压缩时用左靠BCD码表示的2个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 22, dataFldLength = 3, type = "BCD", fldFlag = FldFlag.FIXED)
    private String PointOfServiceEntryMode_22;

    /**
     * 域23卡序列号(Card Sequence Number)
     * 变量属性
     * N3，3个字节的定长数字字符域，压缩时用右靠BCD码表示的2个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 23, dataFldLength = 3, type = "BCD", fldFlag = FldFlag.FIXED)
    private String CardSequenceNumber_23;

    /**
     * 域25服务点条件码(Point Of Service Condition Mode)
     * 变量属性
     * N2，2个字节的定长数字字符域，压缩时用左靠BCD码表示的1个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 25, dataFldLength = 2, type = "BCD", fldFlag = FldFlag.FIXED)
    private String PointOfServiceConditionMode_25;

    /**
     * 域26服务点PIN获取码(Point Of Service PIN Capture Code)
     * 变量属性
     * N2，2个字节的定长数字字符域，压缩时用BCD码表示的1个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 26, dataFldLength = 2, type = "BCD", fldFlag = FldFlag.FIXED)
    private String PointOfServicePINCaptureCode_26;

    /**
     * 域32受理方标识码(Acquiring Institution Identification Code)
     * 变量属性
     * N..11(LLVAR)，2个字节的长度值＋最大11个字节的受理方标识码，
     * 压缩时用BCD码表示的1个字节的长度值＋用左靠BCD码表示的最大6个字节的受理方标识码。
     */
    @ISO8583Annotation(fldIndex = 32, dataFldLength = 11, type = "BCD", fldFlag = FldFlag.UNFIXED_2)
    private String AcquiringInstitutionIdentificationCode_32;

    /**
     * 域352磁道数据(Track 2 Data)
     * 变量属性
     * B..24(LLVAR)，1个字节的BCD格式长度值＋用TRK加密的含第二磁道长度的第二磁道数据。
     */
    @ISO8583Annotation(fldIndex = 35, dataFldLength = 24, type = "BINARY", fldFlag = FldFlag.UNFIXED_2)
    private String Track2Data_35;

    /**
     * 域363磁道数据(Track 3 Data)
     * 变量属性
     * B...56(LLLVAR)，2个字节的BCD格式长度值＋用TRK加密的含第三磁道长度的第三磁道数据。
     */
    @ISO8583Annotation(fldIndex = 36, dataFldLength = 56, type = "BINARY", fldFlag = FldFlag.UNFIXED_3)
    private String Track3Data_36;

    /**
     * 域37检索参考号(Retrieval Reference Number)
     * 变量属性
     * AN12，12个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 37, dataFldLength = 12, type = "ASC", fldFlag = FldFlag.FIXED)
    private String RetrievalReferenceNumber_37;

    /**
     * 域38授权标识应答码(Authorization Identification Response)
     * 变量属性
     * AN6，6个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 38, dataFldLength = 6, type = "ASC", fldFlag = FldFlag.FIXED)
    private String AuthorizationIdentificationResponse_38;

    /**
     * 域39应答码(Response Code)
     * 变量属性
     * AN2，2个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 39, dataFldLength = 2, type = "ASC", fldFlag = FldFlag.FIXED)
    private String ResponseCode_39;

    /**
     * 域41受卡机终端标识码(Card Acceptor Terminal Identification)
     * 变量属性
     * ANS8，8个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 41, dataFldLength = 8, type = "ASC", fldFlag = FldFlag.FIXED)
    private String CardAcceptorTerminalIdentification_41;

    /**
     * 域42受卡方标识码(Card Acceptor Identification Code)
     * 变量属性
     * ANS15，15个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 42, dataFldLength = 15, type = "ASC", fldFlag = FldFlag.FIXED)
    private String CardAcceptorIdentificationCode_42;

    /**
     * 域44附加响应数据(Additional Response Data)
     * 变量属性
     * AN..25，2个字节长度+ 最大25个字节的数据。
     * 压缩时用右靠BCD码表示的1个字节的长度值＋用ASCII码表示的最大25个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 44, dataFldLength = 25, type = "ASC", fldFlag = FldFlag.UNFIXED_2)
    private String AdditionalResponseData_44;

    /**
     * 域46附加数据(Additional Data)
     * 变量属性
     * ans...999(LLVAR)，3个字节长度+ 最大999个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋TAG的ASCII数据。
     * 本域采用TLV格式，所有的TLV总长度不能超过999字节。
     */
    @ISO8583Annotation(fldIndex = 46, dataFldLength = 999, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String AdditionalData_46;

    /**
     * 域47附加数据 - 私有(Additional Data - Private)
     * 变量属性
     * ans...999(LLVAR)，3个字节长度+ 最大999个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋实际数据。
     * 实际数据采用TLV格式，所有的TLV总长度不能超过999字节。
     */
    @ISO8583Annotation(fldIndex = 47, dataFldLength = 999, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String AdditionalDataPrivate_47;

    /**
     * 域48附加数据 - 私有(Additional Data - Private)
     * 变量属性
     * N...322(LLLVAR)，3个字节长度+ 最大322个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大161个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 48, dataFldLength = 322, type = "BCD", fldFlag = FldFlag.UNFIXED_3)
    private String AdditionalDataPrivate_48;

    /**
     * 域49交易货币代码(Currency Code Of Transaction)
     * 变量属性
     * AN3，3个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 49, dataFldLength = 3, type = "ASC", fldFlag = FldFlag.FIXED)
    private String CurrencyCodeOfTransaction_49;

    /**
     * 域51 持卡人扣帐货币代码(Currency Code Of Cardholder Billing)
     * 变量属性
     * AN3，3个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 51, dataFldLength = 3, type = "ASC", fldFlag = FldFlag.FIXED)
    private String CurrencyCodeOfCardholderBilling_51;

    /**
     * 域52个人标识码数据(PIN Data)
     * 变量属性
     * B64，8个字节的定长二进制数域。
     */
    @ISO8583Annotation(fldIndex = 52, dataFldLength = 8, type = "BINARY", fldFlag = FldFlag.FIXED)
    private String PINData_52;

    /**
     * 域53安全控制信息(Security Related Control Information )
     * 变量属性
     * n16，16个字节的定长数字字符域。
     * 压缩时用BCD码表示的8个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 53, dataFldLength = 16, type = "BCD", fldFlag = FldFlag.FIXED)
    private String SecurityRelatedControlInformation_53;

    /**
     * 域54附加金额(Balance Amount)
     * 变量属性
     * AN...020(LLLVAR)，3个字节的长度值＋最大20个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用ASCII码表示的最大20个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 54, dataFldLength = 20, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String BalanceAmount_54;

    /**
     * 域55IC卡数据域(Intergrated Circuit Card System Related Data)
     * 变量属性
     * 该域是一个变长域（LLLVAR），最长可达255个字节，最开始是一个占3个字节的长度值信息。
     * 压缩时采用右靠BCD码表示长度信息，长度信息占两个字节。
     * 所支持的数据属性有：
     * b   二进制（二进制数或者位组合）。
     * cn  BCD码。右对齐，左补‘0’。如，数字12345可以保存在n12的授权金额数据对象中，形如‘00 01 23 45’。
     * An	每个字节包含一个字符字母数字型数据元（A-Z，a-z，0-9）。
     * var. up to N 	变长数据，最大长度可为N。
     */
    @ISO8583Annotation(fldIndex = 55, dataFldLength = 255, type = "BCD", fldFlag = FldFlag.UNFIXED_3)
    private String IntergratedCircuitCardSystemRelatedData_55;

    @ISO8583Annotation(fldIndex = 56, dataFldLength = 512, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String IntergratedCircuitCardSystemRelatedData_56;

    @ISO8583Annotation(fldIndex = 57, dataFldLength = 255, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String IntergratedCircuitCardSystemRelatedData_57;

    @ISO8583Annotation(fldIndex = 58, dataFldLength = 255, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String IntergratedCircuitCardSystemRelatedData_58;

    /**
     * 域59自定义域（Reserved Private）
     */
    @ISO8583Annotation(fldIndex = 59, dataFldLength = 999, type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate_59;

    /**
     * 域60自定义域(Reserved Private)
     * 本域60.1至60.7子域为所有非管理类交易必送域
     * 所有非管理类交易,60.1,60.6,60.7必须填正确的值,和当前交易无关的子域,可用0x00占位
     * 变量属性
     * N...019(LLLVAR)，3个字节的长度值＋最大19个字节的数字字符域。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大10个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 60, dataFldLength = 19, type = "BCD", fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate_60;

    /**
     * 域61原始信息域(Original Message)
     * 变量属性
     * N...029(LLLVAR)，3个字节的长度值＋最大19个字节的数字字符域，
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大15个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 61, dataFldLength = 19, type = "BCD", fldFlag = FldFlag.UNFIXED_3)
    private String OriginalMessage_61;

    /**
     * 域62自定义域(Reserved Private)
     * 变量属性
     * ANS...512(LLLVAR)，3个字节的长度值＋最大512个字节的数据域。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用ASCII码表示的最大512个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 62, dataFldLength = 512,type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate_62;

    /**
     * 域63自定义域(Reserved Private)
     * 变量属性
     * ANS...163(LLLVAR)，3个字节的长度值＋最大163个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用ASCII码表示的最大163个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 63, dataFldLength = 163,type = "ASC", fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate_63;

    /**
     * 域64报文鉴别码(Message Authentication Code)
     * 变量属性
     * B64，8个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 64, dataFldLength = 8, type = "BINARY",fldFlag = FldFlag.FIXED)
    private String MessageAuthenticationCode_64;

    public static void main(String[] args) {
        String key = "B66CC3219FED1938240A81BA218A6BE072A224E1C2CF49CF9D4EA5920000000000000000BF74CDFBB18E3F4E347B3B5F3414079A1D66891F87F2BE4F";
        String substring = key.substring(40, 80);
        System.out.println(substring);
    }
}


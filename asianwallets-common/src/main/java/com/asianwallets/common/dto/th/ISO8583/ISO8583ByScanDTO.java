package com.asianwallets.common.dto.th.ISO8583;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "通华被扫接口DTO", description = "通华被扫接口DTO")
public class ISO8583ByScanDTO {

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 域3 交易处理码(Processing Code)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 3, dataFldLength = 6, fldFlag = FldFlag.FIXED)
    private String processingCode3;

    /**
     * 域4 交易金额(Amount Of Transactions)
     * 不管是账户资金交易还是积分交易，此域的金额将用于受理方、收单机构之间的结算。变量属性
     * N12，12个字节的定长数字字符域，压缩时用BCD码表示的6个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 4, dataFldLength = 12, fldFlag = FldFlag.FIXED)
    private String AmountOfTransactions4;

    /**
     * 域5 小费金额(Amount Of Tips)
     * 该域在消费交易，预授权完成交易，若出现小费，则必须上送。
     */
    @ISO8583Annotation(fldIndex = 5, dataFldLength = 12, fldFlag = FldFlag.FIXED)
    private String AmountOfTips5;

    /**
     * 域11 受卡方系统跟踪号(System Trace Audit Number)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域
     */
    @ISO8583Annotation(fldIndex = 11, dataFldLength = 6, fldFlag = FldFlag.FIXED)
    private String SystemTraceAuditNumber11;

    /**
     * 域12受卡方所在地时间(Time Of Local Transaction)
     * N6，6个字节的定长数字字符域，压缩时用BCD码表示的3个字节的定长域。
     * 格式：hhmmss
     */
    @ISO8583Annotation(fldIndex = 12, dataFldLength = 6, fldFlag = FldFlag.FIXED)
    private String TimeOfLocalTransaction12;

    /**
     * 域13受卡方所在地日期(Date Of Local Transaction)
     * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
     * 格式：MMDD
     */
    @ISO8583Annotation(fldIndex = 13, dataFldLength = 4, fldFlag = FldFlag.FIXED)
    private String DateOfLocalTransaction13;

    /**
     * 域15清算日期(Date Of Settlement)
     * 变量属性
     * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
     * 格式：MMDD
     */
    @ISO8583Annotation(fldIndex = 15, dataFldLength = 4, fldFlag = FldFlag.FIXED)
    private String DateOfSettlement15;

    /**
     * 域22服务点输入方式码(Point Of Service Entry Mode)
     * 变量属性
     * N3，3个字节的定长数字字符域，压缩时用左靠BCD码表示的2个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 22, dataFldLength = 3, fldFlag = FldFlag.FIXED)
    private String PointOfServiceEntryMode22;

    /**
     * 域25服务点条件码(Point Of Service Condition Mode)
     * 变量属性
     * N2，2个字节的定长数字字符域，压缩时用左靠BCD码表示的1个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 25, dataFldLength = 2, fldFlag = FldFlag.FIXED)
    private String PointOfServiceConditionMode25;

    /**
     * 域32受理方标识码(Acquiring Institution Identification Code)
     * 变量属性
     * N..11(LLVAR)，2个字节的长度值＋最大11个字节的受理方标识码，
     * 压缩时用BCD码表示的1个字节的长度值＋用左靠BCD码表示的最大6个字节的受理方标识码。
     */
    @ISO8583Annotation(fldIndex = 32, dataFldLength = 11, fldFlag = FldFlag.UNFIXED_2)
    private String AcquiringInstitutionIdentificationCode32;

    /**
     * 域37检索参考号(Retrieval Reference Number)
     * 变量属性
     * AN12，12个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 37, dataFldLength = 12, fldFlag = FldFlag.FIXED)
    private String RetrievalReferenceNumber37;

    /**
     * 域39应答码(Response Code)
     * 变量属性
     * AN2，2个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 39, dataFldLength = 2, fldFlag = FldFlag.FIXED)
    private String ResponseCode39;

    /**
     * 域41受卡机终端标识码(Card Acceptor Terminal Identification)
     * 变量属性
     * ANS8，8个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 41, dataFldLength = 8, fldFlag = FldFlag.FIXED)
    private String CardAcceptorTerminalIdentification41;

    /**
     * 域42受卡方标识码(Card Acceptor Identification Code)
     * 变量属性
     * ANS15，15个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 42, dataFldLength = 15, fldFlag = FldFlag.FIXED)
    private String CardAcceptorIdentificationCode42;

    /**
     * 域46附加数据(Additional Data)
     * 变量属性
     * ans...999(LLVAR)，3个字节长度+ 最大999个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋TAG的ASCII数据。
     * 本域采用TLV格式，所有的TLV总长度不能超过999字节。
     */
    @ISO8583Annotation(fldIndex = 46, dataFldLength = 999, fldFlag = FldFlag.UNFIXED_3)
    private String AdditionalData46;

    /**
     * 域47附加数据 - 私有(Additional Data - Private)
     * 变量属性
     * ans...999(LLVAR)，3个字节长度+ 最大999个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋实际数据。
     * 实际数据采用TLV格式，所有的TLV总长度不能超过999字节。
     */
    @ISO8583Annotation(fldIndex = 47, dataFldLength = 999, fldFlag = FldFlag.UNFIXED_3)
    private String AdditionalDataPrivate47;

    /**
     * 域49交易货币代码(Currency Code Of Transaction)
     * 变量属性
     * AN3，3个字节的定长字符域。
     */
    @ISO8583Annotation(fldIndex = 49, dataFldLength = 3, fldFlag = FldFlag.FIXED)
    private String CurrencyCodeOfTransaction49;

    /**
     * 域60自定义域(Reserved Private)
     * 本域60.1至60.7子域为所有非管理类交易必送域
     * 所有非管理类交易,60.1,60.6,60.7必须填正确的值,和当前交易无关的子域,可用0x00占位
     * 变量属性
     * N...019(LLLVAR)，3个字节的长度值＋最大19个字节的数字字符域。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大10个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 60, dataFldLength = 100, fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate60;

    /**
     * 域63自定义域(Reserved Private)
     * 变量属性
     * ANS...163(LLLVAR)，3个字节的长度值＋最大163个字节的数据。
     * 压缩时用右靠BCD码表示的2个字节的长度值＋用ASCII码表示的最大163个字节的数据。
     */
    @ISO8583Annotation(fldIndex = 63, dataFldLength = 163, fldFlag = FldFlag.UNFIXED_3)
    private String ReservedPrivate63;

    /**
     * 域64报文鉴别码(Message Authentication Code)
     * 变量属性
     * B64，8个字节的定长域。
     */
    @ISO8583Annotation(fldIndex = 64, dataFldLength = 8, fldFlag = FldFlag.FIXED)
    private String MessageAuthenticationCode64;
}


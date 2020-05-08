package com.asianwallets.common.dto.th;

import com.asianwallets.common.dto.th.ISO8583.FldFlag;
import com.asianwallets.common.dto.th.ISO8583.ISO8583Annotation;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 14:17
 **/
@Data
public class RefundDTO {

    /**
     * 数据类型
     */
    private String messageType;

    /**
     * 交易处理码
     */
    @ISO8583Annotation(
            fldIndex = 3, dataFldLength = 6, fldFlag = FldFlag.FIXED
    )
    private String tradeNo_3;

    /**
     * 退款金额
     */
    @ISO8583Annotation(
            fldIndex = 4, dataFldLength = 12, fldFlag = FldFlag.FIXED
    )
    private String refundAmount_4;
    /**
     * 受卡方系统跟踪号
     */
    @ISO8583Annotation(
            fldIndex = 11, dataFldLength = 6, fldFlag = FldFlag.FIXED
    )
    private String sysNo_11;
    /**
     * 受卡方所在地时间
     */
    @ISO8583Annotation(
            fldIndex = 12, dataFldLength = 6, fldFlag = FldFlag.FIXED
    )
    private String sysTime_12;
    /**
     * 受卡方所在地日期
     */
    @ISO8583Annotation(
            fldIndex = 13, dataFldLength = 4, fldFlag = FldFlag.FIXED
    )
    private String sysDate_13;
    /**
     * 清算日期
     */
    @ISO8583Annotation(
            fldIndex = 15, dataFldLength = 4, fldFlag = FldFlag.FIXED
    )
    private String clearDate_15;
    /**
     * 服务点输入方式码
     */
    @ISO8583Annotation(
            fldIndex = 22, dataFldLength = 3, fldFlag = FldFlag.FIXED
    )
    private String serviceInputNo_22;
    /**
     * 服务点条件码
     */
    @ISO8583Annotation(
            fldIndex = 25, dataFldLength = 2, fldFlag = FldFlag.FIXED
    )
    private String serviceNo_25;
    /**
     * 受理方标识码
     */
    @ISO8583Annotation(
            fldIndex = 32, dataFldLength = 11, fldFlag = FldFlag.FIXED
    )
    private String acceptNo_32;
    /**
     * 系统检索参考号
     */
    @ISO8583Annotation(
            fldIndex = 37, dataFldLength = 12, fldFlag = FldFlag.FIXED
    )
    private String sysSerNo_37;
    /**
     * 应答码
     */
    @ISO8583Annotation(
            fldIndex = 39, dataFldLength = 2, fldFlag = FldFlag.FIXED
    )
    private String repNo_39;
    /**
     * 受卡机终端标识码
     */
    @ISO8583Annotation(
            fldIndex = 41, dataFldLength = 8, fldFlag = FldFlag.FIXED
    )
    private String accCardTemNo_41;
    /**
     * 受卡方标识码
     */
    @ISO8583Annotation(
            fldIndex = 42, dataFldLength = 15, fldFlag = FldFlag.FIXED
    )
    private String accCardNo_42;
    /**
     * 自定义域
     */
    @ISO8583Annotation(
            fldIndex = 46, dataFldLength = 8, fldFlag = FldFlag.UNFIXED
    )
    private String remark_46;
    /**
     * 自定义域
     */
    @ISO8583Annotation(
            fldIndex = 47, dataFldLength = 8, fldFlag = FldFlag.UNFIXED
    )
    private String remark_47;
    /**
     * 交易货币代码
     */
    @ISO8583Annotation(
            fldIndex = 49, dataFldLength = 3, fldFlag = FldFlag.FIXED
    )
    private String remark_49;
    /**
     * 自定义域
     * 交易类型码(2)+批次号(6)+网络管理信息码(3)+保留使用(1)+保留使用(1)
     */
    @ISO8583Annotation(
            fldIndex = 60, dataFldLength = 3, fldFlag = FldFlag.UNFIXED
    )
    private String remark_60;
    /**
     * 自定义域
     * 国际信用卡公司代码(3)
     */
    @ISO8583Annotation(
            fldIndex = 63, dataFldLength = 63, fldFlag = FldFlag.UNFIXED
    )
    private String remark_63;
    /**
     * MAC
     */
    @ISO8583Annotation(
            fldIndex = 64, dataFldLength = 64, fldFlag = FldFlag.UNFIXED
    )
    private String mac_64;



}

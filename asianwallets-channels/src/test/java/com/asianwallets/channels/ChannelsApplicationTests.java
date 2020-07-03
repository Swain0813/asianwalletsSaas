package com.asianwallets.channels;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.service.*;
import com.asianwallets.common.dto.alipay.AliPayCardDTO;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.dto.doku.DOKURequestDTO;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.megapay.MegaPayQueryDTO;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.dto.wechat.WechaRefundDTO;
import com.asianwallets.common.dto.wechat.WechatQueryDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.UUIDHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelsApplicationTests extends SpringBootServletInitializer {

    @Autowired
    private WechatService wechatService;

    @Autowired
    private Help2PayService help2PayService;

    @Autowired
    private MegaPayService megaPayService;

    @Autowired
    private DokuService dokuService;

    @Autowired
    private ThService thService;

    @Autowired
    private AliPayService aliPayService;


    @Test
    public void contextLoads() {

        WechaRefundDTO wechaRefundDTO = new WechaRefundDTO();
        wechaRefundDTO.setApikey("QWERTY2580lkjhgf1649ZXC203mnb742");
        wechaRefundDTO.setAppid("wx14e049b9320bccca");
        //wechaRefundDTO.setSub_appid("");
        wechaRefundDTO.setSign_type("MD5");
        wechaRefundDTO.setMch_id("1500662841");
        wechaRefundDTO.setSub_mch_id("290476699");
        wechaRefundDTO.setNonce_str(IDS.uuid2());
        wechaRefundDTO.setRefund_account("REFUND_SOURCE_UNSETTLED_FUNDS");
        wechaRefundDTO.setTotal_fee(new BigDecimal(1).multiply(new BigDecimal(100)).intValue());
        wechaRefundDTO.setRefund_fee(new BigDecimal(1).multiply(new BigDecimal(100)).intValue());
        wechaRefundDTO.setRefund_fee_type("USD");
        wechaRefundDTO.setRefund_desc("tk");
        wechaRefundDTO.setTransaction_id("4200000344201907154523934534");
        wechaRefundDTO.setOut_trade_no("CBO201907151507277");
        wechaRefundDTO.setOut_refund_no(IDS.uuid2());

        wechatService.wechatRefund(wechaRefundDTO);

    }

    @Test
    public void contextLoads1() {

        WechatQueryDTO wechatQueryDTO = new WechatQueryDTO();
        wechatQueryDTO.setAppid("wx14e049b9320bccca");
        wechatQueryDTO.setSign_type("MD5");
        wechatQueryDTO.setMch_id("1500662841");
        wechatQueryDTO.setSub_mch_id("290476699");
        wechatQueryDTO.setNonce_str(UUIDHelper.getRandomString(32));
        wechatQueryDTO.setOut_trade_no("CBO201907151507277");
        wechatQueryDTO.setMd5KeyStr("QWERTY2580lkjhgf1649ZXC203mnb742");

        wechatService.wechatQuery(wechatQueryDTO);
        //wechatQueryDTO.setAppid("wx14e049b9320bccca");
        //wechatQueryDTO.setSign_type("MD5");
        //wechatQueryDTO.setMch_id("1488514432");
        //wechatQueryDTO.setSub_mch_id("66104046");
        //wechatQueryDTO.setNonce_str( UUIDHelper.getRandomString(32));
        //wechatQueryDTO.setOut_trade_no("CBO201907151507277");
        //wechatQueryDTO.setMd5KeyStr("VwifO1ailf4jzn0Gsio0angM0fpva2N9");
        //wechatService.wechatQuery(wechatQueryDTO);

    }

    @Test
    public void contextLoads2() {
        Help2PayOutDTO help2PayOutDTO = new Help2PayOutDTO();
        //help2PayOutDTO.setClientIP("192.168.124.28");
        help2PayOutDTO.setClientIP("119.23.136.80");
        help2PayOutDTO.setReturnURI("https://hao.360.com");
        help2PayOutDTO.setMerchantCode("M0285");
        help2PayOutDTO.setTransactionID(IDS.uuid2());
        help2PayOutDTO.setCurrencyCode("MYR");
        help2PayOutDTO.setMemberCode("11111");
        help2PayOutDTO.setAmount("100");
        help2PayOutDTO.setTransactionDateTime(DateToolUtils.LONG_DATE_FORMAT_AA.format(new Date()));
        help2PayOutDTO.setBankCode("CIMB");
        help2PayOutDTO.setToBankAccountName("CIMB Bank");
        help2PayOutDTO.setToBankAccountNumber("11111");
        help2PayOutDTO.setToProvince("");
        help2PayOutDTO.setToCity("");
        help2PayOutDTO.setSecurityCode("WbRsYjndLf2FKQH");
        BaseResponse baseResponse = help2PayService.help2PayOut(help2PayOutDTO);
    }

    @Test
    public void tesss() {
        MegaPayQueryDTO megaPayQueryDTO = new MegaPayQueryDTO();
        megaPayQueryDTO.setMerchantID("thb@alldebit.com");
        megaPayQueryDTO.setInvoice("PGO201911041611013");
        BaseResponse baseResponse = megaPayService.megaPayQuery(megaPayQueryDTO);
        System.out.println(JSON.toJSONString(baseResponse));
    }


    @Test
    public void dokuTest() {
        DOKUReqDTO dokuReqDTO = new DOKUReqDTO();
        DOKURequestDTO dokuRequestDTO = new DOKURequestDTO();
        dokuRequestDTO.setMALLID("11321761");
        dokuRequestDTO.setCHAINMERCHANT("11321761");
        dokuRequestDTO.setAMOUNT("10000.00");
        dokuRequestDTO.setPURCHASEAMOUNT("10.00");
        dokuRequestDTO.setTRANSIDMERCHANT("O12345671");
        dokuRequestDTO.setREQUESTDATETIME("20191112112511");
        dokuRequestDTO.setCURRENCY("360");
        dokuRequestDTO.setPURCHASECURRENCY("360");
        dokuRequestDTO.setSESSIONID("11111112");
        dokuRequestDTO.setNAME("aa");
        dokuRequestDTO.setEMAIL("swainfx@163.com");
        dokuRequestDTO.setBASKET("ITEM1,10000.00,2,20000.00;ITEM2,20000.00,4,80000.00");
        dokuRequestDTO.setPAYMENTCHANNEL("35");
        dokuReqDTO.setKey("EA6f3xM4gkjS");
        dokuReqDTO.setDokuRequestDTO(dokuRequestDTO);

        dokuService.payMent(dokuReqDTO);

    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/8
     * @Descripate 通华主扫和被扫的退款接口
     **/
    @Test
    public void thTest() {
        ThDTO thDTO = new ThDTO();
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        /************************************************ 退款 ***************************************************/
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("400100");
        iso8583DTO.setAmountOfTransactions_4("000000000009");
        iso8583DTO.setSystemTraceAuditNumber_11("102994");
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setAdditionalData_46("5F5221303002020232303230303532313030303030313130363130303137383531360202");
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        iso8583DTO.setReservedPrivate_60("55000031");
        thDTO.setIso8583DTO(iso8583DTO);
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("9238048CAEFCC39B");
        thDTO.setChannel(channel);
        thService.thRefund(thDTO);

    }

    /**
     * 通华CSB
     */
    @Test
    public void thCSB() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //主扫
        iso8583DTO.setProcessingCode_3("700200");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000009");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(0, 6));
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        /*
        0x30 微信
        0x31:支付宝
        0x32:银联云闪付
         */
        String payCode = "30";
        //附加信息-主扫
        String domain46 = "303002" + payCode + "0202";
        iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52(domain46));
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("04AFFF774377AEDE");
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thCSB(thDTO);
    }

    /**
     * 通华BSC
     */
    @Test
    public void thBSC() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //被扫
        iso8583DTO.setProcessingCode_3("400101");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000009");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(0, 6));
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        /*
        0x30 微信
        0x31:支付宝
        0x32:银联云闪付
         */
        String payCode = "30";
        String scanCode = "134620119418765853";
        String domain46 = "303002" + payCode + "02" + NumberStringUtil.str2HexStr(scanCode) + "0202";
        //附加信息-被扫
        iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52(domain46));
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("04AFFF774377AEDE");
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBSC(thDTO);
    }

    /**
     * 通华查询
     * 主扫和被扫的查询
     */
    @Test
    public void thQuery() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //查询
        iso8583DTO.setProcessingCode_3("700206");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000009");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(0, 6));
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("000");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        //附加信息
        String merchantOrderId = "3230323030353231303030303031313036313030313738353136";
        String domain46 = "3030020202" + merchantOrderId + "0202";
        iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52(domain46));
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("9238048CAEFCC39B");
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thQuery(thDTO);
    }

    /**
     * 通华签到
     */
    @Test
    public void thSignIn() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(0, 6));
        //机构号
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //终端号
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //商户号
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setReservedPrivate_60("50" + timeStamp.substring(6, 12) + "003");
        iso8583DTO.setReservedPrivate_63("001");
        ThDTO thDTO = new ThDTO();
        thDTO.setIso8583DTO(iso8583DTO);
        Channel channel = new Channel();
        // 通华主密钥
        channel.setMd5KeyStr("");
        thDTO.setChannel(channel);
        thService.thSignIn(thDTO);
    }

    /**
     * 通华线下银行卡消费
     */
    @Test
    public void thBank() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("009000");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000001");
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(timeStamp.substring(0, 6));
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        timeStamp.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("B3045DDECD39FF2B8FA2CE91400851C57EBC27BD60E90927855B741C0000000000000000E4456910D3CC230C534F90763F5B13282DBD872595C33537");
        //银行卡号
        String var2 = "4761340000000019";
        //银行卡 磁道2信息
        String var35 = "4761340000000019=171210114991787";
        //加密信息
        iso8583DTO.setProcessingCode_2(trkEncryption(var2, channel.getMd5KeyStr()));
        iso8583DTO.setTrack2Data_35(trkEncryption(var35, channel.getMd5KeyStr()));
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBankCard(thDTO);
    }

    /**
     * 通华线下银行卡冲正
     */
    @Test
    public void thBankCardReverse() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0400");
        iso8583DTO.setProcessingCode_3("009000");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000001");
        // 原消费11域
        iso8583DTO.setSystemTraceAuditNumber_11("159116");
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //冲正原因
        iso8583DTO.setResponseCode_39("06");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 原批次号
                        "696071" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
      /* 61域可不填
      // 61 自定义域
        String str61 =
                //61.1 原批次号
                "554625" +
                        //61.2 原交易流水号 11域
                        "159116" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0603";
        iso8583DTO.setOriginalMessage_61(str61);*/
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("B3045DDECD39FF2B8FA2CE91400851C57EBC27BD60E90927855B741C0000000000000000E4456910D3CC230C534F90763F5B13282DBD872595C33537");
        //银行卡号
        String var2 = "4761340000000019";
        //银行卡 磁道2信息
        String var35 = "4761340000000019=171210114991787";
        //加密信息
        iso8583DTO.setProcessingCode_2(trkEncryption(var2, channel.getMd5KeyStr()));
        iso8583DTO.setTrack2Data_35(trkEncryption(var35, channel.getMd5KeyStr()));
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBankCardReverse(thDTO);
    }

    /**
     * 通华线下银行卡退款
     */
    @Test
    public void thBankCardRefund() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0220");
        iso8583DTO.setProcessingCode_3("200000");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000001");
        // 原消费11域
        iso8583DTO.setSystemTraceAuditNumber_11("159124");
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setRetrievalReferenceNumber_37("101100180690");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "25" +
                        //60.2 原批次号
                        "265587" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "265587" +
                        //61.2 原交易流水号 11域
                        "159124" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0604";
        iso8583DTO.setOriginalMessage_61(str61);
        iso8583DTO.setReservedPrivate_63("000");
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("B3045DDECD39FF2B8FA2CE91400851C57EBC27BD60E90927855B741C0000000000000000E4456910D3CC230C534F90763F5B13282DBD872595C33537");
        //银行卡号
        String var2 = "4761340000000019";
        //银行卡 磁道2信息
        String var35 = "4761340000000019=171210114991787";
        //加密信息
        iso8583DTO.setProcessingCode_2(trkEncryption(var2, channel.getMd5KeyStr()));
        iso8583DTO.setTrack2Data_35(trkEncryption(var35, channel.getMd5KeyStr()));
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBankCardRefund(thDTO);
    }

    /**
     * 通华线下银行卡撤销
     */
    @Test
    public void thBankCardUndo() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("200000");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000001");
        // 原消费11域
        iso8583DTO.setSystemTraceAuditNumber_11("159132");
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setRetrievalReferenceNumber_37("101100180728");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "23" +
                        //60.2 原批次号
                        "600805" +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                "600805" +
                        //61.2 原交易流水号 11域
                        "159132" +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0605";
        iso8583DTO.setOriginalMessage_61(str61);
        iso8583DTO.setReservedPrivate_63("000");
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        channel.setMd5KeyStr("B3045DDECD39FF2B8FA2CE91400851C57EBC27BD60E90927855B741C0000000000000000E4456910D3CC230C534F90763F5B13282DBD872595C33537");
        //银行卡号
        String var2 = "4761340000000019";
        //银行卡 磁道2信息
        String var35 = "4761340000000019=171210114991787";
        //加密信息
        iso8583DTO.setProcessingCode_2(trkEncryption(var2, channel.getMd5KeyStr()));
        iso8583DTO.setTrack2Data_35(trkEncryption(var35, channel.getMd5KeyStr()));
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBankCardUndo(thDTO);
    }

    private static String trkEncryption(String str, String key) {
        //80-112 Trk密钥位
        String substring = key.substring(80, 112);
        String trk = Objects.requireNonNull(EcbDesUtil.decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", substring)).toUpperCase();
        String newStr;
        if (str.length() % 2 != 0) {
            newStr = str.length() + str + "0";
        } else {
            newStr = str.length() + str;
        }
        byte[] bcd = NumberStringUtil.str2Bcd(newStr);
        return Objects.requireNonNull(EcbDesUtil.encode3DEA(trk, cn.hutool.core.util.HexUtil.encodeHexStr(bcd))).toUpperCase();
    }


    @Test
    public void aliPayCard() {
        AliPayCardDTO aliPayCardDTO = new AliPayCardDTO();
        aliPayCardDTO.setService("alipay.acquire.precreate");
        aliPayCardDTO.setPartner("");
        aliPayCardDTO.set_input_charset("UTF-8");
        aliPayCardDTO.setSign_type("MD5");
        aliPayCardDTO.setTimestamp(DateUtil.formatDateTime(new Date()));
        aliPayCardDTO.setProduct_code("OVERSEAS_MBARCODE_PAY");
        aliPayCardDTO.setCurrency("USD");
        aliPayCardDTO.setTrans_currency("USD");
        aliPayCardDTO.setOut_trade_no(IDS.uuid2());
        aliPayCardDTO.setSubject("Goods");
        aliPayCardDTO.setTotal_fee("1");
        aliPayCardDTO.setSeller_id("");
        aliPayCardDTO.setExtend_params("");
        aliPayCardDTO.setNotify_url("");
        aliPayCardDTO.setSign("");

        //#########################
        aliPayCardDTO.setSecondary_merchant_id("");
        aliPayCardDTO.setSecondary_merchant_name("");
        aliPayCardDTO.setSecondary_merchant_industry("");
        aliPayCardDTO.setStore_name("");
        aliPayCardDTO.setStore_id("");

//       new AliPayCardDTO().
        //#########################

        Channel channel = new Channel();
        aliPayCardDTO.setChannel(channel);
        Orders orders = new Orders();
        aliPayCardDTO.setOrders(orders);
        aliPayService.alipayCard(aliPayCardDTO);
    }

}


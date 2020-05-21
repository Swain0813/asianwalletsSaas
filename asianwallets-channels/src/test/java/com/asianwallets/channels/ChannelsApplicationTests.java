package com.asianwallets.channels;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.service.*;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.dto.doku.DOKURequestDTO;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.megapay.MegaPayQueryDTO;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.NumberStringUtil;
import com.asianwallets.common.dto.th.ISO8583.ThDTO;
import com.asianwallets.common.dto.wechat.WechaRefundDTO;
import com.asianwallets.common.dto.wechat.WechatQueryDTO;
import com.asianwallets.common.entity.Channel;
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
    private THService thService;


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
     * @Descripate 通华
     **/
    @Test
    public void thTest() {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        /************************************************ 退款 ***************************************************/
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("400100");
        iso8583DTO.setAmountOfTransactions_4("000000000100");
        iso8583DTO.setSystemTraceAuditNumber_11("102994");
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setAdditionalData_46("5F5221303002020232303230303531393030303030313130363230303137383430330202");
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        iso8583DTO.setReservedPrivate_60("55000031");

        thService.thRefund(iso8583DTO);

    }

    @Test
    public void thCSB() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //主扫
        iso8583DTO.setProcessingCode_3("700200");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000001000");
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
        String payCode = "31";
        //附加信息-主扫
        iso8583DTO.setAdditionalData_46("5F5206303002" + payCode + "0202");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thCSB(thDTO);
    }

    @Test
    public void thBSC() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //被扫
        iso8583DTO.setProcessingCode_3("400101");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000100");
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
        String payCode = "31";
        String scanCode = "286020382107217225";
        //附加信息-被扫
        iso8583DTO.setAdditionalData_46("5F5219303002" + payCode + "02" + NumberStringUtil.str2HexStr(scanCode) + "0202");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
        ThDTO thDTO = new ThDTO();
        Channel channel = new Channel();
        channel.setExtend1("00018644");
        channel.setExtend2("08600005");
        channel.setChannelMerchantId("852999958120501");
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thService.thBSC(thDTO);
    }

    @Test
    public void thQuery() {
        String timeStamp = System.currentTimeMillis() + "";
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //被扫
        iso8583DTO.setProcessingCode_3("700206");
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("000000000100");
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
        //附加信息
        String merchantOrderId = "3230323030353230303030303031313036313030313738343635";
        iso8583DTO.setAdditionalData_46("5F52213030020202" + merchantOrderId + "0202");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + timeStamp.substring(6, 12));
//        ThDTO thDTO = new ThDTO();
//        Channel channel = new Channel();
//        channel.setExtend1("00018644");
//        channel.setExtend2("08600005");
//        channel.setChannelMerchantId("852999958120501");
//        thDTO.setChannel(channel);
//        thDTO.setIso8583DTO(iso8583DTO);
        thService.thQuery(iso8583DTO);
    }

    @Test
    public void thSignIn() {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11("198124");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120501");
        iso8583DTO.setReservedPrivate_60("50000001003");
        iso8583DTO.setReservedPrivate_63("001");
        thService.thRefund(iso8583DTO);
    }
}


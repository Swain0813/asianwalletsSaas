package com.asianwallets.channels;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.service.DokuService;
import com.asianwallets.channels.service.Help2PayService;
import com.asianwallets.channels.service.MegaPayService;
import com.asianwallets.channels.service.WechatService;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.dto.doku.DOKURequestDTO;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.megapay.MegaPayQueryDTO;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.dto.wechat.WechaRefundDTO;
import com.asianwallets.common.dto.wechat.WechatQueryDTO;
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
    * @Author YangXu
    * @Date 2020/5/8
    * @Descripate 通华
    * @return
    **/
    @Test
    public void thTest() {
        ISO8583DTO thRefundDTO = new ISO8583DTO();
        thRefundDTO.setMessageType("0200");
        thRefundDTO.setProcessingCode_3("200000");
        thRefundDTO.setAmountOfTransactions_4("000000100002");
        thRefundDTO.setSystemTraceAuditNumber_11("111111");
        thRefundDTO.setTimeOfLocalTransaction_12("153344");
        thRefundDTO.setDateOfLocalTransaction_13("0508");
        thRefundDTO.setDateOfSettlement_15("0508");
        thRefundDTO.setPointOfServiceEntryMode_22("030");
        thRefundDTO.setPointOfServiceConditionMode_25("00");
        thRefundDTO.setAcquiringInstitutionIdentificationCode_32("11111111111");
        //thRefundDTO.setSysSerNo_37("123456789011");
        //thRefundDTO.setRepNo_39("");
        thRefundDTO.setCardAcceptorTerminalIdentification_41("12345678");
        thRefundDTO.setCardAcceptorIdentificationCode_42("123456789012345");
        //thRefundDTO.setRemark_46();
        //thRefundDTO.setRemark_47();
        thRefundDTO.setCurrencyCodeOfTransaction_49("156");
        thRefundDTO.setReservedPrivate_60("2500000000000");
        thRefundDTO.setReservedPrivate_63("cup");
        thRefundDTO.setMessageAuthenticationCode_64("");


        String sendMsg;
        try {
            // 组包
            sendMsg = ISO8583Util.packISO8583DTO(thRefundDTO);
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


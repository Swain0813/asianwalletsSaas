package com.asianwallets.channels;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.service.UpiService;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiDownDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.UpiRefundDTO;
import com.asianwallets.common.dto.upi.iso.UpiIsoUtil;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.IDS;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 15:18
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UpiTest extends SpringBootServletInitializer {

    @Autowired
    private UpiService upiService;

    private static String ip = "210.48.142.168";
    private static String port = "7000";
    private static String merchantId = "000000000003421";
    private static String terminalId = "00001903";
    private static String key_62 = "2F9781AE38D8CAFB6D29CD8F9A88CF11C8EEC334F898A9D1507D76837864D01C0CA0D4204BE2468B83E2212BCB7909E1E2FD84C3793F09B6E2E88FD5";
    private static String key = "94A1FB75E03EADEAAD83528983948FC8";  @Test
    public void upiPayTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440189990001");

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("BACKSTAGEALIPAY");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003962");
        upiPayDTO.setOrder_no("pay" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        upiPayDTO.setAmount("0.02");
        upiPayDTO.setCurrency_type("HKD");
        upiPayDTO.setSett_currency_type("HKD");
        //upiPayDTO.setAuth_code("288009714923099525");
        //upiPayDTO.setCvn2();
        //upiPayDTO.setValid();
        upiPayDTO.setProduct_name("Saving the world requires sacrifice.");
        //upiPayDTO.setProduct_desc();
        //upiPayDTO.setProduct_type();
        //upiPayDTO.setUser_name();
        //upiPayDTO.setUser_cert_type();
        //upiPayDTO.setUser_cert_no();
        upiPayDTO.setReturn_url("https://testpay.sicpay.com/");
        upiPayDTO.setNotify_url("https://testpay.sicpay.com/");
        upiPayDTO.setClient_ip("120.236.178.23");

        upiPayDTO.setUser_bank_card_no("6250948200000004");
        upiPayDTO.setCvn2("248");
        upiPayDTO.setValid("1225");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        upiService.upiPay(upiDTO);

    }

    @Test
    public void upiQuerryTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440189990001");

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("SEARCH_REFUND");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003962");
        //upiPayDTO.setOrder_no("O103753129326284");
        upiPayDTO.setRefund_no("R1037808427708801024");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        upiService.upiQueery(upiDTO);
    }

    @Test
    public void upiRefundTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440189990001");

        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();
        upiRefundDTO.setVersion("2.0.0");
        upiRefundDTO.setTrade_code("REFUND");
        upiRefundDTO.setAgencyId(channel.getChannelMerchantId());
        upiRefundDTO.setTerminal_no("20003962");
        upiRefundDTO.setOrder_no("pay20200603160725");
        upiRefundDTO.setRefund_no("R" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        upiRefundDTO.setRefund_amount("0.02");
        upiRefundDTO.setCurrency_type("HKD");
        upiRefundDTO.setSett_currency_type("HKD");
        upiRefundDTO.setNotify_url("https://testpay.sicpay.com/");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiRefundDTO(upiRefundDTO);
        upiService.upiRefund(upiDTO);
    }

    @Test
    public void upiCancelTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440189990001");

        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();
        upiRefundDTO.setVersion("2.0.0");
        upiRefundDTO.setTrade_code("PAYC");
        upiRefundDTO.setAgencyId(channel.getChannelMerchantId());
        upiRefundDTO.setTerminal_no("20003962");
        upiRefundDTO.setOrder_no("payc" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        upiRefundDTO.setOri_order_no("pay20200603160725");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiRefundDTO(upiRefundDTO);
        upiService.upiCancel(upiDTO);
    }

    @Test
    public void upiDownTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440189990001");

        UpiDownDTO upiDownDTO = new UpiDownDTO();
        upiDownDTO.setVersion("2.0.0");
        upiDownDTO.setTrade_code("DOWNLOAD_SETTLE_FILE");
        upiDownDTO.setAgencyId(channel.getChannelMerchantId());
        upiDownDTO.setTerminal_no("20003962");
        upiDownDTO.setSettle_date("20200602");
        upiDownDTO.setFile_type("TRAN");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiDownDTO(upiDownDTO);
        upiService.upiDownSettle(upiDTO);
    }
    @Test
    public void upiCallBackTest() {

        Map<String, String> map = new HashMap<>();
        map.put("signData", "Gl6oXPG7qNfkBbZPs4wapjQ5PQ8k+LQmJJqlmziaJVRSu4nrnGqaagubNq1ww2SFghDxbeKzm3BMJlcdjlPrSXrHuTe+ZfkM+ctbPYe/Ms7P9imiScmBwLD3tEMsE/igsW8vsy//V25TJjGVGs6YgO8PM06p32RFrrkGO/GTmETs7yYRtqdg0gviDGm34Nm+XkwtGIeh88/4BflzIY5MyIBVg6feVYcveiz4ShTPU2/7ctF6LPK9UFHrPxietXOduyHVImsJXXhzao/i8+UWropbwnPcIwzPl/bVhibaqtWYM7rCoOTGh0+8IU7lB+BvYAH7cxGh2DByhMu5AzEuiA==");
        map.put("encryptData", "R5g/exKgZL5uaPMNYUKlMHuSupLE2QHdgqt/Xy2tbIJ3jK3cichi6uujEJkaD18TuaPBmKvkrbI7wRegw7q1gEwR3XuQnFaxOyqwN/a+zKCdeuBGbgshEBeouCNQP6QvUoqFDYBs8kzPT2hDunI8dDMdTrSEfHvgrVN9/sop0xUyNLn8HePJhWRJp0U47CIPN36cFG3/FxOLFtcurTx/Eq2Ygk70NiVlnaI+aNbBknArOhqFPhxjeElioaIvQic10nYAa4YWGg5MMJZRB8kujsaxOXSYc7OT2WPB9gPR7utswOZ6pH8QXSbr0bbF2EKWDzTIr/m5RBXUZj0YrPmYWFylzfYqyx1pXkya5E1s53f/Ot6qFvsA3/Zgyhwiqh5TeRtMpnCRYT5MsJMeF3W8kEvYD8Pr6F5CCFp3qMVx9CRNAOj69RQeGlpRhNwBdz3F5eN/G6p6Ub9RIOMvE6PmZQ78xorxbtord/RWv0VDQ+DXrLrjyEhBTwWS2nM+IYDbn+pBe7YEVxspI+uhp2QIAeLNhsrd2gr1PA/+EiRJprnscPQG/BwBjQxy0zIynJPYoWbwXL+/1jL9mV5qm7nlXg==");
        map.put("agencyId", "549440189990001");
        map.put("encryptKey", "lswQf6WrLzJoFoZ2EzE3CmxGKU+Kdma5CDxQinzQi6r0RfkSXM9fOaBbivNizwHvxXAnZ42ijySrZdk5FTQfMTR9IlVMe9nNW/0sVILbphd/woK7wL0fvLRWn8tuE5g7+B4J0eDh+j9KKjtUXakRCIpqUeXuS2tG16eONkI6xiPcmXX/D5JGgNVu8RanKorwbbeXM9T3m0ti9zORalI8xUF5MWORPvkyp9QykmP31cZW9ucG6GUevE+b7YB/d+KDG6z/eQRb6gt84mQtd3SgVofo3MlyuK9W6ULkxhUDprN7TsFU/bYwolQxE5pVVG7CNKICMa0zxX/jQi6Nk+viNw==");
        try {

            HttpResponse httpResponse = HttpClientUtils.reqPost("http://192.168.124.27:5010/offlineCallback/upiServerCallback", map, null);
            System.out.println(httpResponse.toString());
        } catch (Exception e) {

        }

    }

    //签到
    @Test
    public void upiQDTest()  throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        //自定义域
        iso8583DTO.setReservedPrivate_60("00000002003");//01000001000000000
        iso8583DTO.setReservedPrivate_63("000");
        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, null);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        //String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        //System.out.println(Arrays.toString(split));
    }

    //主密钥下载
    @Test
    public void upiZMYXZTest()  throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        //自定义域
        iso8583DTO.setReservedPrivate_60("96000002400");//
        iso8583DTO.setReservedPrivate_62("9F0605DF000000039F220101");
        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, null);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        //String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        //System.out.println(Arrays.toString(split));
    }

    //主密钥更新
    @Test
    public void upiZMYGXTest()  throws Exception {
        String domain11 = IDS.uniqueID().toString().substring(0, 6);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0800");
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        //自定义域
        //iso8583DTO.setReservedPrivate_60("9600000240000");//01000001000000000
        ////iso8583DTO.setReservedPrivate_62("9F0605DF000000039F220101");
        iso8583DTO.setReservedPrivate_60("99000002003");//01000001000000000
        iso8583DTO.setReservedPrivate_62("DF998180" + "5A9559A5F684F3988224B4FC8C45934C20994D27DF777039BC0B9D2D32B097DC4DFD1908875D58EEFBFF44B10D12B8C7512CF94BFFEAF6DDE46CA47CE672DEB961E9360FD4CFC5CB95B1DE02E6E744A169C0F2F4658051A387AC8269EC0ABB38AAA21E80D8046781E4577EB53917C549EDA64A3C06C30691E948848FC72CF468" +"9F0605DF000000039F220101");
        iso8583DTO.setReservedPrivate_63("00185769819");
        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, null);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
        //String[] split = iso8583DTO1281.getAdditionalData_46().split("02");
        //System.out.println(Arrays.toString(split));
    }
    //消费
    @Test
    public void upiXFTest()  throws Exception {
        String domain11 = String.valueOf(System.currentTimeMillis()).substring(0, 6);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("190000");
        iso8583DTO.setAmountOfTransactions_4("000000000009");
        iso8583DTO.setSystemTraceAuditNumber_11(IDS.getRandomInt(6));
        iso8583DTO.setDateOfExpired_14("5012");
        iso8583DTO.setPointOfServiceEntryMode_22("021");
        iso8583DTO.setCardSequenceNumber_23("001");
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setPointOfServicePINCaptureCode_26("06");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        iso8583DTO.setPINData_52(pINEncryption("111111", "094200000000"));
        iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");

        //自定义域
        iso8583DTO.setReservedPrivate_60("22000001000600");//01000001000000000

        //银行卡号
        String var2 = "6250942000000001";
        //银行卡 磁道2信息
        String var35 = "6250942000000001=49121213715950580001";
        //加密信息
        iso8583DTO.setProcessingCode_2(var2);
        iso8583DTO.setTrack2Data_35(trkEncryption(var35, key_62));

        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, key);
        String sendMsg = "6000060000" + "601410190121" + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
    }

    //撤销
    @Test
    public void upiCXTest()  throws Exception {
        String domain11 = String.valueOf(System.currentTimeMillis()).substring(0, 6);
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("280000");
        iso8583DTO.setAmountOfTransactions_4("000000000100");
        iso8583DTO.setSystemTraceAuditNumber_11("159228");
        iso8583DTO.setDateOfExpired_14("5012");
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        iso8583DTO.setCardSequenceNumber_23("001");
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setRetrievalReferenceNumber_37("016811632025");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        //自定义域
        iso8583DTO.setReservedPrivate_60("22000001000600");//01000001000000000
        iso8583DTO.setOriginalMessage_61("000001159227");

        //银行卡号
        String var2 = "6250942000000001";
        //银行卡 磁道2信息
        String var35 = "6250942000000001=49121213715950580001";
        //加密信息
        iso8583DTO.setProcessingCode_2(var2);
        //iso8583DTO.setTrack2Data_35(trkEncryption(var35, key_62));

        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, key);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
    }

    //退货
    @Test
    public void upiTHTest()  throws Exception {
        String domain11 = String.valueOf(System.currentTimeMillis()).substring(0, 6);
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0220");
        iso8583DTO.setProcessingCode_3("200000");
        iso8583DTO.setAmountOfTransactions_4("000000000100");
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        iso8583DTO.setDateOfExpired_14("5012");
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        iso8583DTO.setCardSequenceNumber_23("001");
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setRetrievalReferenceNumber_37("016814632113");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(terminalId);
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(merchantId);
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        //自定义域
        iso8583DTO.setReservedPrivate_60("25000001000600");//01000001000000000
        iso8583DTO.setOriginalMessage_61("0000000000000616");
        iso8583DTO.setReservedPrivate_63("000");
        //银行卡号
        String var2 = "4761340000000019";
        //银行卡 磁道2信息
        String var35 = "4761340000000019=171210114991787";
        //加密信息
        iso8583DTO.setProcessingCode_2(var2);
        //iso8583DTO.setTrack2Data_35(trkEncryption(var35, key_62));

        //扫码组包
        String isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, key);
        String sendMsg = "6000060000" +"601410190121"+ isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        System.out.println(" ===  扫码sendMsg  ====   " + sendMsg);

        //Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, sendMsg.getBytes());
        Map<String, String> respMap = UpiIsoUtil.sendTCPRequest(ip, port, NumberStringUtil.str2Bcd(sendMsg));
        String result = respMap.get("respData");
        System.out.println(" ====  扫码result  ===   " + result);
        //解包
        ISO8583DTO iso8583DTO1281 = UpiIsoUtil.unpackISO8583DTO(result);
        System.out.println("扫码结果:" + JSON.toJSONString(iso8583DTO1281));
    }


    private static String trkEncryption(String str, String key) {
        //80-112 Trk密钥位
        String substring = key.substring(80, 112);
        String trk = Objects.requireNonNull(EcbDesUtil.decode3DEA("3104BAC458BA1513043E4010FD642619", substring)).toUpperCase();
        String newStr;
        if (str.length() % 2 != 0) {
            newStr = str.length() + str + "0";
        } else {
            newStr = str.length() + str;
        }
        byte[] bcd = NumberStringUtil.str2Bcd(newStr);
        return Objects.requireNonNull(EcbDesUtil.encode3DEA(trk, cn.hutool.core.util.HexUtil.encodeHexStr(bcd))).toUpperCase();

    }
    public static String pINEncryption(String pin, String pan) {

        byte[] apan = NumberStringUtil.formartPan(pan.getBytes());
        System.out.println("pan=== "+ ISOUtil.bytesToHexString(apan));
        byte[] apin = NumberStringUtil.formatPinByX98(pin.getBytes());
        System.out.println("pin=== "+ISOUtil.bytesToHexString(apin));
        byte[] xorMac = new byte[apan.length];
        for (int i = 0; i < apan.length; i++) {//异或
            xorMac[i] = apin[i] ^= apan[i];
        }
        System.out.println("异或==="+ISOUtil.bytesToHexString(xorMac));
        try {
            String substring = key_62.substring(0, 32);
            String pik = Objects.requireNonNull(EcbDesUtil.decode3DEA("3104BAC458BA1513043E4010FD642619", substring)).toUpperCase();
            System.out.println("===== pik ====="+pik);
            String s = DesUtil.doubleDesEncrypt(pik,ISOUtil.bytesToHexString(xorMac));
            System.out.println("===== pINEncryption ====="+s);
            return s;
        } catch (Exception e) {
            System.out.println("===== pINEncryption e ====="+e);
        }
        return null;
    }



}

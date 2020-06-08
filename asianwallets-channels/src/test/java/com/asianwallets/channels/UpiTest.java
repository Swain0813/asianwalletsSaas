package com.asianwallets.channels;

import com.asianwallets.channels.service.UpiService;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiDownDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.UpiRefundDTO;
import com.asianwallets.common.entity.Channel;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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



    @Test
    public void upiPayTest() {

        Channel channel  = new Channel();
        channel.setChannelMerchantId("549440159990001");

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("BACKSTAGEALIPAY");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003968");
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

        upiPayDTO.setUser_bank_card_no("6226388000000095");
        upiPayDTO.setCvn2("248");
        upiPayDTO.setValid("1219");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        upiService.upiPay(upiDTO);

    }

    @Test
    public void upiQuerryTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440159990001");

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("SEARCH");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003968");
        upiPayDTO.setOrder_no("pay20200603160725");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        upiService.upiQueery(upiDTO);
    }
    @Test
    public void upiRefundTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440159990001");

        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();
        upiRefundDTO.setVersion("2.0.0");
        upiRefundDTO.setTrade_code("REFUND");
        upiRefundDTO.setAgencyId(channel.getChannelMerchantId());
        upiRefundDTO.setTerminal_no("20003968");
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
        channel.setChannelMerchantId("549440159990001");

        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();
        upiRefundDTO.setVersion("2.0.0");
        upiRefundDTO.setTrade_code("PAYC");
        upiRefundDTO.setAgencyId(channel.getChannelMerchantId());
        upiRefundDTO.setTerminal_no("20003968");
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
        channel.setChannelMerchantId("549440159990001");

        UpiDownDTO upiDownDTO = new UpiDownDTO();
        upiDownDTO.setVersion("2.0.0");
        upiDownDTO.setTrade_code("DOWNLOAD_SETTLE_FILE");
        upiDownDTO.setAgencyId(channel.getChannelMerchantId());
        upiDownDTO.setTerminal_no("20003968");
        upiDownDTO.setSettle_date("20200602");
        upiDownDTO.setFile_type("TRAN");

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiDownDTO(upiDownDTO);
        upiService.upiDownSettle(upiDTO);
    }




}

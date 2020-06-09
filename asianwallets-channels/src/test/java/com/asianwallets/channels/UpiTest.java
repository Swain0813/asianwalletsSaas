package com.asianwallets.channels;

import com.asianwallets.channels.service.UpiService;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiDownDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.UpiRefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.HttpClientUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        upiPayDTO.setTrade_code("SEARCH");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003962");
        upiPayDTO.setOrder_no("O103753129326284");

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




}

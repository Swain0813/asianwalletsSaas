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

   private final static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsmm1ygBHGnFpO70WAHhQ" +
           "TmG0zLvbP7VlxNhHGQYJVDXuD28e8xMITRfCurFh+d6KGLSRSlZpPMVIHZ8RHWLn" +
           "JxVhtEFxR8CaT6bS1w2ci3kWyVlagGfyRFxtrLvX0rOKQhVD8kqoRMmAUuClctBz" +
           "FHKiAh/uImvXS7y+ggKRixAAeyGt0lWUgBRdoS6ZBlrpzNtsfeOHkWOYW1pyjEtl" +
           "z+hSHojg0e299qCwOMQqp/wnoQmeHozC4A6VtRwwi2XeYBUHyJZxS6YwikmroVWE" +
           "SWT3bBGN5CLjT6lnugRsX2GJKKR0C/ePzPvuPakw0jGE6Tdf36KMr3xIap0ebnEM" +
           "lwIDAQAB";
   private final static String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCT91frKB4gNfOhbCyg5nygclWln1gKmCp3UZIMaD3lv+Heak+jJa74Veud9uD8OPok+/KXydfXv7CyUK0/8Xkp0Kx8Wr9t6xZsytNmdlkvnD4H5/mHpeET5B1vMWG0ui3caLw/PbSuCUAGrubHrcrW2AN16hhJ7CSJdcg4Gm9LpwukwfuB1EAiVjgPrUBH09nQeEd/BSWQVk1opfbOxfactJmJWLLF+sYfauXGLQPfeqf1VWsYqsp3QdjMOrA0UhendbMvV3EL8eUUQ6V/AoBeVBCBqPi/IX1aWxKU8LHxbgVsDhHr5DV85Vuxvd3kcYuCzf2irTnSHBh4ASZSrmXTAgMBAAECggEABxEXF9oQpkUtX5ZFA2I+rYMIaCKWF9twm8Y/jWwlGKXzaaX2K7qPArlHyYGS+TjXaZDG40z2jA1RqRKFU7p6qO6Ybk//OIazyXpeYlRgqg9yPvprRnk7qLGKXlcjbXg+3Hn8E6Ek0fMLJcaQqxS3zU7/y0/k3RL0I8SGTXelvR0or94Kp9QZsjXqXIZomVHD+2C3ohFa/j015MqVVjFV6NDmhS5LvaY5viCFBKaaiXr1hLZeU6eHfr02ONWOllv1uyatnoxrhfh5r53tO76NZdsi5QbMirtzTUxNK2glQ83CcPfbSesReLYoUp4GKp7ylffkTS+cXipTII5gCOuMYQKBgQDD/qU4+71pxjfkcRcZUGmIhl3pzJI49Q1W+rGdqdI6jQrQvjJ44d3AuHiC1YDm74f4V5fs6LezGB4xAc4/UwKv4Y6LXxaD9y/HBjJPWpeLaxZCMHBHswA/9nBaJiBe6tcHEcpot/mztLy8eQUzfxANnBsCAUUbAbau4mofngMCmQKBgQDBRGWCaWaz0cNUkJ6Q/MiYhB237ZVaPLhZOwrpW15hGWljiC3WxGHGEYmaTCS8lTQJHtAOTEKo7zGlAZFLGbMuRCbjVmJ0Ki/oEQs27Pt3c8Uv9c5mqFkEZ2Jr5u+tKVkkFGRbB+Jgwc85P7oS3qoveOYpLkE5qMDpGnTKPUqbSwKBgAMSQ36QG1jD/W48A3OnGfBEwsX+KbBwzqO1TAE/fwbh3PCGen4AdBwoB+Ns+xcjW8sdfEj/IJnS3+4+q/+8Cke9TiGK/OxWfE4vH6y0q7lIlVG2Npw5BuD5uKsA+/Lg1TvMIJLhhy00wNxnPRigfhzofFfcszuIHFfDPox+SmfJAoGAQ7zvPygmIPxyU99kVDjlLd+QcvjIkhoaGtxA1M17ZNj9QS9nsZCfHEblTblGXvqEhHXSQlCkRIWhhs3n2MSnMp4cay3J0CFBLTtcDOI+uP1QraCfuQdkO9DohB0rACwv3B9xlR5MT9FeChi15Qs55u2e7ewSXF0zUAA/0upsjcUCgYBRlRiAl5Ad+CuHxkf3ZKzs/o/cOwit7tTYNv87MBonkYuzNuyW1rO5khk6gnzVAPAvkP97Wh2C6vL7jjfG3keKyFx6oTMelWoZ3RDSDQXbk7DyTxeRlZDEOPaALr1WmvgvN+ETXyo8HJderh6MLR8EdESG4MoK1rWQ3VG6Krha3A==";


    @Test
    public void upiPayTest() {

        Channel channel  = new Channel();
        channel.setChannelMerchantId("549440159990001");
        channel.setMd5KeyStr(privateKey);
        channel.setExtend5(publicKey);

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("UNIONZS");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        //upiPayDTO.setChild_merchant_no("574034451110001");
        upiPayDTO.setTerminal_no("20003968");
        upiPayDTO.setOrder_no("pay" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        upiPayDTO.setAmount("0.02");
        upiPayDTO.setCurrency_type("HKD");
        upiPayDTO.setSett_currency_type("HKD");
        upiPayDTO.setAuth_code("288009714923099525");
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

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        upiService.upiPay(upiDTO);

    }

    @Test
    public void upiQuerryTest() {

        Channel channel = new Channel();
        channel.setChannelMerchantId("549440159990001");
        channel.setMd5KeyStr(privateKey);
        channel.setExtend5(publicKey);

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
        channel.setMd5KeyStr(privateKey);
        channel.setExtend5(publicKey);

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
        channel.setMd5KeyStr(privateKey);
        channel.setExtend5(publicKey);

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
        channel.setMd5KeyStr(privateKey);
        channel.setExtend5(publicKey);

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

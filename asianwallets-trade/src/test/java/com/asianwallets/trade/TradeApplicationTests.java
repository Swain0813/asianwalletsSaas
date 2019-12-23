package com.asianwallets.trade;

import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.feign.ClearingFeign;
import com.asianwallets.trade.feign.MessageFeign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TradeApplication.class)
public class TradeApplicationTests {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private ClearingFeign clearingFeign;

    @Test
    public void test() {
        channelsFeign.xenditPay(null);
        clearingFeign.intoAndOutMerhtAccount(null);
        messageFeign.sendSimple("", "");
    }
}

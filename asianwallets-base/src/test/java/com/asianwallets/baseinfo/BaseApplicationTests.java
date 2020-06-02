package com.asianwallets.baseinfo;
import com.asianwallets.base.BaseApplication;
import com.asianwallets.base.dao.InstitutionProductMapper;
import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.impl.InstitutionServiceImpl;
import com.asianwallets.base.service.impl.TradeCheckAccountServiceImpl;
import com.asianwallets.common.entity.Orders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BaseApplication.class)
public class BaseApplicationTests {

    @Autowired
    private InstitutionServiceImpl institutionServiceimpl;

    @Autowired
    private InstitutionProductMapper institutionProductMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private TradeCheckAccountServiceImpl tradeCheckAccountService;


    @Test
    public void contextLoads() {
        Orders orders = new Orders();
        orders.setInstitutionId("3");
        orders.setInstitutionName("3");
        orders.setMerchantId("3");
        orders.setMerchantName("3");
        orders.setSecondMerchantName("3");
        orders.setSecondMerchantCode("3");
        orders.setAgentCode("3");
        orders.setAgentName("3");
        orders.setGroupMerchantCode("3");
        orders.setGroupMerchantName("3");
        orders.setTradeType((byte)3);
        orders.setTradeDirection((byte)3);
        orders.setMerchantOrderTime(new Date());
        orders.setMerchantOrderId("3");
        orders.setOrderAmount(new BigDecimal("3"));
        orders.setOrderCurrency("3");
        orders.setImei("3");
        orders.setOperatorId("3");
        orders.setOrderForTradeRate(new BigDecimal("3"));
        orders.setTradeForOrderRate(new BigDecimal("3"));
        orders.setExchangeRate(new BigDecimal("3"));
        orders.setExchangeTime(new Date());
        orders.setExchangeStatus((byte)3);
        orders.setProductCode(3);
        orders.setProductName("3");
        orders.setChannelCode("3");
        orders.setChannelName("3");
        orders.setTradeCurrency("3");
        orders.setTradeAmount(new BigDecimal("3"));
        orders.setTradeStatus((byte)3);
        orders.setCancelStatus((byte)3);
        orders.setRefundStatus((byte)3);
        orders.setConnectMethod((byte)3);
        orders.setSettleStatus((byte)3);
        orders.setChannelNumber("3");
        orders.setChargeStatus((byte)3);
        orders.setChargeTime(new Date());
        orders.setPayMethod("3");
        orders.setReqIp("3");
        orders.setChannelAmount(new BigDecimal("3"));
        orders.setReportNumber("3");
        orders.setReportChannelTime(new Date());
        orders.setChannelCallbackTime(new Date());
        orders.setUpChannelFee(new BigDecimal("3"));
        orders.setFloatRate(new BigDecimal("0.1000"));
        orders.setAddValue(new BigDecimal("3"));
        orders.setPayerName("3");
        orders.setPayerAccount("3");
        orders.setPayerBank("3");
        orders.setPayerEmail("3");
        orders.setPayerPhone("3");
        orders.setPayerAddress("3");
        orders.setInvoiceNo("3");
        orders.setProviderName("3");
        orders.setCourierCode("3");
        orders.setDeliveryTime(new Date());
        orders.setDeliveryStatus((byte) 3);
        orders.setReceivedStatus((byte) 3);
        orders.setReceivedTime(new Date());
        orders.setProductSettleCycle("3");
        orders.setIssuerId("3");
        orders.setBankName("3");
        orders.setBrowserUrl("3");
        orders.setServerUrl("3");
        orders.setFeePayer((byte)3);
        orders.setRateType("3");
        orders.setRate(new BigDecimal("3"));
        orders.setFee(new BigDecimal("3"));
        orders.setChannelFeeType("3");
        orders.setChannelRate(new BigDecimal("3"));
        orders.setChannelFee(new BigDecimal("3"));
        orders.setChannelGatewayCharge((byte)3);
        orders.setChannelGatewayStatus((byte)3);
        orders.setChannelGatewayFeeType("3");
        orders.setChannelGatewayRate(new BigDecimal("3"));
        orders.setChannelGatewayFee(new BigDecimal("3"));
        orders.setLanguage("3");
        orders.setSign("3");
        orders.setRemark3("3");
        orders.setRemark3("3");
        orders.setRemark3("3");
        orders.setRemark4("3");
        orders.setRemark5("3");
        orders.setRemark6("3");
        orders.setRemark7("3");
        orders.setRemark8("3");
        orders.setId("3");
        orders.setCreateTime(new Date());
        orders.setUpdateTime(new Date());
        orders.setCreator("3");
        orders.setModifier("3");
        orders.setRemark("3");
        ordersMapper.insert(orders);
    }
    @Test
    public void Testa() {
        tradeCheckAccountService.tradeCheckAccount();
    }


}

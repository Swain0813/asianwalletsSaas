package com.asianwallets.baseinfo;
import java.math.BigDecimal;
import java.util.Date;

import com.asianwallets.base.BaseApplication;
import com.asianwallets.base.dao.InstitutionProductMapper;
import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.impl.InstitutionServiceImpl;
import com.asianwallets.common.entity.Orders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BaseApplication.class)
public class BaseApplicationTests {

    @Autowired
    private InstitutionServiceImpl institutionServiceimpl;

    @Autowired
    private InstitutionProductMapper institutionProductMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Test
    public void contextLoads() {
        Orders orders = new Orders();
        orders.setInstitutionId("2");
        orders.setInstitutionName("2");
        orders.setMerchantId("2");
        orders.setMerchantName("2");
        orders.setSecondMerchantName("2");
        orders.setSecondMerchantCode("2");
        orders.setAgentCode("2");
        orders.setAgentName("2");
        orders.setGroupMerchantCode("2");
        orders.setGroupMerchantName("2");
        orders.setTradeType((byte)2);
        orders.setTradeDirection((byte)2);
        orders.setMerchantOrderTime(new Date());
        orders.setMerchantOrderId("2");
        orders.setOrderAmount(new BigDecimal("2"));
        orders.setOrderCurrency("2");
        orders.setImei("2");
        orders.setOperatorId("2");
        orders.setOrderForTradeRate(new BigDecimal("2"));
        orders.setTradeForOrderRate(new BigDecimal("2"));
        orders.setExchangeRate(new BigDecimal("2"));
        orders.setExchangeTime(new Date());
        orders.setExchangeStatus((byte)2);
        orders.setProductCode(2);
        orders.setProductName("2");
        orders.setChannelCode("2");
        orders.setChannelName("2");
        orders.setTradeCurrency("2");
        orders.setTradeAmount(new BigDecimal("2"));
        orders.setTradeStatus((byte)2);
        orders.setCancelStatus((byte)2);
        orders.setRefundStatus((byte)2);
        orders.setConnectMethod((byte)2);
        orders.setSettleStatus((byte)2);
        orders.setChannelNumber("2");
        orders.setChargeStatus((byte)2);
        orders.setChargeTime(new Date());
        orders.setPayMethod("2");
        orders.setReqIp("2");
        orders.setChannelAmount(new BigDecimal("2"));
        orders.setReportNumber("2");
        orders.setReportChannelTime(new Date());
        orders.setChannelCallbackTime(new Date());
        orders.setUpChannelFee(new BigDecimal("2"));
        orders.setFloatRate(new BigDecimal("0.1000"));
        orders.setAddValue(new BigDecimal("2"));
        orders.setPayerName("2");
        orders.setPayerAccount("2");
        orders.setPayerBank("2");
        orders.setPayerEmail("2");
        orders.setPayerPhone("2");
        orders.setPayerAddress("2");
        orders.setInvoiceNo("2");
        orders.setProviderName("2");
        orders.setCourierCode("2");
        orders.setDeliveryTime(new Date());
        orders.setDeliveryStatus(false);
        orders.setReceivedStatus(false);
        orders.setReceivedTime(new Date());
        orders.setProductSettleCycle("2");
        orders.setIssuerId("2");
        orders.setBankName("2");
        orders.setBrowserUrl("2");
        orders.setServerUrl("2");
        orders.setFeePayer((byte)2);
        orders.setRateType("2");
        orders.setRate(new BigDecimal("2"));
        orders.setFee(new BigDecimal("2"));
        orders.setChannelFeeType("2");
        orders.setChannelRate(new BigDecimal("2"));
        orders.setChannelFee(new BigDecimal("2"));
        orders.setChannelGatewayCharge((byte)2);
        orders.setChannelGatewayStatus((byte)2);
        orders.setChannelGatewayFeeType("2");
        orders.setChannelGatewayRate(new BigDecimal("2"));
        orders.setChannelGatewayFee(new BigDecimal("2"));
        orders.setLanguage("2");
        orders.setSign("2");
        orders.setRemark2("2");
        orders.setRemark2("2");
        orders.setRemark3("2");
        orders.setRemark4("2");
        orders.setRemark5("2");
        orders.setRemark6("2");
        orders.setRemark7("2");
        orders.setRemark8("2");
        orders.setId("2");
        orders.setCreateTime(new Date());
        orders.setUpdateTime(new Date());
        orders.setCreator("2");
        orders.setModifier("2");
        orders.setRemark("2");
        ordersMapper.insert(orders);
    }
   /* @Test
    public void Testa() {
        System.out.println(institutionServiceimpl.getMerchantByInId());
    }*/
}

package com.asianwallets.trade.rabbitmq.receive;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.nganluong.NganLuongMQDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.XMLUtil;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: NGANLUONG通道队列
 * @author: YangXu
 * @create: 2019-06-18 15:23
 **/
@Component
@Slf4j
public class NganLuongMQReciveSecond {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private CommonService commonService;

    @Value("${custom.nganLuong.check_url}")
    private String checkUrl;

    @RabbitListener(queues = "MQ_NGANLUONG_CHECK_ORDER_DL2")
    public void processNganLuongCheckOrder(String value) {
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        NganLuongMQDTO nganLuongMQDTO = JSON.parseObject(rabbitMassage.getValue(), NganLuongMQDTO.class);
        if (rabbitMassage.getCount() > 0) {
            //请求次数减一
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            log.info("==============【NL查询队列2】============== value: {}", value);
            Map<String, Object> map = new HashMap<>();
            map.put("merchant_id", nganLuongMQDTO.getChannelMerchantId());
            map.put("merchant_password", MD5.MD5Encode(nganLuongMQDTO.getMd5Key()));
            map.put("version", "3.1");
            map.put("function", "GetTransactionDetail");
            map.put("token", nganLuongMQDTO.getToken());
            log.info("==============【NL查询队列2】==============【查询参数记录】 map:{}", JSON.toJSONString(map));
            cn.hutool.http.HttpResponse execute = HttpRequest.post(checkUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(map)
                    .timeout(30000)
                    .execute();
            int status = execute.getStatus();
            String body = execute.body();
            log.info("==============【NL查询队列2】============== http状态码: {},body: {}", status, JSON.toJSONString(body));
            //判断HTTP状态码
            if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS) {
                log.info("==============【NL查询队列2】==============【状态码异常】");
                return;
            }
            // 注解方式xml转换为map对象
            if (StringUtils.isEmpty(body)) {
                log.info("==============【NL查询队列2】==============【调用查询接口返回body为空】");
                return;
            }
            Map<String, String> resultMap = null;
            try {
                resultMap = XMLUtil.xmlToMap(body, "UTF-8");
                log.info("==============【NL查询队列2】==============【解析后的XML结果】 resultMap:{}", JSON.toJSONString(resultMap));
            } catch (Exception e) {
                log.error("==============【NL查询队列2】==============【xml转换异常】", e);
            }
            String transactionStatus = resultMap.get("transaction_status");
            String token = resultMap.get("token");
            String errorCode = resultMap.get("error_code");
            String orderCode = resultMap.get("order_code");
            String transactionId = resultMap.get("transaction_id");
            //校验参数
            if (StringUtils.isEmpty(transactionStatus) || !"00".equals(errorCode)) {
                log.info("==============【NL查询队列2】==============【回调参数有错误 继续上报NL查询订单队列2 E_MQ_NGANLUONG_CHECK_ORDER_DL2】==========");
                rabbitMQSender.send(AD3MQConstant.E_MQ_NGANLUONG_CHECK_ORDER_DL2, JSON.toJSONString(rabbitMassage));
                return;
            }
            //查询原订单信息
            Orders orders = ordersMapper.selectByPrimaryKey(orderCode);
            if (orders == null) {
                log.info("==============【NL查询队列2】==============【查询订单信息不存在】 orderCode :{}", orderCode);
                return;
            }
            //订单已支付
            if (!orders.getTradeStatus().equals(TradeConstant.ORDER_PAYING)) {
                log.info("==============【NL查询队列2】==============【订单状态不为支付中】");
                return;
            }
            orders.setChannelNumber(transactionId);//通道流水号
            orders.setChannelCallbackTime(new Date());//通道回调时间
            orders.setUpdateTime(new Date());//修改时间
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("tradeStatus", "2");
            criteria.andEqualTo("id", orders.getId());
            if ("00".equals(transactionStatus)) {
                log.info("==============【NL查询队列2】============== 【订单已支付成功】 orderID : {}", orders.getId());
                //支付成功
                orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
                //更改channelsOrders状态
                try {
                    channelsOrderMapper.updateStatusById(orderCode, transactionId, TradeConstant.TRADE_SUCCESS);
                } catch (Exception e) {
                    log.error("==============【NL查询队列2】============== 【更新通道订单异常】", e);
                }
                //修改订单状态
                int i = ordersMapper.updateByExampleSelective(orders, example);
                if (i > 0) {
                    log.info("==============【NL查询队列2】============== 【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("==============【NL查询队列2】============== 【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                        if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                        }
                        //更新成功,上报清结算
                        commonService.fundChangePlaceOrderSuccess(orders);
                    } catch (Exception e) {
                        log.error("==============【NL查询队列2】============== 【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage massage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(massage));
                    }
                } else {
                    log.info("==============【NL查询队列2】============== 【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if ("02".equals(transactionStatus)) {
                log.info("==============【NL查询队列2】==============【订单已支付失败】 orderID : {}", orders.getId());
                //支付失败
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                //更改channelsOrders状态
                try {
                    channelsOrderMapper.updateStatusById(orderCode, transactionId, TradeConstant.TRADE_FALID);
                } catch (Exception e) {
                    log.error("==============【NL查询队列2】============== 【更新通道订单异常】", e);
                }
                //计算支付失败时通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【NL查询队列】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                } else {
                    log.error("==============【NL查询队列2】============== 【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else {
                log.info("==============【NL查询队列2】============== 【订单是交易中】 orderID : {}", orders.getId());
                log.info("==============【NL查询队列2】============== 【继续上报NL查询订单队列2】 【E_MQ_NGANLUONG_CHECK_ORDER_DL2】  token: {}", token);
                rabbitMQSender.send(AD3MQConstant.E_MQ_NGANLUONG_CHECK_ORDER_DL2, JSON.toJSONString(rabbitMassage));
                return;
            }
            //回调商户
            try {
                if (!StringUtils.isEmpty(orders.getServerUrl())) {
                    commonBusinessService.replyReturnUrl(orders);
                }
                if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                    commonBusinessService.replyBrowserUrl(orders);
                }
            } catch (Exception e) {
                log.info("==============【NL查询队列2】==============【回调商户异常】", e);
            }
        } else {
            log.info("==============【NL查询队列2】==============【二次查询,订单为交易中】 rabbitMassage : {}", JSON.toJSONString(rabbitMassage));
        }
    }
}

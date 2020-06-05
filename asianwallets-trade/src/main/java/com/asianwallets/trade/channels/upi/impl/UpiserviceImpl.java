package com.asianwallets.trade.channels.upi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.UpiRefundDTO;
import com.asianwallets.common.dto.upi.utils.CryptoUtil;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.upi.Upiservice;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-04 14:40
 **/
@Slf4j
@Service
@HandlerType(TradeConstant.UPI)
public class UpiserviceImpl extends ChannelsAbstractAdapter implements Upiservice {

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;
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
    private ClearingService clearingService;

    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiPayDTO upiPayDTO = this.createCSBDTO(orders, channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiPay(upiDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(jsonObject.get("qrCode"));
        return baseResponse;
    }

    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiPayDTO upiPayDTO = this.createBSCDTO(orders, channel,authCode);
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiPay(upiDTO);
        log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        //通道订单号
        orders.setChannelNumber(jsonObject.getString("pay_no"));
        ordersMapper.updateByPrimaryKeySelective(orders);
        //订单状态
        String status = jsonObject.getString("pay_result");
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("1".equals(status)) {
            //支付成功
            log.info("==================【UPI线下BSC】==================【支付成功】orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), jsonObject.getString("pay_no"), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【UPI线下BSC】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI线下BSC】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【UPI线下BSC】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【UPI线下BSC】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【UPI线下BSC】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【UPI线下BSC】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        }else if("2".equals(status)){
            //支付失败
            log.info("==================【UPI线下BSC】==================【支付失败】orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(jsonObject.getString("resp_desc"));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【UPI线下BSC】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI线下BSC】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【UPI线下BSC】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }else{
            //未支付
            log.info("==================【UPI线下BSC】==================【未支付】orderId: {}", orders.getId());

        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund,RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiRefundDTO upiRefundDTO = this.createRefundDTO(orderRefund, channel);
        upiDTO.setUpiRefundDTO(upiRefundDTO);
        log.info("==================【UPI退款】==================【调用Channels服务】【UPI-接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiRefund(upiDTO);
        log.info("==================【UPI退款】==================【调用Channels服务】【UPI-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));



        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate 创建退款DTO
     * @return
     **/
    private UpiRefundDTO createRefundDTO(OrderRefund orderRefund, Channel channel) {
        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();

        return upiRefundDTO;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate 创建bscDTO
     * @return
     **/
    private UpiPayDTO createBSCDTO(Orders orders, Channel channel,String authCode) {
        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("BACKSTAGEUNION");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orders.getId());
        upiPayDTO.setAmount(orders.getTradeAmount().toString());
        upiPayDTO.setCurrency_type(orders.getTradeCurrency());
        upiPayDTO.setSett_currency_type(orders.getTradeCurrency());
        upiPayDTO.setProduct_name(channel.getExtend6());
        upiPayDTO.setAuth_code(authCode);
        upiPayDTO.setReturn_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setNotify_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setClient_ip(orders.getReqIp());
        return upiPayDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/4
     * @Descripate 创建CSBDTO
     **/
    private UpiPayDTO createCSBDTO(Orders orders, Channel channel) {
        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("UNIONZS");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orders.getId());
        upiPayDTO.setAmount(orders.getTradeAmount().toString());
        upiPayDTO.setCurrency_type(orders.getTradeCurrency());
        upiPayDTO.setSett_currency_type(orders.getTradeCurrency());
        upiPayDTO.setProduct_name(channel.getExtend6());
        upiPayDTO.setReturn_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setNotify_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setClient_ip(orders.getReqIp());
        return upiPayDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate upi回调
     **/
    @Override
    public String upiServerCallback(JSONObject jsonObject) {
        JSONObject json;
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(this.getClass().getResource(ad3ParamsConfig.getUpiPublicKeyPath()).getPath(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(this.getClass().getResource(ad3ParamsConfig.getUpiPrivateKeyPath()).getPath(), "pem", null, "RSA");


            String result = CryptoUtil.respDecryption(jsonObject, hzfPriKey, yhPubKey);
            log.info("===============【upi回调】===============【返回】 result: {}", result);
            json = (JSONObject) JSONObject.parse(result);
        } catch (Exception e) {
            log.info("================【upi回调】================【异常】", e);
            return "success";
        }
        //查询订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(json.get("order_no").toString());
        if (orders == null) {
            log.info("==================【upi回调】==================【订单为空】");
            return "success";
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【upi回调】=================【订单状态不为支付中】");
            return "success";
        }
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("1".equals(json.get("pay_result").toString())) {
            //支付成功
            log.info("=================【upi回调】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            orders.setChannelNumber(json.get("pay_no").toString());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【upi回调】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【upi回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【upi回调】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【upi回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【upi回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【upi回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }

        } else if ("2".equals(json.get("pay_result").toString())) {
            //支付失败
            log.info("=================【upi回调】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(json.get("resp_desc").toString());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), json.get("pay_no").toString(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【upi回调】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【upi回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【upi回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }

        } else {
            log.info("=================【upi回调】=================【订单是交易中】 orderId: {}", orders.getId());
            return "success";
        }

        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【QfPay服务器回调】=================【回调商户异常】", e);
        }


        return "success";
    }
}

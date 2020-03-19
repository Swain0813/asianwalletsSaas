package com.asianwallets.trade.channels.nextpos.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.megapay.NextPosCallbackDTO;
import com.asianwallets.common.dto.megapay.NextPosQueryDTO;
import com.asianwallets.common.dto.megapay.NextPosRefundDTO;
import com.asianwallets.common.dto.megapay.NextPosRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-19 16:47
 **/
@Slf4j
@Service
@Transactional
@HandlerType(TradeConstant.NEXTPOS)
public class NextPosServiceImpl extends ChannelsAbstractAdapter implements NextPosService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Value("${custom.nextPosUrl}")
    private String nextPosUrl;

    /**
     * NextPos线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //NextPos-CSB接口请求实体
        NextPosRequestDTO nextPosRequestDTO = new NextPosRequestDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/nextPosCallback"));
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【NextPos-CSB接口请求参数】 nextPosRequestDTO: {}", JSON.toJSONString(nextPosRequestDTO));
        BaseResponse channelResponse = channelsFeign.nextPosCsb(nextPosRequestDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【NextPos-CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        //NextPos-CSB接口请求实体
        NextPosRequestDTO nextPosRequestDTO = new NextPosRequestDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/nextPosCallback"));
        log.info("==================【线上动态扫码】==================【调用Channels服务】【NextPos-CSB接口请求参数】 nextPosRequestDTO: {}", JSON.toJSONString(nextPosRequestDTO));
        BaseResponse channelResponse = channelsFeign.nextPosCsb(nextPosRequestDTO);
        log.info("==================【线上动态扫码】==================【调用Channels服务】【NextPos-CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线上动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * NextPos回调
     *
     * @param map      响应参数
     * @param response 响应实体
     */
    @Override
    public void nextPosCallback(Map<String, Object> map, HttpServletResponse response) {
        String einv = String.valueOf(map.get("einv"));
        if (einv.startsWith("CBO")) {
            log.info("================【NextPos回调】================【该笔回调订单属于AD3】 orderId:{}", einv);
            log.info("================【NextPos回调】================【分发AD3URL】ad3URL: {}", nextPosUrl);
            log.info("================【NextPos回调】================【分发AD3参数】map: {}", JSON.toJSONString(map));
            //分发给AD3
            cn.hutool.http.HttpResponse execute = HttpRequest.post(nextPosUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(map)
                    .timeout(20000)
                    .execute();
            String body = execute.body();
            log.info("================【NextPos回调】================【AD3回调返回参数】 http状态码:{}, body:{}", execute.getStatus(), body);
            try {
                response.getWriter().write(body);
            } catch (IOException e) {
                log.info("================【NextPos回调】================【接口异常】", e);
            }
            return;
        }
        Orders orders = ordersMapper.selectByPrimaryKey(einv);
        if (orders == null) {
            log.info("================【NextPos回调】================【回调订单信息不存在】");
            return;
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【NextPos回调】=================【订单状态不为支付中】");
            try {
                response.getWriter().write("00");
            } catch (IOException e) {
                log.info("================【NextPos回调】================【接口异常】", e);
            }
            return;
        }
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        //订单状态
        String status = String.valueOf(map.get(channel.getPayCode()));
        String refCode = String.valueOf(map.get("refCode"));
        String amt = String.valueOf(map.get("amt"));
        String transactionID = String.valueOf(map.get("transactionID"));
        String mark = String.valueOf(map.get("mark"));
        NextPosCallbackDTO nextPosCallbackDTO = new NextPosCallbackDTO();
        nextPosCallbackDTO.setEinv(einv);//订单id
        nextPosCallbackDTO.setRefCode(refCode);//响应码
        nextPosCallbackDTO.setAmt(amt);//金额
        nextPosCallbackDTO.setTransactionID(transactionID);//通道流水号
        nextPosCallbackDTO.setMark(mark);//签名
        nextPosCallbackDTO.setStatus(status);//订单状态
        nextPosCallbackDTO.setMerRespID(channel.getPayCode());
        nextPosCallbackDTO.setMerRespPassword(channel.getMd5KeyStr());
        log.info("================【NextPos回调】================【回调参数记录】 nextPosCallbackDTO:{}", JSON.toJSONString(nextPosCallbackDTO));
        //校验订单参数
        if (!checkNextPosCallback(nextPosCallbackDTO)) {
            return;
        }
        //金额转换,格式化设置"#,##0.00"
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Double amtDouble = new Double(nextPosCallbackDTO.getAmt());
        String amtSign = decimalFormat.format(amtDouble);
        String respCode = new String(new Base64().decode(nextPosCallbackDTO.getRefCode().getBytes()));
        log.info("================【NextPos回调】================ respCode: {}", respCode);
        //组装签名前的明文
        String clearText = respCode + nextPosCallbackDTO.getEinv() + nextPosCallbackDTO.getMerRespPassword() + nextPosCallbackDTO.getMerRespID()
                + nextPosCallbackDTO.getStatus() + amtSign;
        log.info("================【NextPos回调】================【签名前的明文】  clearText: {}", clearText);
        String sign = MD5.MD5Encode(clearText).toUpperCase();
        log.info("================【NextPos回调】================【签名后的密文】 sign: {}", sign);
        if (!sign.equals(nextPosCallbackDTO.getMark())) {
            log.info("================【NextPos回调】================【签名不匹配】");
            return;
        }
        if ("000000000000".equals(respCode)) {
            log.info("================【NextPos回调】================【有问题的transaction,联系NextPos】");
            return;
        }
        //校验订单信息
        if (new BigDecimal(nextPosCallbackDTO.getAmt()).compareTo(orders.getTradeAmount()) != 0) {
            log.info("================【NextPos回调】================【订单信息不匹配】");
            return;
        }
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //通道流水号
        orders.setChannelNumber(nextPosCallbackDTO.getTransactionID());
        //将respCode存入订单,退款时用
        orders.setSign(respCode);
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("000".equals(nextPosCallbackDTO.getStatus())) {
            log.info("================【NextPos回调】================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //更新订单信息
            if (ordersMapper.updateByPrimaryKeySelective(orders) == 1) {
                log.info("=================【NextPos回调】下单信息记录=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【NextPos回调】=================【上报清结算前线下下单创建账户信息】");
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
                        log.info("=================【NextPos回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【NextPos回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【NextPos回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if ("099".equals(nextPosCallbackDTO.getStatus())) {
            log.info("==================【NextPos回调】==================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(nextPosCallbackDTO.getStatus());
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【NextPos回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【NextPos回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("================【NextPos回调】================【订单是其他状态】 orderId: {}", orders.getId());
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
            response.getWriter().write("00");
        } catch (Exception e) {
            log.error("=================【NextPos回调】=================【回调商户异常】", e);
        }
    }

    /**
     * 校验nextPos服务回调参数
     *
     * @param nextPosCallbackDTO nextPos回调实体
     * @return 布尔值
     */
    private boolean checkNextPosCallback(NextPosCallbackDTO nextPosCallbackDTO) {
        if (StringUtils.isEmpty(nextPosCallbackDTO.getAmt())) {
            log.info("================【NextPos回调】================【金额为空】");
            return false;
        }
        if (StringUtils.isEmpty(nextPosCallbackDTO.getEinv())) {
            log.info("================【NextPos回调】================【订单id为空】");
            return false;
        }
        if (StringUtils.isEmpty(nextPosCallbackDTO.getMark())) {
            log.info("================【NextPos回调】================【签名为空】");
            return false;
        }
        if (StringUtils.isEmpty(nextPosCallbackDTO.getStatus())) {
            log.info("================【NextPos回调】================【交易结果为空】");
            return false;
        }
        return true;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        NextPosRefundDTO nextPosRefundDTO = new NextPosRefundDTO(orderRefund, channel);
        log.info("=================【NextPos退款】=================【请求Channels服务NextPos退款】请求参数 nextPosRefundDTO: {} ", JSON.toJSONString(nextPosRefundDTO));
        BaseResponse response = channelsFeign.nextPosRefund(nextPosRefundDTO);
        log.info("=================【NextPos退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            Map<String, Object> respMap = (Map<String, Object>) response.getData();
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【NextPos退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, String.valueOf(respMap.get("transactionID")), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【NextPos退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【NextPos退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【NextPos退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【NextPos退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【NextPos退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【NextPos退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 撤销
     **/
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse response = new BaseResponse();
        NextPosQueryDTO nextPosQueryDTO = new NextPosQueryDTO(orderRefund.getOrderId(), channel);
        log.info("=================【NextPos撤销】=================【请求Channels服务NextPos查询】请求参数 nextPosQueryDTO: {} ", JSON.toJSONString(nextPosQueryDTO));
        BaseResponse baseResponse = channelsFeign.nextPosQuery(nextPosQueryDTO);
        log.info("=================【NextPos撤销】=================【Channels服务响应】请求参数 baseResponse: {} ", JSON.toJSONString(baseResponse));
        if (baseResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            Map<String, Object> map = (Map<String, Object>) baseResponse.getData();
            if (baseResponse.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                //交易成功
                String refCode = String.valueOf(map.get("refCode"));
                Base64 b64 = new Base64();
                byte[] asciiByteArr = b64.decode(refCode.getBytes());
                String respCode = new String(asciiByteArr);
                orderRefund.setSign(respCode);
                if (map.get(channel.getPayCode()).equals("SUCCESS")) {
                    //更新订单状态
                    if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, null, new Date()) == 1) {
                        //更新成功
                        response = this.cancelPaying(channel, orderRefund, null);
                    } else {
                        response.setCode(EResultEnum.REFUNDING.getCode());
                        //更新失败后去查询订单信息
                        rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                    }
                } else if (map.get(channel.getPayCode()).equals("PAYERROR")) {
                    //交易失败
                    response.setCode(EResultEnum.REFUND_FAIL.getCode());
                    log.info("=================【NextPos撤销】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, null, new Date());
                } else {
                    response.setCode(EResultEnum.REFUND_FAIL.getCode());
                    log.info("=================【NextPos撤销】================= 【其他状态】orderId : {}", orderRefund.getOrderId());
                }
            } else {
                //请求失败
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【NextPos撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【NextPos撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     **/
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        //获取原订单的refCode字段(NextPos用)
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        NextPosRefundDTO nextPosRefundDTO = new NextPosRefundDTO(orderRefund, channel);
        log.info("=================【NextPos撤销 cancelPaying】=================【请求Channels服务NextPos退款】请求参数 nextPosRefundDTO: {} ", JSON.toJSONString(nextPosRefundDTO));
        BaseResponse response = channelsFeign.nextPosRefund(nextPosRefundDTO);
        log.info("=================【NextPos退款 cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                //撤销成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【NextPos退款 cancelPaying】=================【撤销成功】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                //撤销失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【NextPos退款 cancelPaying】=================【撤销失败】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【NextPos退款 cancelPaying】=================【请求失败】orderId : {}", orders.getId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【NextPos退款 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));

        }

        return baseResponse;
    }
}

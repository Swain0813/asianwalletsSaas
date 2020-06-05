package com.asianwallets.trade.channels.qfpay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.qfpay.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.qfpay.QfPayService;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.dto.QfPayCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.QFPAY)
public class QfPayServiceImpl extends ChannelsAbstractAdapter implements QfPayService {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;


    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        QfPayCSBDTO qfPayCSBDTO = new QfPayCSBDTO(orders, channel);
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setOrderId(orders.getId());
        qfPayDTO.setReqIp(orders.getReqIp());
        qfPayDTO.setChannel(channel);
        qfPayDTO.setQfPayCSBDTO(qfPayCSBDTO);
        log.info("==================【Qfpay线下CSB】==================【调用Channels服务】【QfPay-CSB接口】  qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        BaseResponse channelResponse = channelsFeign.qfPayCSB(qfPayDTO);
        log.info("==================【Qfpay线下CSB】==================【调用Channels服务】【QfPay-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【Qfpay线下CSB】==================【调用Channels服务】【QfPay-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        JSONObject jsonObject = JSONObject.fromObject(channelResponse.getData());
        //通道订单号
        orders.setChannelNumber(jsonObject.getString("orderNum"));
        ordersMapper.updateByPrimaryKeySelective(orders);
        //构造查询实体
        QfPayDTO qfPayInquiryDTO = new QfPayDTO();
        qfPayInquiryDTO.setChannel(channel);
        QfPayQueryDTO qfPayQueryDTO = new QfPayQueryDTO(channel, orders);
        qfPayInquiryDTO.setQfPayQueryDTO(qfPayQueryDTO);
        log.info("==================【Qfpay线下CSB】==================【上报查询队列】【MQ_QFPAY_CSB_CHECK_ORDER-CSB】");
        //QfPayCSB查询队列
        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.FIVE, JSON.toJSONString(qfPayInquiryDTO));
        rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_CSB_CHECK_ORDER, JSON.toJSONString(rabbitMassage));
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(jsonObject.getString("qrcode"));
        return baseResponse;
    }

    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        QfPayBSCDTO qfPayBSCDTO = new QfPayBSCDTO(orders, channel, authCode);
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setOrderId(orders.getId());
        qfPayDTO.setReqIp(orders.getReqIp());
        qfPayDTO.setChannel(channel);
        qfPayDTO.setQfPayBSCDTO(qfPayBSCDTO);
        log.info("==================【线下BSC】==================【调用Channels服务】【QfPay-BSC接口】  qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        BaseResponse channelResponse = channelsFeign.qfPayBSC(qfPayDTO);
        log.info("==================【线下BSC】==================【调用Channels服务】【QfPay-BSC接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下BSC】==================【调用Channels服务】【QfPay-BSC接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        JSONObject jsonObject = JSONObject.fromObject(channelResponse.getData());
        //通道订单号
        orders.setChannelNumber(jsonObject.getString("orderNum"));
        ordersMapper.updateByPrimaryKeySelective(orders);
        //订单状态
        String status = jsonObject.getString("status");
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("0000".equals(status)) {
            log.info("=================【QfPay-BSC】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【QfPay-BSC】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【QfPay-BSC】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【QfPay-BSC】=================【上报清结算前线下下单创建账户信息】");
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
                        log.info("=================【QfPay-BSC】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【QfPay-BSC】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【QfPay-BSC】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if ("1143".equals(status)) {
            log.info("=================【QfPay-BSC】=================【订单是交易中】 orderId: {}", orders.getId());
        } else {
            log.info("=================【QfPay-BSC】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(status);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【QfPay-BSC】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【QfPay-BSC】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【QfPay-BSC】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        return null;
    }

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        return null;
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
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orderRefund.getReqIp());
        QfPayRefundDTO qfPayRefundDTO = new QfPayRefundDTO(channel, orderRefund);
        qfPayDTO.setQfPayRefundDTO(qfPayRefundDTO);
        log.info("=================【QfPay退款】=================【请求Channels服务QfPay退款】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse response = channelsFeign.qfPayRefund(qfPayDTO);
        log.info("=================【QfPay退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【QfPay退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, qfResDTO.getOrderNum(), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else if ("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())) {
                //退款中
                if (rabbitMassage == null) {
                    rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                }
                log.info("===============【QfPay退款】===============【退款操作 退款中查询上报队列 E_MQ_QFPAY_REFUND_SEARCH】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_REFUND_SEARCH, JSON.toJSONString(rabbitMassage));

            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【QfPay退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【QfPay退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【QfPay退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【QfPay退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【QfPay退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
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
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orderRefund.getReqIp());
        QfPayQueryDTO qfPayQueryDTO = new QfPayQueryDTO(channel, orderRefund);
        qfPayDTO.setQfPayQueryDTO(qfPayQueryDTO);
        log.info("============【QfPay cancel】========【请求Channels服务qfPayQuery】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse baseResponse = channelsFeign.qfPayQuery(qfPayDTO);
        log.info("=================【QfPay cancel】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));

        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                //交易成功
                //更新订单状态
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, qfResDTO.getOrderNum(), new Date()) == 1) {
                    //更新成功
                    response = this.cancelPaying(channel, orderRefund, null);
                } else {
                    response.setCode(EResultEnum.REFUNDING.getCode());
                    //更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }
            } else if ("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())) {
                //交易中
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【QfPay cancel】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            } else {
                //交易失败
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay cancel】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, qfResDTO.getOrderNum(), new Date());
            }
        } else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【QfPay cancel】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
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
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orders.getReqIp());
        QfPayRefundDTO qfPayRefundDTO = new QfPayRefundDTO(channel, orders);
        qfPayDTO.setQfPayRefundDTO(qfPayRefundDTO);
        log.info("=================【QfPay cancelPaying】=================【请求Channels服务QfPay退款】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse response = channelsFeign.qfPayRefund(qfPayDTO);
        log.info("=================【QfPay cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                //撤销成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【QfPay cancelPaying】=================【撤销成功】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else if ("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())) {
                //退款中
                if (rabbitMassage == null) {
                    rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                }
                log.info("=================【QfPay cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_CANNEL_SEARCH, JSON.toJSONString(rabbitMassage));
            } else {
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay cancelPaying】=================【撤销失败】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【QfPay cancelPaying】=================【请求失败】orderId : {}", orders.getId());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("=================【QfPay cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * qfPay服务器回调
     *
     * @param qfPayCallbackDTO QfPay回调实体
     * @return
     */
    @Override
    public String qfPayServerCallback(QfPayCallbackDTO qfPayCallbackDTO) {
        //校验输入参数
        if (StringUtils.isEmpty(qfPayCallbackDTO.getOrderNum())
                || StringUtils.isEmpty(qfPayCallbackDTO.getStatus())
                || StringUtils.isEmpty(qfPayCallbackDTO.getSign())) {
            log.info("==================【QfPay服务器回调】==================【参数值为空】");
            return "success";
        }
        //查询订单信息
        Orders orders = ordersMapper.selectByChannelNumber(qfPayCallbackDTO.getOrderNum());
        if (orders == null) {
            log.info("==================【QfPay服务器回调】==================【订单为空】");
            return "success";
        }
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        //验签
        if (!checkSign(BeanToMapUtil.beanToStringMap(qfPayCallbackDTO), qfPayCallbackDTO.getSign(), channel.getMd5KeyStr())) {
            log.info("==================【QfPay服务器回调】==================【签名不匹配】");
            return "success";
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【QfPay服务器回调】=================【订单状态不为支付中】");
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
        if ("0000".equals(qfPayCallbackDTO.getStatus())) {
            log.info("=================【QfPay服务器回调】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【QfPay服务器回调】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【QfPay服务器回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【QfPay服务器回调】=================【上报清结算前线下下单创建账户信息】");
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
                        log.info("=================【QfPay服务器回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                       RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【QfPay服务器回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                   RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【QfPay服务器回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if ("1143".equals(qfPayCallbackDTO.getStatus())) {
            log.info("=================【QfPay服务器回调】=================【订单是交易中】 orderId: {}", orders.getId());
            return "success";
        } else {
            log.info("=================【QfPay服务器回调】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(qfPayCallbackDTO.getStatus());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【QfPay服务器回调】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【QfPay服务器回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【QfPay服务器回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
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

    /**
     * 验签
     *
     * @param map    参数Map
     * @param sign   验证的签名
     * @param secret md5Key
     * @return 布尔值
     **/
    private boolean checkSign(Map<String, String> map, String sign, String secret) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(map.keySet());
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            if (StringUtil.isBlank(value)) {
                continue;
            }
            content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
        }
        content.append(secret);
        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc = signSrc.replaceFirst("&", "");
        }
        log.info("==================【QfPay服务器回调】==================【签名前的明文】  signSrc: {}", signSrc);
        String newSign = MD5.MD5Encode(signSrc).toUpperCase();
        log.info("==================【QfPay服务器回调】==================【签名后的密文】  newSign: {}", newSign);
        log.info("==================【QfPay服务器回调】==================【接受到的密文】  sign: {}", sign);
        return newSign.equals(sign);
    }
}

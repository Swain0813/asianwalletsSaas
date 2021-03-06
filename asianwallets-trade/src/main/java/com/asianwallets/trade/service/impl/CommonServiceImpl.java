package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.*;
import com.asianwallets.common.dto.ArtificialDTO;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * 共通模块的实现类
 */
@Service
@Slf4j
@Transactional
public class CommonServiceImpl implements CommonService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private PreOrdersMapper preOrdersMapper;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private OrderRefundMapper orderRefundMapper;


    /**
     * 校验密码
     *
     * @param oldPassword
     * @param password
     * @return
     */
    @Override
    public Boolean checkPassword(String oldPassword, String password) {
        return passwordEncoder.matches(oldPassword, password);
    }


    /**
     * 人工回调
     *
     * @param artificialDTO artificialDTO
     * @return
     */
    @Override
    public BaseResponse artificialCallback(ArtificialDTO artificialDTO) {
        BaseResponse baseResponse = new BaseResponse();
        //查询订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(artificialDTO.getOrderId());
        if (orders == null) {
            log.info("=================【人工回调】=================【回调订单信息不存在】");
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus()) && !TradeConstant.ORDER_PAY_FAILD.equals(orders.getTradeStatus())) {
            log.info("=================【人工回调】=================【订单状态不为支付中或支付失败】");
            throw new BusinessException(EResultEnum.DEVICE_OPERATION_FAILED.getCode());
        }
        //通道流水号
        orders.setChannelNumber(artificialDTO.getChannelNumber());
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //修改人
        orders.setModifier(artificialDTO.getUserName());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", 2);
        criteria.andEqualTo("id", orders.getId());
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andEqualTo("tradeStatus", 4);
        criteria2.andEqualTo("id", orders.getId());
        example.or(criteria2);
        if (TradeConstant.ORDER_PAY_SUCCESS.equals(artificialDTO.getTradeStatus())) {
            log.info("=================【人工回调】=================【订单已支付成功】 orderId: {}", orders.getId());
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("==============【人工回调】==============【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【人工回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                if(orders.getPreRemark().equals("预授权")){
                    //如果订单是预授权的订单要去更新预授权订单表--->预授权完成以及完成金额
                    preOrdersMapper.updatePreStatusByMerchantOrderId(orders.getMerchantOrderId(),orders.getOrderAmount(),orders.getTradeAmount(),
                            artificialDTO.getUserName(),TradeConstant.PRE_ORDER_COMPLETE_SUCCESS);
                }
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【人工回调】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    this.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【人工回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【人工回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if (TradeConstant.ORDER_PAY_FAILD.equals(artificialDTO.getTradeStatus())) {
            log.info("==============【人工回调】==============【订单已支付失败】 orderId:{}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("==============【人工回调】==============【更新通道订单异常】", e);
            }
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【人工回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【人工回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【人工回调】=================【订单为其他状态】 orderId: {}", orders.getId());
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【人工回调】=================【回调商户异常】", e);
        }
        return baseResponse;
    }

    /**
     * 下单成功后上报清结算
     * 由于saas系统如果对接的通道能结算就不要上报清结算
     * @param orders
     */
    @Override
    public void fundChangePlaceOrderSuccess(Orders orders) {
        //查询通道
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        if(!channel.getChannelSupportSettle()){
            //更新成功,上报清结算
            FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
            //上报清结算资金变动接口
            BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
            log.info("=================【交易服务共通下单成功后上报清结算】=================【上报清结算返回信息】 fundChangeResponse:{}", JSON.toJSONString(fundChangeResponse));
            if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                log.info("=================【交易服务共通下单成功后上报清结算】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
            }
        }
    }


    /**
     * 退款失败调用清结算
     * @param orderRefund
     * @param channel
     */
    @Override
    public void orderRefundFailFundChange(OrderRefund orderRefund,Channel channel) {
        if(!channel.getChannelSupportSettle()){
            log.info("=====================【交易服务 退款】==================== 【退款单信息】 : {} ", JSON.toJSON(orderRefund));
            String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
            String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
            Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
            reconciliationMapper.insert(reconciliation);
            FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
            log.info("==================【交易服务 退款】================== 【调用资金变动接口输入参数】 cFundChange: {}", JSON.toJSONString(fundChangeDTO));
            BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
            if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                //调账成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                //改原订单状态
                commonBusinessService.updateOrderRefundFail(orderRefund);
            } else {
                //调账失败
                RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                log.info("=================【交易服务 退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
            }
        }else {
            //更新退款订单
            orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
            //改原订单状态
            commonBusinessService.updateOrderRefundFail(orderRefund);
        }

    }


}

package com.asianwallets.trade.channels.enets.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.dto.enets.EnetsSMRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.enets.EnetsService;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.EnetsPosCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.ENETS)
public class EnetsServiceImpl extends ChannelsAbstractAdapter implements EnetsService {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;


    /**
     * Enets线下CSB方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        EnetsSMRequestDTO enetsSMRequestDTO = new EnetsSMRequestDTO(orders, channel);
        EnetsOffLineRequestDTO enetsOffLineRequestDTO = new EnetsOffLineRequestDTO(enetsSMRequestDTO, orders, channel);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Enets-CSB接口请求参数】 enetsOffLineRequestDTO: {}", JSON.toJSONString(enetsOffLineRequestDTO));
        BaseResponse channelResponse = channelsFeign.eNetsPosCSBPay(enetsOffLineRequestDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Enets-CSB接口响应参数】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * 校验enetsPos回调参数
     *
     * @param enetsPosCallbackDTO enetsPos通道回调实体
     * @return
     */
    private boolean checkPosCallback(EnetsPosCallbackDTO enetsPosCallbackDTO) {
        if (StringUtils.isEmpty(enetsPosCallbackDTO.getRetrieval_ref())) {
            log.info("=============【eNets线下Csb回调】=============【Retrieval为空】");
            return false;
        }
        if (StringUtils.isEmpty(enetsPosCallbackDTO.getResponse_code())) {
            log.info("=============【eNets线下Csb回调】=============【订单状态为空】");
            return false;
        }
        return true;
    }

    /**
     * EnetsCSB回调
     *
     * @param enetsPosCallbackDTO eNetsCsb回调实体
     * @return
     */
    @Override
    public ResponseEntity<Void> eNetsCsbCallback(EnetsPosCallbackDTO enetsPosCallbackDTO, HttpServletResponse response) {
        //校验订单参数
        if (!checkPosCallback(enetsPosCallbackDTO)) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        //根据回调数据查询通道订单
        ChannelsOrder channelsOrder = channelsOrderMapper.selectByRemarks(enetsPosCallbackDTO.getRetrieval_ref(), enetsPosCallbackDTO.getStan(), enetsPosCallbackDTO.getTxn_identifier());
        if (channelsOrder == null) {
            log.info("=============【eNets线下Csb回调】=============【根据回调数据查询通道订单失败】");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(channelsOrder.getId());
        if (orders == null) {
            log.info("=============【eNets线下Csb回调】=============【回调订单信息不存在】");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【eNets线下Csb回调】=================【订单状态不为支付中】");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        //通道流水号
        orders.setChannelNumber(enetsPosCallbackDTO.getTxn_identifier());
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("00".equals(enetsPosCallbackDTO.getResponse_code())) {
            log.info("=============【eNets线下Csb回调】=============【订单已支付成功】 orderId: {}", orders.getId());
            //支付成功
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), enetsPosCallbackDTO.getTxn_identifier(), TradeConstant.TRADE_SUCCESS);
                channelsOrderMapper.updateRemarkById(enetsPosCallbackDTO.getTxn_identifier(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【eNets线下Csb回调】=================【更新通道订单异常】", e);
            }
            //修改原订单状态
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【eNets线下Csb回调】下单信息记录=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【eNets线下Csb回调】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //TODO 分润
                    //if (!StringUtils.isEmpty(orders.getAgentCode())) {
                    //rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orders.getId());
                    //}
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【eNets线下Csb回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【eNets线下Csb回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【eNets线下Csb回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=============【eNets线下Csb回调】=============【订单已支付失败】 ordersId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), enetsPosCallbackDTO.getTxn_identifier(), TradeConstant.TRADE_FALID);
                channelsOrderMapper.updateRemarkById(enetsPosCallbackDTO.getTxn_identifier(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【eNets线下Csb回调】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【eNets线下Csb回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【eNets线下Csb回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.info("=================【eNets线下Csb回调】=================【回调商户异常】", e);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}

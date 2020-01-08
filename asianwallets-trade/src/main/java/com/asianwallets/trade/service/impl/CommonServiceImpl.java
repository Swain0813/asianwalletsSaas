package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.ArtificialDTO;
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
//        //修改人
//        orders.setModifier(artificialDTO.getUserName());
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
                    if (!org.apache.commons.lang.StringUtils.isEmpty(orders.getAgentCode())) {
                        rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【人工回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        com.asianwallets.common.dto.RabbitMassage rabbitMassage = new com.asianwallets.common.dto.RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【人工回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    com.asianwallets.common.dto.RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
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
}

package com.asianwallets.trade.service.impl;

import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.RefundTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-18 14:01
 **/
@Slf4j
@Service
@Transactional
public class RefundTradeServiceImpl implements RefundTradeService {

    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private CommonRedisDataService commonRedisDataService;
    @Autowired
    private OrdersMapper ordersMapper;
    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
     * @return
     **/
    @Override
    public BaseResponse refundOrder(RefundDTO refundDTO, String reqIp) {

        BaseResponse baseResponse = new BaseResponse();
        //签名校验
        if (!commonBusinessService.checkUniversalSign(refundDTO)) {
            log.info("-----------------【退款】信息记录--------------【签名错误】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }

        /**************************************************** 查询原订单 *************************************************/
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(refundDTO.getOrderNo());
        if (oldOrder == null) {
            //商户订单号不存在
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }

        Channel channel = commonRedisDataService.getChannelByChannelCode(oldOrder.getChannelCode());
        /********************************* 判断通道是否支持退款 线下不支持退款直接拒绝*************************************************/
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection()) && !channel.getSupportRefundState()) {
            throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
        }
        /********************************* 原订单撤销成功和撤销中不能退款*************************************************/
        if (TradeConstant.ORDER_CANNELING.equals(oldOrder.getCancelStatus())||TradeConstant.ORDER_CANNEL_SUCCESS.equals(oldOrder.getCancelStatus())) {
            //撤销的单子不能退款--该交易已撤销
            throw new BusinessException(EResultEnum.REFUND_CANCEL_ERROR.getCode());
        }
        /********************************* AD3-eNets退款只能当天退款---线下支付*************************************************/
        String channelCallbackTime = oldOrder.getChannelCallbackTime()==null? DateToolUtils.getReqDate(oldOrder.getCreateTime()):DateToolUtils.getReqDate(oldOrder.getChannelCallbackTime());
        String today = DateToolUtils.getReqDate();
        if (channel.getChannelCnName().toLowerCase().contains(AD3Constant.ENETS) && TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection())) {
            if (!channelCallbackTime.equals(today)) {
                throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
            }
        }



        if (TradeConstant.ORDER_PAY_SUCCESS.equals(oldOrder.getTradeStatus())) {
        /*************************************************************** 订单是付款成功的场合 *************************************************************/

        }else if (TradeConstant.ORDER_PAYING.equals(oldOrder.getTradeStatus())){
        /***************************************************************  订单是付款中的场合  *************************************************************/

        }


        return null;
    }



}

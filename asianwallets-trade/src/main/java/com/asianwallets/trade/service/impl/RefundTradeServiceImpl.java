package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.TcsCtFlowMapper;
import com.asianwallets.trade.dao.TcsStFlowMapper;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.RefundTradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private TcsCtFlowMapper tcsCtFlowMapper;
    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;
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
        /***************************************************************  检查是否可以退款或是撤销  *************************************************************/
        Merchant merchant = commonRedisDataService.getMerchantById(refundDTO.getMerchantId());
        //已退款金额
        BigDecimal oldRefundAmount = orderRefundMapper.getTotalAmountByOrderId(oldOrder.getId());
        oldRefundAmount = oldRefundAmount == null ? BigDecimal.ZERO : oldRefundAmount;
        String type = this.checkRefundDTO(merchant, refundDTO, oldOrder, oldRefundAmount);


        if (TradeConstant.ORDER_PAY_SUCCESS.equals(oldOrder.getTradeStatus())) {
        /*************************************************************** 订单是付款成功的场合 *************************************************************/

        }else if (TradeConstant.ORDER_PAYING.equals(oldOrder.getTradeStatus())){
        /***************************************************************  订单是付款中的场合  *************************************************************/

        }


        return null;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/19
     * @Descripate 校验退款单参数
     **/
    public String checkRefundDTO(Merchant merchant, RefundDTO refundDTO, Orders oldOrder, BigDecimal oldRefundAmount) {
        //1.商户编号是否存在
        if (merchant == null) {
            //商户编号不存在
            log.info("----------------- 校验退款参数 机构信息不存在 -------------- merchant : {} ,refundDTO : {},oldOrder :{}",  JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!TradeConstant.AUDIT_SUCCESS.equals(merchant.getAuditStatus())) {
            //商户状态检验
            log.info("----------------- 校验退款参数 商户状态检验不通过 -------------- institution : {} ,refundDTO : {},oldOrder :{}", JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
            throw new BusinessException(EResultEnum.INSTITUTION_STATUS_ABNORMAL.getCode());
        }
        if (!merchant.getEnabled()) {
            //商户状态检验
            log.info("----------------- 校验退款参数 商户启用禁用 -------------- institution : {} ,refundDTO : {},oldOrder :{}", JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
            throw new BusinessException(EResultEnum.INSTITUTION_STATUS_ABNORMAL.getCode());
        }

        //3.验证退款金额
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        //4.验证原订单交易状态状态(只有交易状态为交易成功和退款)
        Integer CTstatus = tcsCtFlowMapper.getCTstatus(oldOrder.getId());
        Integer STstatus = tcsStFlowMapper.getSTstatus(oldOrder.getId());
        if (TradeConstant.ORDER_CLEAR_SUCCESS.equals(CTstatus) && TradeConstant.ORDER_SETTLE_SUCCESS.equals(STstatus)) {
            //退款
            if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 1) {
                log.info("----------------- 校验退款参数 退款金额不合法 -------------- merchant : {} ,refundDTO : {},oldOrder :{}", JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
                throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
            }
            return TradeConstant.RF;
        } else {
            //部分退款的场合
            if (refundDTO.getRefundType() == 2) {
                throw new BusinessException(EResultEnum.ORDER_NOT_SETTLE.getCode());
            }
            //撤销
            if (newRefundAmount.compareTo(oldOrder.getTradeAmount()) == 1) {
                log.info("----------------- 校验退款参数 退款金额不合法 -------------- institution : {} ,refundDTO : {},oldOrder :{}", institution, JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
                throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
            }
            //线上退款,验证退款人的用户信息
            if (refundDTO.getTradeDirection() == 1) {
                //付款人姓名
                if (StringUtils.isBlank(refundDTO.getPayerName())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
                //付款人账户
                if (StringUtils.isBlank(refundDTO.getPayerAccount())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
                //付款人银行
                if (StringUtils.isBlank(refundDTO.getPayerBank())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
                //付款人邮箱
                if (StringUtils.isBlank(refundDTO.getPayerEmail())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
                //Swift Code
                if (StringUtils.isBlank(refundDTO.getSwiftCode())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
            }
            return TradeConstant.RV;
        }
    }


}

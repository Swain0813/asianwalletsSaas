package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.TcsCtFlowMapper;
import com.asianwallets.trade.dao.TcsStFlowMapper;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.RefundTradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

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
    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
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
        if (TradeConstant.ORDER_CANNELING.equals(oldOrder.getCancelStatus()) || TradeConstant.ORDER_CANNEL_SUCCESS.equals(oldOrder.getCancelStatus())) {
            //撤销的单子不能退款--该交易已撤销
            throw new BusinessException(EResultEnum.REFUND_CANCEL_ERROR.getCode());
        }
        /********************************* AD3-eNets退款只能当天退款---线下支付*************************************************/
        String channelCallbackTime = oldOrder.getChannelCallbackTime() == null ? DateToolUtils.getReqDate(oldOrder.getCreateTime()) : DateToolUtils.getReqDate(oldOrder.getChannelCallbackTime());
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
        /***************************************************************  校验退款单参数  *************************************************************/
        String type = this.checkRefundDTO(merchant, refundDTO, oldOrder, oldRefundAmount);
        /***************************************************************  创建订单  *************************************************************/
        OrderRefund orderRefund = this.creatOrderRefundSys(refundDTO, oldOrder);
        orderRefund.setReqIp(reqIp);
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == -1) {
            orderRefund.setRemark2("部分");
        } else if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 0) {
            orderRefund.setRemark2("全额");
        }
        /***************************************************************  计算退还手续费  *************************************************************/

        BigDecimal refundOrderFee = BigDecimal.ZERO;
        if (channel.getTransType() != null && TradeConstant.REFUND_ORDER_FEE == channel.getRefundingIsReturnFee()) {
            if ("全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
            } else {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
            }
        } else if (channel.getTransType() != null && TradeConstant.REFUND_TODAY_ORDER_FEE == channel.getTransType()) {
            //仅限当日退还的场合需要退还收单手续费
            if (channelCallbackTime.equals(today) && "全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
            } else if (channelCallbackTime.equals(today) && "部分".equals(orderRefund.getRemark2())) {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
            }
        }
        //五舍六入保留2位 只舍不入保留2位
        refundOrderFee =refundOrderFee.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        orderRefund.setRefundOrderFee(refundOrderFee);
        log.info("******************************后台系统和机构系统退款订单接口********************是否退还收单手续费:{},退还收单收单手续费金额:{},退款类型:{}******",channel.getRefundingIsReturnFee(),refundOrderFee,refundDTO.getRefundType());

        /***************************************************************  计算退款手续费  *************************************************************/



        if (TradeConstant.RV.equals(type) || TradeConstant.RF.equals(type)) {
            /*************************************************************** 订单是付款成功的场合 *************************************************************/


        } else if (TradeConstant.PAYING.equals(type)) {
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
            log.info("----------------- 校验退款参数 机构信息不存在 -------------- merchant : {} ,refundDTO : {},oldOrder :{}", JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
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
        } else if ((TradeConstant.ORDER_CLEAR_SUCCESS.equals(CTstatus) && TradeConstant.ORDER_SETTLE_WAIT.equals(STstatus))
                || (TradeConstant.ORDER_CLEAR_WAIT.equals(CTstatus) && STstatus == null)
        ) {
            //部分退款的场合
            if (refundDTO.getRefundType() == 2) {
                throw new BusinessException(EResultEnum.ORDER_NOT_SETTLE.getCode());
            }
            //撤销
            if (newRefundAmount.compareTo(oldOrder.getTradeAmount()) == 1) {
                log.info("----------------- 校验退款参数 退款金额不合法 -------------- merchant : {} ,refundDTO : {},oldOrder :{}", JSON.toJSON(merchant), JSON.toJSON(refundDTO), JSON.toJSON(oldOrder));
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
                //PayerPhone
                if (StringUtils.isBlank(refundDTO.getPayerPhone())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
            }
            return TradeConstant.RV;
        } else if (CTstatus == null && STstatus == null) {
            return TradeConstant.PAYING;
        } else {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     * 后台和机构退款设置属性值
     *
     * @param refundDTO
     * @param oldOrder
     * @return
     */
    public OrderRefund creatOrderRefundSys(RefundDTO refundDTO, Orders oldOrder) {
        OrderRefund orderRefund = new OrderRefund();
        BeanUtils.copyProperties(oldOrder, orderRefund);
        orderRefund.setLanguage(auditorProvider.getLanguage());//语言
        //检查币种是否一致，若不一致掉换汇接口----这块逻辑主要是针对部分退款的问题
        if (!oldOrder.getTradeCurrency().equals(refundDTO.getRefundCurrency())) {
            //调用汇率计算
            BigDecimal tradeAmount = refundDTO.getRefundAmount().multiply(oldOrder.getOrderForTradeRate());
            //转换的退款金额
            orderRefund.setTradeAmount(tradeAmount);
        } else {
            //退款金额
            orderRefund.setTradeAmount(refundDTO.getRefundAmount());
        }
        orderRefund.setTradeAmount(orderRefund.getTradeAmount().setScale(2, BigDecimal.ROUND_DOWN));
        orderRefund.setId("R" + IDS.uniqueID());//退款订单号
        orderRefund.setOrderId(oldOrder.getId());//原订单流水号
        orderRefund.setRefundType(refundDTO.getRefundType());//退款类型
        //全额退款的场合，直接拿订单表中的交易金额退款
        if (refundDTO.getRefundType() == 1) {
            orderRefund.setTradeAmount(oldOrder.getTradeAmount());//全额退款退原订单交易金额
        }
        orderRefund.setMerchantOrderTime(DateToolUtils.parseDate(refundDTO.getRefundTime(), DateToolUtils.DATE_FORMAT_DATETIME));//商户请求退款时间(商户所在地)
        orderRefund.setOrderAmount(refundDTO.getRefundAmount());//商户请求退款金额
        orderRefund.setOrderCurrency(refundDTO.getRefundCurrency());//客户请求收单币种
        orderRefund.setRefundStatus(TradeConstant.REFUND_WAIT);//待退款
        orderRefund.setPayerName(refundDTO.getPayerName());//付款人姓名
        orderRefund.setPayerAccount(refundDTO.getPayerAccount());//付款人账户
        orderRefund.setPayerBank(refundDTO.getPayerBank());//付款人银行
        orderRefund.setPayerEmail(refundDTO.getPayerEmail());//付款人邮箱
        orderRefund.setPayerPhone(refundDTO.getPayerPhone());//付款人电话
        orderRefund.setChannelRate(null);//通道费率
        orderRefund.setChannelFee(null);
        orderRefund.setChannelFeeType(null);
        //退款时的应结算时间就是当前退款时间
        orderRefund.setProductSettleCycle(DateToolUtils.formatTimestamp.format(new Date()));
        //备注
        orderRefund.setRemark(null);
        //备注1
        orderRefund.setRemark1(null);
        //备注2
        orderRefund.setRemark2(null);
        //备注3
        orderRefund.setRemark3(null);
        orderRefund.setChannelGatewayRate(null);//通道网关费率
        orderRefund.setChannelGatewayCharge(null);
        orderRefund.setChannelGatewayFee(null);
        orderRefund.setChannelGatewayFeeType(null);
        orderRefund.setChannelGatewayStatus(null);
        orderRefund.setCreateTime(new Date());//创建时间
        orderRefund.setUpdateTime(new Date());//修改时间
        orderRefund.setCreator(refundDTO.getModifier());//创建人
        orderRefund.setModifier(refundDTO.getModifier());//修改人
        return orderRefund;
    }

}

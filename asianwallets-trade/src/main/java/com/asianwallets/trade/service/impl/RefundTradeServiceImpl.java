package com.asianwallets.trade.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.RefundTradeService;
import com.asianwallets.trade.utils.HandlerContext;
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

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private HandlerContext handlerContext;

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
            log.info("=========================【退款 refundOrder】=========================【签名错误】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }


        /**************************************************** 查询原订单 *************************************************/
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(refundDTO.getOrderNo());
        if (oldOrder == null) {
            //商户订单号不存在
            log.info("=========================【退款 refundOrder】=========================【商户订单号不存在】");
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        Channel channel = commonRedisDataService.getChannelByChannelCode(oldOrder.getChannelCode());
        log.info("=========================【退款 refundOrder】========================= Channel:【{}】", JSON.toJSONString(channel));


        /********************************* 判断通道是否支持退款 线下不支持退款直接拒绝*************************************************/
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection()) && !channel.getSupportRefundState()) {
            log.info("=========================【退款 refundOrder】=========================【通道线下不支持退款】");
            throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
        }


        /********************************* 原订单撤销成功和撤销中不能退款*************************************************/
        if (TradeConstant.ORDER_CANNELING.equals(oldOrder.getCancelStatus()) || TradeConstant.ORDER_CANNEL_SUCCESS.equals(oldOrder.getCancelStatus())) {
            //撤销的单子不能退款--该交易已撤销
            log.info("=========================【退款 refundOrder】=========================【该交易已撤销】");
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

        /*****************************************************  校验退款单参数 判断退款类型 *****************************************************/
        Merchant merchant = commonRedisDataService.getMerchantById(refundDTO.getMerchantId());
        //已退款金额
        BigDecimal oldRefundAmount = orderRefundMapper.getTotalAmountByOrderId(oldOrder.getId());
        oldRefundAmount = oldRefundAmount == null ? BigDecimal.ZERO : oldRefundAmount;
        String type = this.checkRefundDTO(merchant, refundDTO, oldOrder, oldRefundAmount);

        /***************************************************************  创建退款单  *************************************************************/
        OrderRefund orderRefund = this.creatOrderRefundSys(refundDTO, oldOrder);
        orderRefund.setReqIp(reqIp);
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == -1) {
            orderRefund.setRemark2("部分");
        } else if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 0) {
            orderRefund.setRemark2("全额");
        }
        log.info("=========================【退款 refundOrder】========================= 创建订单 OrderRefund ：【{}】", JSON.toJSONString(orderRefund));


        /***********************************************************  计算退还手续费  ******************************************************/

        BigDecimal refundOrderFee = BigDecimal.ZERO;
        BigDecimal refundOrderFeeTrade = BigDecimal.ZERO;
        if (channel.getTransType() != null && TradeConstant.REFUND_ORDER_FEE == channel.getRefundingIsReturnFee()) {
            if ("全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
                refundOrderFeeTrade = oldOrder.getFeeTrade();
            } else {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
                refundOrderFeeTrade = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFeeTrade());
            }
        } else if (channel.getTransType() != null && TradeConstant.REFUND_TODAY_ORDER_FEE == channel.getTransType()) {
            //仅限当日退还的场合需要退还收单手续费
            if (channelCallbackTime.equals(today) && "全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
                refundOrderFeeTrade = oldOrder.getFeeTrade();
            } else if (channelCallbackTime.equals(today) && "部分".equals(orderRefund.getRemark2())) {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
                refundOrderFeeTrade = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFeeTrade());
            }
        }
        //五舍六入保留2位 只舍不入保留2位
        refundOrderFee = refundOrderFee.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        refundOrderFeeTrade = refundOrderFeeTrade.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        orderRefund.setRefundOrderFee(refundOrderFee);
        orderRefund.setRefundOrderFeeTrade(refundOrderFeeTrade);
        log.info("=========================【退款 refundOrder】========================= 是否退还收单手续费:{},退还收单手续费金额(订单):{},退还收单手续费金额(订单):{},退款类型:{}******", channel.getRefundingIsReturnFee(), refundOrderFee, refundOrderFeeTrade, refundDTO.getRefundType());


        /***************************************************************  计算退款手续费  *************************************************************/
        //退款手续费
        BigDecimal poundage = BigDecimal.ZERO;
        Product product = commonRedisDataService.getProductByCode(oldOrder.getProductCode());
        MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(refundDTO.getMerchantId(), product.getId());
        //退款收费
        if (merchantProduct.getRefundDefault()) {
            if (merchantProduct.getRefundRate() == null || merchantProduct.getRefundRateType() == null) {
                log.info("=========================【退款 refundOrder】=========================费率:{},费率类型:{}", merchantProduct.getRefundRate(), merchantProduct.getRefundRateType());
                throw new BusinessException(EResultEnum.SYS_ERROR_CREATE_ORDER_FAIL.getCode());
            }
            //单笔费率
            if (merchantProduct.getRefundRateType().equals(TradeConstant.FEE_TYPE_RATE)) {
                //手续费=交易金额*单笔费率
                poundage = orderRefund.getTradeAmount().multiply(merchantProduct.getRefundRate());
                //判断手续费是否小于最小值，大于最大值
                if (merchantProduct.getRefundMinTate() != null && poundage.compareTo(merchantProduct.getRefundMinTate()) == -1) {
                    poundage = merchantProduct.getRefundMinTate();
                }
                if (merchantProduct.getRefundMaxTate() != null && poundage.compareTo(merchantProduct.getRefundMaxTate()) == 1) {
                    poundage = merchantProduct.getRefundMaxTate();
                }
            }
            //单笔定额
            if (merchantProduct.getRefundRateType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
                //手续费=单笔定额值
                poundage = merchantProduct.getRefundRate();
            }
            //设置退款手续费交易币种
            orderRefund.setRefundFeeTrade(poundage);
            //转为订单币种费率
            poundage = poundage.multiply(oldOrder.getTradeForOrderRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
            //设置退款手续费订单币种
            orderRefund.setRefundFee(poundage);
        }
        //设置退款费率类型
        orderRefund.setRefundRateType(merchantProduct.getRefundRateType());
        //退款费率
        orderRefund.setRefundRate(merchantProduct.getRefundRate());

        log.info("=========================【退款 refundOrder】========================= 退款是否收费:{},退款手续费金额:{},费率类型:{}******", merchantProduct.getRefundDefault(), poundage, merchantProduct.getRefundRateType());

        /***************************************************************  判断账户余额  *************************************************************/
        //判断结算户金额
        Account account = accountMapper.getAccount(oldOrder.getMerchantId(), refundDTO.getRefundCurrency());
        log.info("=========================【退款 refundOrder】========================= 当前 清算余额:【{}】，结算余额:【{}】,冻结余额:【{}】", account.getClearBalance(), account.getSettleBalance(), account.getFreezeBalance());
        //退款金额=退款金额+退款手续费-收单手续费
        BigDecimal add = BigDecimal.ZERO;
        if (orderRefund.getFeePayer() == 1) {
            //商家承担
            add = refundDTO.getRefundAmount().add(poundage).subtract(refundOrderFee);
        } else {
            //用户承担
            add = refundDTO.getRefundAmount();
        }
        //账户金额
        BigDecimal balanceAmount = BigDecimal.ZERO;
        if (type.equals(TradeConstant.RF)) {
            //查询结算表中未结算的金额
            BigDecimal unSettleAmount = tcsStFlowMapper.getUnSettleAmount(oldOrder.getMerchantId(), oldOrder.getOrderCurrency());
            unSettleAmount = unSettleAmount == null ? BigDecimal.ZERO : unSettleAmount;
            //退款的场合直接看结算户金额+未结算的金额-冻结户金额
            balanceAmount = account.getSettleBalance().add(unSettleAmount).subtract(account.getFreezeBalance());
        } else {
            //查询清算表中未清算的金额
            BigDecimal unClearAmount = tcsCtFlowMapper.getUnClearAmount(oldOrder.getMerchantId(), refundDTO.getRefundCurrency());
            unClearAmount = unClearAmount == null ? BigDecimal.ZERO : unClearAmount;
            //撤销的场合=未清算的记录+清算账户金额
            balanceAmount = unClearAmount.add(account.getClearBalance());
        }
        log.info("=========================【退款 refundOrder】========================= 当前 退款金额余额:【{}】，账户余额:【{}】", add, balanceAmount);
        if (balanceAmount.compareTo(add) == -1) {
            throw new BusinessException(EResultEnum.INSUFFICIENT_ACCOUNT_BALANCE.getCode());
        }


        if (TradeConstant.RV.equals(type) || TradeConstant.RF.equals(type)) {
            orderRefund.setRemark4(type);
            /*************************************************************** 订单是付款成功的场合 *************************************************************/
            //把原订单状态改为退款中
            ordersMapper.updateOrderRefundStatus(refundDTO.getOrderNo(), TradeConstant.ORDER_REFUND_WAIT);

            /********************************************************* 通道不支持退款 人工退款*************************************************************/
            //线下订单不支持退款上面已经拒绝 若是线上订单，通道不支持退款走人工退款
            if (!channel.getSupportRefundState()) {
                log.info("=========================【退款 refundOrder】========================= 【通道不支持退款 人工退款】");
                orderRefund.setRefundMode(TradeConstant.REFUND_MODE_PERSON);
                orderRefundMapper.insert(orderRefund);

                //上报清结算
                FundChangeDTO fundChangeDTO = new FundChangeDTO(type, orderRefund);
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {//请求成功
                    orderRefund.setRefundStatus(TradeConstant.REFUND_SYS_FALID);
                    orderRefund.setRemark("后台系统和机构系统退款订单接口上报清结算失败");
                } else {//请求失败
                    orderRefund.setRefundStatus(TradeConstant.REFUND_SYS_FALID);
                    orderRefund.setRemark("后台系统和机构系统退款订单接口上报清结算失败");
                }
                //人工退款失败的场合更新退款单表的信息
                orderRefundMapper.updaterefundOrder(orderRefund.getId(), orderRefund.getRefundStatus(), orderRefund.getRemark());
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());//人工退款失败
                return baseResponse;
            }

            /********************************************************* 通道支持支持退款 *************************************************************/
            log.info("=========================【退款 refundOrder】========================= 【通道支持支持退款】");
            orderRefund.setRefundMode(TradeConstant.REFUND_MODE_AUTO);//退款方式 1：系统退款 2：人工退款
            orderRefundMapper.insert(orderRefund);

            //获取原订单的refCode字段(NextPos用)
            orderRefund.setSign(oldOrder.getSign());
            baseResponse = this.doRefundOrder(orderRefund, channel);
        } else if (TradeConstant.PAYING.equals(type)) {
            /***************************************************************  订单是付款中的场合  *************************************************************/
            if (TradeConstant.TRADE_ONLINE.equals(refundDTO.getTradeDirection())) {
                log.info("=========================【退款 refundOrder】=========================【线上通道不支持撤销】");
                throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
            }
            ChannelsAbstract channelsAbstract = null;
            try {
                log.info("=========================【退款 refundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
                channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark());
            } catch (Exception e) {
                log.info("=========================【退款 refundOrder】========================= Exception:【{}】", e);
            }
            baseResponse = channelsAbstract.cancel(channel, orderRefund, null);
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/3/14
     * @Descripate 退款操作
     **/
    @Override
    public BaseResponse doRefundOrder(OrderRefund orderRefund, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        log.info("=========================【退款 doRefundOrder】======================= 【doRefundOrder】 orderRefund:【{}】", JSON.toJSONString(orderRefund));
        FundChangeDTO fundChangeDTO = new FundChangeDTO(orderRefund.getRemark4(), orderRefund);
        log.info("=========================【退款 doRefundOrder】======================= 【上报清结算 {}】， fundChangeDTO:【{}】", orderRefund.getRemark4(), JSON.toJSONString(fundChangeDTO));
        BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
        log.info("=========================【退款 doRefundOrder】======================= 【清结算 {} 返回】 cFundChange:【{}】", orderRefund.getRemark4(), JSON.toJSONString(cFundChange));
        if (!cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
            log.info("=========================【退款 doRefundOrder】======================= 【清结算 {} 上报失败】 cFundChange:【{}】",orderRefund.getRemark4(), JSON.toJSONString(cFundChange));
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            log.info("=========================【退款 doRefundOrder】=========================【上报队列 RV_RF_FAIL_DL】RabbitMassage : 【{}】", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.RV_RF_FAIL_DL, JSON.toJSONString(rabbitMassage));
            baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
            return baseResponse;
        }
        ChannelsAbstract channelsAbstract = null;
        try {
            log.info("=========================【退款 doRefundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
            channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark());
        } catch (Exception e) {
            log.info("=========================【退款 doRefundOrder】========================= 【doRefundOrder Exception】 Exception:【{}】", e);
        }
        baseResponse = channelsAbstract.refund(channel, orderRefund, null);
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate 人工退款接口
     * @return
     **/
    @Override
    public BaseResponse artificialRefund(String username, String refundOrderId, Boolean enabled, String remark) {
        BaseResponse baseResponse = new BaseResponse();
        OrderRefund orderRefund = orderRefundMapper.selectByPrimaryKey(refundOrderId);
        if (enabled) {
            //审核通过
            //退款成功
            orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, null, remark);
            //改原订单状态
            commonBusinessService.updateOrderRefundSuccess(orderRefund);
        }else{
            //审核不通过

        }
        return baseResponse;
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
        orderRefund.setTradeDirection(refundDTO.getTradeDirection());
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

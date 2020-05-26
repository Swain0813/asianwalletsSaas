package com.asianwallets.trade.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.dto.UndoDTO;
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
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-18 14:01
 **/
@Slf4j
@Service
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
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private HandlerContext handlerContext;

    @Autowired
    private DeviceBindingMapper deviceBindingMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
     **/
    @Override
    public BaseResponse refundOrder(RefundDTO refundDTO, String reqIp) {
        //返回结果
        BaseResponse baseResponse = new BaseResponse();
        //退款功能验签，撤销功能的验签在自己的方法里面
        if(refundDTO.getFunctionType()==null){
            //签名校验
            if (!commonBusinessService.checkUniversalSign(refundDTO)) {
                log.info("=========================【退款 refundOrder】=========================【签名错误】");
                throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
            }
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
        /**************************************** 原订单撤销成功和撤销中不能退款 *************************************************/
        if (TradeConstant.ORDER_CANNELING.equals(oldOrder.getCancelStatus()) || TradeConstant.ORDER_CANNEL_SUCCESS.equals(oldOrder.getCancelStatus())) {
            //撤销的单子不能退款--该交易已撤销
            log.info("=========================【退款 refundOrder】=========================【该交易已撤销】");
            throw new BusinessException(EResultEnum.REFUND_CANCEL_ERROR.getCode());
        }
        /******************************************** 判断通道是否仅限当天退款 *************************************************/
        String channelCallbackTime = oldOrder.getChannelCallbackTime() == null ? DateToolUtils.getReqDate(oldOrder.getCreateTime())
                :DateToolUtils.getReqDate(oldOrder.getChannelCallbackTime());
        String today = DateToolUtils.getReqDate();
        if (channel.getOnlyTodayOrderRefund()) {
            if (!channelCallbackTime.equals(today)) {
                throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
            }
        }
        /*****************************************************  校验退款相关参数 判断退款类型 *****************************************************/
        commonRedisDataService.getMerchantById(refundDTO.getMerchantId());
        //已退款金额
        BigDecimal oldRefundAmount = orderRefundMapper.getTotalAmountByOrderId(oldOrder.getId());
        oldRefundAmount = oldRefundAmount == null ? BigDecimal.ZERO : oldRefundAmount;
        String type = this.checkRefundDTO(refundDTO,oldOrder,oldRefundAmount);
        /***************************************************************  创建退款单  *************************************************************/
        OrderRefund orderRefund = this.createOrderRefund(channel, refundDTO, oldOrder);
        orderRefund.setReqIp(reqIp);
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == -1) {
            orderRefund.setRemark2("部分");
        } else if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 0) {
            orderRefund.setRemark2("全额");
        }
        log.info("=========================【退款 refundOrder】========================= 创建订单OrderRefund{}", JSON.toJSONString(orderRefund));
        /***********************************************************  计算退还收单手续费  ******************************************************/
        //退还收单手续费 订单币种的收单手续费
        BigDecimal refundOrderFee = BigDecimal.ZERO;
        //退还收单手续费 交易币种的收单手续费
        BigDecimal refundOrderFeeTrade = BigDecimal.ZERO;
        if (channel.getRefundingIsReturnFee()!=null && TradeConstant.REFUND_ORDER_FEE == channel.getRefundingIsReturnFee()) {
            if ("全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
                refundOrderFeeTrade = oldOrder.getFeeTrade();
            } else {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
                refundOrderFeeTrade = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFeeTrade());
            }
        } else if (channel.getRefundingIsReturnFee()!=null && TradeConstant.REFUND_TODAY_ORDER_FEE == channel.getRefundingIsReturnFee()) {
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
        //退还收单手续费 订单币种的收单手续费
        orderRefund.setRefundOrderFee(refundOrderFee);
        //退还收单手续费 交易币种的收单手续费
        orderRefund.setRefundOrderFeeTrade(refundOrderFeeTrade);
        if(orderRefund.getFeePayer()==1){
            orderRefund.setChannelAmount(orderRefund.getTradeAmount());
        }else{
            orderRefund.setChannelAmount(orderRefund.getTradeAmount().add(orderRefund.getRefundOrderFeeTrade()));
        }

        log.info("=========================【退款 refundOrder】========================= 是否退还收单手续费:{},退还收单手续费金额(订单):{}," +
                        "退还收单手续费金额(交易):{},退款类型:{}******", channel.getRefundingIsReturnFee(),
                refundOrderFee, refundOrderFeeTrade, refundDTO.getRefundType());
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
        }
        //退款手续费交易币种 四舍五入保留2位
        poundage =poundage.setScale(2, BigDecimal.ROUND_HALF_UP);
        //设置退款手续费交易币种
        orderRefund.setRefundFeeTrade(poundage);
        //转为订单币种费率
        poundage = poundage.multiply(oldOrder.getTradeForOrderRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
        //设置退款手续费订单币种
        orderRefund.setRefundFee(poundage);
        //设置退款费率类型
        orderRefund.setRefundRateType(merchantProduct.getRefundRateType());
        //退款费率
        orderRefund.setRefundRate(merchantProduct.getRefundRate());
        if (merchantProduct.getRefundMaxTate() != null && merchantProduct.getRefundMinTate() != null) {
            orderRefund.setRefundMaxTate(merchantProduct.getRefundMaxTate());
            orderRefund.setRefundMinTate(merchantProduct.getRefundMinTate());
        }
        log.info("=========================【退款 refundOrder】========================= 退款是否收费:{},退款手续费金额:{},费率类型:{}******", merchantProduct.getRefundDefault(), poundage, merchantProduct.getRefundRateType());
        /***************************************************************  判断账户余额  *************************************************************/
        //判断结算户金额
        Account account = accountMapper.getAccount(oldOrder.getMerchantId(), refundDTO.getRefundCurrency());
        log.info("=========================【退款 refundOrder】========================= 当前 清算余额:【{}】，结算余额:【{}】,冻结余额:【{}】", account.getClearBalance(), account.getSettleBalance(), account.getFreezeBalance());
        //退款金额
        BigDecimal add = BigDecimal.ZERO;
        if (orderRefund.getFeePayer() == TradeConstant.FEE_PAYER_IN) {
            //商家承担 退款金额=退款金额+退款手续费-收单手续费
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
            if(refundDTO.getFunctionType()==null){
                orderRefund.setRemark3(TradeConstant.RF);
            }else{
                orderRefund.setRemark3(TradeConstant.RV);
            }
            orderRefund.setRemark4(type);
            /*************************************************************** 订单是付款成功的场合 将订单表中的退款状态修改成退款中*******************************************/
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
                    orderRefund.setRemark("人工退款上报清结算成功");
                    orderRefund.setRefundStatus(TradeConstant.REFUND_WAIT);
                    //退款中
                    baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                } else {//请求失败
                    orderRefund.setRemark("人工退款上报清结算失败");
                    orderRefund.setRefundStatus(TradeConstant.REFUND_SYS_FALID);
                    //退款失败
                    baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                }
                //人工退款上报清结算成功或者失败更新退款单表
                orderRefundMapper.updaterefundOrder(orderRefund.getId(),orderRefund.getRefundStatus(),orderRefund.getRemark());
                return baseResponse;
            }
            /********************************************************* 通道支持支持退款 *************************************************************/
            log.info("=========================【退款 refundOrder】========================= 【通道支持支持退款】");
            orderRefund.setRefundMode(TradeConstant.REFUND_MODE_AUTO);
            orderRefundMapper.insert(orderRefund);
            //获取原订单的refCode字段(NextPos用)
            orderRefund.setSign(oldOrder.getSign());
            baseResponse = this.doRefundOrder(orderRefund,channel);
        } else if (TradeConstant.PAYING.equals(type)) {
            orderRefund.setRemark3(TradeConstant.RV);
            ordersMapper.updateOrderCancelStatus(refundDTO.getOrderNo(),refundDTO.getOperatorId(), TradeConstant.ORDER_CANNELING);
            /***************************************************************  订单是付款中的场合  *************************************************************/
            if (TradeConstant.TRADE_ONLINE.equals(refundDTO.getTradeDirection())) {
                log.info("=========================【退款 refundOrder】=========================【线上通道不支持撤销】");
                throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
            }
            ChannelsAbstract channelsAbstract = null;
            try {
                log.info("=========================【退款 refundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
                channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark().split("_")[0]);
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
            log.info("=========================【退款 doRefundOrder】======================= 【清结算 {} 上报失败】 cFundChange:【{}】", orderRefund.getRemark4(), JSON.toJSONString(cFundChange));
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            log.info("=========================【退款 doRefundOrder】=========================【上报队列 RV_RF_FAIL_DL】RabbitMassage : 【{}】", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.RV_RF_FAIL_DL, JSON.toJSONString(rabbitMassage));
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            return baseResponse;
        }
        ChannelsAbstract channelsAbstract = null;
        try {
            log.info("=========================【退款 doRefundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
            channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark().split("_")[0]);
        } catch (Exception e) {
            log.info("=========================【退款 doRefundOrder】========================= 【doRefundOrder Exception】 Exception:【{}】", e);
        }
        baseResponse = channelsAbstract.refund(channel, orderRefund, null);
        return baseResponse;
    }

    /**
     * 撤销接口
     * @param undoDTO
     * @param reqIp
     * @return
     */
    @Override
    public BaseResponse undo(UndoDTO undoDTO, String reqIp){
        //签名校验
        if (!commonBusinessService.checkUniversalSign(undoDTO)) {
            log.info("=========================【撤销接口】=========================【签名错误】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }
        //检查商户编号
        commonRedisDataService.getMerchantById(undoDTO.getMerchantId());
        //校验商户绑定设备
        DeviceBinding deviceBinding = deviceBindingMapper.selectByMerchantIdAndImei(undoDTO.getMerchantId(),undoDTO.getImei());
        if (deviceBinding == null) {
            log.info("**************校验撤销订单参数 设备编号不合法******************merchantId:{},imei:{}",undoDTO.getMerchantId(),undoDTO.getImei());
            //设备编号不合法
            throw new BusinessException(EResultEnum.DEVICE_CODE_INVALID.getCode());
        }
        String username = undoDTO.getOperatorId().concat(undoDTO.getMerchantId());
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        if (sysUser == null) {
            log.info("===========校验撤销订单参数 设备操作员不合法==========【操作员ID不存在】***********merchantId:{},operatorId{}",undoDTO.getMerchantId(),undoDTO.getOperatorId());
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //根据商户订单号获取订单信息
        Orders order = ordersMapper.selectByMerchantOrderId(undoDTO.getOrderNo());
        //订单不存在的场合
        if (order == null) {
            log.info("**************** 校验撤销订单参数 订单信息不存在**************** order : {}", JSON.toJSON(undoDTO));
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        //防止线上订单调用该接口或者阻止调用退款的接口后再调用撤销接口
        if (TradeConstant.TRADE_ONLINE.equals(order.getTradeDirection()) || order.getRefundStatus() != null) {
            //该订单不支持撤销
            throw new BusinessException(EResultEnum.ONLINE_ORDER_IS_NOT_ALLOW_UNDO.getCode());
        }
        //创建退款接口需要的输入参数
        RefundDTO refundDTO = this.getRefundDTO(undoDTO,order);
        return this.refundOrder(refundDTO,reqIp);
    }

    /**
     * 根据撤销输入参数和订单信息创建退款输入参数
     * @param undoDTO
     * @param order
     * @return
     */
    private RefundDTO getRefundDTO(UndoDTO undoDTO,Orders order){
        RefundDTO refundDTO = new RefundDTO();
        //商户编号
        refundDTO.setMerchantId(undoDTO.getMerchantId());
        //商户订单号
        refundDTO.setOrderNo(undoDTO.getOrderNo());
        //全额退款
        refundDTO.setRefundType(TradeConstant.REFUND_TYPE_TOTAL);
        //退款时间
        refundDTO.setRefundTime(DateToolUtils.formatTimestamp.format(new Date()));
        //退款币种
        refundDTO.setRefundCurrency(order.getOrderCurrency());
        //退款金额
        refundDTO.setRefundAmount(order.getOrderAmount());
        //交易方向
        refundDTO.setTradeDirection(order.getTradeDirection());
        //设备编号
        refundDTO.setImei(undoDTO.getImei());
        //设备操作员
        refundDTO.setOperatorId(undoDTO.getOperatorId());
        //签名类型
        refundDTO.setSignType(undoDTO.getSignType());
        //签名
        refundDTO.setSign(undoDTO.getSign());
        //撤销功能的标志,为了在退款功能里面不要再验签
        refundDTO.setFunctionType(TradeConstant.RV);
        return refundDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate 人工退款接口
     **/
    @Override
    public BaseResponse artificialRefund(String username, String refundOrderId, Boolean enabled, String remark) {
        BaseResponse baseResponse = new BaseResponse();
        OrderRefund orderRefund = orderRefundMapper.selectByPrimaryKey(refundOrderId);
        log.info("=========================【人工退款】========================= refundOrderId:【{}】,审核是否通过：【{}】，审核人：【{}】", refundOrderId, enabled, username);
        if (enabled) {
            //审核通过
            orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, null, remark);
            //改原订单状态
            commonBusinessService.updateOrderRefundSuccess(orderRefund);
        } else {
            //审核不通过
            //退款失败
            Reconciliation reconciliation = commonBusinessService.createReconciliation(orderRefund.getRemark4(), orderRefund, remark);
            reconciliationMapper.insert(reconciliation);
            FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
            log.info("=========================【人工退款】======================= 【调账 {}】， fundChangeDTO:【{}】", orderRefund.getRemark4(), JSON.toJSONString(fundChangeDTO));
            BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
            if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                //调账成功
                log.info("=================【人工退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, remark);
                reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                //改原订单状态
                commonBusinessService.updateOrderRefundFail(orderRefund);
            } else {
                //调账失败
                log.info("=================【人工退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                log.info("=================【人工退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
            }
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/5/25
     * @Descripate 银行卡退款接口
     * @return
     **/
    @Override
    public BaseResponse bankCardrefund(RefundDTO refundDTO, String reqIp) {
        //返回结果
        BaseResponse baseResponse = new BaseResponse();
        //退款功能验签，撤销功能的验签在自己的方法里面
        if(refundDTO.getFunctionType()==null){
            //签名校验
            if (!commonBusinessService.checkUniversalSign(refundDTO)) {
                log.info("=========================【银行卡退款 refundOrder】=========================【签名错误】");
                throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
            }
        }
        /**************************************************** 查询原订单 *************************************************/
        Orders oldOrder = ordersMapper.selectByMerchantOrderId(refundDTO.getOrderNo());
        if (oldOrder == null) {
            //商户订单号不存在
            log.info("=========================【银行卡退款 refundOrder】=========================【商户订单号不存在】");
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        if (org.springframework.util.StringUtils.isEmpty(refundDTO.getBankCardNo())) {
            log.info("==================【银行卡退款】==================【银行卡号为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (org.springframework.util.StringUtils.isEmpty(refundDTO.getCvv())) {
            log.info("==================【银行卡退款】==================【CVV为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (org.springframework.util.StringUtils.isEmpty(refundDTO.getCardValidDate())) {
            log.info("==================【银行卡退款】==================【卡有效期为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (org.springframework.util.StringUtils.isEmpty(refundDTO.getTrackInfor())) {
            log.info("==================【银行卡退款】==================【磁道信息为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Channel channel = commonRedisDataService.getChannelByChannelCode(oldOrder.getChannelCode());
        log.info("=========================【银行卡退款 refundOrder】========================= Channel:【{}】", JSON.toJSONString(channel));
        /********************************* 判断通道是否支持退款 线下不支持退款直接拒绝*************************************************/
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection()) && !channel.getSupportRefundState()) {
            log.info("=========================【退款 refundOrder】=========================【通道线下不支持退款】");
            throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
        }
        /**************************************** 原订单撤销成功和撤销中不能退款 *************************************************/
        if (TradeConstant.ORDER_CANNELING.equals(oldOrder.getCancelStatus()) || TradeConstant.ORDER_CANNEL_SUCCESS.equals(oldOrder.getCancelStatus())) {
            //撤销的单子不能退款--该交易已撤销
            log.info("=========================【银行卡退款 refundOrder】=========================【该交易已撤销】");
            throw new BusinessException(EResultEnum.REFUND_CANCEL_ERROR.getCode());
        }
        /******************************************** 判断通道是否仅限当天退款 *************************************************/
        String channelCallbackTime = oldOrder.getChannelCallbackTime() == null ? DateToolUtils.getReqDate(oldOrder.getCreateTime())
                :DateToolUtils.getReqDate(oldOrder.getChannelCallbackTime());
        String today = DateToolUtils.getReqDate();
        if (channel.getOnlyTodayOrderRefund()) {
            if (!channelCallbackTime.equals(today)) {
                throw new BusinessException(EResultEnum.NOT_SUPPORT_REFUND.getCode());
            }
        }
        /*****************************************************  校验退款相关参数 判断退款类型 *****************************************************/
        commonRedisDataService.getMerchantById(refundDTO.getMerchantId());
        //已退款金额
        BigDecimal oldRefundAmount = orderRefundMapper.getTotalAmountByOrderId(oldOrder.getId());
        oldRefundAmount = oldRefundAmount == null ? BigDecimal.ZERO : oldRefundAmount;
        String type = this.checkRefundDTO(refundDTO,oldOrder,oldRefundAmount);
        /***************************************************************  创建退款单  *************************************************************/
        OrderRefund orderRefund = this.createOrderRefund(channel, refundDTO, oldOrder);
        orderRefund.setReqIp(reqIp);
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == -1) {
            orderRefund.setRemark2("部分");
        } else if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 0) {
            orderRefund.setRemark2("全额");
        }
        log.info("=========================【银行卡退款 refundOrder】========================= 创建订单OrderRefund{}", JSON.toJSONString(orderRefund));
        /***********************************************************  计算退还收单手续费  ******************************************************/
        //退还收单手续费 订单币种的收单手续费
        BigDecimal refundOrderFee = BigDecimal.ZERO;
        //退还收单手续费 交易币种的收单手续费
        BigDecimal refundOrderFeeTrade = BigDecimal.ZERO;
        if (channel.getRefundingIsReturnFee()!=null && TradeConstant.REFUND_ORDER_FEE == channel.getRefundingIsReturnFee()) {
            if ("全额".equals(orderRefund.getRemark2())) {
                //如果是全额退款的场合则退还收单手续费金额=收单的手续费
                refundOrderFee = oldOrder.getFee();
                refundOrderFeeTrade = oldOrder.getFeeTrade();
            } else {
                //如果是部分退款的场合则退还收单手续费金额=退款金额/原订单金额*收单手续费
                refundOrderFee = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFee());
                refundOrderFeeTrade = refundDTO.getRefundAmount().divide(oldOrder.getOrderAmount()).multiply(oldOrder.getFeeTrade());
            }
        } else if (channel.getRefundingIsReturnFee()!=null && TradeConstant.REFUND_TODAY_ORDER_FEE == channel.getRefundingIsReturnFee()) {
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
        //退还收单手续费 订单币种的收单手续费
        orderRefund.setRefundOrderFee(refundOrderFee);
        //退还收单手续费 交易币种的收单手续费
        orderRefund.setRefundOrderFeeTrade(refundOrderFeeTrade);
        if(orderRefund.getFeePayer()==1){
            orderRefund.setChannelAmount(orderRefund.getTradeAmount());
        }else{
            orderRefund.setChannelAmount(orderRefund.getTradeAmount().add(orderRefund.getRefundOrderFeeTrade()));
        }

        log.info("=========================【银行卡退款 refundOrder】========================= 是否退还收单手续费:{},退还收单手续费金额(订单):{}," +
                        "退还收单手续费金额(交易):{},退款类型:{}******", channel.getRefundingIsReturnFee(),
                refundOrderFee, refundOrderFeeTrade, refundDTO.getRefundType());
        /***************************************************************  计算退款手续费  *************************************************************/
        //退款手续费
        BigDecimal poundage = BigDecimal.ZERO;
        Product product = commonRedisDataService.getProductByCode(oldOrder.getProductCode());
        MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(refundDTO.getMerchantId(), product.getId());
        //退款收费
        if (merchantProduct.getRefundDefault()) {
            if (merchantProduct.getRefundRate() == null || merchantProduct.getRefundRateType() == null) {
                log.info("=========================【银行卡退款 refundOrder】=========================费率:{},费率类型:{}", merchantProduct.getRefundRate(), merchantProduct.getRefundRateType());
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
        }
        //退款手续费交易币种 四舍五入保留2位
        poundage =poundage.setScale(2, BigDecimal.ROUND_HALF_UP);
        //设置退款手续费交易币种
        orderRefund.setRefundFeeTrade(poundage);
        //转为订单币种费率
        poundage = poundage.multiply(oldOrder.getTradeForOrderRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
        //设置退款手续费订单币种
        orderRefund.setRefundFee(poundage);
        //设置退款费率类型
        orderRefund.setRefundRateType(merchantProduct.getRefundRateType());
        //退款费率
        orderRefund.setRefundRate(merchantProduct.getRefundRate());
        if (merchantProduct.getRefundMaxTate() != null && merchantProduct.getRefundMinTate() != null) {
            orderRefund.setRefundMaxTate(merchantProduct.getRefundMaxTate());
            orderRefund.setRefundMinTate(merchantProduct.getRefundMinTate());
        }
        log.info("=========================【银行卡退款 refundOrder】========================= 退款是否收费:{},退款手续费金额:{},费率类型:{}******", merchantProduct.getRefundDefault(), poundage, merchantProduct.getRefundRateType());
        /***************************************************************  判断账户余额  *************************************************************/
        //判断结算户金额
        Account account = accountMapper.getAccount(oldOrder.getMerchantId(), refundDTO.getRefundCurrency());
        log.info("=========================【银行卡退款 refundOrder】========================= 当前 清算余额:【{}】，结算余额:【{}】,冻结余额:【{}】", account.getClearBalance(), account.getSettleBalance(), account.getFreezeBalance());
        //退款金额
        BigDecimal add = BigDecimal.ZERO;
        if (orderRefund.getFeePayer() == TradeConstant.FEE_PAYER_IN) {
            //商家承担 退款金额=退款金额+退款手续费-收单手续费
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
        log.info("=========================【银行卡退款 refundOrder】========================= 当前 退款金额余额:【{}】，账户余额:【{}】", add, balanceAmount);
        if (balanceAmount.compareTo(add) == -1) {
            throw new BusinessException(EResultEnum.INSUFFICIENT_ACCOUNT_BALANCE.getCode());
        }
        if (TradeConstant.RV.equals(type) || TradeConstant.RF.equals(type)) {
            if(refundDTO.getFunctionType()==null){
                orderRefund.setRemark3(TradeConstant.RF);
            }else{
                orderRefund.setRemark3(TradeConstant.RV);
            }
            orderRefund.setRemark4(type);
            /*************************************************************** 订单是付款成功的场合 将订单表中的退款状态修改成退款中*******************************************/
            //把原订单状态改为退款中
            ordersMapper.updateOrderRefundStatus(refundDTO.getOrderNo(), TradeConstant.ORDER_REFUND_WAIT);
            /********************************************************* 通道不支持退款 人工退款*************************************************************/
            //线下订单不支持退款上面已经拒绝 若是线上订单，通道不支持退款走人工退款
            if (!channel.getSupportRefundState()) {
                log.info("=========================【银行卡退款 refundOrder】========================= 【通道不支持退款 人工退款】");
                orderRefund.setRefundMode(TradeConstant.REFUND_MODE_PERSON);
                orderRefundMapper.insert(orderRefund);
                //上报清结算
                FundChangeDTO fundChangeDTO = new FundChangeDTO(type, orderRefund);
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {//请求成功
                    orderRefund.setRemark("人工退款上报清结算成功");
                    orderRefund.setRefundStatus(TradeConstant.REFUND_WAIT);
                    //退款中
                    baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                } else {//请求失败
                    orderRefund.setRemark("人工退款上报清结算失败");
                    orderRefund.setRefundStatus(TradeConstant.REFUND_SYS_FALID);
                    //退款失败
                    baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                }
                //人工退款上报清结算成功或者失败更新退款单表
                orderRefundMapper.updaterefundOrder(orderRefund.getId(),orderRefund.getRefundStatus(),orderRefund.getRemark());
                return baseResponse;
            }
            /********************************************************* 通道支持支持退款 *************************************************************/
            log.info("=========================【银行卡退款 refundOrder】========================= 【通道支持支持退款】");
            orderRefund.setRefundMode(TradeConstant.REFUND_MODE_AUTO);
            orderRefundMapper.insert(orderRefund);
            //获取原订单的refCode字段(NextPos用)
            orderRefund.setSign(oldOrder.getSign());
            FundChangeDTO fundChangeDTO = new FundChangeDTO(orderRefund.getRemark4(), orderRefund);
            log.info("=========================【银行卡退款 doRefundOrder】======================= 【上报清结算 {}】， fundChangeDTO:【{}】", orderRefund.getRemark4(), JSON.toJSONString(fundChangeDTO));
            BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
            log.info("=========================【银行卡退款 doRefundOrder】======================= 【清结算 {} 返回】 cFundChange:【{}】", orderRefund.getRemark4(), JSON.toJSONString(cFundChange));
            if (!cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                log.info("=========================【银行卡退款 doRefundOrder】======================= 【清结算 {} 上报失败】 cFundChange:【{}】", orderRefund.getRemark4(), JSON.toJSONString(cFundChange));
                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                log.info("=========================【银行卡退款 doRefundOrder】=========================【上报队列 RV_RF_FAIL_DL】RabbitMassage : 【{}】", JSON.toJSON(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.BANK_RV_RF_FAIL_DL, JSON.toJSONString(rabbitMassage));
                baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                return baseResponse;
            }
            ChannelsAbstract channelsAbstract = null;
            try {
                log.info("=========================【银行卡退款 doRefundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
                channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark().split("_")[0]);
            } catch (Exception e) {
                log.info("=========================【银行卡退款 doRefundOrder】========================= 【doRefundOrder Exception】 Exception:【{}】", e);
            }
            baseResponse = channelsAbstract.bankRefund(channel, orderRefund, null);
        } else if (TradeConstant.PAYING.equals(type)) {
            orderRefund.setRemark3(TradeConstant.CZ);
            ordersMapper.updateOrderCancelStatus(refundDTO.getOrderNo(),refundDTO.getOperatorId(), TradeConstant.ORDER_RESEVALING);
            /***************************************************************  订单是付款中的场合  *************************************************************/
            ChannelsAbstract channelsAbstract = null;
            try {
                log.info("=========================【银行卡冲正 refundOrder】========================= Channel ServiceName:【{}】", channel.getServiceNameMark());
                channelsAbstract = handlerContext.getInstance(channel.getServiceNameMark().split("_")[0]);
            } catch (Exception e) {
                log.info("=========================【银行卡冲正 refundOrder】========================= Exception:【{}】", e);
            }
            baseResponse = channelsAbstract.reversal(channel, orderRefund, null);
        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/19
     * @Descripate 校验退款单参数
     **/
    public String checkRefundDTO(RefundDTO refundDTO, Orders oldOrder, BigDecimal oldRefundAmount) {
        //验证退款金额
        BigDecimal newRefundAmount = oldRefundAmount.add(refundDTO.getRefundAmount());
        //验证原订单交易状态状态(只有交易状态为交易成功和退款)
        Integer CTstatus = tcsCtFlowMapper.getCTstatus(oldOrder.getId());
        Integer STstatus = tcsStFlowMapper.getSTstatus(oldOrder.getId());
        log.info("-----------------验证原订单清结算状态 -------------- merchantId:{} oldOrder:{} ,CTstatus:{},STstatus:{}", refundDTO.getMerchantId(),oldOrder.getId(),CTstatus,STstatus);
        if (TradeConstant.ORDER_CLEAR_SUCCESS.equals(CTstatus) && TradeConstant.ORDER_SETTLE_SUCCESS.equals(STstatus)) {
            //退款
            if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 1) {
                log.info("----------------- 校验退款参数 退款金额不合法 -------------- merchantId:{} ,refundDTO:{},oldOrder:{}", refundDTO.getMerchantId(), JSON.toJSON(refundDTO),JSON.toJSON(oldOrder));
                throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
            }
            return TradeConstant.RF;
        } else if ((TradeConstant.ORDER_CLEAR_SUCCESS.equals(CTstatus) && TradeConstant.ORDER_SETTLE_WAIT.equals(STstatus))
                || (TradeConstant.ORDER_CLEAR_WAIT.equals(CTstatus) && STstatus == null)) {
            //部分退款的场合
            if (refundDTO.getRefundType() == TradeConstant.REFUND_TYPE_PART) {
                throw new BusinessException(EResultEnum.ORDER_NOT_SETTLE.getCode());
            }
            //撤销
            if (newRefundAmount.compareTo(oldOrder.getOrderAmount()) == 1) {
                log.info("----------------- 撤销时校验退款参数 退款金额不合法 -------------- merchantId:{},refundDTO:{},oldOrder:{}",refundDTO.getMerchantId(), JSON.toJSON(refundDTO),JSON.toJSON(oldOrder));
                throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
            }
            //线上退款,验证退款人的用户信息
            if (refundDTO.getTradeDirection() ==TradeConstant.TRADE_ONLINE) {
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
                //swiftCode
                if (StringUtils.isBlank(refundDTO.getSwiftCode())) {
                    throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
                }
            }
            return TradeConstant.RV;
        } else if (CTstatus == null && STstatus == null) {
            return TradeConstant.PAYING;
        } else {
            //订单交易状态不合法
            throw new BusinessException(EResultEnum.ORDER_STATUS_IS_WRONG.getCode());
        }
    }

    /**
     * 创建退款订单
     *
     * @param refundDTO
     * @param oldOrder
     * @return
     */
    public OrderRefund createOrderRefund(Channel channel, RefundDTO refundDTO, Orders oldOrder) {
        OrderRefund orderRefund = new OrderRefund();
        BeanUtils.copyProperties(oldOrder, orderRefund);
        orderRefund.setLanguage(auditorProvider.getLanguage());//语言
        //检查币种是否一致，若不一致掉换汇接口----这块逻辑主要是针对部分退款的问题
        if (!oldOrder.getTradeCurrency().equals(refundDTO.getRefundCurrency())) {
            BigDecimal tradeAmount = refundDTO.getRefundAmount().multiply(oldOrder.getOrderForTradeRate());
            //退款金额
            orderRefund.setTradeAmount(tradeAmount);
        } else {
            //退款金额
            orderRefund.setTradeAmount(refundDTO.getRefundAmount());
        }
        //直接保留2位 直接舍
        orderRefund.setTradeAmount(orderRefund.getTradeAmount().setScale(2, BigDecimal.ROUND_DOWN));
        if (!org.springframework.util.StringUtils.isEmpty(refundDTO.getBankCardNo())) {
            //银行卡号
            orderRefund.setUserBankCardNo(refundDTO.getBankCardNo());
        }
        if (!org.springframework.util.StringUtils.isEmpty(refundDTO.getCvv())) {
            //CVV
            orderRefund.setCvv(refundDTO.getCvv());
        }
        if (!org.springframework.util.StringUtils.isEmpty(refundDTO.getCardValidDate())) {
            //卡有效期
            orderRefund.setValid(refundDTO.getCardValidDate());
        }
        if (!org.springframework.util.StringUtils.isEmpty(refundDTO.getTrackInfor())) {
            //磁道信息
            orderRefund.setTrackData(refundDTO.getTrackInfor());
        }
        orderRefund.setId("R" + IDS.uniqueID());//退款订单号
        orderRefund.setOrderId(oldOrder.getId());//原订单流水号
        orderRefund.setRefundType(refundDTO.getRefundType());//退款类型
        //全额退款的场合，直接拿订单表中的交易金额退款
        if (refundDTO.getRefundType() == 1) {
            orderRefund.setTradeAmount(oldOrder.getTradeAmount());//全额退款退原订单交易金额
        }
        orderRefund.setTradeDirection(refundDTO.getTradeDirection());
        //商户请求退款时间(商户所在地)
        orderRefund.setMerchantOrderTime(DateToolUtils.parseDate(refundDTO.getRefundTime(), DateToolUtils.DATE_FORMAT_DATETIME));
        orderRefund.setOrderAmount(refundDTO.getRefundAmount().setScale(2, BigDecimal.ROUND_DOWN));//商户请求退款金额
        orderRefund.setOrderCurrency(refundDTO.getRefundCurrency());//客户请求收单币种
        orderRefund.setRefundStatus(TradeConstant.REFUND_WAIT);//退款中
        orderRefund.setPayerName(refundDTO.getPayerName());//付款人姓名
        orderRefund.setPayerAccount(refundDTO.getPayerAccount());//付款人账户
        orderRefund.setPayerBank(refundDTO.getPayerBank());//付款人银行
        orderRefund.setPayerEmail(refundDTO.getPayerEmail());//付款人邮箱
        orderRefund.setPayerPhone(refundDTO.getPayerPhone());//付款人电话

        //通道退款费率
        if (channel.getSupportRefundState()) {
            orderRefund.setChannelRate(channel.getChannelRefundFeeRate());//通道费率
            orderRefund.setChannelFeeType(channel.getChannelRefundFeeType());
            if (channel.getChannelRefundFeeType().equals(TradeConstant.FEE_TYPE_RATE)) {
                BigDecimal fl = orderRefund.getTradeAmount().multiply(channel.getChannelRefundFeeRate()).setScale(2, BigDecimal.ROUND_UP);
                if (channel.getChannelRefundMaxRate() != null && fl.compareTo(channel.getChannelRefundMaxRate()) == 1) {
                    fl = channel.getChannelRefundMaxRate();
                }
                if (channel.getChannelRefundMinRate() != null && fl.compareTo(channel.getChannelRefundMinRate()) == -1) {
                    fl = channel.getChannelRefundMinRate();
                }
                orderRefund.setChannelFee(fl);
            } else {
                orderRefund.setChannelFee(channel.getChannelRefundFeeRate());
            }
        } else {
            orderRefund.setChannelRate(null);//通道费率
            orderRefund.setChannelFeeType(null);
            orderRefund.setChannelFee(null);
        }
        //Swift Code
        orderRefund.setSwiftCode(refundDTO.getSwiftCode());
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
        //通道网关费率
        orderRefund.setChannelGatewayRate(null);
        orderRefund.setChannelGatewayCharge(null);
        orderRefund.setChannelGatewayFee(null);
        orderRefund.setChannelGatewayFeeType(null);
        orderRefund.setChannelGatewayStatus(null);
        //创建时间
        orderRefund.setCreateTime(new Date());
        //创建人
        orderRefund.setCreator(StringUtils.isBlank(refundDTO.getModifier())?refundDTO.getOperatorId():refundDTO.getModifier());
        //修改时间
        orderRefund.setUpdateTime(null);
        //修改人
        orderRefund.setModifier(null);
        return orderRefund;
    }

}

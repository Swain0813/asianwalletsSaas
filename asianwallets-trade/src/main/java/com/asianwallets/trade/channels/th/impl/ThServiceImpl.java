package com.asianwallets.trade.channels.th.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.AESUtil;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.th.ThService;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.PreOrdersMapper;
import com.asianwallets.trade.dto.ThCheckOrderQueueDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import com.asianwallets.trade.utils.HandlerType;
import com.payneteasy.tlv.BerTag;
import com.payneteasy.tlv.BerTlvBuilder;
import com.payneteasy.tlv.HexUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * The type Th service.
 */
@Slf4j
@Service
@HandlerType(TradeConstant.TH)
public class ThServiceImpl extends ChannelsAbstractAdapter implements ThService {

    /**
     * The Channels feign.
     */
    @Autowired
    public ChannelsFeign channelsFeign;

    /**
     * The Channels order mapper.
     */
    @Autowired
    public ChannelsOrderMapper channelsOrderMapper;

    /**
     * The Rabbit mq sender.
     */
    @Autowired
    public RabbitMQSender rabbitMQSender;

    /**
     * The Order refund mapper.
     */
    @Autowired
    public OrderRefundMapper orderRefundMapper;

    /**
     * The Common business service.
     */
    @Autowired
    public CommonBusinessService commonBusinessService;

    /**
     * The Common redis data service.
     */
    @Autowired
    public CommonRedisDataService commonRedisDataService;

    /**
     * The Orders mapper.
     */
    @Autowired
    public OrdersMapper ordersMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PreOrdersMapper preOrdersMapper;


    /**
     * 插入通道订单
     *
     * @param orders  订单
     * @param channel the channel
     */
    public void insertChannelsOrder(Orders orders, Channel channel) {
        ChannelsOrder channelsOrder = new ChannelsOrder();
        channelsOrder.setId(orders.getId());
        channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
        channelsOrder.setTradeCurrency(orders.getTradeCurrency());
        channelsOrder.setTradeAmount(orders.getTradeAmount());
        channelsOrder.setReqIp(orders.getReqIp());
        channelsOrder.setServerUrl(orders.getServerUrl());
        channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
        channelsOrder.setIssuerId(orders.getIssuerId());
        channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
        channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
        channelsOrder.setPayerPhone(orders.getPayerPhone());
        channelsOrder.setPayerName(orders.getPayerName());
        channelsOrder.setPayerBank(orders.getPayerBank());
        channelsOrder.setPayerEmail(orders.getPayerEmail());
        channelsOrder.setCreateTime(new Date());
        channelsOrder.setCreator(orders.getCreator());
        channelsOrderMapper.insert(channelsOrder);
    }

    /**
     * 创建通华扫码订单
     *
     * @param orders  订单
     * @param channel the channel
     * @return the iso 8583 dto
     */
    public ISO8583DTO createScanOrder(Orders orders, Channel channel) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        //当前时间戳
        String timeStamp = System.currentTimeMillis() + "";
        String domain11 = timeStamp.substring(4, 10);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        orders.setReportNumber(domain11 + domain60_2);
        //消息类型
        iso8583DTO.setMessageType("0200");
        //获取交易金额的小数位数
        setFiled4(iso8583DTO, orders.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        setFiled41And42(orders.getMerchantId(), orders.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + domain60_2);
        return iso8583DTO;
    }

    /**
     * 通华主扫接口
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //插入通道订单
        insertChannelsOrder(orders, channel);
        //创建通华DTO
        ISO8583DTO iso8583DTO = createScanOrder(orders, channel);
        iso8583DTO.setProcessingCode_3("700200");
        iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52("303002" + channel.getPayCode() + "0202"));
        log.info("==================【通华线下CSB】==================【调用Channels服务】【请求参数】 iso8583DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.thCSB(new ThDTO(iso8583DTO, channel, orders.getMerchantId()));
        log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        //将46域信息按02分割
        String[] domain46 = iso8583VO.getAdditionalData_46().split("02");
        log.info("===============【通华线下CSB】===============【46域信息】 domain46: {}", Arrays.toString(domain46));
        //索引第4位 : 通华返回的商户订单号
        orders.setChannelNumber(NumberStringUtil.hexStr2Str(domain46[4]));
        ordersMapper.updateByPrimaryKeySelective(orders);
        //索引第5位 : 二维码URL
        String codeUrl = NumberStringUtil.hexStr2Str(domain46[5]);
        log.info("===============【通华线下CSB】===============【解析二维码URL】 codeUrl: {}", codeUrl);
        BaseResponse baseResponse = new BaseResponse();
        log.info("===============【通华线下CSB】===============【上报通华查询队列】 【E_MQ_TH_CHECK_ORDER】");
        ThCheckOrderQueueDTO thCheckOrderQueueDTO = new ThCheckOrderQueueDTO(orders, channel, iso8583DTO);
        RabbitMassage rabbitMassage = new RabbitMassage(30, JSON.toJSONString(thCheckOrderQueueDTO));
        rabbitMQSender.send(AD3MQConstant.E_MQ_TH_CHECK_ORDER, JSON.toJSONString(rabbitMassage));
        baseResponse.setData(codeUrl);
        return baseResponse;
    }

    /**
     * 通华被扫接口
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        //插入通道订单
        insertChannelsOrder(orders, channel);
        //创建通华DTO
        ISO8583DTO iso8583DTO = createScanOrder(orders, channel);
        iso8583DTO.setProcessingCode_3("400101");
        iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52("303002" + channel.getPayCode() + "02" + NumberStringUtil.str2HexStr(authCode) + "0202"));
        log.info("==================【通华线下BSC】==================【调用Channels服务】【请求参数】 iso8583DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.thBSC(new ThDTO(iso8583DTO, channel, orders.getMerchantId()));
        log.info("==================【通华线下BSC】==================【调用Channels服务】【通华-BSC接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华线下BSC】==================【调用Channels服务】【通华-BSC接口】-【接口异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【通华线下BSC】==================【调用Channels服务】【通华-BSC接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        //将46域信息按02分割
        String[] domain46 = iso8583VO.getAdditionalData_46().split("02");
        log.info("===============【通华线下BSC】===============【46域信息】 domain46: {}", Arrays.toString(domain46));
        //索引第4位 : 通华返回的商户订单号
        orders.setChannelNumber(NumberStringUtil.hexStr2Str(domain46[4]));
        BaseResponse baseResponse = new BaseResponse();
        if ("AS".equals(iso8583VO.getResponseCode_39())) {
            //当39域等于AS: 该响应表示该交易已受理,未承兑
            log.info("===============【通华线下BSC】===============【39域返回AS,上报通华查询队列】 【E_MQ_TH_CHECK_ORDER】");
            ordersMapper.updateByPrimaryKeySelective(orders);
            ThCheckOrderQueueDTO thCheckOrderQueueDTO = new ThCheckOrderQueueDTO(orders, channel, iso8583DTO);
            RabbitMassage rabbitMassage = new RabbitMassage(20, JSON.toJSONString(thCheckOrderQueueDTO));
            rabbitMQSender.send(AD3MQConstant.E_MQ_TH_CHECK_ORDER, JSON.toJSONString(rabbitMassage));
            return baseResponse;
        }
        orders.setChannelCallbackTime(new Date());
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("00".equals(iso8583VO.getResponseCode_39())) {
            log.info("=================【通华线下BSC】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【通华线下BSC】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【通华线下BSC】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【通华线下BSC】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【通华线下BSC】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【通华线下BSC】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【通华线下BSC】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(iso8583VO.getResponseCode_39());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【通华线下BSC】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【通华线下BSC】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【通华线下BSC】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/15
     * @Descripate 通华退款
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        ThDTO thDTO = new ThDTO();
        ISO8583DTO iso8583DTO = this.createRefundISO8583DTO(channel, orderRefund);
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thDTO.setMerchantId(orderRefund.getMerchantId());
        log.info("=================【TH退款】=================【请求Channels服务TH退款】请求参数 iso8583DTO: {} ", JSON.toJSONString(iso8583DTO));
        BaseResponse response = channelsFeign.thRefund(thDTO);
        log.info("=================【TH退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));

        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            ISO8583DTO thResDTO = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            //请求成功
            if (response.getMsg().equals("success")) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【TH退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, thResDTO.getRetrievalReferenceNumber_37(), thResDTO.getResponseCode_39());
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【TH退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund, channel);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【TH退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;

    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/15
     * @Descripate 通华撤销
     **/
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse baseResponse = new BaseResponse();
        ThDTO thDTO = new ThDTO();
        ISO8583DTO iso8583DTO = this.createQueryISO8583DTO(channel, orderRefund);
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thDTO.setMerchantId(orderRefund.getMerchantId());
        log.info("=================【TH撤销 cancel】=================【请求Channels服务TH退款】请求参数 iso8583DTO: {} ", JSON.toJSONString(iso8583DTO));
        BaseResponse response = channelsFeign.thQuery(thDTO);
        log.info("=================【TH撤销 cancel】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            ISO8583DTO iso8583DTO1 = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            if (iso8583DTO1.getAdditionalData_46().equals("1")) {
                //交易成功
                //更新订单状态
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, iso8583DTO1.getRetrievalReferenceNumber_37(), new Date()) == 1) {
                    //更新成功
                    baseResponse = this.cancelPaying(channel, orderRefund, null);
                } else {
                    baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                    //更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }
            } else if (iso8583DTO1.getAdditionalData_46().equals("3")) {
                //交易中
                baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【TH撤销 cancel】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            } else {
                //交易失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【TH撤销 cancel】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, iso8583DTO1.getRetrievalReferenceNumber_37(), new Date());
            }

        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【TH撤销 cancel】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    @Override
    public BaseResponse thSign(ThDTO thDTO) {
        log.info("++++++++++++++++++++通华签到开始++++++++++++++++++++");
        BaseResponse baseResponse = channelsFeign.thSign(thDTO);
        log.info("++++++++++++++++++++通华签到结束++++++++++++++++++++");
        return baseResponse;
    }

    /**
     * 撤销中
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        ThDTO thDTO = new ThDTO();
        ISO8583DTO iso8583DTO = this.createRefundISO8583DTO(channel, orderRefund);
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thDTO.setMerchantId(orderRefund.getMerchantId());
        log.info("=================【TH撤销 cancelPaying】=================【请求Channels服务TH退款】请求参数 iso8583DTO: {} ", JSON.toJSONString(iso8583DTO));
        BaseResponse response = channelsFeign.thRefund(thDTO);
        log.info("=================【TH撤销 cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));

        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if (response.getMsg().equals("success")) {
                //退款成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【TH撤销 cancelPaying】=================【撤销成功】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【TH撤销 cancelPaying】=================【撤销失败】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【TH撤销 cancelPaying】=================【请求失败】orderId : {}", orders.getId());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("=================【TH撤销 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * Create refund iso 8583 dto iso 8583 dto.
     *
     * @param channel     the channel
     * @param orderRefund the order refund
     * @return iso 8583 dto
     * @Author YangXu
     * @Date 2020 /5/18
     * @Descripate 创建退款DTO
     */
    public ISO8583DTO createRefundISO8583DTO(Channel channel, OrderRefund orderRefund) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("400100");
        iso8583DTO.setAmountOfTransactions_4(NumberStringUtil.addLeftChar(orderRefund.getTradeAmount().toString().replace(".", ""), 12, '0'));
        iso8583DTO.setSystemTraceAuditNumber_11(String.valueOf(System.currentTimeMillis()).substring(6, 12));
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId()); //机构号
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        String s46 = "3030020202" + NumberStringUtil.str2HexStr(orderRefund.getChannelNumber()) + "0202";
        BerTlvBuilder berTlvBuilder = new BerTlvBuilder();
        //这里的Tag要用16进制,Length是自动算出来的,最后是要存的数据
        berTlvBuilder.addHex(new BerTag(0x5F52), s46);
        byte[] bytes = berTlvBuilder.buildArray();
        ////转成Hex码来传输
        String hexString = HexUtil.toHexString(bytes);
        iso8583DTO.setAdditionalData_46("5F" + hexString);
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        iso8583DTO.setReservedPrivate_60("55" + orderRefund.getReportNumber().substring(6, 12));//批次号
        return iso8583DTO;
    }

    /**
     * Create query iso 8583 dto iso 8583 dto.
     *
     * @param channel     the channel
     * @param orderRefund the order refund
     * @return iso 8583 dto
     * @Author YangXu
     * @Date 2020 /5/18
     * @Descripate 创建查询DTO
     */
    public ISO8583DTO createQueryISO8583DTO(Channel channel, OrderRefund orderRefund) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("700206");
        iso8583DTO.setAmountOfTransactions_4(NumberStringUtil.addLeftChar(orderRefund.getTradeAmount().toString().replace(".", ""), 12, '0'));
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getReportNumber().substring(0, 6));
        //服务点输入方式码
        iso8583DTO.setPointOfServiceEntryMode_22("030");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId()); //机构号
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //附加信息
        String s46 = "3030020202" + NumberStringUtil.str2HexStr(orderRefund.getChannelNumber()) + "0202";
        BerTlvBuilder berTlvBuilder = new BerTlvBuilder();
        //这里的Tag要用16进制,Length是自动算出来的,最后是要存的数据
        berTlvBuilder.addHex(new BerTag(0x5F52), s46);
        byte[] bytes = berTlvBuilder.buildArray();
        ////转成Hex码来传输
        String hexString = HexUtil.toHexString(bytes);
        iso8583DTO.setAdditionalData_46("5F" + hexString);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        iso8583DTO.setReservedPrivate_60("01" + orderRefund.getReportNumber().substring(6, 12));
        return iso8583DTO;
    }


    /**
     * 从缓存中获取62域信息
     *
     * @param channel    the channel
     * @param merchantId
     * @return th key
     */
    private String getThKey(Channel channel, String merchantId) {
        MerchantReport merchantReport = commonRedisDataService.getMerchantReport(merchantId, channel.getChannelCode());
        return commonRedisDataService.getThKey(merchantReport.getExtend1(), merchantReport.getSubMerchantCode(), channel);
    }

    /**
     * 从缓存中获取62域信息
     *
     * @param orderRefund the order refund
     * @param channel     the channel
     * @param merchantId
     * @return th key
     */
    private String getThKey(OrderRefund orderRefund, Channel channel, String merchantId) {
        MerchantReport merchantReport = commonRedisDataService.getMerchantReport(merchantId, channel.getChannelCode());
        return commonRedisDataService.getThKey(merchantReport.getExtend1(), merchantReport.getSubMerchantCode(), channel);
    }

    /**
     * 52域加密
     *
     * @param pin           the pin
     * @param pan           the pan
     * @param key           the key
     * @param channelMd5Key the channel md 5 key
     * @return string string
     */
    public static String pinEncryption(String pin, String pan, String key, String channelMd5Key) {
        //加密pin
        byte[] apan = NumberStringUtil.formartPan(pan.getBytes());
        System.out.println("pan=== " + ISOUtil.bytesToHexString(apan));
        byte[] apin = NumberStringUtil.formatPinByX98(pin.getBytes());
        System.out.println("pin=== " + ISOUtil.bytesToHexString(apin));
        byte[] xorMac = new byte[apan.length];
        for (int i = 0; i < apan.length; i++) {//异或
            xorMac[i] = apin[i] ^= apan[i];
        }
        System.out.println("异或===" + ISOUtil.bytesToHexString(xorMac));
        try {
            String substring = key.substring(0, 32);
            String pik = Objects.requireNonNull(EcbDesUtil.decode3DEA(channelMd5Key, substring)).toUpperCase();
            System.out.println("===== pik =====" + pik);
            String s = DesUtil.doubleDesEncrypt(pik, ISOUtil.bytesToHexString(xorMac));
            System.out.println("===== pINEncryption =====" + s);
            return s;
        } catch (Exception e) {
            System.out.println("===== pINEncryption e =====" + e);
        }
        return null;
    }

    /**
     * trk 加密
     *
     * @param str           the str
     * @param key           the key
     * @param channelMd5Key the channel md 5 key
     * @return string
     */
    public static String trkEncryption(String str, String key, String channelMd5Key) {
        //80-112 Trk密钥位
        String substring = key.substring(80, 112);
        String trk = Objects.requireNonNull(EcbDesUtil.decode3DEA(channelMd5Key, substring)).toUpperCase();
        String newStr;
        if (str.length() % 2 != 0) {
            newStr = str.length() + str + "0";
        } else {
            newStr = str.length() + str;
        }
        byte[] bcd = NumberStringUtil.str2Bcd(newStr);
        return Objects.requireNonNull(EcbDesUtil.encode3DEA(trk, cn.hutool.core.util.HexUtil.encodeHexStr(bcd))).toUpperCase();
    }

    /**
     * 从商户报备中获取商户号与设备号信息
     *
     * @param merchantId  the merchant id
     * @param channelCode the channel code
     * @param dto         the dto
     * @return filed 41 and 42
     */
    private ISO8583DTO setFiled41And42(String merchantId, String channelCode, ISO8583DTO dto) {
        MerchantReport merchantReport = commonRedisDataService.getMerchantReport(merchantId, channelCode);
        //受卡机终端标识码 (设备号)
        dto.setCardAcceptorTerminalIdentification_41(merchantReport.getExtend1());
        //受卡方标识码 (商户号)
        log.info("++++++++++++++++++设置41 42信息 +++++++++++++mr:{}", JSON.toJSONString(merchantReport));
        dto.setCardAcceptorIdentificationCode_42(merchantReport.getSubMerchantCode());
        return dto;
    }

    /**
     * ISO8583 中与 pin 银行卡密码相关的域赋值
     *
     * @param orders     the orders
     * @param iso8583DTO the iso 8583 dto
     * @param channels   the channels
     * @return filed 22 and 26 and 52 and 53
     */
    private ISO8583DTO setFiled22And26And52And53(Orders orders, ISO8583DTO iso8583DTO, Channel channels) {
        //个人PIN
        if (StringUtils.isEmpty(orders.getPin())) {
            log.info("***************************银行卡pin不存在***************************");
            //服务点输入方式码 022：刷卡，无PIN
            iso8583DTO.setPointOfServiceEntryMode_22("022");
            return iso8583DTO;
        }
        log.info("***************************银行卡pin存在***************************");
        //服务点输入方式码 021 刷卡，且PIN可输入
        iso8583DTO.setPointOfServiceEntryMode_22("021");
        //密码长度
        iso8583DTO.setPointOfServicePINCaptureCode_26("06");
        //获取62域信息
        String thKey = getThKey(channels, orders.getMerchantId());
        iso8583DTO.setPINData_52(pinEncryption(AESUtil.aesDecrypt(orders.getPin()), AESUtil.aesDecrypt(orders.getUserBankCardNo()).substring(3, 15), thKey, channels.getMd5KeyStr()));
        iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        return iso8583DTO;
    }

    /**
     * ISO8583 中与 pin 银行卡密码相关的域赋值
     *
     * @param orderRefund the orderRefund
     * @param iso8583DTO  the iso 8583 dto
     * @param channels    the channels
     * @return filed 22 and 26 and 52 and 53
     */
    private ISO8583DTO setFiled22And26And52And53(OrderRefund orderRefund, ISO8583DTO iso8583DTO, Channel channels) {
        //个人PIN
        if (StringUtils.isEmpty(orderRefund.getPin())) {
            log.info("***************************银行卡pin不存在***************************");
            //服务点输入方式码 022：刷卡，无PIN
            iso8583DTO.setPointOfServiceEntryMode_22("022");
            return iso8583DTO;
        }
        log.info("***************************银行卡pin存在***************************");
        //服务点输入方式码 021 刷卡，且PIN可输入
        iso8583DTO.setPointOfServiceEntryMode_22("021");
        //密码长度
        iso8583DTO.setPointOfServicePINCaptureCode_26("06");
        //获取62域信息
        String thKey = getThKey(channels, orderRefund.getMerchantId());
        iso8583DTO.setPINData_52(pinEncryption(AESUtil.aesDecrypt(orderRefund.getPin()), AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()).substring(3, 15), thKey, channels.getMd5KeyStr()));
        iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        return iso8583DTO;
    }

    /**
     * ISO8583 中与 pin 银行卡密码相关的域赋值
     *
     * @param preOrders  the preOrders
     * @param iso8583DTO the iso 8583 dto
     * @param channels   the channels
     * @return filed 22 and 26 and 52 and 53
     */
    private ISO8583DTO setFiled22And26And52And53(PreOrders preOrders, ISO8583DTO iso8583DTO, Channel channels) {
        //个人PIN
        if (StringUtils.isEmpty(preOrders.getPin())) {
            log.info("***************************银行卡pin不存在***************************");
            //服务点输入方式码 022：刷卡，无PIN
            iso8583DTO.setPointOfServiceEntryMode_22("022");
            return iso8583DTO;
        }
        log.info("***************************银行卡pin存在***************************");
        //服务点输入方式码 021 刷卡，且PIN可输入
        iso8583DTO.setPointOfServiceEntryMode_22("021");
        //密码长度
        iso8583DTO.setPointOfServicePINCaptureCode_26("06");
        //获取62域信息
        String thKey = getThKey(channels, preOrders.getMerchantId());
        iso8583DTO.setPINData_52(pinEncryption(AESUtil.aesDecrypt(preOrders.getPin()), AESUtil.aesDecrypt(preOrders.getUserBankCardNo()).substring(3, 15), thKey, channels.getMd5KeyStr()));
        iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        return iso8583DTO;
    }

    /**
     * 将BigDecimal格式金额转换至8583格式
     *
     * @param dto
     * @param decimal
     */
    private void setFiled4(ISO8583DTO dto, BigDecimal decimal) {
        //获取交易金额的小数位数
        int numOfBits = String.valueOf(decimal).length() - String.valueOf(decimal).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = decimal.intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = decimal.movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        //交易金额
        dto.setAmountOfTransactions_4(formatAmount);
    }


    /**
     * 通华线下银行卡下单
     *
     * @param orders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse bankCardReceipt(Orders orders, Channel channel) {
        //插入通道订单
        insertChannelsOrder(orders, channel);
        //创建通华DTO
        ISO8583DTO iso8583DTO = createBankOrder(orders, channel);
        log.info("==================【通华线下银行卡下单】==================【调用Channels服务】【请求参数】 iso8583DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.thBankCard(new ThDTO(iso8583DTO, channel, orders.getMerchantId()));
        log.info("==================【通华线下银行卡下单】==================【调用Channels服务】【通华线下银行卡下单接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华线下银行卡下单】==================【调用Channels服务】【通华线下银行卡下单接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【通华线下银行卡下单】==================【调用Channels服务】【通华线下银行卡下单接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        ordersMapper.updateByPrimaryKeySelective(orders);
        orders.setUpdateTime(new Date());
        orders.setChannelCallbackTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        BaseResponse response = new BaseResponse();
        log.info("***************************调用DTO*************************** dto:{}", JSON.toJSONString(iso8583DTO));
        log.info("***************************返回VO*************************** vo:{}:", JSON.toJSONString(iso8583VO));
        if ("00".equals(iso8583VO.getResponseCode_39())) {
            log.info("=================【通华线下银行卡下单】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            //退款时需要使用到37域信息
            String retrievalReferenceNumber_37 = iso8583VO.getRetrievalReferenceNumber_37();
            orders.setRemark1(retrievalReferenceNumber_37);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), retrievalReferenceNumber_37, TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【通华线下银行卡下单】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【通华线下银行卡下单】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【通华线下银行卡下单】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【通华线下银行卡下单】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【通华线下银行卡下单】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【通华线下银行卡下单】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(iso8583VO.getResponseCode_39());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【通华线下银行卡下单】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【通华线下银行卡下单】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【通华线下银行卡下单】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return response;
    }

    /**
     * 创建银行卡订单
     *
     * @param orders  the orders
     * @param channel the channel
     * @return the iso 8583 dto
     */
    private ISO8583DTO createBankOrder(Orders orders, Channel channel) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        //当前时间戳
        String timeStamp = System.currentTimeMillis() + "";
        //11 域需要在冲正的时候使用
        String domain11 = orders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        orders.setReportNumber(domain11 + domain60_2);
        //消息类型
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("009000");
        setFiled4(iso8583DTO, orders.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(orders, iso8583DTO, channel);
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //设置41域和42域信息
        setFiled41And42(orders.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        domain60_2 +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        //获取62域信息
        String thKey = getThKey(channel, orders.getMerchantId());
        //银行卡号
        iso8583DTO.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orders.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orders.getTrackData()), thKey, channel.getMd5KeyStr()));
        return iso8583DTO;
    }

    /**
     * 银行卡冲正接口
     *
     * @return
     * @Author YangXu
     * @Date 2020/5/26
     * @Descripate 银行卡冲正接口
     **/
    @Override
    public BaseResponse reversal(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        ThDTO thDTO = new ThDTO();
        ISO8583DTO iso8583DTO = this.createReversalDTO(channel, orderRefund);
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(iso8583DTO);
        thDTO.setMerchantId(orderRefund.getMerchantId());
        log.info("=================【TH冲正 reversal】=================【请求Channels服务TH冲正】请求参数 iso8583DTO: {} ", JSON.toJSONString(iso8583DTO));
        BaseResponse response = channelsFeign.thBankCardReverse(thDTO);
        log.info("=================【TH冲正 reversal】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(response.getData()), ISO8583DTO.class);
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("merchantOrderId", orderRefund.getMerchantOrderId());
        Orders orders = new Orders();
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                // 修改订单状态为冲正成功
                orders.setTradeStatus((TradeConstant.ORDER_RESEVAL_SUCCESS));
            } else {
                // 修改订单状态为冲正失败
                orders.setTradeStatus((TradeConstant.ORDER_RESEVAL_FALID));
                orders.setRemark5(iso8583VO.getResponseCode_39());
                baseResponse.setCode(EResultEnum.REVERSAL_ERROR.getCode());
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REVERSAL_ERROR.getCode());
        }
        if (ordersMapper.updateByExampleSelective(orders, example) != 1) {
            log.info("=================【通华冲正】=================【订单冲正后后更新数据库失败】 orderId: {}", orders.getId());
        }
        return baseResponse;
    }

    /**
     * 创建冲正DTO
     *
     * @param channel     the channel
     * @param orderRefund the order refund
     * @return iso 8583 dto
     * @Author YangXu
     * @Date 2020 /5/18
     * @Descripate 创建冲正DTO
     */
    private ISO8583DTO createReversalDTO(Channel channel, OrderRefund orderRefund) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0400");
        //获取62域信息
        String thKey = getThKey(orderRefund, channel, orderRefund.getMerchantId());
        iso8583DTO.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        iso8583DTO.setProcessingCode_3("009000");
        //获取交易金额的小数位数
        setFiled4(iso8583DTO, orderRefund.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getReportNumber().substring(0, 6));
        //服务点输入方式码 同原交易 文档上PIN参数不用输入
        iso8583DTO.setPointOfServiceEntryMode_22("022");
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId()); //机构号
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), thKey, channel.getMd5KeyStr()));
        //冲正原因
        //a)  POS终端在时限内未能收到POS中心的应答消息而引发，冲正原因码填“98”。
        //b)  POS终端在时限内收到POS中心的批准应答消息，但由于POS机故障无法完成交易而引发，冲正原因码填“96”。
        //c)  POS终端对收到POS中心的应答消息，验证MAC出错，冲正原因码填“A0”。
        //d)  其他情况，冲正原因码填“06”。
        iso8583DTO.setResponseCode_39("06");
        //设置41域 42域信息
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        orderRefund.getReportNumber().substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        /* 61域可不填
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                orderRefund.getReportNumber().substring(6, 12) +
                        //61.2 原交易流水号 11域
                        orderRefund.getReportNumber().substring(0, 6) +
                        //61.3 原交易日期 由消费返回的13域中获取
                        "0603";
        iso8583DTO.setOriginalMessage_61(str61);*/
        return iso8583DTO;
    }

    /**
     * th 银行卡退款
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse bankRefund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        ThDTO thDTO = new ThDTO();
        ISO8583DTO dto = null;
        Orders orders = ordersMapper.selectByMerchantOrderId(orderRefund.getMerchantOrderId());
        if (DateUtil.isSameDay(orders.getCreateTime(), new Date())) {
            //当日内走撤销
            log.info("=================【TH银行退款 --订单当日内 走撤销方式退款】=================");
            dto = this.createBankUndoDTO(channel, orderRefund, orders);
        } else {
            dto = this.createBankRefundDTO(channel, orderRefund, orders);
        }
        thDTO.setChannel(channel);
        thDTO.setIso8583DTO(dto);
        thDTO.setMerchantId(orderRefund.getMerchantId());
        log.info("=================【TH银行退款】=================【请求Channels服务TH银行退款】请求参数 iso8583DTO: {} ", JSON.toJSONString(dto));
        BaseResponse response = channelsFeign.thBankCardRefund(thDTO);
        log.info("=================【TH银行退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            ISO8583DTO vo = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            log.info("***************************调用DTO*************************** dto:{}", JSON.toJSONString(dto));
            log.info("***************************返回VO*************************** vo:{}:", JSON.toJSONString(vo));
            //请求成功
            if ("00".equals(vo.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【TH银行退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, vo.getRetrievalReferenceNumber_37(), vo.getResponseCode_39());
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【TH银行退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund, channel);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【TH退款】===============【请求失败 上报队列 BTK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.BTK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * 创建银行卡退款DTO
     *
     * @param channel     the channel
     * @param orderRefund the order refund
     * @param orders      the orders
     * @return iso 8583 dto
     * @Author YangXu
     * @Date 2020 /5/18
     * @Descripate 创建银行卡退款DTO
     */
    private ISO8583DTO createBankRefundDTO(Channel channel, OrderRefund orderRefund, Orders orders) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0220");
        //获取62域信息
        String thKey = getThKey(channel, orders.getMerchantId());
        iso8583DTO.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        iso8583DTO.setProcessingCode_3("200000");
        //获取交易金额的小数位数
        setFiled4(iso8583DTO, orderRefund.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(orders.getId().substring(9, 15));
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(orders, iso8583DTO, channel);
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId()); //机构号
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), thKey, channel.getMd5KeyStr()));
        //37 域
        iso8583DTO.setRetrievalReferenceNumber_37(orders.getRemark1());
        //设置41域 42域信息
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        String str60 =
                //60.1 消息类型码
                "25" +
                        //60.2 批次号
                        orderRefund.getReportNumber().substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        // 61 自定义域
        String str = DateUtil.format(orders.getCreateTime(), "MMdd");
        String str61 =
                //61.1 原批次号
                orderRefund.getReportNumber().substring(6, 12) +
                        //61.2 原交易流水号 11域
                        orderRefund.getReportNumber().substring(0, 6) +
                        //61.3 原交易日期
                        str;
        iso8583DTO.setOriginalMessage_61(str61);
        return iso8583DTO;
    }

    /**
     * 创建银行卡撤销DTO
     *
     * @param channel     the channel
     * @param orderRefund the order refund
     * @param orders      the orders
     * @return iso 8583 dto
     * @Author YangXu
     * @Date 2020 /5/18
     * @Descripate 创建银行卡撤销DTO
     */
    private ISO8583DTO createBankUndoDTO(Channel channel, OrderRefund orderRefund, Orders orders) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //获取62域信息
        String thKey = getThKey(channel, orders.getMerchantId());
        iso8583DTO.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        iso8583DTO.setProcessingCode_3("200000");
        //获取交易金额的小数位数
        setFiled4(iso8583DTO, orderRefund.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(orders.getId().substring(9, 15));
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //对 22域 26域 52域 53域 相关参数进行封装
        setFiled22And26And52And53(orders, iso8583DTO, channel);
        //机构号
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), thKey, channel.getMd5KeyStr()));
        //37 域
        iso8583DTO.setRetrievalReferenceNumber_37(orders.getRemark1());
        //设置41域 42域信息
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        String str60 =
                //60.1 消息类型码
                "23" +
                        //60.2 批次号
                        orderRefund.getReportNumber().substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        // 61 自定义域
        String str = DateUtil.format(orders.getCreateTime(), "MMdd");
        String str61 =
                //61.1 原批次号
                orderRefund.getReportNumber().substring(6, 12) +
                        //61.2 原交易流水号 11域
                        orders.getId().substring(10, 16) +
                        //61.3 原交易日期
                        str;
        iso8583DTO.setOriginalMessage_61(str61);
        return iso8583DTO;
    }

    /**
     * 通华预授权
     *
     * @param preOrders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse preAuth(PreOrders preOrders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        ISO8583DTO iso8583DTO = createPreAuthDto(preOrders, channel);
        log.info("==================【通华预授权】==================【调用Channels服务】【通华-预授权接口】  DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.preAuth(new ThDTO(iso8583DTO, channel, preOrders.getMerchantId()));
        log.info("==================【通华预授权】==================【调用Channels服务】【通华-预授权接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        preOrders.setChannelCallbackTime(new Date());
        Example example = new Example(PreOrders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderStatus", "0");
        criteria.andEqualTo("id", preOrders.getId());
        //请求失败
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【通华预授权】==================【预授权】iso8583VO:{}", com.alibaba.fastjson.JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrders.setRemark2(iso8583VO.getDateOfLocalTransaction_13());
                preOrders.setRemark3(iso8583VO.getRetrievalReferenceNumber_37());
                preOrders.setRemark4(iso8583VO.getAuthorizationIdentificationResponse_38());
                preOrders.setOrderStatus((byte) 1);
            } else {
                log.info("==================【通华预授权】==================【预授权失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                preOrders.setRemark(iso8583VO.getResponseCode_39());
                preOrders.setOrderStatus((byte) 2);
            }
        } else {
            //请求失败
            log.info("==================【通华预授权】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            preOrders.setOrderStatus((byte) 2);
        }
        preOrdersMapper.updateByExampleSelective(preOrders, example);
        return baseResponse;
    }

    /**
     * 创建预授权DTO
     *
     * @param preOrders
     * @param channel
     * @return
     */
    private ISO8583DTO createPreAuthDto(PreOrders preOrders, Channel channel) {
        ISO8583DTO dto = new ISO8583DTO();
        //当前时间戳
        String timeStamp = System.currentTimeMillis() + "";
        //11 域需要在冲正的时候使用
        String domain11 = preOrders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        preOrders.setRemark1(domain11 + domain60_2);
        //消息类型
        dto.setMessageType("0100");
        dto.setProcessingCode_3("030000");
        //设置4域金额
        setFiled4(dto, preOrders.getTradeAmount());
        //受卡方系统跟踪号
        dto.setSystemTraceAuditNumber_11(domain11);
        //服务点条件码
        dto.setPointOfServiceConditionMode_25("00");
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(preOrders, dto, channel);
        //受理方标识码 (机构号)
        dto.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //设置41域和42域信息
        setFiled41And42(preOrders.getMerchantId(), channel.getChannelCode(), dto);
        //交易货币代码
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "10" +
                        //60.2 批次号 自定义
                        domain60_2 +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);
        //获取62域信息
        String thKey = getThKey(channel, preOrders.getMerchantId());
        //银行卡号
        dto.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        //磁道2 信息
        dto.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(preOrders.getTrackData()), thKey, channel.getMd5KeyStr()));
        return dto;
    }


    /**
     * 通华预授权冲正
     *
     * @param channel
     * @param preOrders
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse preAuthReverse(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        ISO8583DTO iso8583DTO = createPreAuthReverseDto(preOrders, channel);
        log.info("==================【 通华预授权冲正】==================【调用Channels服务】【通华- 通华预授权冲正接口】  DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.preAuthReverse(new ThDTO(iso8583DTO, channel, preOrders.getMerchantId()));
        log.info("==================【 通华预授权冲正】==================【调用Channels服务】【通华- 通华预授权冲正接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        preOrders.setChannelCallbackTime(new Date());
        Example example = new Example(PreOrders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderStatus", "1");
        criteria.andEqualTo("id", preOrders.getId());
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【 通华预授权冲正】==================【预授权】iso8583VO:{}", com.alibaba.fastjson.JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrders.setChannelNumber(iso8583VO.getRetrievalReferenceNumber_37());
                preOrders.setOrderStatus((byte) 3);
            } else {
                log.info("==================【 通华预授权冲正】==================【预授权失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ORDER_NOT_SUPPORT_REVERSE.getCode());
                preOrders.setRemark(iso8583VO.getResponseCode_39());
            }
        } else {
            //请求失败
            log.info("==================【 通华预授权冲正】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ORDER_NOT_SUPPORT_REVERSE.getCode());
        }
        preOrdersMapper.updateByExampleSelective(preOrders, example);
        return baseResponse;
    }

    /**
     * 创建 通华预授权冲正 dto
     *
     * @param preOrders
     * @param channel
     * @return
     */
    private ISO8583DTO createPreAuthReverseDto(PreOrders preOrders, Channel channel) {
        ISO8583DTO dto = new ISO8583DTO();
        String filed11 = preOrders.getRemark1().substring(0, 6);
        String filed60_2 = preOrders.getRemark1().substring(6, 12);
        String filed61_3 = preOrders.getRemark2();
        //消息类型
        dto.setMessageType("0400");
        dto.setProcessingCode_3("030000");
        //设置4域金额
        setFiled4(dto, preOrders.getTradeAmount());
        //受卡方系统跟踪号
        dto.setSystemTraceAuditNumber_11(filed11);
        //服务点条件码
        dto.setPointOfServiceConditionMode_25("06");
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(preOrders, dto, channel);
        //受理方标识码 (机构号)
        dto.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //设置41域和42域信息
        setFiled41And42(preOrders.getMerchantId(), channel.getChannelCode(), dto);
        //交易货币代码
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "10" +
                        //60.2 批次号
                        filed60_2 +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                filed60_2 +
                        //61.2 原交易流水号 11域
                        filed11 +
                        //61.3 原交易日期 由预授权返回的13域中获取
                        filed61_3;
        dto.setOriginalMessage_61(str61);
        //获取62域信息
        String thKey = getThKey(channel, preOrders.getMerchantId());
        //银行卡号
        dto.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        //磁道2 信息
        dto.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(preOrders.getTrackData()), thKey, channel.getMd5KeyStr()));
        return dto;
    }

    /**
     * 通华预授权撤销
     *
     * @param channel
     * @param preOrders
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse preAuthRevoke(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        ISO8583DTO iso8583DTO = createPreAuthRevokeDto(preOrders, channel);
        log.info("==================【 通华预授权撤销】==================【调用Channels服务】【通华- 通华预授权撤销接口】  DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.preAuthRevoke(new ThDTO(iso8583DTO, channel, preOrders.getMerchantId()));
        log.info("==================【 通华预授权撤销】==================【调用Channels服务】【通华- 通华预授权撤销接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        preOrders.setChannelCallbackTime(new Date());
        Example example = new Example(PreOrders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderStatus", "1");
        criteria.andEqualTo("id", preOrders.getId());
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【 通华预授权撤销】==================【通华预授权撤销】iso8583VO:{}", com.alibaba.fastjson.JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrders.setChannelNumber(iso8583VO.getRetrievalReferenceNumber_37());
                preOrders.setOrderStatus((byte) 4);
            } else {
                log.info("==================【 通华预授权撤销】==================【通华预授权撤销失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ONLINE_ORDER_IS_NOT_ALLOW_UNDO.getCode());
                preOrders.setRemark(iso8583VO.getResponseCode_39());
            }
        } else {
            //请求失败
            log.info("==================【 通华预授权撤销】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ONLINE_ORDER_IS_NOT_ALLOW_UNDO.getCode());
            preOrdersMapper.updatePreStatusById0(preOrders.getId(), null, (byte) 2, null);
        }
        return baseResponse;
    }

    /**
     * 通华预授权撤销 dto
     *
     * @param preOrders
     * @param channel
     * @return
     */
    private ISO8583DTO createPreAuthRevokeDto(PreOrders preOrders, Channel channel) {
        ISO8583DTO dto = new ISO8583DTO();
        String filed11 = preOrders.getRemark1().substring(0, 6);
        String filed60_2 = preOrders.getRemark1().substring(6, 12);
        String filed61_3 = preOrders.getRemark2();
        //当前时间戳
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //11 域需要在冲正的时候使用
        String domain11 = preOrders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //暂存
        preOrders.setRemark5(domain11 + domain60_2);
        //消息类型
        dto.setMessageType("0100");
        dto.setProcessingCode_3("200000");
        //设置4域金额
        setFiled4(dto, preOrders.getTradeAmount());
        //受卡方系统跟踪号
        dto.setSystemTraceAuditNumber_11(domain11);
        //服务点条件码
        dto.setPointOfServiceConditionMode_25("06");
        //38域
        dto.setAuthorizationIdentificationResponse_38(preOrders.getRemark4());
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(preOrders, dto, channel);
        //受理方标识码 (机构号)
        dto.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //设置41域和42域信息
        setFiled41And42(preOrders.getMerchantId(), channel.getChannelCode(), dto);
        //交易货币代码
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "11" +
                        //60.2 批次号
                        domain60_2 +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                filed60_2 +
                        //61.2 原交易流水号 11域
                        filed11 +
                        //61.3 原交易日期 由预授权返回的13域中获取
                        filed61_3;
        dto.setOriginalMessage_61(str61);
        //获取62域信息
        String thKey = getThKey(channel, preOrders.getMerchantId());
        //银行卡号
        dto.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        //磁道2 信息
        dto.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(preOrders.getTrackData()), thKey, channel.getMd5KeyStr()));
        return dto;
    }

    /**
     * 通华预授权完成
     *
     * @param orders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse preAuthComplete(Orders orders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        ISO8583DTO dto = createPreAuthCompleteDTO(orders, channel);
        log.info("==================【通华预授权完成】==================【调用Channels服务】【通华预授权完成接口】  dto: {}", JSON.toJSONString(dto));
        BaseResponse channelResponse = channelsFeign.preAuthComplete(new ThDTO(dto, channel, orders.getMerchantId()));
        log.info("==================【通华预授权完成】==================【调用Channels服务】【通华预授权完成接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华预授权完成】==================【调用Channels服务】【通华预授权完成接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【通华预授权完成】==================【调用Channels服务】【通华线下银行卡下单接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        orders.setUpdateTime(new Date());
        orders.setChannelCallbackTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("00".equals(iso8583VO.getResponseCode_39())) {
            //支付成功
            log.info("==================【通华预授权完成】==================【支付成功】orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            orders.setChannelNumber(iso8583VO.getRetrievalReferenceNumber_37() + iso8583VO.getAuthorizationIdentificationResponse_38());
            orders.setReportNumber(orders.getReportNumber() + iso8583VO.getDateOfLocalTransaction_13());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), iso8583VO.getRetrievalReferenceNumber_37(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【通华预授权完成】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                if (preOrdersMapper.updatePreStatusByMerchantOrderId(orders.getMerchantOrderId(), orders.getOrderAmount(), null, (byte) 5) == 1) {
                    log.info("=================【通华预授权完成】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("=================【通华预授权完成】=================【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                        if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                        }
                        //更新成功,上报清结算
                        commonService.fundChangePlaceOrderSuccess(orders);
                    } catch (Exception e) {
                        log.error("=================【通华预授权完成】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    log.info("=================【通华预授权完成】=================【订单支付成功后更新原订单失败】 MerchantOrderId: {}", orders.getMerchantOrderId());
                }
            } else {
                log.info("=================【通华预授权完成】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            //支付失败
            log.info("==================【通华预授权完成】==================【支付失败】orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(iso8583VO.getResponseCode_39());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【通华预授权完成】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【通华预授权完成】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【通华预授权完成】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        return baseResponse;
    }

    /**
     * 创建通华预授权完成 dto
     *
     * @param orders
     * @param channel
     * @return
     */
    private ISO8583DTO createPreAuthCompleteDTO(Orders orders, Channel channel) {
        ISO8583DTO dto = new ISO8583DTO();
        String filed11 = orders.getPreRemark().substring(0, 6);
        String filed60_2 = orders.getPreRemark().substring(6, 12);
        String filed61_3 = orders.getRemark2();
        //当前时间戳
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //11 域需要在冲正的时候使用
        String domain11 = orders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //暂存
        orders.setRemark5(domain11 + domain60_2);
        //消息类型
        dto.setMessageType("0100");
        dto.setProcessingCode_3("200000");
        //设置4域金额
        setFiled4(dto, orders.getTradeAmount());
        //受卡方系统跟踪号
        dto.setSystemTraceAuditNumber_11(domain11);
        //服务点条件码
        dto.setPointOfServiceConditionMode_25("06");
        //38域
        dto.setAuthorizationIdentificationResponse_38(orders.getRemark4());
        //对 pin 相关参数进行封装
        setFiled22And26And52And53(orders, dto, channel);
        //受理方标识码 (机构号)
        dto.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //设置41域和42域信息
        setFiled41And42(orders.getMerchantId(), channel.getChannelCode(), dto);
        //交易货币代码
        dto.setCurrencyCodeOfTransaction_49("344");
        // 60 自定义域
        String str60 =
                //60.1 消息类型码
                "11" +
                        //60.2 批次号
                        domain60_2 +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        dto.setReservedPrivate_60(str60);
        // 61 自定义域
        String str61 =
                //61.1 原批次号
                filed60_2 +
                        //61.2 原交易流水号 11域
                        filed11 +
                        //61.3 原交易日期 由预授权返回的13域中获取
                        filed61_3;
        dto.setOriginalMessage_61(str61);
        //获取62域信息
        String thKey = getThKey(channel, orders.getMerchantId());
        //银行卡号
        dto.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orders.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        //磁道2 信息
        dto.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orders.getTrackData()), thKey, channel.getMd5KeyStr()));
        return dto;
    }

    /**
     * 通华预授权完成撤销
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse preAuthCompleteRevoke(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        log.info("==================【通华预授权完成撤销】==================orderId: {}", orderRefund.getOrderId());
        BaseResponse baseResponse = new BaseResponse();
        ISO8583DTO upiDTO = createCompleteRevokeDTO(orderRefund, channel);
        log.info("==================【通华预授权完成撤销】==================【调用Channels服务】 upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.preAuthCompleteRevoke(new ThDTO(upiDTO, channel, orderRefund.getMerchantId()));
        log.info("==================【通华预授权完成撤销】==================【调用Channels服务返回】channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(channelResponse.getData());
            ISO8583DTO vo = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            //请求成功
            if ("00".equals(vo.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, vo.getRetrievalReferenceNumber_37() + vo.getAuthorizationIdentificationResponse_38(), vo.getResponseCode_39());
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                log.info("==================【通华预授权完成撤销】=================退款失败: {}", JSON.toJSONString(orderRefund));
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund, channel);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【通华预授权完成撤销】===============【请求失败 上报队列 SAAS_YSQWC_CCQQSB_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.SAAS_YSQWC_CCQQSB_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * 设置通华预授权完成撤销 DTO
     *
     * @param orderRefund
     * @param channel
     * @return
     */
    private ISO8583DTO createCompleteRevokeDTO(OrderRefund orderRefund, Channel channel) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        //获取62域信息
        String thKey = getThKey(channel, orderRefund.getMerchantId());
        iso8583DTO.setProcessingCode_2(trkEncryption(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()), thKey, channel.getMd5KeyStr()));
        iso8583DTO.setProcessingCode_3("200000");
        //当前时间戳
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //11 域需要在冲正的时候使用
        String domain11 = timeStamp.substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //获取交易金额的小数位数
        setFiled4(iso8583DTO, orderRefund.getTradeAmount());
        //受卡方系统跟踪号
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getId().substring(9, 15));
        //服务点条件码
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //对 22域 26域 52域 53域 相关参数进行封装
        setFiled22And26And52And53(orderRefund, iso8583DTO, channel);
        //机构号
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32(channel.getChannelMerchantId());
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), thKey, channel.getMd5KeyStr()));
        //37 域
        iso8583DTO.setRetrievalReferenceNumber_37(orderRefund.getRemark3());
        //设置41域 42域信息
        setFiled41And42(orderRefund.getMerchantId(), channel.getChannelCode(), iso8583DTO);
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");
        //自定义域
        String str60 =
                //60.1 消息类型码
                "23" +
                        //60.2 批次号
                        orderRefund.getReportNumber().substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        // 61 自定义域
        String str = DateUtil.format(orderRefund.getCreateTime(), "MMdd");
        String str61 =
                //61.1 原批次号
                orderRefund.getReportNumber().substring(6, 12) +
                        //61.2 原交易流水号 11域
                        orderRefund.getId().substring(10, 16) +
                        //61.3 原交易日期
                        str;
        iso8583DTO.setOriginalMessage_61(str61);
        return iso8583DTO;
    }
}


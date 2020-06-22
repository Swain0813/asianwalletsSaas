package com.asianwallets.trade.channels.upi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.th.ISO8583.*;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.UpiRefundDTO;
import com.asianwallets.common.dto.upi.iso.UpiIsoUtil;
import com.asianwallets.common.dto.upi.utils.CryptoUtil;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.AESUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.upi.Upiservice;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.*;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Objects;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-04 14:40
 **/
@Slf4j
@Service
@HandlerType(TradeConstant.UPI)
public class UpiserviceImpl extends ChannelsAbstractAdapter implements Upiservice {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private PreOrdersMapper preOrdersMapper;

    @Autowired
    private CommonService commonService;

    /**
     * 银联主扫接口
     *
     * @param orders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiPayDTO upiPayDTO = this.createCSBDTO(orders, channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiPay(upiDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI线下CSB】==================【调用Channels服务】【UPI-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(jsonObject.get("qrCode"));
        return baseResponse;
    }

    /**
     * 银联被扫接口
     *
     * @param orders
     * @param channel
     * @param authCode
     * @return
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiPayDTO upiPayDTO = this.createBSCDTO(orders, channel, authCode);
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiPay(upiDTO);
        log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI线下BSC】==================【调用Channels服务】【UPI-BSC接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        //通道订单号
        orders.setChannelNumber(jsonObject.getString("pay_no"));
        ordersMapper.updateByPrimaryKeySelective(orders);
        //订单状态
        String status = jsonObject.getString("pay_result");
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("1".equals(status)) {
            //支付成功
            log.info("==================【UPI线下BSC】==================【支付成功】orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), jsonObject.getString("pay_no"), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【UPI线下BSC】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI线下BSC】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【UPI线下BSC】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【UPI线下BSC】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【UPI线下BSC】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if ("2".equals(status)) {
            //支付失败
            log.info("==================【UPI线下BSC】==================【支付失败】orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(jsonObject.getString("resp_desc"));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【UPI线下BSC】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI线下BSC】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【UPI线下BSC】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            //未支付
            log.info("==================【UPI线下BSC】==================【未支付】orderId: {}", orders.getId());

        }
        return baseResponse;
    }

    /**
     * UPI线下银行卡下单
     *
     * @param orders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse bankCardReceipt(Orders orders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createBankDTO(orders, channel);
        log.info("==================【UPI银行卡下单】==================【调用Channels服务】【UPI-银行卡下单接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI银行卡下单】==================【调用Channels服务】【UPI-银行卡下单接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI银行卡下单】==================【调用Channels服务】【UPI-银行卡下单】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【UPI银行卡下单】==================【调用Channels服务】【通华线下银行卡下单接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        ordersMapper.updateByPrimaryKeySelective(orders);
        orders.setUpdateTime(new Date());
        orders.setChannelCallbackTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        BaseResponse response = new BaseResponse();
        if ("00".equals(iso8583VO.getResponseCode_39())) {
            //支付成功
            log.info("==================【UPI银行卡下单】==================【支付成功】orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            orders.setChannelNumber(iso8583VO.getRetrievalReferenceNumber_37());
            orders.setReportNumber(orders.getReportNumber() + iso8583VO.getDateOfLocalTransaction_13());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), iso8583VO.getRetrievalReferenceNumber_37(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【UPI银行卡下单】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI银行卡下单】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【UPI银行卡下单】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【UPI银行卡下单】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【UPI银行卡下单】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            //支付失败
            log.info("==================【UPI银行卡下单】==================【支付失败】orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(iso8583VO.getResponseCode_39());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【UPI银行卡下单】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI银行卡下单】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【UPI银行卡下单】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/26
     * @Descripate 银行卡冲正接口
     **/
    @Override
    public BaseResponse reversal(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        log.info("==================【UPI银行卡冲正接口】==================orderId: {}", orderRefund.getOrderId());
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createReversalDTO(orderRefund, channel);
        log.info("==================【UPI银行卡冲正接口】==================【调用Channels服务】【UPI-银行卡下单接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI银行卡冲正接口】==================【调用Channels服务】【UPI-银行卡下单接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("merchantOrderId", orderRefund.getMerchantOrderId());
        Orders orders = new Orders();
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                // 修改订单状态为冲正成功
                orders.setCancelStatus((TradeConstant.ORDER_RESEVAL_SUCCESS));
            } else {
                // 修改订单状态为冲正失败
                orders.setCancelStatus((TradeConstant.ORDER_RESEVAL_FALID));
                orders.setRemark5(iso8583VO.getResponseCode_39());
                baseResponse.setCode(EResultEnum.REVERSAL_ERROR.getCode());
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REVERSAL_ERROR.getCode());
        }
        if (ordersMapper.updateByExampleSelective(orders, example) != 1) {
            log.info("=================【UPI银行卡冲正接口】=================【订单冲正后后更新数据库失败】 orderId: {}", orders.getId());
        }
        return baseResponse;
    }

    private UpiDTO createReversalDTO(OrderRefund orderRefund, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0400");
        iso8583DTO.setProcessingCode_3("190000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(orderRefund.getTradeAmount()).length() - String.valueOf(orderRefund.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = orderRefund.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = orderRefund.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getReportNumber().substring(0, 6));
        iso8583DTO.setDateOfExpired_14(orderRefund.getValid());
        if (StringUtils.isEmpty(orderRefund.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setResponseCode_39("96");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
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

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()));
        //加密信息
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡撤销】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/16
     * @Descripate 退款DTO
     **/
    private UpiDTO createBankRefundDTO(OrderRefund orderRefund, Channel channel, String type) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String domain11 = orderRefund.getReportNumber().substring(0, 6);
        String domain60_2 = orderRefund.getReportNumber().substring(6, 12);
        String domain62_3 = orderRefund.getReportNumber().substring(12, 16);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0220");
        iso8583DTO.setProcessingCode_3("200000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(orderRefund.getTradeAmount()).length() - String.valueOf(orderRefund.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = orderRefund.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = orderRefund.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getId().substring(10, 16));
        iso8583DTO.setDateOfExpired_14(orderRefund.getValid());
        if (!StringUtils.isEmpty(orderRefund.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(orderRefund.getPin()), AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setRetrievalReferenceNumber_37(orderRefund.getChannelNumber());
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        //自定义域
        String str60 =
                //60.1 消息类型码
                "25" +
                        //60.2 批次号 自定义
                        orderRefund.getId().substring(1, 7) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        iso8583DTO.setOriginalMessage_61(domain60_2 + domain11 + domain62_3);
        iso8583DTO.setReservedPrivate_63("000");
        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()));
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), channel.getMd5KeyStr()));
        //加密信息
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡撤销】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;
    }

    /**
     * UPI 银行卡退款
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse bankRefund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        log.info("==================【UPI银行卡退款接口】==================orderId: {}", orderRefund.getOrderId());
        BaseResponse baseResponse = new BaseResponse();
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        //拿到当天的23时间
        Date endtime = DateToolUtils.addHour(DateToolUtils.getDayEnd(orders.getCreateTime()), -1);
        String type = null;
        UpiDTO upiDTO;
        if (System.currentTimeMillis() < endtime.getTime() && orderRefund.getRefundType() == 1) {
            //撤销接口
            type = "CANCEL";
            upiDTO = this.createBankUndoDTO(orderRefund, channel, type);
        } else {
            //退款接口
            type = "REFUND";
            upiDTO = this.createBankRefundDTO(orderRefund, channel, type);
        }
        log.info("==================【UPI退款】==================【调用Channels服务】type：{}  upiDTO: {}", type, JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI退款】==================【调用Channels服务返回】type：{}  channelResponse: {}", type, JSON.toJSONString(channelResponse));
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(channelResponse.getData());
            ISO8583DTO vo = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            //请求成功
            if ("00".equals(vo.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, vo.getRetrievalReferenceNumber_37(), vo.getResponseCode_39());
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
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
     * @return
     * @Author YangXu
     * @Date 2020/6/16
     * @Descripate 撤销DTO
     **/
    private UpiDTO createBankUndoDTO(OrderRefund orderRefund, Channel channel, String type) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String domain11 = orderRefund.getReportNumber().substring(0, 6);
        String domain60_2 = orderRefund.getReportNumber().substring(6, 12);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("280000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(orderRefund.getTradeAmount()).length() - String.valueOf(orderRefund.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = orderRefund.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = orderRefund.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(orderRefund.getId().substring(10, 16));
        iso8583DTO.setDateOfExpired_14(orderRefund.getValid());
        if (!StringUtils.isEmpty(orderRefund.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(orderRefund.getPin()), AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }
        iso8583DTO.setPointOfServiceConditionMode_25("82");
        iso8583DTO.setRetrievalReferenceNumber_37(orderRefund.getChannelNumber());
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        //自定义域
        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        orderRefund.getId().substring(1, 7) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        iso8583DTO.setOriginalMessage_61(domain60_2 + domain11);

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(orderRefund.getUserBankCardNo()));
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orderRefund.getTrackData()), channel.getMd5KeyStr()));
        //加密信息
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡撤销】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/8
     * @Descripate 创建银行卡下单dto
     **/
    private UpiDTO createBankDTO(Orders orders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String timeStamp = System.currentTimeMillis() + "";
        String domain11 = orders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        orders.setReportNumber(domain11 + domain60_2);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("190000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(orders.getTradeAmount()).length() - String.valueOf(orders.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = orders.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = orders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        iso8583DTO.setDateOfExpired_14(orders.getValid());
        if (!StringUtils.isEmpty(orders.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(orders.getPin()), AESUtil.aesDecrypt(orders.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }

        iso8583DTO.setPointOfServiceConditionMode_25("82");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        timeStamp.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(orders.getUserBankCardNo()));
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orders.getTrackData()), channel.getMd5KeyStr()));
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡下单】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;
    }

    /**
     * UPI预授权
     *
     * @param preOrders
     * @param channel
     * @return
     */
    @Override
    public BaseResponse preAuth(PreOrders preOrders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createPreAuthDTO(preOrders, channel);
        log.info("==================【UPI预授权】==================【调用Channels服务】【UPI-预授权接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI预授权】==================【调用Channels服务】【UPI-预授权接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【UPI预授权】==================【预授权】iso8583VO:{}", JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrdersMapper.updatePreStatusById0(preOrders.getId(), iso8583VO.getRetrievalReferenceNumber_37() + iso8583VO.getAuthorizationIdentificationResponse_38(), (byte) 1, null);
            } else {
                log.info("==================【UPI预授权】==================【预授权失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                preOrdersMapper.updatePreStatusById0(preOrders.getId(), null, (byte) 2, null);
            }
        } else {
            //请求失败
            log.info("==================【UPI预授权】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            preOrdersMapper.updatePreStatusById0(preOrders.getId(), null, (byte) 2, null);
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 创造预授权DTO
     **/
    private UpiDTO createPreAuthDTO(PreOrders preOrders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String timeStamp = System.currentTimeMillis() + "";
        String domain11 = preOrders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        preOrders.setRemark1(domain60_2 + domain11 + DateToolUtils.SHORT_DATE_FORMAT_T.format(new Date()));

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("030000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(preOrders.getTradeAmount()).length() - String.valueOf(preOrders.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = preOrders.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = preOrders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        iso8583DTO.setDateOfExpired_14(preOrders.getValid());
        if (!StringUtils.isEmpty(preOrders.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(preOrders.getPin()), AESUtil.aesDecrypt(preOrders.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }

        iso8583DTO.setPointOfServiceConditionMode_25("06");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        timeStamp.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()));
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(preOrders.getTrackData()), channel.getMd5KeyStr()));
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡下单】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;

    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 预授权撤销接口
     **/
    @Override
    public BaseResponse preAuthReverse(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createPreAuthhReverseDTO(preOrders, channel);
        log.info("==================【UPI预授权撤销】==================【调用Channels服务】【UPI-预授权接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI预授权撤销】==================【调用Channels服务】【UPI-预授权接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【UPI预授权撤销】==================【预授权撤销】iso8583VO:{}", JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrdersMapper.updatePreStatusById1(preOrders.getId(), iso8583VO.getRetrievalReferenceNumber_37(), (byte) 4, null);
            } else {
                log.info("==================【UPI预授权撤销】==================【预授权撤销失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ONLINE_ORDER_IS_NOT_ALLOW_UNDO.getCode());
            }
        } else {
            //请求失败
            log.info("==================【UPI预授权撤销】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ONLINE_ORDER_IS_NOT_ALLOW_UNDO.getCode());
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 创建预授权撤销DTO
     **/
    private UpiDTO createPreAuthhReverseDTO(PreOrders preOrders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String timeStamp = System.currentTimeMillis() + "";
        String domain11 = preOrders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        preOrders.setRemark1(domain60_2 + domain11);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0100");
        iso8583DTO.setProcessingCode_3("200000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(preOrders.getTradeAmount()).length() - String.valueOf(preOrders.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = preOrders.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = preOrders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        iso8583DTO.setDateOfExpired_14(preOrders.getValid());
        if (!StringUtils.isEmpty(preOrders.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(preOrders.getPin()), AESUtil.aesDecrypt(preOrders.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }

        iso8583DTO.setPointOfServiceConditionMode_25("06");
        iso8583DTO.setAuthorizationIdentificationResponse_38(preOrders.getChannelNumber().substring(12));
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        preOrders.getRemark1().substring(0, 6) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        iso8583DTO.setOriginalMessage_61(preOrders.getRemark1());

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()));
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(preOrders.getTrackData()), channel.getMd5KeyStr()));
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡下单】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;

    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 预授权冲正接口
     **/
    @Override
    public BaseResponse preAuthRevoke(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createPreAuthhRevokeDTO(preOrders, channel);
        log.info("==================【UPI预授权冲正】==================【调用Channels服务】【UPI-预授权接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI预授权冲正】==================【调用Channels服务】【UPI-预授权接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            //请求成功
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【UPI预授权冲正】==================【预授权冲正】iso8583VO:{}", JSONObject.toJSONString(iso8583VO));
            if (iso8583VO.getResponseCode_39() != null && "00 ".equals(iso8583VO.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                preOrdersMapper.updatePreStatusById1(preOrders.getId(), iso8583VO.getRetrievalReferenceNumber_37(), (byte) 4, null);
            } else {
                log.info("==================【UPI预授权冲正】==================【预授权冲正失败】preOrders:{}", preOrders.getId());
                baseResponse.setCode(EResultEnum.ORDER_NOT_SUPPORT_REVERSE.getCode());
            }
        } else {
            //请求失败
            log.info("==================【UPI预授权冲正】==================【请求状态码异常】preOrders:{}", preOrders.getId());
            baseResponse.setCode(EResultEnum.ORDER_NOT_SUPPORT_REVERSE.getCode());
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 预授权冲正DTO
     **/
    private UpiDTO createPreAuthhRevokeDTO(PreOrders preOrders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0400");
        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(preOrders.getUserBankCardNo()));
        iso8583DTO.setProcessingCode_3("030000");
        //获取交易金额的小数位数
        int numOfBits = String.valueOf(preOrders.getTradeAmount()).length() - String.valueOf(preOrders.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = preOrders.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = preOrders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(preOrders.getRemark1().substring(6, 12));
        iso8583DTO.setDateOfExpired_14(preOrders.getValid());
        if (!StringUtils.isEmpty(preOrders.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }
        iso8583DTO.setPointOfServiceConditionMode_25("06");


        iso8583DTO.setResponseCode_39("96");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        preOrders.getRemark1().substring(0, 6) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);

        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【预授权冲正DTO】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);

        return upiDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 预授权完成
     **/
    @Override
    public BaseResponse preAuthComplete(Orders orders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = this.createPreAuthCompleteDTO(orders, channel);
        log.info("==================【UPI预授权完成】==================【调用Channels服务】【UPI-预授权完成接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI预授权完成】==================【调用Channels服务】【UPI-预授权完成接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI预授权完成】==================【调用Channels服务】【UPI-银行卡下单】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.PAYMENT_ABNORMAL.getCode());
        }
        ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
        log.info("==================【UPI预授权完成】==================【调用Channels服务】【通华线下银行卡下单接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
        ordersMapper.updateByPrimaryKeySelective(orders);
        orders.setUpdateTime(new Date());
        orders.setChannelCallbackTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        BaseResponse response = new BaseResponse();
        if ("00".equals(iso8583VO.getResponseCode_39())) {
            //支付成功
            log.info("==================【UPI预授权完成】==================【支付成功】orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            orders.setChannelNumber(iso8583VO.getRetrievalReferenceNumber_37());
            orders.setReportNumber(orders.getReportNumber() + iso8583VO.getDateOfLocalTransaction_13());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), iso8583VO.getRetrievalReferenceNumber_37(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【UPI预授权完成】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                if (preOrdersMapper.updatePreStatusByMerchantOrderId(orders.getMerchantOrderId(), orders.getOrderAmount(), null, (byte) 5) == 1) {
                    log.info("=================【UPI预授权完成】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("=================【UPI预授权完成】=================【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                        if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                        }
                        //更新成功,上报清结算
                        commonService.fundChangePlaceOrderSuccess(orders);
                    } catch (Exception e) {
                        log.error("=================【UPI预授权完成】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    log.info("=================【UPI预授权完成】=================【订单支付成功后更新原订单失败】 MerchantOrderId: {}", orders.getMerchantOrderId());
                }
            } else {
                log.info("=================【UPI预授权完成】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            //支付失败
            log.info("==================【UPI预授权完成】==================【支付失败】orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(iso8583VO.getResponseCode_39());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【UPI预授权完成】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【UPI预授权完成】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【UPI预授权完成】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 预授权完成DTO
     **/
    private UpiDTO createPreAuthCompleteDTO(Orders orders, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        String timeStamp = System.currentTimeMillis() + "";
        String domain11 = orders.getId().substring(10, 16);
        String domain60_2 = timeStamp.substring(6, 12);
        //保存11域与60.2域
        orders.setReportNumber(domain60_2 + domain11);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("000000");

        //获取交易金额的小数位数
        int numOfBits = String.valueOf(orders.getTradeAmount()).length() - String.valueOf(orders.getTradeAmount()).indexOf(".") - 1;
        int tradeAmount;
        if (numOfBits == 0) {
            //整数
            tradeAmount = orders.getTradeAmount().intValue();
        } else {
            //小数,扩大对应小数位数
            tradeAmount = orders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        iso8583DTO.setSystemTraceAuditNumber_11(domain11);
        iso8583DTO.setDateOfExpired_14(orders.getValid());
        if (!StringUtils.isEmpty(orders.getPin())) {
            iso8583DTO.setPointOfServiceEntryMode_22("021");
            iso8583DTO.setPointOfServicePINCaptureCode_26("06");
            iso8583DTO.setPINData_52(pINEncryption(AESUtil.aesDecrypt(orders.getPin()), AESUtil.aesDecrypt(orders.getUserBankCardNo()).substring(3, 15), channel.getMd5KeyStr()));
            iso8583DTO.setSecurityRelatedControlInformation_53("2600000000000000");
        } else {
            iso8583DTO.setPointOfServiceEntryMode_22("022");
        }

        iso8583DTO.setPointOfServiceConditionMode_25("06");
        iso8583DTO.setAuthorizationIdentificationResponse_38(orders.getChannelNumber().substring(12));
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41(channel.getExtend1());
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        iso8583DTO.setCurrencyCodeOfTransaction_49("344");

        String str60 =
                //60.1 消息类型码
                "22" +
                        //60.2 批次号 自定义
                        timeStamp.substring(6, 12) +
                        //60.3 网络管理信息码
                        "000" +
                        //60.4 终端读取能力
                        "6" +
                        //60. 5，6，7 缺省
                        "00";
        iso8583DTO.setReservedPrivate_60(str60);
        iso8583DTO.setOriginalMessage_61(orders.getRemark1());

        //银行卡号
        iso8583DTO.setProcessingCode_2(AESUtil.aesDecrypt(orders.getUserBankCardNo()));
        //磁道2 信息
        iso8583DTO.setTrack2Data_35(trkEncryption(AESUtil.aesDecrypt(orders.getTrackData()), channel.getMd5KeyStr()));
        String isoMsg = null;
        //扫码组包
        try {
            isoMsg = UpiIsoUtil.packISO8583DTO(iso8583DTO, makEncryption(channel.getMd5KeyStr()));
        } catch (Exception e) {
            log.info("=================【UPI银行卡下单】=================【组包异常】");
        }
        String sendMsg = ad3ParamsConfig.getUpiTdpu() + ad3ParamsConfig.getUpiHeader() + isoMsg;
        String strHex2 = String.format("%04x", sendMsg.length() / 2).toUpperCase();
        sendMsg = strHex2 + sendMsg;
        upiDTO.setIso8583DTO(sendMsg);
        return upiDTO;
    }

    /**
     * 预授权完成撤销
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    @Override
    public BaseResponse preAuthCompleteRevoke(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        log.info("==================【UPI银行卡预授权完成撤销】==================orderId: {}", orderRefund.getOrderId());
        BaseResponse baseResponse = new BaseResponse();
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        UpiDTO upiDTO = this.createCompleteRevokeDTO(orderRefund, channel);
        log.info("==================【UPI银行卡预授权完成撤销】==================【调用Channels服务】 upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiBankPay(upiDTO);
        log.info("==================【UPI银行卡预授权完成撤销】==================【调用Channels服务返回】channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(channelResponse.getData());
            ISO8583DTO vo = JSON.parseObject(String.valueOf(jsonObject), ISO8583DTO.class);
            //请求成功
            if ("00".equals(vo.getResponseCode_39())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, vo.getRetrievalReferenceNumber_37(), vo.getResponseCode_39());
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                log.info("==================【UPI银行卡预授权完成撤销】=================退款失败: {}", JSON.toJSONString(orderRefund));
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund, channel);
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【UPI银行卡预授权完成撤销】===============【请求失败 上报队列 SAAS_YSQWC_CCQQSB_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.SAAS_YSQWC_CCQQSB_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/18
     * @Descripate 创建预授权完成撤销DTO
     **/
    private UpiDTO createCompleteRevokeDTO(OrderRefund orderRefund, Channel channel) {
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0200");
        iso8583DTO.setProcessingCode_3("190000");
        return upiDTO;
    }


    /**
     * MAK 加密
     *
     * @param str
     * @param key
     * @return
     */
    private static String makEncryption(String key) {
        //80-112 Trk密钥位
        String substring = key.substring(40, 72);
        String mak = Objects.requireNonNull(EcbDesUtil.decode3DEA("3104BAC458BA1513043E4010FD642619", substring)).toUpperCase();
        return mak;
    }

    /**
     * trk 加密
     *
     * @param str
     * @param key
     * @return
     */
    private static String trkEncryption(String str, String key) {
        //80-112 Trk密钥位
        String substring = key.substring(80, 112);
        String trk = Objects.requireNonNull(EcbDesUtil.decode3DEA("3104BAC458BA1513043E4010FD642619", substring)).toUpperCase();
        String newStr;
        if (str.length() % 2 != 0) {
            newStr = str.length() + str + "0";
        } else {
            newStr = str.length() + str;
        }
        byte[] bcd = NumberStringUtil.str2Bcd(newStr);
        return Objects.requireNonNull(EcbDesUtil.encode3DEA(trk, cn.hutool.core.util.HexUtil.encodeHexStr(bcd))).toUpperCase();
    }

    public static String pINEncryption(String pin, String pan, String key) {

        byte[] apan = NumberStringUtil.formartPan(pan.getBytes());
        byte[] apin = NumberStringUtil.formatPinByX98(pin.getBytes());
        byte[] xorMac = new byte[apan.length];
        for (int i = 0; i < apan.length; i++) {//异或
            xorMac[i] = apin[i] ^= apan[i];
        }
        try {
            String substring = key.substring(0, 32);
            String pik = Objects.requireNonNull(EcbDesUtil.decode3DEA("3104BAC458BA1513043E4010FD642619", substring)).toUpperCase();
            String s = DesUtil.doubleDesEncrypt(pik, ISOUtil.bytesToHexString(xorMac));
            return s;
        } catch (Exception e) {
            log.info("===== pINEncryption e =====" + e);
        }
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
        BaseResponse channelResponse;
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        //拿到当天的23时间
        Date endtime = DateToolUtils.addHour(DateToolUtils.getDayEnd(orders.getCreateTime()), -1);
        String type = null;
        if (System.currentTimeMillis() < endtime.getTime() && orderRefund.getRefundType() == 1) {
            //撤销接口
            type = "PAYC";
            UpiRefundDTO upiRefundDTO = this.createRefundDTO(orderRefund, channel, type);
            upiDTO.setUpiRefundDTO(upiRefundDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiCancel】  upiDTO: {}", JSON.toJSONString(upiDTO));
            channelResponse = channelsFeign.upiCancel(upiDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiCancel】  channelResponse: {}", JSON.toJSONString(channelResponse));
        } else {
            //退款接口
            type = "REFUND";
            UpiRefundDTO upiRefundDTO = this.createRefundDTO(orderRefund, channel, type);
            upiDTO.setUpiRefundDTO(upiRefundDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiRefund】  upiDTO: {}", JSON.toJSONString(upiDTO));
            channelResponse = channelsFeign.upiRefund(upiDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiRefund】  channelResponse: {}", JSON.toJSONString(channelResponse));
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if ("1".equals(jsonObject.getString("refund_result")) || "1".equals(jsonObject.getString("pay_result"))) {
                //退款成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【UPI退款】=================【退款成功】 Order: {} ", orderRefund.getOrderId());
                //退款成功
                String channelNum = type.equals("REFUND") ? jsonObject.getString("refund_id") : jsonObject.getString("pay_no");
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, channelNum, null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);

            } else if ("2".equals(jsonObject.getString("refund_result")) || "2".equals(jsonObject.getString("pay_result"))) {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【UPI退款】=================【退款失败】  Order: {} ", orderRefund.getOrderId());
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund, channel);
            } else if ("0".equals(jsonObject.getString("refund_result")) || "0".equals(jsonObject.getString("pay_result"))) {
                //退款未处理或撤销情况未知
                log.info("=================【UPI退款】=================【退款未处理或撤销情况未知】  Order: {} ", orderRefund.getOrderId());
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【UPI退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
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
        BaseResponse baseResponse = new BaseResponse();
        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);

        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("SEARCH");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orderRefund.getOrderId());
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI撤销】==================【调用Channels服务】【UPI-upiQueery】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiQueery(upiDTO);
        log.info("==================【UPI撤销】==================【调用Channels服务】【UPI-upiQueery】  channelResponse: {}", JSON.toJSONString(channelResponse));
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if ("1".equals(jsonObject.getString("pay_result"))) {
                //支付成功
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, jsonObject.getString("pay_no"), new Date()) == 1) {
                    //更新成功
                    baseResponse = this.cancelPaying(channel, orderRefund, null);
                } else {
                    baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                    //更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }

            } else if ("2".equals(jsonObject.getString("pay_result"))) {
                //支付失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【UPI撤销】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, jsonObject.getString("pay_no"), new Date());
            } else if ("0".equals(jsonObject.getString("pay_result"))) {
                //支付中
                baseResponse.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【UPI撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【UPI撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));

        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     **/
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse channelResponse;
        BaseResponse baseResponse = new BaseResponse();
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        UpiDTO upiDTO = new UpiDTO();
        //拿到当天的23时间
        Date endtime = DateToolUtils.addHour(DateToolUtils.getDayEnd(orders.getCreateTime()), -1);
        String type = null;
        if (System.currentTimeMillis() < endtime.getTime() && orderRefund.getRefundType() == 1) {
            //撤销接口
            type = "PAYC";
            UpiRefundDTO upiRefundDTO = this.createRefundDTO(orderRefund, channel, type);
            upiDTO.setUpiRefundDTO(upiRefundDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiCancel】  upiDTO: {}", JSON.toJSONString(upiDTO));
            channelResponse = channelsFeign.upiCancel(upiDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiCancel】  channelResponse: {}", JSON.toJSONString(channelResponse));
        } else {
            //退款接口
            type = "REFUND";
            UpiRefundDTO upiRefundDTO = this.createRefundDTO(orderRefund, channel, type);
            upiDTO.setUpiRefundDTO(upiRefundDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiRefund】  upiDTO: {}", JSON.toJSONString(upiDTO));
            channelResponse = channelsFeign.upiRefund(upiDTO);
            log.info("==================【UPI退款】==================【调用Channels服务】【UPI-upiRefund】  channelResponse: {}", JSON.toJSONString(channelResponse));
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        if (channelResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if ("1".equals(jsonObject.getString("refund_result")) || "1".equals(jsonObject.getString("pay_result"))) {
                //退款成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【UPI退款 cancelPaying】=================【撤销成功】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else if ("2".equals(jsonObject.getString("refund_result")) || "2".equals(jsonObject.getString("pay_result"))) {
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【UPI退款 cancelPaying】=================【撤销失败】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            } else if ("0".equals(jsonObject.getString("refund_result")) || "0".equals(jsonObject.getString("pay_result"))) {
                //退款未处理或撤销情况未知
                log.info("=================【UPI退款 cancelPaying】=================【退款未处理或撤销情况未知】  Order: {} ", orderRefund.getOrderId());
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【UPI退款 cancelPaying】=================【请求失败】orderId : {}", orders.getId());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("=================【UPI退款 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate 创建退款DTO
     **/
    private UpiRefundDTO createRefundDTO(OrderRefund orderRefund, Channel channel, String type) {
        UpiRefundDTO upiRefundDTO = new UpiRefundDTO();
        upiRefundDTO.setVersion("2.0.0");
        upiRefundDTO.setTrade_code(type);
        upiRefundDTO.setAgencyId(channel.getChannelMerchantId());
        upiRefundDTO.setTerminal_no(channel.getExtend1());
        if (type.equals("REFUND")) {
            upiRefundDTO.setRefund_amount(orderRefund.getTradeAmount().toString());
            upiRefundDTO.setCurrency_type(orderRefund.getTradeCurrency());
            upiRefundDTO.setSett_currency_type(orderRefund.getTradeCurrency());

            upiRefundDTO.setRefund_no(orderRefund.getId());
            upiRefundDTO.setOrder_no(orderRefund.getOrderId());
        } else {
            upiRefundDTO.setOrder_no(orderRefund.getId());
            upiRefundDTO.setOri_order_no(orderRefund.getOrderId());
        }
        return upiRefundDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate 创建bscDTO
     **/
    private UpiPayDTO createBSCDTO(Orders orders, Channel channel, String authCode) {
        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("BACKSTAGEUNION");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orders.getId());
        upiPayDTO.setAmount(orders.getTradeAmount().toString());
        upiPayDTO.setCurrency_type(orders.getTradeCurrency());
        upiPayDTO.setSett_currency_type(orders.getTradeCurrency());
        upiPayDTO.setProduct_name(channel.getExtend6());
        upiPayDTO.setAuth_code(authCode);
        upiPayDTO.setReturn_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setNotify_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setClient_ip(orders.getReqIp());
        return upiPayDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/4
     * @Descripate 创建CSBDTO
     **/
    private UpiPayDTO createCSBDTO(Orders orders, Channel channel) {
        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("UNIONZS");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orders.getId());
        upiPayDTO.setAmount(orders.getTradeAmount().toString());
        upiPayDTO.setCurrency_type(orders.getTradeCurrency());
        upiPayDTO.setSett_currency_type(orders.getTradeCurrency());
        upiPayDTO.setProduct_name(channel.getExtend6());
        upiPayDTO.setReturn_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setNotify_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setClient_ip(orders.getReqIp());
        return upiPayDTO;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/5
     * @Descripate upi回调
     **/
    @Override
    public String upiServerCallback(JSONObject jsonObject) {
        JSONObject json;
        try {

            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(ad3ParamsConfig.getUpiPublicKeyPath(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(ad3ParamsConfig.getUpiPrivateKeyPath(), "pem", null, "RSA");


            String result = CryptoUtil.respDecryption(jsonObject, hzfPriKey, yhPubKey);
            log.info("===============【upi回调】===============【返回】 result: {}", result);
            json = (JSONObject) JSONObject.parse(result);
        } catch (Exception e) {
            log.info("================【upi回调】================【异常】", e);
            return "success";
        }
        //查询订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(json.get("order_no").toString());
        if (orders == null) {
            log.info("==================【upi回调】==================【订单为空】");
            return "success";
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【upi回调】=================【订单状态不为支付中】");
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
        if ("1".equals(json.get("pay_result").toString())) {
            //支付成功
            log.info("=================【upi回调】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            orders.setChannelNumber(json.get("pay_no").toString());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【upi回调】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【upi回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【upi回调】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【upi回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【upi回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }

        } else if ("2".equals(json.get("pay_result").toString())) {
            //支付失败
            log.info("=================【upi回调】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(json.get("resp_desc").toString());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), json.get("pay_no").toString(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【upi回调】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【upi回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【upi回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }

        } else {
            log.info("=================【upi回调】=================【订单是交易中】 orderId: {}", orders.getId());
            return "success";
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
}

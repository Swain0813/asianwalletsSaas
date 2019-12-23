package com.asianwallets.trade.channels.ad3.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.ad3.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.AD3LoginVO;
import com.asianwallets.common.vo.RefundAdResponseVO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.dto.AD3OfflineCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.utils.HandlerType;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service
@HandlerType(TradeConstant.AD3)
public class Ad3ServiceImpl extends ChannelsAbstractAdapter implements Ad3Service {

    @ApiModelProperty("AD3系统私钥")
    @Value("${custom.platformProvidesPrivateKey}")
    private String platformProvidesPrivateKey;//私钥
    @ApiModelProperty("AD3签名方式")
    @Value("${custom.merchantSignType}")
    private String merchantSignType;//签名方式
    //@ApiModelProperty("AD3系统操作员id")
    //@Value("${custom.operatorId}")
    //private String operatorId;
    //
    //@ApiModelProperty("AD3系统imei编号")
    //@Value("${custom.imei}")
    //private String imei;
    //
    //@ApiModelProperty("AD3系统登录密码")
    //@Value("${custom.password}")
    //private String password;

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private ReconciliationMapper reconciliationMapper;
    @Autowired
    private ClearingService clearingService;
    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private RedisService redisService;


    /**
     * AD3线上
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        //封装参数
        AD3OnlineAcquireDTO ad3OnlineAcquireDTO = new AD3OnlineAcquireDTO(orders, channel);
        ad3OnlineAcquireDTO.setSignMsg(signMsg(ad3OnlineAcquireDTO));
        log.info("-------AD3线上收单参数-------AD3OnlineAcquireDTO:{}", JSON.toJSON(ad3OnlineAcquireDTO));
        //返回收款消息
        log.info("-----------------URL---------------- type:{}**issuerId:{}**url:{}", channel.getChannelEnName(), channel.getIssuerId(), channel.getPayUrl());
        log.info("==================【线上AD3收款】==================【调用Channels服务】【AD3线上收单接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3OnlineAcquireDTO));
        BaseResponse channelResponse = channelsFeign.ad3OnlinePay(ad3OnlineAcquireDTO);
        log.info("==================【线上AD3收款】==================【调用Channels服务】【AD3线上收单接口请求参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return channelResponse;
    }

//    /**
//     * ad3线下回调
//     *
//     * @param ad3OfflineCallbackDTO ad3线下回调输入实体
//     * @return
//     */
//    @Override
//    public String ad3Callback(AD3OfflineCallbackDTO ad3OfflineCallbackDTO) {
//        //校验输入参数
//        checkParam(ad3OfflineCallbackDTO);
//        try {
//            //生成签名
//            String mySign = createSign(ad3OfflineCallbackDTO, redisService.get(AD3Constant.AD3_LOGIN_TOKEN));
//            log.info("=================【AD3线下回调接口信息记录】=================【回调签名】 mySign: {} ad3Sign: {}", mySign, ad3OfflineCallbackDTO.getSignMsg());
//            //验签
//            if (!ad3OfflineCallbackDTO.getSignMsg().equals(mySign)) {
//                log.info("=================【AD3线下回调接口信息记录】=================【签名不匹配】");
//            } else {
//                log.info("=================【AD3线下回调接口信息记录】=================【签名已匹配】");
//            }
//        } catch (Exception e) {
//            log.info("=============【签名异常】=============", e);
//        }
//        //查询订单信息
//        Orders orders = ordersMapper.selectByPrimaryKey(ad3OfflineCallbackDTO.getMerorderNo());
//        //校验业务信息
//        checkBusiness(ad3OfflineCallbackDTO, orders);
//        //校验订单状态
//        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
//            log.info("=================【AD3线下回调接口信息记录】=================【订单状态不为支付中】");
//            return "success";
//        }
//        //通道流水号
//        orders.setChannelNumber(ad3OfflineCallbackDTO.getTxnId());
//        //通道回调时间
//        orders.setChannelCallbackTime(DateToolUtils.getDateFromString(ad3OfflineCallbackDTO.getTxnDate(), "yyyyMMddHHmmss"));
//        //修改时间
//        orders.setUpdateTime(new Date());
//        Example example = new Example(Orders.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("tradeStatus", "2");
//        criteria.andEqualTo("id", orders.getId());
//        if (AD3Constant.ORDER_SUCCESS.equals(ad3OfflineCallbackDTO.getStatus())) {
//            log.info("=================【AD3线下回调接口信息记录】=================【订单已支付成功】 orderId: {}", orders.getId());
//            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
//            //更新订单信息
//            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
//                log.info("=================【AD3线下回调接口信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
//                //计算支付成功时的通道网关手续费
//                commonService.calcCallBackGatewayFeeSuccess(orders);
//                //添加日交易限额与日交易笔数
//                commonService.quota(orders.getInstitutionCode(), orders.getProductCode(), orders.getTradeAmount());
//                //新增交易成功的订单物流信息
//                commonService.insertOrderLogistics(orders);
//                //支付成功后向用户发送邮件
//                commonService.sendEmail(orders.getDraweeEmail(), orders.getLanguage(), Status._1, orders);
//                try {
//                    //账户信息不存在的场合创建对应的账户信息
//                    if (commonService.getAccount(orders.getInstitutionCode(), orders.getOrderCurrency()) == null) {
//                        log.info("=================【AD3线下回调接口信息记录】=================【上报清结算前线下下单创建账户信息】");
//                        commonService.createAccount(orders.getInstitutionCode(), orders.getOrderCurrency());
//                    }
//                    //分润
//                    if (!StringUtils.isEmpty(orders.getAgencyCode())) {
//                        rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orders.getId());
//                    }
//                    //更新成功,上报清结算
//                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT, orders.getInstitutionCode());
//                    //上报清结算资金变动接口
//                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO, null);
//                    if (fundChangeResponse.getCode() != null && TradeConstant.HTTP_SUCCESS.equals(fundChangeResponse.getCode())) {
//                        //请求成功
//                        FundChangeVO fundChangeVO = (FundChangeVO) fundChangeResponse.getData();
//                        if (!fundChangeVO.getRespCode().equals(TradeConstant.CLEARING_SUCCESS)) {
//                            //业务处理失败
//                            log.info("=================【AD3线下回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
//                            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
//                            rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
//                        }
//                    } else {
//                        log.info("=================【AD3线下回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
//                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
//                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
//                    }
//                } catch (Exception e) {
//                    log.error("=================【AD3线下回调接口信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
//                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
//                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
//                }
//            } else {
//                log.info("=================【AD3线下回调接口信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
//            }
//        } else if (AD3Constant.ORDER_FAILED.equals(ad3OfflineCallbackDTO.getStatus())) {
//            log.info("=================【AD3线下回调接口信息记录】=================【订单已支付失败】 orderId: {}", orders.getId());
//            //支付失败
//            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
//            //计算支付失败时的通道网关手续费
//            commonService.calcCallBackGatewayFeeFailed(orders);
//            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
//                log.info("=================【AD3线下回调接口信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
//            } else {
//                log.info("=================【AD3线下回调接口信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
//            }
//        } else {
//            log.info("=================【AD3线下回调接口信息记录】=================【订单为其他状态】 orderId: {}", orders.getId());
//        }
//        try {
//            //商户服务器回调地址不为空,回调商户服务器
//            if (!StringUtils.isEmpty(orders.getReturnUrl())) {
//                commonService.replyReturnUrl(orders);
//            }
//        } catch (Exception e) {
//            log.error("=================【AD3线下回调接口信息记录】=================【回调商户异常】", e);
//        }
//        return "success";
//    }

    /**
     * AD3线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //CSB请求二维码接口公共参数实体
        AD3CSBScanPayDTO ad3CSBScanPayDTO = new AD3CSBScanPayDTO(orders, channel);
        //CSB请求二维码接口业务参数实体
        CSBScanBizContentDTO csbScanBizContent = new CSBScanBizContentDTO(orders, channel);
        ad3CSBScanPayDTO.setBizContent(csbScanBizContent);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3CSBScanPayDTO));
        BaseResponse channelResponse = channelsFeign.ad3OfflineCsb(ad3CSBScanPayDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【NextPos-CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
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
        if (TradeConstant.TRADE_ONLINE.equals(orderRefund.getTradeDirection())) {

            /**************************************************** AD3线上退款 *******************************************************/
            SendAdRefundDTO sendAdRefundDTO = new SendAdRefundDTO(channel.getChannelMerchantId(), orderRefund);
            sendAdRefundDTO.setMerchantSignType(merchantSignType);
            sendAdRefundDTO.setSignMsg(this.signMsg(sendAdRefundDTO));

            AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
            ad3ONOFFRefundDTO.setChannel(channel);
            ad3ONOFFRefundDTO.setSendAdRefundDTO(sendAdRefundDTO);
            log.info("=================【AD3线上退款】=================【请求Channels服务AD3线上退款】请求参数 ad3ONOFFRefundDTO: {} ", JSON.toJSONString(ad3ONOFFRefundDTO));
            BaseResponse response = channelsFeign.ad3OnlineRefund(ad3ONOFFRefundDTO);
            log.info("=================【AD3线上退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
            if (response.getCode().equals(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS))) {
                if (response.getMsg().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                    RefundAdResponseVO refundAdResponseVO = JSONObject.parseObject(response.getData().toString(), RefundAdResponseVO.class);
                    log.info("==================【AD3线上退款】================== 【退款成功】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    //退款成功
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, refundAdResponseVO.getTxnId(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                } else if (response.getCode().equals("T001")) {
                    //退款失败
                    baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                    String type = orderRefund.getRemark4().equals(TradeConstant.RF )? TradeConstant.AA : TradeConstant.RA;
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                    log.info("=========================【AD3线上退款】======================= 【调账 {}】， fundChangeDTO:【{}】",type, JSON.toJSONString(fundChangeDTO));
                    BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                    if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                        log.info("==================【AD3线上退款】================== 【调账成功】 cFundChange: {}", JSON.toJSONString(cFundChange));
                        //调账成功
                        orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                        reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                        //改原订单状态
                        commonBusinessService.updateOrderRefundFail(orderRefund);
                    } else {
                        //调账失败
                        log.info("==================【AD3线上退款】================== 【调账失败】 cFundChange: {}", JSON.toJSONString(cFundChange));
                        RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                        log.info("=================【AD3线上退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                        rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                    }
                }
            } else {
                //请求失败
                baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
                if (rabbitMassage == null) {
                    rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                }
                log.info("===============【AD3线上退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));

            }
        } else if (TradeConstant.TRADE_UPLINE.equals(orderRefund.getTradeDirection())) {

            /***************************************************** AD3线下退款 ************************************************/
            log.info("==================【AD3线下退款】================== OrderRefund: {}", JSON.toJSONString(orderRefund));
            //获取ad3的终端号和token
            AD3LoginVO ad3LoginVO = this.getTerminalIdAndToken(channel);
            if (ad3LoginVO == null) {
                log.info("************退款时 --- 退款操作AD3登录时未获取到终端号和token*****************ad3LoginVO：{}", JSON.toJSON(ad3LoginVO));
                baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
                return baseResponse;
            }
            AD3RefundDTO ad3RefundDTO = new AD3RefundDTO(channel.getChannelMerchantId());
            AD3RefundWorkDTO ad3RefundWorkDTO = new AD3RefundWorkDTO(ad3LoginVO.getTerminalId(), channel.getExtend2(), channel.getExtend4(), orderRefund);
            ad3RefundDTO.setSignMsg(this.createAD3Signature(ad3RefundDTO, ad3RefundWorkDTO, ad3LoginVO.getToken()));
            ad3RefundDTO.setBizContent(ad3RefundWorkDTO);
            AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
            ad3ONOFFRefundDTO.setChannel(channel);
            ad3ONOFFRefundDTO.setAd3RefundDTO(ad3RefundDTO);
            log.info("=================【AD3线下退款】=================【请求Channels服务AD3线下退款】请求参数 ad3ONOFFRefundDTO: {} ", JSON.toJSONString(ad3ONOFFRefundDTO));
            BaseResponse response = channelsFeign.ad3OfflineRefund(ad3ONOFFRefundDTO);
            log.info("=================【AD3线下退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
            if (response.getCode().equals(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS))) {
                RefundAdResponseVO refundAdResponseVO = JSONObject.parseObject(response.getData().toString(), RefundAdResponseVO.class);
                if (response.getMsg().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                    //退款成功
                    log.info("==================【AD3线下退款】================== 【退款成功】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, refundAdResponseVO.getTxnId(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                } else {
                    //退款失败
                    log.info("==================【AD3线下退款】================== 【退款失败】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                    String type = orderRefund.getRemark4().equals(TradeConstant.RF )? TradeConstant.AA : TradeConstant.RA;
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                    log.info("=========================【AD3线下退款】======================= 【调账 {}】， fundChangeDTO:【{}】",type, JSON.toJSONString(fundChangeDTO));
                    BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                    if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                        log.info("==================【AD3线下退款】================== 【调账成功】 cFundChange: {}", JSON.toJSONString(cFundChange));
                        //调账成功
                        orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                        reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                        //改原订单状态
                        commonBusinessService.updateOrderRefundFail(orderRefund);
                    } else {
                        //调账失败
                        log.info("==================【AD3线下退款】================== 【调账失败】 cFundChange: {}", JSON.toJSONString(cFundChange));
                        RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                        log.info("=================【AD3线下退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                        rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                    }
                }
            } else {
                //请求失败
                baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
                if (rabbitMassage == null) {
                    rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                }
                log.info("===============【AD3线下退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate    撤销
     * @return
     **/
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
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
        return null;
    }

    /**
     * 对向ad3的请求进行签名
     *
     * @param object
     * @return
     */
    @Override
    public String signMsg(Object object) {
        //去空
        String privateKey = platformProvidesPrivateKey.replaceAll("\\s*", "");
        HashMap<String, Object> dtoMap = BeanToMapUtil.beanToMap(object);
        HashMap<String, String> map = new HashMap<>();
        Set<String> keySet = dtoMap.keySet();
        for (String dtoKey : keySet) {
            map.put(dtoKey, String.valueOf(dtoMap.get(dtoKey)));
        }
        byte[] msg = SignTools.getSignStr(map).getBytes();
        String signMsg = null;
        try {
            //签名
            signMsg = RSAUtils.sign(msg, privateKey);
        } catch (Exception e) {
            log.info("----------------- 线上签名错误信息记录 ----------------签名原始明文:{},签名:{}", msg, signMsg);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return signMsg;
    }

    /**
     * 获取终端编号和token
     *
     * @return
     */
    @Override
    public AD3LoginVO getTerminalIdAndToken(Channel channel) {
        AD3LoginVO ad3LoginVO = null;
        String token = redisService.get(AD3Constant.AD3_LOGIN_TOKEN);
        String terminalId = redisService.get(AD3Constant.AD3_LOGIN_TERMINAL);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(terminalId)) {
            //AD3登陆业务参数实体
            LoginBizContentDTO bizContent = new LoginBizContentDTO(AD3Constant.LOGIN_OUT, channel);
            //AD3登陆公共参数实体
            AD3LoginDTO ad3LoginDTO = new AD3LoginDTO(channel.getChannelMerchantId(), bizContent);
            //先登出
            HttpClientUtils.reqPost(channel.getExtend5(), ad3LoginDTO, null);
            //再登陆
            bizContent.setType(AD3Constant.LOGIN_IN);
            HttpResponse httpResponse = HttpClientUtils.reqPost(channel.getExtend5(), ad3LoginDTO, null);
            //状态码为200
            if (httpResponse != null && httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                ad3LoginVO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), AD3LoginVO.class);
                //业务返回码为成功
                if (ad3LoginVO != null && ad3LoginVO.getRespCode().equals(AD3Constant.AD3_OFFLINE_SUCCESS)) {
                    redisService.set(AD3Constant.AD3_LOGIN_TOKEN, ad3LoginVO.getToken());
                    redisService.set(AD3Constant.AD3_LOGIN_TERMINAL, ad3LoginVO.getTerminalId());
                }
            }
        } else {//存在的场合
            ad3LoginVO = new AD3LoginVO();//创建对象
            ad3LoginVO.setTerminalId(terminalId);//终端编号
            ad3LoginVO.setToken(token);//token
        }
        return ad3LoginVO;
    }

    /**
     * 生成AD3认证签名
     *
     * @param commonObj   AD3公共参数输入实体
     * @param businessObj AD3业务参数输入实体
     * @param token       token
     * @return ad3签名
     */
    @Override
    public String createAD3Signature(Object commonObj, Object businessObj, String token) {
        Map<String, Object> commonMap = ReflexClazzUtils.getFieldNames(commonObj);
        Map<String, Object> businessMap = ReflexClazzUtils.getFieldNames(businessObj);
        commonMap.putAll(businessMap);
        HashMap<String, String> paramMap = new HashMap<>();
        for (String str : commonMap.keySet()) {
            paramMap.put(str, String.valueOf(commonMap.get(str)));
        }
        String signature = SignTools.getSignStr(paramMap);//密文字符串拼装处理
        String ad3Signature = MD5Util.getMD5String(signature + "&" + token).toUpperCase();//与token进行拼接MD5加密
        log.info("ad3签名:{}", ad3Signature);
        return ad3Signature;
    }

}

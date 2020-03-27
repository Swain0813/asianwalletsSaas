package com.asianwallets.trade.channels.ad3.impl;

import cn.hutool.core.date.DateUtil;
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
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.AD3BSCScanVO;
import com.asianwallets.common.vo.AD3LoginVO;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.common.vo.RefundAdResponseVO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.dto.AD3OfflineCallbackDTO;
import com.asianwallets.trade.dto.AD3OnlineCallbackDTO;
import com.asianwallets.trade.dto.AD3OnlineOrderQueryDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
@HandlerType(TradeConstant.AD3)
public class Ad3ServiceImpl extends ChannelsAbstractAdapter implements Ad3Service {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;


    /**
     * 查询
     *
     * @param ad3OnlineOrderQueryDTO
     * @param headerMap
     * @param channel
     * @return
     */
    @Override
    public HttpResponse ad3OnlineOrderQuery(AD3OnlineOrderQueryDTO ad3OnlineOrderQueryDTO, Map<String, Object> headerMap, Channel channel) {
        //channel.getExtend2() ad3私钥
        ad3OnlineOrderQueryDTO.setSignMsg(signMsg(ad3OnlineOrderQueryDTO, "2"));
        return HttpClientUtils.reqPost(ad3ParamsConfig.getAd3Url() + "/v1/merQueryOneOrder.json", ad3OnlineOrderQueryDTO, headerMap);
    }

    /**
     * AD3线上
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        //封装参数
        AD3OnlineAcquireDTO ad3OnlineAcquireDTO = new AD3OnlineAcquireDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl() + "/onlinecallback/callback", ad3ParamsConfig.getChannelCallbackUrl() + "/onlinecallback/paysuccess");
        String url = ad3OnlineAcquireDTO.getUrl();
        ad3OnlineAcquireDTO.setUrl(null);
        //channel.getExtend2() ad3私钥
        ad3OnlineAcquireDTO.setSignMsg(signMsg(ad3OnlineAcquireDTO, "2"));
        ad3OnlineAcquireDTO.setUrl(url);
        log.info("-------AD3线上收单参数-------AD3OnlineAcquireDTO:{}", JSON.toJSON(ad3OnlineAcquireDTO));
        //返回收款消息
        //log.info("-----------------URL---------------- type:{}**issuerId:{}**url:{}", channel.getChannelEnName(), channel.getIssuerId(), channel.getPayUrl());
        log.info("==================【线上AD3收款】==================【调用Channels服务】【AD3线上收单接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3OnlineAcquireDTO));
        BaseResponse channelResponse = channelsFeign.ad3OnlinePay(ad3OnlineAcquireDTO);
        log.info("==================【线上AD3收款】==================【调用Channels服务】【AD3线上收单接口请求参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        String aD3ReturnParameter = (String) channelResponse.getData();
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        if (aD3ReturnParameter.replaceAll("\\s*", "").matches(".*html.*")) {
            //网银
            onlineTradeVO.setRespCode("T000");
            onlineTradeVO.setCode_url(aD3ReturnParameter);
            onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
            channelResponse.setData(onlineTradeVO);
            return channelResponse;
        } else {
            //扫码
            onlineTradeVO = JSON.parseObject(aD3ReturnParameter, OnlineTradeVO.class);
            if (!onlineTradeVO.getRespCode().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                channelResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                return channelResponse;
            }
            onlineTradeVO.setType(channel.getIssuerId().toUpperCase());
            log.info("------------AD3线上收单通道响应参数------------ad3OnlineVO:{}", JSON.toJSONString(onlineTradeVO));
            channelResponse.setData(onlineTradeVO);
            return channelResponse;
        }
    }

    /**
     * ad3线上回调
     *
     * @param ad3OnlineCallbackDTO ad3线下回调输入实体
     * @return SUCCESS
     */
    @Override
    public String ad3OnlineServerCallback(AD3OnlineCallbackDTO ad3OnlineCallbackDTO) {
        //校验输入参数
        checkOnlineParam(ad3OnlineCallbackDTO);
        //生成签名
        String mySign = createSign(ad3OnlineCallbackDTO, redisService.get(AD3Constant.AD3_LOGIN_TOKEN));
        log.info("=================【AD3线上回调接口信息记录】=================【回调签名】 mySign: {} ad3Sign: {}", mySign, ad3OnlineCallbackDTO.getSignMsg());
        //验签
        if (!ad3OnlineCallbackDTO.getSignMsg().equals(mySign)) {
            log.info("=================【AD3线上回调接口信息记录】=================【签名不匹配】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }
        //查询订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(ad3OnlineCallbackDTO.getMerOrderNo());
        //校验业务信息
        if (orders == null) {
            //订单信息不存在
            log.info("=================【AD3线上回调接口信息记录】=================【回调订单信息不存在】");
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        log.info("=================【AD3线上回调接口信息记录】=================【回调订单信息记录】 orders: {}", JSON.toJSONString(orders));
        //校验交易金额与交易币种
        BigDecimal ad3Amount = new BigDecimal(ad3OnlineCallbackDTO.getMerorderAmount());
        BigDecimal tradeAmount = orders.getTradeAmount();
        if (ad3Amount.compareTo(tradeAmount) != 0 || !ad3OnlineCallbackDTO.getMerorderCurrency().equals(orders.getTradeCurrency())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单信息不匹配】");
            throw new BusinessException(EResultEnum.ORDER_INFO_NO_MATCHING.getCode());
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单状态不为支付中】");
            return "success";
        }
        //通道流水号
        orders.setChannelNumber(ad3OnlineCallbackDTO.getTxnid());
        //通道回调时间
        orders.setChannelCallbackTime(DateToolUtils.getDateFromString(ad3OnlineCallbackDTO.getTxndate(), "yyyyMMddHHmmss"));
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if (AD3Constant.ORDER_SUCCESS.equals(ad3OnlineCallbackDTO.getStatus())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【AD3线上回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【AD3线上回调接口信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【AD3线上回调接口信息记录】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【AD3线上回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【AD3线上回调接口信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【AD3线上回调接口信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if (AD3Constant.ORDER_FAILED.equals(ad3OnlineCallbackDTO.getStatus())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(ad3OnlineCallbackDTO.getRespcode());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【AD3线上回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【AD3线上回调接口信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【AD3线上回调接口信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【AD3线上回调接口信息记录】=================【订单为其他状态】 orderId: {}", orders.getId());
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【AD3线上回调接口信息记录】=================【回调商户异常】", e);
        }
        return "success";
    }

    /**
     * AD3浏览器回调
     *
     * @param ad3OnlineCallbackDTO 参数
     * @param response             响应
     */
    @Override
    public void ad3OnlineBrowserCallback(AD3OnlineCallbackDTO ad3OnlineCallbackDTO, HttpServletResponse response) {
        //商户上送的商户订单号
        if (StringUtils.isEmpty(ad3OnlineCallbackDTO.getMerOrderNo())) {
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(ad3OnlineCallbackDTO.getMerOrderNo());
        if (orders == null) {
            log.info("------------------【AD3线上网关Browser回调】------------回调订单信息不存在");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("------------------【AD3线上网关Browser回调】------------开始回调商户浏览器");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getAd3Url() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.error("------------------【AD3线上网关Browser回调】--------------调用AW支付成功页面失败", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("------------------【AD3线上网关Browser回调】------------开始回调商户浏览器");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getAd3Url() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.error("------------------【AD3线上网关Browser回调】--------------调用AW支付中页面失败", e);
                }
            }
        }
    }

    /**
     * 校验ad3线上回调参数
     *
     * @param ad3OnlineCallbackDTO
     */
    private void checkOnlineParam(AD3OnlineCallbackDTO ad3OnlineCallbackDTO) {
        if (ad3OnlineCallbackDTO == null) {
            log.info("=================【AD3线上回调接口信息记录】=================【回调参数为空】");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OnlineCallbackDTO.getMerOrderNo())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单号为空】");
            //订单号
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OnlineCallbackDTO.getStatus())) {
            log.info("=================【AD3线上回调接口信息记录】=================【订单状态为空】");
            //订单状态
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OnlineCallbackDTO.getSignMsg())) {
            log.info("=================【AD3线上回调接口信息记录】=================【签名为空】");
            //签名
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
    }

    /**
     * 生成AD3线下回调签名
     *
     * @param obj obj
     * @return AD3回调签名
     */
    private static String createSign(Object obj, String token) {
        //获得对象属性名对应的属性值Map
        Map<String, Object> objMap = ReflexClazzUtils.getCommonFieldNames(obj);
        HashMap<String, String> paramMap = new HashMap<>();
        //转换成String
        for (String str : objMap.keySet()) {
            paramMap.put(str, String.valueOf(objMap.get(str)));
        }
        paramMap.put("signMsg", null);
        //排序,去空,将属性值按属性名首字母升序排序
        String signature = SignTools.getSignStr(paramMap);
        String clearText = signature + "&" + token;
        log.info("=================【AD3线下回调接口信息记录】=================【签名前的明文】 clearText: {}", clearText);
        return MD5Util.getMD5String(clearText);
    }

    /**
     * 校验ad3输入参数
     *
     * @param ad3OfflineCallbackDTO ad3线下回调输入实体
     */
    private void checkParam(AD3OfflineCallbackDTO ad3OfflineCallbackDTO) {
        if (ad3OfflineCallbackDTO == null) {
            log.info("=================【AD3线下回调接口信息记录】=================【回调参数为空】");
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OfflineCallbackDTO.getMerorderNo())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单号为空】");
            //订单号
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OfflineCallbackDTO.getStatus())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单状态为空】");
            //订单状态
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
        if (StringUtils.isEmpty(ad3OfflineCallbackDTO.getSignMsg())) {
            log.info("=================【AD3线下回调接口信息记录】=================【签名为空】");
            //签名
            throw new BusinessException(EResultEnum.CALLBACK_PARAMETER_IS_NULL.getCode());
        }
    }

    /**
     * ad3线下回调
     *
     * @param ad3OfflineCallbackDTO ad3线下回调输入实体
     * @return String
     */
    @Override
    public String ad3OfflineCsbServerCallback(AD3OfflineCallbackDTO ad3OfflineCallbackDTO) {
        //校验输入参数
        checkParam(ad3OfflineCallbackDTO);
        //生成签名
        String mySign = createSign(ad3OfflineCallbackDTO, redisService.get(AD3Constant.AD3_LOGIN_TOKEN));
        log.info("=================【AD3线下回调接口信息记录】=================【回调签名】 mySign: {} ad3Sign: {}", mySign, ad3OfflineCallbackDTO.getSignMsg());
        //验签
        if (!ad3OfflineCallbackDTO.getSignMsg().equals(mySign)) {
            log.info("=================【AD3线下回调接口信息记录】=================【签名不匹配】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }
        //查询订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(ad3OfflineCallbackDTO.getMerorderNo());
        //校验业务信息
        if (orders == null) {
            //订单信息不存在
            log.info("=================【AD3线下回调接口信息记录】=================【回调订单信息不存在】");
            throw new BusinessException(EResultEnum.ORDER_NOT_EXIST.getCode());
        }
        log.info("=================【AD3线下回调接口信息记录】=================【回调订单信息记录】 orders: {}", JSON.toJSONString(orders));
        //校验交易金额与交易币种
        BigDecimal ad3Amount = new BigDecimal(ad3OfflineCallbackDTO.getMerorderAmount());
        BigDecimal tradeAmount = orders.getTradeAmount();
        if (ad3Amount.compareTo(tradeAmount) != 0 || !ad3OfflineCallbackDTO.getMerorderCurrency().equals(orders.getTradeCurrency())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单信息不匹配】");
            throw new BusinessException(EResultEnum.ORDER_INFO_NO_MATCHING.getCode());
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单状态不为支付中】");
            return "success";
        }
        //通道流水号
        orders.setChannelNumber(ad3OfflineCallbackDTO.getTxnId());
        //通道回调时间
        orders.setChannelCallbackTime(DateToolUtils.getDateFromString(ad3OfflineCallbackDTO.getTxnDate(), "yyyyMMddHHmmss"));
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if (AD3Constant.ORDER_SUCCESS.equals(ad3OfflineCallbackDTO.getStatus())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单已支付成功】 orderId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【AD3线下回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【AD3线下回调接口信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【AD3线下回调接口信息记录】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【AD3线下回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【AD3线下回调接口信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【AD3线下回调接口信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if (AD3Constant.ORDER_FAILED.equals(ad3OfflineCallbackDTO.getStatus())) {
            log.info("=================【AD3线下回调接口信息记录】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(ad3OfflineCallbackDTO.getRespcode());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【AD3线下回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //计算支付失败时的通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【AD3线下回调接口信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【AD3线下回调接口信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【AD3线下回调接口信息记录】=================【订单为其他状态】 orderId: {}", orders.getId());
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【AD3线下回调接口信息记录】=================【回调商户异常】", e);
        }
        return "success";
    }

    /**
     * AD3线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //获取ad3的终端号和token
        AD3LoginVO ad3LoginVO = this.getTerminalIdAndToken(channel);
        if (ad3LoginVO == null) {
            log.info("==================【线下CSB动态扫码】==================【调用【AD3登陆接口异常】】ad3LoginVO: {}", JSON.toJSONString(ad3LoginVO));
            orders.setUpdateTime(new Date());//修改时间
            orders.setRemark("调用AD3登陆接口异常");
            ordersMapper.updateByPrimaryKeySelective(orders);
            throw new BusinessException(EResultEnum.TOKEN_IS_INVALID.getCode());
        }
        //CSB请求二维码接口公共参数实体
        AD3CSBScanPayDTO ad3CSBScanPayDTO = new AD3CSBScanPayDTO(channel.getChannelMerchantId());
        //CSB请求二维码接口业务参数实体
        CSBScanBizContentDTO csbScanBizContent = new CSBScanBizContentDTO(orders, ad3LoginVO.getTerminalId(), channel.getExtend2(), ad3ParamsConfig.getChannelCallbackUrl() + "/offlineCallback/ad3Callback", channel);
        //生成ad3签名
        ad3CSBScanPayDTO.setSignMsg(createAD3Signature(ad3CSBScanPayDTO, csbScanBizContent, ad3LoginVO.getToken()));
        ad3CSBScanPayDTO.setBizContent(csbScanBizContent);
        ad3CSBScanPayDTO.setOrders(orders);
        ad3CSBScanPayDTO.setChannel(channel);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3CSBScanPayDTO));
        BaseResponse channelResponse = channelsFeign.ad3OfflineCsb(ad3CSBScanPayDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * AD3线下BSC
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        //获取ad3的终端号和token
        AD3LoginVO ad3LoginVO = this.getTerminalIdAndToken(channel);
        if (ad3LoginVO == null) {
            log.info("==================【线下BSC动态扫码】==================【调用【AD3登陆接口异常】】ad3LoginVO: {}", JSON.toJSONString(ad3LoginVO));
            orders.setUpdateTime(new Date());//修改时间
            orders.setRemark("调用AD3登陆接口异常");
            ordersMapper.updateByPrimaryKeySelective(orders);
            throw new BusinessException(EResultEnum.TOKEN_IS_INVALID.getCode());
        }
        //BSC请求二维码接口公共参数实体
        AD3BSCScanPayDTO ad3BSCScanPayDTO = new AD3BSCScanPayDTO(channel.getChannelMerchantId());
        //BSC支付接口业务参数实体
        BSCScanBizContentDTO bscScanBizContentDTO = new BSCScanBizContentDTO(orders, ad3LoginVO.getTerminalId(), channel.getExtend2(), authCode, channel);
        //生成ad3签名
        ad3BSCScanPayDTO.setSignMsg(createAD3Signature(ad3BSCScanPayDTO, bscScanBizContentDTO, ad3LoginVO.getToken()));
        ad3BSCScanPayDTO.setBizContent(bscScanBizContentDTO);
        ad3BSCScanPayDTO.setOrders(orders);
        ad3BSCScanPayDTO.setChannel(channel);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【AD3线下BSC接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3BSCScanPayDTO));
        BaseResponse channelResponse = channelsFeign.ad3OfflineBsc(ad3BSCScanPayDTO);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【AD3线下BSC接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下BSC动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        AD3BSCScanVO ad3BSCScanVO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), AD3BSCScanVO.class);
        orders.setChannelNumber(ad3BSCScanVO.getTxnId());
        orders.setChannelCallbackTime(DateUtil.parse(ad3BSCScanVO.getPayFinishTime(), "yyyyMMddHHmmss"));//通道回调时间
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if (AD3Constant.AD3_OFFLINE_SUCCESS.equals(ad3BSCScanVO.getRespCode())) {
            log.info("=================【线下BSC动态扫码】=================【订单已支付成功】 orderId: {}", orders.getId());
            //校验订单信息
            BigDecimal ad3Amount = new BigDecimal(ad3BSCScanVO.getMerorderAmount());
            BigDecimal tradeAmount = orders.getTradeAmount();
            if (ad3Amount.compareTo(tradeAmount) != 0 || !ad3BSCScanVO.getMerorderCurrency().equals(orders.getTradeCurrency())) {
                log.info("==================【线下BSC动态扫码】==================【订单信息不匹配】");
                throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
            }
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【线下BSC动态扫码】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByPrimaryKeySelective(orders) == 1) {
                log.info("=================【线下BSC动态扫码】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【线下BSC动态扫码】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【线下BSC动态扫码】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【线下BSC动态扫码】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【线下BSC动态扫码】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【线下BSC动态扫码】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(ad3BSCScanVO.getRespCode());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【线下BSC动态扫码】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【线下BSC动态扫码】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【线下BSC动态扫码】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return null;
    }

    /**
     * 退款接口
     *
     * @return BaseResponse
     * @Author YangXu
     * @Date 2019/12/19
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        if (TradeConstant.TRADE_ONLINE.equals(orderRefund.getTradeDirection())) {
            /**************************************************** AD3线上退款 *******************************************************/
            SendAdRefundDTO sendAdRefundDTO = new SendAdRefundDTO(channel, orderRefund);
            //channel.getExtend1() 签名方式 2020年1月15日15:51:49
            sendAdRefundDTO.setMerchantSignType("2");
            //channel.getExtend2() ad3私钥
            sendAdRefundDTO.setSignMsg(this.signMsg(sendAdRefundDTO, channel.getMd5KeyStr()));
            AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
            ad3ONOFFRefundDTO.setChannel(channel);
            ad3ONOFFRefundDTO.setSendAdRefundDTO(sendAdRefundDTO);
            log.info("=================【AD3线上退款】=================【请求Channels服务AD3线上退款】请求参数 ad3ONOFFRefundDTO: {} ", JSON.toJSONString(ad3ONOFFRefundDTO));
            BaseResponse response = channelsFeign.ad3OnlineRefund(ad3ONOFFRefundDTO);
            log.info("=================【AD3线上退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
            if (response.getCode().equals(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS))) {
                if (response.getMsg().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                    baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                    RefundAdResponseVO refundAdResponseVO = JSONObject.parseObject(JSON.toJSONString(response.getData()), RefundAdResponseVO.class);
                    log.info("==================【AD3线上退款】================== 【退款成功】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    //退款成功
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, refundAdResponseVO.getTxnId(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                    //退还分润
                    commonBusinessService.refundShareBinifit(orderRefund);
                } else if (response.getMsg().equals("T001")) {
                    //退款失败
                    baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                    String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                    String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                    log.info("=========================【AD3线上退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
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
                baseResponse.setCode(EResultEnum.REFUNDING.getCode());
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
                RefundAdResponseVO refundAdResponseVO = JSON.parseObject(String.valueOf(response.getData()), RefundAdResponseVO.class);
                if (response.getMsg().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                    //退款成功
                    baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                    log.info("==================【AD3线下退款】================== 【退款成功】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, refundAdResponseVO.getTxnId(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                    //退还分润
                    commonBusinessService.refundShareBinifit(orderRefund);
                } else {
                    //退款失败
                    log.info("==================【AD3线下退款】================== 【退款失败】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                    String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                    String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                    log.info("=========================【AD3线下退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
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
                baseResponse.setCode(EResultEnum.REFUNDING.getCode());
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
     * 撤销
     *
     * @return BaseResponse
     * @Author YangXu
     * @Date 2019/12/23
     **/
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse response = new BaseResponse();
        //获取ad3的终端号和token
        AD3LoginVO ad3LoginVO = this.getTerminalIdAndToken(channel);
        if (ad3LoginVO == null) {
            log.info("************退款时 --- 退款操作AD3登录时未获取到终端号和token*****************ad3LoginVO：{}", JSON.toJSON(ad3LoginVO));
            response.setCode(EResultEnum.REFUND_FAIL.getCode());
            return response;
        }
        //AD3通道订单信息-查询订单接口公共参数实体
        AD3QuerySingleOrderDTO ad3QuerySingleOrderDTO = new AD3QuerySingleOrderDTO(channel.getChannelMerchantId());//商户号
        //查询订单接业务共参数实体
        QueryOneOrderBizContentDTO queryBizContent = new QueryOneOrderBizContentDTO(ad3LoginVO.getTerminalId(), channel.getExtend2(), 1, orderRefund.getOrderId(), "");
        //生成查询签名
        String querySign = this.createAD3Signature(ad3QuerySingleOrderDTO, queryBizContent, ad3LoginVO.getToken());
        ad3QuerySingleOrderDTO.setBizContent(queryBizContent);
        ad3QuerySingleOrderDTO.setSignMsg(querySign);
        AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
        ad3ONOFFRefundDTO.setChannel(channel);
        ad3ONOFFRefundDTO.setAd3QuerySingleOrderDTO(ad3QuerySingleOrderDTO);
        log.info("=================【AD3撤销】=================【请求Channels服务AD3查询】请求参数 ad3ONOFFRefundDTO: {} ", JSON.toJSONString(ad3ONOFFRefundDTO));
        BaseResponse baseResponse = channelsFeign.query(ad3ONOFFRefundDTO);
        log.info("=================【AD3撤销】=================【Channels服务响应】请求参数 baseResponse: {} ", JSON.toJSONString(baseResponse));
        if (baseResponse.getCode().equals("T000")) {
            //请求成功
            AD3OrdersVO ad3OrdersVO = JSON.parseObject(String.valueOf(baseResponse.getData()), AD3OrdersVO.class);
            if (ad3OrdersVO.getState().equals(AD3Constant.ORDER_SUCCESS)) {
                //交易成功
                log.info("=================【AD3撤销】=================【交易成功】orderId : {}", orderRefund.getOrderId());
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS,
                        ad3OrdersVO.getTxnId(), DateUtil.parse(ad3OrdersVO.getTxnDate(), "yyyyMMddHHmmss")) == 1) {//更新成功
                    response = this.cancelPaying(channel, orderRefund, null);
                } else {//更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }

            } else if (ad3OrdersVO.getState().equals(AD3Constant.ORDER_IN_TRADING)) {
                //交易中
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【AD3撤销】=================【交易中】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            } else {
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                //支付失败
                log.info("=================【AD3撤销】=================【支付失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, ad3OrdersVO.getTxnId(), DateUtil.parse(ad3OrdersVO.getTxnDate(), "yyyyMMddHHmmss"));
            }
        } else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【AD3撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }


    /**
     * 退款不上报清结算
     *
     * @return BaseResponse
     * @Author YangXu
     * @Date 2019/12/23
     **/
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse response = new BaseResponse();
        //获取ad3的终端号和token
        AD3LoginVO ad3LoginVO = this.getTerminalIdAndToken(channel);
        if (ad3LoginVO == null) {
            log.info("************退款时 --- 退款操作AD3登录时未获取到终端号和token*****************ad3LoginVO：{}", JSON.toJSON(ad3LoginVO));
            response.setCode(EResultEnum.REFUND_FAIL.getCode());
            return response;
        }
        AD3RefundDTO ad3RefundDTO = new AD3RefundDTO(channel.getChannelMerchantId());
        AD3RefundWorkDTO ad3RefundWorkDTO = new AD3RefundWorkDTO(ad3LoginVO.getTerminalId(), channel.getExtend2(), channel.getExtend4(), orderRefund);
        ad3RefundDTO.setSignMsg(this.createAD3Signature(ad3RefundDTO, ad3RefundWorkDTO, ad3LoginVO.getToken()));
        ad3RefundDTO.setBizContent(ad3RefundWorkDTO);
        AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
        ad3ONOFFRefundDTO.setChannel(channel);
        ad3ONOFFRefundDTO.setAd3RefundDTO(ad3RefundDTO);
        log.info("=================【AD3撤销 cancelPaying】=================【请求Channels服务AD3线下退款】请求参数 ad3ONOFFRefundDTO: {} ", JSON.toJSONString(ad3ONOFFRefundDTO));
        BaseResponse baseResponse = channelsFeign.ad3OfflineRefund(ad3ONOFFRefundDTO);
        log.info("=================【AD3撤销 cancelPaying】=================【Channels服务响应】请求参数 baseResponse: {} ", JSON.toJSONString(baseResponse));
        if (baseResponse.getCode().equals(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS))) {
            if (baseResponse.getMsg().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                response.setCode(EResultEnum.SUCCESS.getCode());
                //撤销成功
                log.info("=================【AD3撤销 cancelPaying】=================【撤销成功】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                //撤销失败
                log.info("=================【AD3撤销 cancelPaying】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【AD3撤销 cancelPaying】===============【请求失败 上报队列 CX_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }

    /**
     * 对向ad3的请求进行签名
     *
     * @param object     参数
     * @param privateKey
     * @return String
     */
    @Override
    public String signMsg(Object object, String privateKey) {
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
            signMsg = RSAUtils.sign(msg, privateKey.replaceAll("\\s*", ""));
        } catch (Exception e) {
            log.info("----------------- 线上签名错误信息记录 ----------------签名原始明文:{},签名:{}", msg, signMsg);
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return signMsg;
    }


    /**
     * 获取终端编号和token
     *
     * @return AD3LoginVO
     */
    @Override
    public AD3LoginVO getTerminalIdAndToken(Channel channel) {
        AD3LoginVO ad3LoginVO = null;
        String token = redisService.get(AD3Constant.AD3_LOGIN_TOKEN);
        String terminalId = redisService.get(AD3Constant.AD3_LOGIN_TERMINAL);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(terminalId)) {
            //AD3登陆业务参数实体
            LoginBizContentDTO bizContent = new LoginBizContentDTO(AD3Constant.LOGIN_OUT, channel.getExtend2(), MD5Util.getMD5String(channel.getExtend3()), channel.getExtend1());
            //AD3登陆公共参数实体
            AD3LoginDTO ad3LoginDTO = new AD3LoginDTO(channel.getChannelMerchantId(), bizContent);
            //先登出
            HttpClientUtils.reqPost(ad3ParamsConfig.getAd3Url() + "/terminalLogin.json", ad3LoginDTO, null);
            //再登陆
            bizContent.setType(AD3Constant.LOGIN_IN);
            HttpResponse httpResponse = HttpClientUtils.reqPost(ad3ParamsConfig.getAd3Url() + "/terminalLogin.json", ad3LoginDTO, null);
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
        Map<String, String> commonMap = ReflexClazzUtils.getFieldForStringValue(commonObj);
        Map<String, String> businessMap = ReflexClazzUtils.getFieldForStringValue(businessObj);
        commonMap.putAll(businessMap);
        String signature = SignTools.getSignStr(commonMap);
        String clearText = signature + "&" + token;
        log.info("=================【AD3线下接口】=================【签名前的明文】 clearText: {}", clearText);
        //与token进行拼接MD5加密
        String sign = MD5Util.getMD5String(clearText);
        log.info("=================【AD3线下接口】=================【签名后的密文】 sign: {}", sign);
        return sign;
    }

}

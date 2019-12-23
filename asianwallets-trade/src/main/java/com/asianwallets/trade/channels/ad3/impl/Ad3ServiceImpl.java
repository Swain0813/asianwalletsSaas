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
                    log.info("==================【AD3线下退款】================== 【退款成功】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    //退款成功
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, refundAdResponseVO.getTxnId(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                } else if (response.getCode().equals("T001")) {
                    //退款失败
                    baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(TradeConstant.AA, orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
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
                if(rabbitMassage ==null){
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
                }else{
                    //退款失败
                    log.info("==================【AD3线下退款】================== 【退款失败】 refundAdResponseVO: {}", JSON.toJSONString(refundAdResponseVO));
                    baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(TradeConstant.AA, orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
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
            }else {
                //请求失败
                baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
                if(rabbitMassage ==null){
                    rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
                }
                log.info("===============【AD3线下退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        }
        return baseResponse;
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
            LoginBizContentDTO bizContent = new LoginBizContentDTO(AD3Constant.LOGIN_OUT,channel);
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

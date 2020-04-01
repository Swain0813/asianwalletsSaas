package com.asianwallets.trade.channels.enets.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ComTools;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.enets.EnetsBankService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;

/**
 * Created by Intellij ABC.
 *
 * @author XinRanShen
 * @date 2020/3/31
 */

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.ENETS_OB)
public class EnetsBankServiceImpl extends ChannelsAbstract implements EnetsBankService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private ChannelsFeign channelsFeign;

    /**
     * 线下CSB处理方法
     *
     * @param orders  订单
     * @param channel 通道
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        return null;
    }

    /**
     * 线下BSC处理方法
     *
     * @param orders   订单
     * @param channel  通道
     * @param authCode 支付条码
     * @return 通用响应实体
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        return null;
    }

    /**
     * enets网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        //最内层实体
        //EnetsBankCoreDTO enetsBankCoreDTO = new EnetsBankCoreDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/enetsBrowserCallback"), ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/enetsServerCallback"), amt);
        //内层实体
        //EnetsBankInSideDTO enetsBankInSideDTO = new EnetsBankInSideDTO(enetsBankCoreDTO);
        //请求实体
        //EnetsBankRequestDTO enetsBankRequestDTO = new EnetsBankRequestDTO(enetsBankInSideDTO, channel.getMd5KeyStr(), orders.getInstitutionOrderId(), sign);
        //需要把金额变成分,金额转换 通道要求要放大100倍上送
        double tempAmount = ComTools.mul(orders.getTradeAmount().doubleValue(), 100);
        BigDecimal b1 = new BigDecimal(tempAmount);
        tempAmount = b1.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat decimalFormat0 = new DecimalFormat("###0");
        String amt = decimalFormat0.format(tempAmount);
        JSONObject json = new JSONObject();
        //区分排序
        json.put("netsMid", channel.getChannelMerchantId());
        json.put("tid", "192.168.15.92");
        json.put("submissionMode", "B");
        json.put("txnAmount", amt);
        json.put("merchantTxnRef", orders.getId());
        json.put("merchantTxnDtm", DateToolUtils.getReqDateH(orders.getMerchantOrderTime()));
        json.put("paymentType", "SALE");
        json.put("currencyCode", orders.getTradeCurrency());
        json.put("paymentMode", "DD");
        json.put("merchantTimeZone", "+8:00");
        json.put("b2sTxnEndURL", ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/eNetsBankBrowserCallback"));
        json.put("b2sTxnEndURLParam", "");
        json.put("s2sTxnEndURL", ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/eNetsBankServerCallback"));
        json.put("s2sTxnEndURLParam", "");
        json.put("clientType", "W");
        json.put("supMsg", "");
        json.put("netsMidIndicator", "U");
        json.put("ipAddress", "192.168.15.92");
        json.put("language", "en");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ss", "1");
        jsonObject.put("msg", json.toString());
        //交易请求参数
        String txnReq = jsonObject.toString();
        //生成签名
        String sign = createSign(txnReq, channel.getMd5KeyStr());
        //请求实体
        EnetsBankRequestDTO enetsBankRequestDTO = new EnetsBankRequestDTO(txnReq, orders.getMerchantOrderId(), sign, channel);
        log.info("----------------- enets网银收单方法 ----------------- enetsBankRequestDTO: {}", JSON.toJSONString(enetsBankRequestDTO));
        BaseResponse channelResponse = channelsFeign.eNetsBankPay(enetsBankRequestDTO);
        log.info("----------------- enets网银收单方法 返回----------------- baseResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        String responseData = (String) channelResponse.getData();
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        if (!StringUtils.isEmpty(responseData)) {
            //网银
            onlineTradeVO.setRespCode("T000");
            onlineTradeVO.setCode_url(responseData);
            onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
            channelResponse.setData(onlineTradeVO);
            return channelResponse;
        }
        return channelResponse;
    }

    /**
     * 退款方法
     *
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return 通用响应实体
     */
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    /**
     * 撤销方法
     *
     * @param channel       通道
     * @param orderRefund
     * @param rabbitMassage
     * @return 通用响应实体
     */
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    /**
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 付款中撤销
     */
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    /**
     * 生成enets签名
     *
     * @param txnReq    请求实体
     * @param md5KeyStr md5key
     * @return
     */
    private static String createSign(String txnReq, String md5KeyStr) {
        String concatPayloadAndSecretKey = txnReq + md5KeyStr;
        log.info("【eNets通道签名前的明文】: {}", concatPayloadAndSecretKey);
        String sign;
        try {
            sign = encodeBase64(hashSHA256ToBytes(concatPayloadAndSecretKey.getBytes()));
            log.info("【eNets通道签名后的密文】: {}", sign);
        } catch (Exception e) {
            log.error("*********************生成enets签名发生异常********************", e);
            return null;
        }
        return sign;
    }

    /**
     * 生成enets签名
     *
     * @return
     */
    private static String encodeBase64(byte[] data) throws Exception {
        return DatatypeConverter.printBase64Binary(data);
    }

    /**
     * 生成enets签名
     *
     * @return
     */
    private static byte[] hashSHA256ToBytes(byte[] input) throws Exception {
        byte[] byteData = null;
        if (input != null) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input);
            byteData = md.digest();
        } else {
            log.error("hashSHA256ToBytes#输入参数为空");
        }
        return byteData;
    }
}

package com.asianwallets.trade.channels.ad3.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3ONOFFRefundDTO;
import com.asianwallets.common.dto.ad3.CSBScanBizContentDTO;
import com.asianwallets.common.dto.ad3.SendAdRefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @Autowired
    private ChannelsFeign channelsFeign;

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
            log.info("==================【AD3线上退款】================== OrderRefund: {}", JSON.toJSONString(orderRefund));
            SendAdRefundDTO sendAdRefundDTO = new SendAdRefundDTO(channel.getChannelMerchantId(), orderRefund);
            sendAdRefundDTO.setMerchantSignType(merchantSignType);
            sendAdRefundDTO.setSignMsg(this.signMsg(sendAdRefundDTO));

            AD3ONOFFRefundDTO ad3ONOFFRefundDTO = new AD3ONOFFRefundDTO();
            ad3ONOFFRefundDTO.setChannel(channel);
            ad3ONOFFRefundDTO.setSendAdRefundDTO(sendAdRefundDTO);
            BaseResponse response = channelsFeign.ad3OnlineRefund(ad3ONOFFRefundDTO);
            if (response.getCode().equals(AD3Constant.AD3_ONLINE_SUCCESS)) {
                //请求成功


            }else{


            }
        } else if (TradeConstant.TRADE_UPLINE.equals(orderRefund.getTradeDirection())) {
            log.info("==================【AD3线下退款】================== OrderRefund: {}", JSON.toJSONString(orderRefund));


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
}

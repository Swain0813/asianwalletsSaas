package com.asianwallets.trade.channels.ad3;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.AD3LoginVO;
import com.asianwallets.trade.dto.AD3OfflineCallbackDTO;
import com.asianwallets.trade.dto.AD3OnlineCallbackDTO;

import javax.servlet.http.HttpServletResponse;

public interface Ad3Service {

    /**
     * AD3线上
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse onlinePay(Orders orders, Channel channel);

    /**
     * AD3线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse offlineCSB(Orders orders, Channel channel);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 撤销接口
     * @return
     **/
    BaseResponse cancel(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     * @return
     **/
    BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * 对向ad3的请求进行签名
     *
     * @param object
     * @return
     */
    String signMsg(Object object);

    /**
     * 获取终端编号和token
     *
     * @return
     */
    AD3LoginVO getTerminalIdAndToken(Channel channel);


    /**
     * 生成AD3认证签名
     *
     * @param commonObj   AD3公共参数输入实体
     * @param businessObj AD3业务参数输入实体
     * @param token       token
     * @return ad3签名
     */
    String createAD3Signature(Object commonObj, Object businessObj, String token);

    /**
     * ad3线下CSB回调
     *
     * @param ad3OfflineCallbackDTO ad3线下回调输入实体
     * @return
     */
    String ad3OfflineCsbServerCallback(AD3OfflineCallbackDTO ad3OfflineCallbackDTO);

    /**
     * ad3线上回调
     *
     * @param ad3OnlineCallbackDTO ad3线下回调输入实体
     * @return SUCCESS
     */
    String ad3OnlineServerCallback(AD3OnlineCallbackDTO ad3OnlineCallbackDTO);

    /**
     * AD3服务器回调
     *
     * @param aavo     参数
     * @param response 响应
     */
    void ad3OnlineBrowserCallback(AD3OnlineCallbackDTO aavo, HttpServletResponse response);
}

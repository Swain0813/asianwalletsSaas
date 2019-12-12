package com.asianwallets.channels.service;

import com.asianwallets.common.dto.wechat.*;
import com.asianwallets.common.response.BaseResponse;

public interface WechatService {

    /**
     * wechat线下BSC收单方法
     *
     * @param wechatBSCDTO WECHAT线下BSC实体
     * @return
     */
    BaseResponse wechatBSC(WechatBSCDTO wechatBSCDTO);

    /**
     * wechat线下CSB收单方法
     *
     * @param wechatCSBDTO WECHAT线下CSB实体
     * @return
     */
    BaseResponse wechatCSB(WechatCSBDTO wechatCSBDTO);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/6/26
     * @Descripate 微信退款接口
     **/
    BaseResponse wechatRefund(WechaRefundDTO wechaRefundDTO);

    /**
     * @return
     * @Author XuWenQi
     * @Date 2019/7/1
     * @Descripate 微信撤销接口
     **/
    BaseResponse wechatCancel(WechatCancelDTO wechatCancelDTO);


    /**
     * wechat
     *
     * @param wechatQueryDTO 微信查询实体
     * @return
     */
    BaseResponse wechatQuery(WechatQueryDTO wechatQueryDTO);
}

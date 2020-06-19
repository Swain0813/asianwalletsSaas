package com.asianwallets.trade.service;

import com.asianwallets.common.dto.BankCardUndoDTO;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.dto.UndoDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.response.BaseResponse;

public interface RefundTradeService {


    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款接口
     * @return
     **/
    BaseResponse refundOrder(RefundDTO refundDTO, String reqIp);

    /**
     * 撤销接口
     * @param undoDTO
     * @param reqIp
     * @return
     */
    BaseResponse reverse(UndoDTO undoDTO, String reqIp);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/3/14
     * @Descripate 退款操作
     **/
    BaseResponse doRefundOrder(OrderRefund orderRefund, Channel channel);


    /**
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate 人工退款接口
     * @return
     **/
    BaseResponse artificialRefund(String username, String refundOrderId, Boolean enabled, String remark);


    /**
     * 银行卡冲正接口
     * @param bankCardUndoDTO
     * @param reqIp
     * @return
     */
    BaseResponse bankCardReverse(BankCardUndoDTO bankCardUndoDTO, String reqIp);


    /**
     * 银行卡撤销接口
     * @param bankCardUndoDTO
     * @param reqIp
     * @return
     */
    BaseResponse bankCardRevoke(BankCardUndoDTO bankCardUndoDTO, String reqIp);

    /**
     * @Author YangXu
     * @Date 2020/5/25
     * @Descripate 银行卡退款接口
     * @return
     **/
    BaseResponse bankCardRefund(RefundDTO refundDTO, String reqIp);

    /**
     * 预授权冲正接口
     * 预授权撤销接口
     * @param bankCardUndoDTO
     * @param reqIp
     * @return
     */
    BaseResponse preAuthReverseAndRevoke(BankCardUndoDTO bankCardUndoDTO, String reqIp);

    /**
     * 预授权完成撤销接口
     * @param refundDTO
     * @param reqIp
     * @return
     */
    BaseResponse preAuthCompleteRevoke(RefundDTO refundDTO, String reqIp);

}

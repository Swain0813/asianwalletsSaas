package com.asianwallets.channels.service;

import com.asianwallets.common.dto.th.ISO8583.ThDTO;
import com.asianwallets.common.response.BaseResponse;


public interface ThService {

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华退款
     **/
    BaseResponse thRefund(ThDTO thDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华查询
     **/
    BaseResponse thQuery(ThDTO thDTO);

    /**
     * 通华签到
     *
     * @return
     */
    BaseResponse thSignIn(ThDTO thDTO);

    /**
     * 通华CSB
     *
     * @param thDTO 通华dto
     * @return
     */
    BaseResponse thCSB(ThDTO thDTO);

    /**
     * 通华BSC
     *
     * @param thDTO 通华dto
     * @return
     */
    BaseResponse thBSC(ThDTO thDTO);

    /**
     * 通华线下银行卡消费
     *
     * @param thDTO
     * @return
     */
    BaseResponse thBankCard(ThDTO thDTO);

    /**
     * 通华线下银行卡冲正
     *
     * @param thDTO
     * @return
     */
    BaseResponse thBankCardReverse(ThDTO thDTO);

    /**
     * 通华线下银行卡退款
     *
     * @param thDTO
     * @return
     */
    BaseResponse thBankCardRefund(ThDTO thDTO);

    /**
     * 通华线下银行卡撤销
     *
     * @param thDTO
     * @return
     */
    BaseResponse thBankCardUndo(ThDTO thDTO);

    /**
     * 预授权
     *
     * @param thDTO
     * @return
     */
    BaseResponse preAuth(ThDTO thDTO);

    /**
     * 预授权冲正
     *
     * @param thDTO
     * @return
     */
    BaseResponse preAuthReverse(ThDTO thDTO);

    /**
     * 预授权撤销
     *
     * @param thDTO
     * @return
     */
    BaseResponse preAuthRevoke(ThDTO thDTO);

    /**
     * 预授权完成
     *
     * @param thDTO
     * @return
     */
    BaseResponse preAuthComplete(ThDTO thDTO);

    /**
     * 预授权完成撤销
     *
     * @param thDTO
     * @return
     */
    BaseResponse preAuthCompleteRevoke(ThDTO thDTO);
}

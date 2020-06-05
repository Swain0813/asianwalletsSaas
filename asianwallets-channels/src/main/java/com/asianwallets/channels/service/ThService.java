package com.asianwallets.channels.service;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
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
    BaseResponse thSignIn(ISO8583DTO iso8583DTO);

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
     * @param thDTO
     * @return
     */
    BaseResponse thBankCard(ThDTO thDTO);

    /**
     * 通华线下银行卡冲正
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


}

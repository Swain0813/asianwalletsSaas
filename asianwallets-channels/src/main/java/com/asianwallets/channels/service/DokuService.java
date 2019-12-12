package com.asianwallets.channels.service;

import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.response.BaseResponse;

public interface DokuService {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate Doku收单接口
     **/
    BaseResponse payMent(DOKUReqDTO dokuReqDTO);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/13
     * @Descripate 检查交易状态
     **/
    BaseResponse checkStatus(DOKUReqDTO dokuReqDTO);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/13
     * @Descripate 检查交易状态
     **/
    BaseResponse refund(DOKUReqDTO dokuReqDTO);

}

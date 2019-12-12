package com.asianwallets.channels.service;

import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.help2pay.Help2PayRequestDTO;
import com.asianwallets.common.response.BaseResponse;

public interface Help2PayService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate help2Pay收单接口
     **/
    BaseResponse help2Pay(Help2PayRequestDTO help2PayRequestDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/17
     * @Descripate HELP2PAY汇款接口
     **/
    BaseResponse help2PayOut(Help2PayOutDTO help2PayOutDTO);
}

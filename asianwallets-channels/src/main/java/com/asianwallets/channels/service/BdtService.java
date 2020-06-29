package com.asianwallets.channels.service;

import com.asianwallets.common.dto.bdt.BdtDTO;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.response.BaseResponse;

public interface BdtService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate Doku收单接口
     **/
    BaseResponse payMent(BdtDTO BdtDTO);
}

package com.asianwallets.channels.service;


import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.response.BaseResponse;

public interface Ad3Service {

    /**
     * AD3线下CSB
     *
     * @param ad3CSBScanPayDTO   AD3线下CSB输入实体
     * @return BaseResponse
     */
    BaseResponse offlineCsb(AD3CSBScanPayDTO ad3CSBScanPayDTO);

}

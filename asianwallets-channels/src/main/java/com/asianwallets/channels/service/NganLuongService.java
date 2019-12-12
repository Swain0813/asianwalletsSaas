package com.asianwallets.channels.service;

import com.asianwallets.common.dto.nganluong.NganLuongDTO;
import com.asianwallets.common.dto.nganluong.NganLuongQueryDTO;
import com.asianwallets.common.response.BaseResponse;

public interface NganLuongService {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate NganLuong收单接口
     **/
    BaseResponse nganLuongPay(NganLuongDTO nganLuongDTO);

    /**
     * @param nganLuongQueryDTO 查询实体
     * @return BaseResponse
     * @Descripate NganLuong查询接口
     **/
    BaseResponse nganLuongQuery(NganLuongQueryDTO nganLuongQueryDTO);
}

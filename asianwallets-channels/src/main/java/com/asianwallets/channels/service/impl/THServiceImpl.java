package com.asianwallets.channels.service.impl;

import com.asianwallets.channels.service.THService;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-07 11:01
 **/
@Slf4j
@Service
public class THServiceImpl implements THService {


    /**
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华退款
     * @return
     **/
    @Override
    public BaseResponse thRefund(ISO8583DTO thRefundDTO) {

        return null;
    }
}

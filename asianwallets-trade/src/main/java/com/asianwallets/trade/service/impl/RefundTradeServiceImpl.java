package com.asianwallets.trade.service.impl;

import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.RefundTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-18 14:01
 **/
@Slf4j
@Service
@Transactional
public class RefundTradeServiceImpl implements RefundTradeService {

    @Autowired
    private CommonBusinessService commonBusinessService;


    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
     * @return
     **/
    @Override
    public BaseResponse refundOrder(RefundDTO refundDTO, String reqIp) {

        BaseResponse baseResponse = new BaseResponse();
        //输入参数的check
        if (refundDTO.getSign() == null) {//签名必填
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //验签
        //签名校验
        if (!commonBusinessService.checkUniversalSign(refundDTO)) {
            log.info("-----------------【退款】信息记录--------------【签名错误】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }


        return null;
    }



}

package com.asianwallets.trade.service.impl;

import com.asianwallets.common.constant.TradeConstant;
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
    private CommonBusinessService commonBusinessServicel;


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
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection())) {//线下
            if (!commonBusinessServicel.checkSignByMd5(refundDTO)) {
                throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());//验签不匹配
            }
        } else {
            if (!commonBusinessServicel.checkOnlineSignMsg(refundDTO)) {
                throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());//验签不匹配
            }
        }



        return null;
    }



}

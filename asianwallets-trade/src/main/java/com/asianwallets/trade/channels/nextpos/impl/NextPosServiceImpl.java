package com.asianwallets.trade.channels.nextpos.impl;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-19 16:47
 **/
@Slf4j
@Service
@Transactional
public class NextPosServiceImpl extends ChannelsAbstractAdapter implements NextPosService {


    /**
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     * @return
     **/
    @Override
    public BaseResponse refund(Channel channel,OrderRefund orderRefund) {




        return null;
    }
}

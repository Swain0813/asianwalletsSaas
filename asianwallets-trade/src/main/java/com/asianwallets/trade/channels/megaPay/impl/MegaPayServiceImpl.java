package com.asianwallets.trade.channels.megaPay.impl;

import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.megaPay.MegaPayService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.MEGAPAY_ONLINE)
public class MegaPayServiceImpl extends ChannelsAbstractAdapter implements MegaPayService {


}

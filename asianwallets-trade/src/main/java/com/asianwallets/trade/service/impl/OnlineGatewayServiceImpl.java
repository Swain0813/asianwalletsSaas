package com.asianwallets.trade.service.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.channels.help2pay.impl.Help2PayServiceImpl;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.OnlineGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
public class OnlineGatewayServiceImpl implements OnlineGatewayService {

    @Autowired
    private Help2PayService help2PayService;


    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    @Override
    public OnlineTradeVO gateway(OnlineTradeDTO onlineTradeDTO) {

        help2PayService.onlinePay(null,null);

        try {
            ChannelsAbstract channelsAbstract = (Help2PayServiceImpl)Class.forName("com.asianwallets.trade.channels.help2pay.impl.Help2PayServiceImpl").newInstance();

            channelsAbstract.offlineBSC(null,null,null);
        }catch (Exception e) {
            e.printStackTrace();
        }


        if (StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {

        }
        return null;
    }
}

package com.asianwallets.trade.service.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.OnlineGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
public class OnlineGatewayServiceImpl implements OnlineGatewayService {

    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    @Override
    public OnlineTradeVO gateway(OnlineTradeDTO onlineTradeDTO) {
        if (StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {

        }
        return null;
    }
}

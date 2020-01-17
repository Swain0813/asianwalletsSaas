package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.SettleCheckAccountFeign;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-14 15:53
 **/
@Component
public class SettleCheckAccountFeignImpl implements SettleCheckAccountFeign {

    @Override
    public BaseResponse selectTcsStFlow(String time) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageSettleAccountCheck(TradeCheckAccountDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageSettleAccountCheckDetail(TradeCheckAccountDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public Map<String, Object> exportSettleAccountCheck(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

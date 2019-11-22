package com.asianwallets.task.service;
import com.asianwallets.common.dto.FundChangeDTO;
import com.asianwallets.common.response.BaseResponse;
import java.util.Map;

/**
 * 清结算相关接口
 */
public interface ClearingService {

    /**
     * 资金变动接口
     * 场景支付成功后上报清结算系统
     *
     * @return
     */
    BaseResponse fundChange(FundChangeDTO fundChangeDTO, Map<String, Object> headerMap);
}

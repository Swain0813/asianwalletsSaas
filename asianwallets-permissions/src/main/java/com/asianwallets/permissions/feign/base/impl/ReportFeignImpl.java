package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.DccReportVO;
import com.asianwallets.permissions.feign.base.ReportFeign;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 报表相关接口的Feign端的实现类
 */
@Component
public class ReportFeignImpl implements ReportFeign {

    /**
     * DCC报表查询
     * @param dccReportDTO dcc报表查询实体
     * @return
     */
    @Override
    public BaseResponse getDccReport(DccReportDTO dccReportDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * DCC报表导出
     * @param dccReportDTO
     * @return
     */
    @Override
    public List<DccReportVO> exportDccReport(DccReportDTO dccReportDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

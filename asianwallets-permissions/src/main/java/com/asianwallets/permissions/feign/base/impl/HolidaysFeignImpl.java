package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.entity.Holidays;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.HolidaysFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 节假日Feign端熔断降级实现类
 */

@Component
public class HolidaysFeignImpl implements HolidaysFeign {

    /**
     * 添加节假日信息
     * @param holidaysDTO
     * @return
     */
    @Override
    public BaseResponse addHolidays(HolidaysDTO holidaysDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 禁用节假日信息
     * @param holidaysDTO
     * @return
     */
    @Override
    public BaseResponse banHolidays(HolidaysDTO holidaysDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页多条件查询节假日信息
     * @param holidaysDTO
     * @return
     */
    @Override
    public BaseResponse getByMultipleConditions(HolidaysDTO holidaysDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }


    /**
     * 导入节假日信息
     * @param list
     * @return
     */
    @Override
    public BaseResponse uploadFiles(List<Holidays> list) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

}

package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.ChargesTypeDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.ChargesFeign;
import org.springframework.stereotype.Component;

/**
 * @author shenxinran
 * @Date: 2019/1/28 09:21
 * @Description: 算费Feign熔断类
 */
@Component
public class ChargesFeignImpl implements ChargesFeign {

    /**
     * 分页查询所有算费
     *
     * @param chargesTypeDTO
     * @return
     */
    @Override
    public BaseResponse pageChargesCondition(ChargesTypeDTO chargesTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 根据ID 查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseResponse getChargesInfo(ChargesTypeDTO chargesTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 新增算费
     *
     * @param chargesTypeDTO
     * @return
     */
    @Override
    public BaseResponse addChargesType(ChargesTypeDTO chargesTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 更新算费
     *
     * @param chargesTypeDTO
     * @return
     */
    @Override
    public BaseResponse updateChargesType(ChargesTypeDTO chargesTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 启用禁用算费
     *
     * @param
     * @return
     */
    @Override
    public BaseResponse banChargesType(ChargesTypeDTO chargesTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

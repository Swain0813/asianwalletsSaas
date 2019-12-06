package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.InstitutionRequestFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * 机构请求参数设置的Feign端的实现类
 */
@Component
public class InstitutionRequestFeignImpl implements InstitutionRequestFeign {

    /**
     *添加机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public BaseResponse addInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页查询机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public BaseResponse pageInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 根据机构编号查询机构请求参数设置的详情
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public BaseResponse getInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 修改机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public BaseResponse updateInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

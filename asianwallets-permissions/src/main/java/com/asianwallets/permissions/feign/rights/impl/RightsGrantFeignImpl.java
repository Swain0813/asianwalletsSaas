package com.asianwallets.permissions.feign.rights.impl;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.permissions.feign.rights.RightsGrantFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 权益发放管理的feign端的实现类
 */
@Component
public class RightsGrantFeignImpl implements RightsGrantFeign {

    /**
     * 分页查询权益发放管理信息
     * @param rightsGrantDTO
     * @return
     */
    @Override
    public BaseResponse pageFindRightsGrant(RightsGrantDTO rightsGrantDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出权益发放管理信息
     *
     * @param rightsGrantDTO
     * @return
     */
    @Override
    public List<ExportRightsGrantVO> exportRightsGrants(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 新增权益发放管理
     * @param rightsGrantInsertDTO
     * @return
     */
    @Override
    public BaseResponse addRightsGrant(RightsGrantInsertDTO rightsGrantInsertDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页查询权益票券信息
     * @param rightsGrantDTO
     * @return
     */
    @Override
    public BaseResponse pageFindRightsUserGrant(RightsGrantDTO rightsGrantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询权益票券详情
     * @param ticketId
     * @return
     */
    @Override
    public BaseResponse getRightsUserGrantDetail(String ticketId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<ExportRightsUserGrantVO> exportRightsUserGrant(RightsGrantDTO rightsGrantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

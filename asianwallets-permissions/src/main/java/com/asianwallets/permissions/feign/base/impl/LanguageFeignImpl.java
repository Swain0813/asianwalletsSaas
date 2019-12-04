package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.LanguageFeign;
import org.springframework.stereotype.Component;

/**
 * @author shenxinran
 * @Date: 2019/1/29 15:58
 * @Description: 语种管理熔断器
 */
@Component
public class LanguageFeignImpl implements LanguageFeign {
    /**
     * 新增语种
     *
     * @param languageDTO
     * @return
     */
    @Override
    public BaseResponse addLanguage(LanguageDTO languageDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 更新语种
     *
     * @param languageDTO
     * @return
     */
    @Override
    public BaseResponse updateLanguage(LanguageDTO languageDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页查询语种
     *
     * @param languageDTO
     * @return
     */
    @Override
    public BaseResponse pageFindLanguage(LanguageDTO languageDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 根据ID 查询语种
     */
    @Override
    public BaseResponse getLanguageInfo(LanguageDTO languageDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 启用禁用语种
     *
     * @return
     */
    @Override
    public BaseResponse banLanguage(LanguageDTO languageDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

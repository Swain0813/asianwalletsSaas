package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.LanguageFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author shenxinran
 * @Date: 2019/1/29 15:57
 * @Description: 语种管理 feign
 */
@Component
@FeignClient(value = "asianwallets-base", fallback = LanguageFeignImpl.class)
public interface LanguageFeign {
    /**
     * 新增语种
     *
     * @param languageDTO
     * @return
     */
    @PostMapping("/language/addLanguage")
    BaseResponse addLanguage(@RequestBody @ApiParam LanguageDTO languageDTO);

    /**
     * 更新语种
     *
     * @param languageDTO
     * @return
     */
    @PostMapping("/language/updateLanguage")
    BaseResponse updateLanguage(@RequestBody @ApiParam LanguageDTO languageDTO);

    /**
     * 分页查询语种
     *
     * @param languageDTO
     * @return
     */
    @PostMapping("/language/pageFindLanguage")
    BaseResponse pageFindLanguage(@RequestBody @ApiParam LanguageDTO languageDTO);

    /**
     * 根据ID 查询语种
     *
     * @return
     */
    @GetMapping("/language/getLanguageInfo")
    BaseResponse getLanguageInfo(@RequestBody @ApiParam LanguageDTO languageDTO);

    /**
     * 启用禁用语种
     *
     * @return
     */
    @PostMapping("/language/banLanguage")
    BaseResponse banLanguage(@RequestBody @ApiParam LanguageDTO languageDTO);
}

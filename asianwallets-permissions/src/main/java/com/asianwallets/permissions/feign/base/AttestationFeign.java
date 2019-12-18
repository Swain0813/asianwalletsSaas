package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.AttestationFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 密钥管理Feign接口
 */
@FeignClient(value = "asianwallets-base", fallback = AttestationFeignImpl.class)
public interface AttestationFeign {
    /**
     * 生成RSA公私钥
     *
     * @return
     */
    @GetMapping("/attestation/getRSA")
    BaseResponse getRSA();

    /**
     * 查询商户密钥列表
     *
     * @param attestationDTO
     * @return
     */
    @PostMapping("/attestation/pageKeyInfo")
    BaseResponse pageKeyInfo(@RequestBody @ApiParam AttestationDTO attestationDTO);

    /**
     * 更新密钥
     *
     * @param attestationDTO
     * @return
     */
    @PostMapping("/attestation/updateKeyInfo")
    BaseResponse updateKeyInfo(@RequestBody @ApiParam AttestationDTO attestationDTO);

}

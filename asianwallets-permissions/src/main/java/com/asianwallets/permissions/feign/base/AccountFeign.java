package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.AccountListVO;
import com.asianwallets.permissions.feign.base.impl.AccountFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 账户信息相关
 */
@FeignClient(value = "asianwallets-base", fallback = AccountFeignImpl.class)
public interface AccountFeign {

    @ApiOperation(value = "分页查询账户信息")
    @PostMapping("/account/pageFindAccount")
    BaseResponse pageFindAccount(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "导出账户信息")
    @PostMapping("/account/exportAccountList")
    List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);
}

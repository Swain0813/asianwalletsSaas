package com.asianwallets.base.controller;
import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.AccountListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 账户信息相关
 */
@RestController
@Api(description = "账户管理接口")
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;


    @ApiOperation(value = "分页查询账户信息")
    @PostMapping("/pageFindAccount")
    public BaseResponse pageFindAccount(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return ResultUtil.success(accountService.pageFindAccount(accountSearchDTO));
    }

    @ApiOperation(value = "导出账户信息")
    @PostMapping("/exportAccountList")
    public List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return accountService.exportAccountList(accountSearchDTO);
    }
}

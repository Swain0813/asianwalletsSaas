package com.asianwallets.rights.controller;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.dto.SendReceiptDTO;
import com.asianwallets.common.dto.SendTicketDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.rights.service.RightsGrantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Api(description = "权益发放管理模块")
@RestController
@RequestMapping("/rightsGrant")
public class RightsGrantController extends BaseController {

    @Autowired
    private RightsGrantService rightsGrantService;

    @ApiOperation(value = "短信和邮箱发券")
    @PostMapping("/sendReceipt")
    @CrossOrigin
    public BaseResponse sendReceipt(@RequestBody @ApiParam @Valid SendReceiptDTO sendReceiptDTO) {
        return ResultUtil.success(rightsGrantService.sendReceipt(sendReceiptDTO));
    }

    @ApiOperation(value = "发券接口【对外API】")
    @PostMapping("/sendTicket")
    @CrossOrigin
    public BaseResponse sendReceipt(@RequestBody @ApiParam @Valid SendTicketDTO sendTicketDTO) {
        return ResultUtil.success(rightsGrantService.sendTicket(sendTicketDTO));
    }

    @ApiOperation(value = "分页查询权益票券信息")
    @PostMapping("/pageFindRightsUserGrant")
    @CrossOrigin
    public BaseResponse pageFindRightsUserGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        return ResultUtil.success(rightsGrantService.pageFindRightsUserGrant(rightsGrantDTO));
    }

    @ApiOperation(value = "导出权益票券信息")
    @PostMapping("/exportRightsUserGrant")
    public List<ExportRightsUserGrantVO> exportRightsUserGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        return rightsGrantService.exportRightsUserGrant(rightsGrantDTO);
    }

    @ApiOperation(value = "查询权益票券详情")
    @GetMapping("/getRightsUserGrantDetail")
    public BaseResponse getRightsUserGrantDetail(@RequestParam @ApiParam String ticketId) {
        return ResultUtil.success(rightsGrantService.getRightsUserGrantDetail(ticketId));
    }

    @ApiOperation(value = "分页查询权益发放管理信息")
    @PostMapping("/pageFindRightsGrant")
    @CrossOrigin
    public BaseResponse pageFindRightsGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        return ResultUtil.success(rightsGrantService.pageFindRightsGrant(rightsGrantDTO));
    }

    @ApiOperation(value = "查询权益发放管理信息详情")
    @PostMapping("/selectRightsGrantInfo")
    @CrossOrigin
    public BaseResponse selectRightsGrantInfo(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        return ResultUtil.success(rightsGrantService.selectRightsGrantInfo(rightsGrantDTO));
    }

    @ApiOperation(value = "导出权益发放管理信息")
    @PostMapping("/exportRightsGrants")
    public List<ExportRightsGrantVO> exportRightsGrants(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        return rightsGrantService.exportRightsGrants(rightsGrantDTO);
    }

    @ApiOperation(value = "新增权益发放管理")
    @PostMapping("/addRightsGrant")
    public BaseResponse addRightsGrant(@RequestBody @ApiParam @Valid RightsGrantInsertDTO rightsGrantInsertDTO) {
        return ResultUtil.success(rightsGrantService.addRightsGrant(getSysUserVO().getUsername(), rightsGrantInsertDTO));
    }

}

package com.asianwallets.permissions.feign.rights;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.dto.SendReceiptDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.permissions.feign.rights.impl.RightsGrantFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 权益发放管理的feign端
 */
@FeignClient(value = "asianwallets-rights", fallback = RightsGrantFeignImpl.class)
public interface RightsGrantFeign {

    @ApiOperation(value = "分页查询权益发放管理信息")
    @PostMapping("/rightsGrant/pageFindRightsGrant")
    BaseResponse pageFindRightsGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO);

    @ApiOperation(value = "导出权益发放管理信息")
    @PostMapping("/rightsGrant/exportRightsGrants")
    List<ExportRightsGrantVO> exportRightsGrants(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO);

    @ApiOperation(value = "新增权益发放管理")
    @PostMapping("/rightsGrant/addRightsGrant")
    BaseResponse addRightsGrant(@RequestBody @ApiParam @Valid RightsGrantInsertDTO rightsGrantInsertDTO);

    @ApiOperation(value = "发券接口")
    @PostMapping("/rightsGrant/sendReceipt")
    BaseResponse sendReceipt(@RequestBody @ApiParam @Valid SendReceiptDTO sendReceiptDTO);

    @ApiOperation(value = "分页查询权益票券信息")
    @PostMapping("/rightsGrant/pageFindRightsUserGrant")
    BaseResponse pageFindRightsUserGrant(RightsGrantDTO rightsGrantDTO);

    @ApiOperation(value = "查询权益票券详情")
    @GetMapping("/rightsGrant/getRightsUserGrantDetail")
    BaseResponse getRightsUserGrantDetail(@RequestParam("ticketId") String ticketId);

    @ApiOperation(value = "导出权益票券信息")
    @PostMapping("/rightsGrant/exportRightsUserGrant")
    List<ExportRightsUserGrantVO> exportRightsUserGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO);
}

package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.dto.RightsOrdersDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.asianwallets.permissions.feign.rights.OtaChannelFeign;
import com.asianwallets.permissions.feign.rights.RightsOrdersFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-31 10:19
 **/
@Api(description = "权益核销")
@RestController
@RequestMapping("/rightsOrders")
public class RightsOrdersFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private OtaChannelFeign otaChannelFeign;

    @Autowired
    private RightsOrdersFeign rightsOrdersFeign;

    @Autowired
    private ExportService exportService;


    @ApiOperation(value = "OTA平台分页查询")
    @PostMapping("pageOtaChannel")
    public BaseResponse pageOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(otaChannelDTO),
                "OTA平台分页查询"));
        return otaChannelFeign.pageOtaChannel(otaChannelDTO);
    }

    @ApiOperation(value = "添加修改OTA平台")
    @PostMapping("addOtaChannel")
    public BaseResponse addOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(otaChannelDTO),
                "添加修改OTA平台"));
        return otaChannelFeign.addOtaChannel(otaChannelDTO);
    }

    @ApiOperation(value = "发放平台的下来框")
    @PostMapping("/getOtaChannels")
    public List<OtaChannel> getOtaChannels() {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "发放平台的下来框"));
        return otaChannelFeign.getOtaChannels();
    }

    @ApiOperation(value = "权益核销分页查询")
    @PostMapping("pageRightsOrders")
    public BaseResponse pageRightsOrders(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsOrdersDTO),
                "权益核销分页查询"));
        return rightsOrdersFeign.pageRightsOrders(rightsOrdersDTO);
    }

    @ApiOperation(value = "查询权益核销详情")
    @PostMapping("getRightsOrdersInfo")
    public BaseResponse getRightsOrdersInfo(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsOrdersDTO),
                "查询权益核销详情"));
        return rightsOrdersFeign.getRightsOrdersInfo(rightsOrdersDTO);
    }

    @ApiOperation(value = "导出权益核销")
    @PostMapping("exportRightsOrders")
    public BaseResponse exportRightsOrders(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsOrdersDTO),
                "导出权益核销"));
        List<RightsOrdersVO> list = rightsOrdersFeign.exportRightsOrders(rightsOrdersDTO);
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(list)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.exportRightsOrders(this.getLanguage(), list);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

}

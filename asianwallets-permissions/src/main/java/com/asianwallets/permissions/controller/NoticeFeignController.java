package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.NoticeFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告模块
 */
@RestController
@Api(description = "公告管理接口")
@RequestMapping("/notice")
public class NoticeFeignController extends BaseController {

    @Autowired
    private NoticeFeign noticeFeign;

    /**
     * 操作日志模块
     */
    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加公告信息")
    @PostMapping("/addNotice")
    public BaseResponse addNotice(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(noticeDTO),
                "添加公告信息"));
        return noticeFeign.addNotice(noticeDTO);
    }

    @ApiOperation(value = "修改公告信息")
    @PostMapping("/updateNotice")
    public BaseResponse updateNotice(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(noticeDTO),
                "修改公告信息"));
        return noticeFeign.updateNotice(noticeDTO);
    }

    @ApiOperation(value = "查询所有公告信息")
    @PostMapping("/pageNotice")
    public BaseResponse pageNotice(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(noticeDTO),
                "查询所有公告信息"));
        return noticeFeign.pageNotice(noticeDTO);
    }

    @ApiOperation(value = "根据公告类别,机构编号以及语言查询公告信息")
    @PostMapping("/pageNoticeByLanguageAndCategory")
    public BaseResponse pageNoticeByLanguageAndCategory(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(noticeDTO),
                "根据公告类别,机构编号以及语言查询公告信息"));
        return noticeFeign.pageNoticeByLanguageAndCategory(noticeDTO);
    }

}

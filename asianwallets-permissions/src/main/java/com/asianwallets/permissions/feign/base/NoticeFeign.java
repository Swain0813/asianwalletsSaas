package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.NoticeAddDTO;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.NoticeFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 公告模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = NoticeFeignImpl.class)
public interface NoticeFeign {
    /**
     * 添加公告信息
     *
     * @param noticeDTO
     * @return
     */
    @PostMapping("/notice/addNotice")
    BaseResponse addNotice(@RequestBody @ApiParam NoticeAddDTO noticeDTO);

    /**
     * 修改公告信息
     *
     * @param noticeDTO
     * @return
     */
    @PostMapping("/notice/updateNotice")
    BaseResponse updateNotice(@RequestBody @ApiParam NoticeAddDTO noticeDTO);

    /**
     * 查询所有公告信息
     *
     * @param noticeDTO
     * @return
     */
    @PostMapping("/notice/pageNotice")
    BaseResponse pageNotice(@RequestBody @ApiParam NoticeDTO noticeDTO);

    /**
     * 根据公告类别,机构编号以及语言查询公告信息
     *
     * @param noticeDTO
     * @return
     */
    @PostMapping("/notice/pageNoticeByLanguageAndCategory")
    BaseResponse pageNoticeByLanguageAndCategory(@RequestBody @ApiParam NoticeDTO noticeDTO);
}

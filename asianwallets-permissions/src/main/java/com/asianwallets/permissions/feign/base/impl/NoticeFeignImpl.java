package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.NoticeAddDTO;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.NoticeFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 公告模块Feign端的实现类
 */
@Component
public class NoticeFeignImpl implements NoticeFeign {

    /**
     * 添加公告信息
     *
     * @param noticeDTO
     */
    @Override
    public BaseResponse addNotice(@RequestBody @ApiParam NoticeAddDTO noticeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 修改公告信息
     *
     * @param noticeDTO
     * @return
     */
    @Override
    public BaseResponse updateNotice(@RequestBody @ApiParam NoticeAddDTO noticeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询所有公告信息
     *
     * @param noticeDTO
     * @return
     */
    @Override
    public BaseResponse pageNotice(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 根据公告类别,机构编号以及语言查询公告信息
     *
     * @param noticeDTO
     * @return
     */
    @Override
    public BaseResponse pageNoticeByLanguageAndCategory(@RequestBody @ApiParam NoticeDTO noticeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}

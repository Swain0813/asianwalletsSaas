package com.asianwallets.base.service;
import com.asianwallets.common.dto.NoticeAddDTO;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.entity.Notice;
import com.github.pagehelper.PageInfo;

/**
 * 公告模块相关业务
 */
public interface NoticeService {
    /**
     * 添加公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    int addNotice(String userName, NoticeAddDTO noticeDTO);

    /**
     * 修改公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    int updateNotice(String userName, NoticeAddDTO noticeDTO);

    /**
     * 查询所有公告信息
     * @param noticeDTO
     * @return
     */
    PageInfo<Notice> pageNotice(NoticeDTO noticeDTO);

    /**
     * 根据语言和公告类别查询启用的公告信息
     * @param noticeDTO
     * @return
     */
    PageInfo<Notice> pageNoticeByLanguageAndCategory(NoticeDTO noticeDTO);
}

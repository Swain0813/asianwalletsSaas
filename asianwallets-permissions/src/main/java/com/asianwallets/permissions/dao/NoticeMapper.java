package com.asianwallets.permissions.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.entity.Notice;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeMapper extends BaseMapper<Notice> {
    /**
     * 查询所有的公告信息
     * @param noticeDTO
     * @return
     */
    List<Notice> pageNotice(NoticeDTO noticeDTO);

    /**
     * 根据语言查询公告信息
     * @param noticeDTO
     * @return
     */
    List<Notice> pageNoticeByLanguageAndCategory(NoticeDTO noticeDTO);
}
package com.asianwallets.base.job;
import com.asianwallets.base.dao.NoticeMapper;
import com.asianwallets.common.entity.Notice;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公告过期用job
 */
@Slf4j
@Transactional
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NoticeInfoJob implements BaseJob {
    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        log.info("---------------------NoticeInfoJob--------------------------------------- jobDataMap :{}", jobDataMap);
        if (jobDataMap.get("noticeId") == null) {
            return;
        }
        String noticeId = jobDataMap.get("noticeId").toString();
        Notice  notice = noticeMapper.selectByPrimaryKey(noticeId);
        //将要过期的公告禁用
        notice.setEnabled(false);
        noticeMapper.updateByPrimaryKeySelective(notice);

    }
}

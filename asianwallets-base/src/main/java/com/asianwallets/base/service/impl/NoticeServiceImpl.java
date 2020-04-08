package com.asianwallets.base.service.impl;
import com.asianwallets.base.job.NoticeInfoJob;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.NoticeAddDTO;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.entity.Notice;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.base.dao.NoticeMapper;
import com.asianwallets.base.service.NoticeService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Calendar;
import java.util.Date;

/**
 * 公告模块的实现类
 **/
@Service
@Transactional
@Slf4j
public class NoticeServiceImpl extends BaseServiceImpl<Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    @Override
    public int addNotice(String userName, NoticeAddDTO noticeDTO){
        //必要的非空check
        if(StringUtils.isEmpty(noticeDTO.getCategory())){//公告类别
            throw new BusinessException(EResultEnum.NOTICE_CATEGORY_IS_NOT_NULL.getCode());
        }
        if(StringUtils.isEmpty(noticeDTO.getLanguage())){//公告语言
            throw new BusinessException(EResultEnum.NOTICE_LANGUAGE_IS_NOT_NULL.getCode());
        }
        if(StringUtils.isEmpty(noticeDTO.getTitle())){//公告标题
            throw new BusinessException(EResultEnum.NOTICE_TITLE_IS_NOT_NULL.getCode());
        }
        if(StringUtils.isEmpty(noticeDTO.getContext())){//公告内容
            throw new BusinessException(EResultEnum.NOTICE_CONTEXT_IS_NOT_NULL.getCode());
        }
        //公告过期时间的设置和判断
        if(noticeDTO.getEndDate().getTime()<= new Date().getTime()){
            throw new BusinessException(EResultEnum.EXPIR_TIME_IS_ERROR.getCode());
        }
        //创建公告对象
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO,notice);
        String noticeId = IDS.uuid2();
        notice.setId(noticeId);//id
        notice.setCreateTime(new Date());//创建时间
        notice.setCreator(userName);//创建人
        notice.setEnabled(true);//启用

        //构建job信息
        String name = noticeId;
        String group = noticeId.concat("_NOTICE_INFO");
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("noticeId", noticeId);
        JobDetail jobDetail = JobBuilder.newJob(NoticeInfoJob.class).withIdentity(name, group).setJobData(jobDataMap).build();
        //表达式调度构建器(即任务执行的时间)
        Date runDate = noticeDTO.getEndDate();
        //根据配置动态生成cron表达式
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(runDate);
        String yyyy = String.valueOf(calendar.get(Calendar.YEAR));
        String mm = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String dd = String.valueOf(calendar.get(Calendar.DATE));
        String HH = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String ss = String.valueOf(calendar.get(Calendar.SECOND));
        //生成 eg:【30 45 10 20 8 2018】格式 固定时间执行任务
        String cronExpression = ss.concat(" ").concat(minute)
                .concat(" ").concat(HH)
                .concat(" ").concat(dd)
                .concat(" ").concat(mm)
                .concat(" ").concat("?")
                .concat(" ").concat(yyyy);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                .withSchedule(scheduleBuilder).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("******添加公告信息时发生异常***********:",e.getMessage());
        }
      return noticeMapper.insert(notice);
    }

    /**
     * 修改公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    @Override
    public int updateNotice(String userName,NoticeAddDTO noticeDTO){
        //公告id的非空check
        if(StringUtils.isEmpty(noticeDTO.getId())){
            throw new BusinessException(EResultEnum.NOTICE_ID_IS_NOT_NULL.getCode());
        }
        //创建公告对象
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO,notice);
        notice.setUpdateTime(new Date());//修改时间
        notice.setModifier(userName);//修改人
        notice.setId(noticeDTO.getId());//公告id
        return noticeMapper.updateByPrimaryKeySelective(notice);
    }

    /**
     * 查询所有公告信息
     * @param noticeDTO
     * @return
     */
    @Override
    public PageInfo<Notice> pageNotice(NoticeDTO noticeDTO){
        //获取当前请求的语言
        noticeDTO.setLanguage(auditorProvider.getLanguage());//设置语言
        return new PageInfo(noticeMapper.pageNotice(noticeDTO));
    }

    /**
     * 根据语言和公告类别查询公告信息
     * @param noticeDTO
     * @return
     */
    @Override
    public PageInfo<Notice> pageNoticeByLanguageAndCategory(NoticeDTO noticeDTO){
        if(noticeDTO.getLanguage()==null){//如果不传语言的值
            //获取当前请求的语言
            noticeDTO.setLanguage(auditorProvider.getLanguage());//设置语言
        }
        //必要的非空check
        if(StringUtils.isEmpty(noticeDTO.getCategory())){//公告类别
            throw new BusinessException(EResultEnum.NOTICE_CATEGORY_IS_NOT_NULL.getCode());
        }
      return new PageInfo(noticeMapper.pageNoticeByLanguageAndCategory(noticeDTO));
    }
}

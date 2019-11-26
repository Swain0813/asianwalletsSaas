package com.asianwallets.base.service.impl;

import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.NoticeDTO;
import com.asianwallets.common.entity.Notice;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.base.dao.NoticeMapper;
import com.asianwallets.base.service.NoticeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 公告模块的实现类
 **/
@Service
@Transactional
public class NoticeServiceImpl extends BaseServiceImpl<Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 添加公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    @Override
    public int addNotice(String userName, NoticeDTO noticeDTO){
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
        //创建公告对象
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO,notice);
        notice.setId(IDS.uuid2());//id
        notice.setCreateTime(new Date());//创建时间
        notice.setCreator(userName);//创建人
        notice.setEnabled(false);//启用
      return noticeMapper.insert(notice);
    }

    /**
     * 修改公告信息
     * @param userName
     * @param noticeDTO
     * @return
     */
    @Override
    public int updateNotice(String userName,NoticeDTO noticeDTO){
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

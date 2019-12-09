package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.MccChannelMapper;
import com.asianwallets.base.dao.MccMapper;
import com.asianwallets.base.service.MccChannelService;
import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.MccChannelVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MccChannelServiceImpl implements MccChannelService {

    @Autowired
    MccChannelMapper mccChannelMapper;

    @Autowired
    MccMapper mccMapper;

    @Autowired
    ChannelMapper channelMapper;

    /**
     * 添加
     *
     * @param mc
     * @return
     */
    @Override
    public int addMccChannel(MccChannelDTO mc) {
        if (StringUtils.isBlank(mc.getCid()) || StringUtils.isBlank(mc.getMid()) || StringUtils.isBlank(mc.getCode())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (mccChannelMapper.selectByCidAndMid(mc) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        if (mccMapper.selectByExtend1(mc.getMid()).size() == 0) {
            throw new BusinessException(EResultEnum.MCC_DOES_NOT_EXIST.getCode());
        }
        if (channelMapper.selectByChannelCode(mc.getCid()) == null) {
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        MccChannel mccChannel = new MccChannel();
        BeanUtils.copyProperties(mc, mccChannel);
        mccChannel.setId(IDS.uniqueID().toString());
        mccChannel.setEnabled(true);
        mccChannel.setCreateTime(new Date());
        return mccChannelMapper.insert(mccChannel);
    }

    /**
     * 分页查询
     *
     * @param mc
     * @return
     */
    @Override
    public PageInfo<MccChannelVO> pageMccChannel(MccChannelDTO mc) {
        mc.setSort("mc.create_time");
        return new PageInfo<>(mccChannelMapper.pageMccChannel(mc));
    }

    /**
     * 禁用启用 映射表
     *
     * @param mc
     * @return
     */
    @Override
    public int banMccChannel(MccChannelDTO mc) {
        if (StringUtils.isBlank(mc.getId()) || mc.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        MccChannel mccChannel = mccChannelMapper.selectByPrimaryKey(mc.getId());
        if (mccChannel == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        mccChannel.setEnabled(mc.getEnabled());
        mccChannel.setModifier(mc.getModifier());
        mccChannel.setUpdateTime(new Date());
        return mccChannelMapper.updateByPrimaryKeySelective(mccChannel);
    }

    /**
     * 查询所有数据
     *
     * @param language
     * @return
     */
    @Override
    public List<MccChannelVO> inquireAllMccChannel(String language) {
        return mccChannelMapper.inquireAllMccChannel(language);
    }

    /**
     * 导入
     *
     * @param list
     * @return
     */
    @Override
    public int importMccChannel(List<MccChannel> list) {
        return mccChannelMapper.insertList(list);
    }

    /**
     * 导出
     *
     * @param mc
     * @return
     */
    @Override
    public List<MccChannelVO> exportMccChannel(MccChannelDTO mc) {
        mc.setPageSize(Integer.MAX_VALUE);
        List<MccChannelVO> voList = mccChannelMapper.pageMccChannel(mc);
        List<MccChannelVO> collect = voList.stream().sorted(Comparator.comparing(MccChannelVO::getCreateTime).reversed()).collect(Collectors.toList());
        for (MccChannelVO mccChannelVO : collect) {
            if (mccChannelVO.getEnabled()) {
                mccChannelVO.setEnabledStr("启用");
            } else {
                mccChannelVO.setEnabledStr("禁用");
            }
        }
        return collect;
    }
}

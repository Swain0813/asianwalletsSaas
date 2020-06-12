package com.asianwallets.rights.service.impl;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.rights.dao.OtaChannelMapper;
import com.asianwallets.rights.service.OtaChannelService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yx
 * @since 2020-01-02
 */
@Service
public class OtaChannelServiceImpl extends BaseServiceImpl<OtaChannel> implements OtaChannelService {

    @Autowired
    private OtaChannelMapper otaChannelMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate OTA平台分页查询
     **/
    @Override
    public PageInfo<OtaChannel> pageOtaChannel(OtaChannelDTO otaChannelDTO) {
        return new PageInfo<OtaChannel>(otaChannelMapper.pageOtaChannel(otaChannelDTO));
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 添加修改OTA平台
     **/
    @Override
    @Transactional
    public int addOtaChannel(OtaChannelDTO otaChannelDTO) {
        //非空check
        if (StringUtils.isEmpty(otaChannelDTO.getReportUrl())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        OtaChannel otaChannel = otaChannelMapper.selectByPrimaryKey(otaChannelDTO.getId());
        int num = 0;
        if (otaChannel == null) {
            otaChannel = new OtaChannel();
            BeanUtils.copyProperties(otaChannelDTO, otaChannel);
            otaChannel.setId(IDS.uniqueID().toString());
            otaChannel.setCreateTime(new Date());
            otaChannel.setCreator(otaChannelDTO.getModifier());
            otaChannel.setEnabled(true);
            num = otaChannelMapper.insert(otaChannel);
        } else {
            otaChannel.setSystemName(otaChannelDTO.getSystemName());
            otaChannel.setCancelDefault(otaChannelDTO.getCancelDefault());
            otaChannel.setCancelUrl(otaChannelDTO.getCancelUrl());
            otaChannel.setReportUrl(otaChannelDTO.getReportUrl());
            otaChannel.setVerificationDefault(otaChannelDTO.getVerificationDefault());
            otaChannel.setVerificationUrl(otaChannelDTO.getVerificationUrl());
            otaChannel.setSystemImg(otaChannelDTO.getSystemImg());
            otaChannel.setEnabled(otaChannelDTO.getEnabled());
            otaChannel.setUpdateTime(new Date());
            otaChannel.setModifier(otaChannelDTO.getModifier());
            num = otaChannelMapper.updateByPrimaryKeySelective(otaChannel);

        }
        return num;
    }

    /**
     * 发放平台的下来框
     * @return
     */
    @Override
    public List<OtaChannel> getOtaChannels() {
        return otaChannelMapper.getOtaChannelLists();
    }

}

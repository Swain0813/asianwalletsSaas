package com.asianwallets.rights.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.github.pagehelper.PageInfo;
import java.util.List;


public interface OtaChannelService extends BaseService<OtaChannel> {

    /**
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate OTA平台分页查询
     * @return
     **/
    PageInfo<OtaChannel> pageOtaChannel(OtaChannelDTO otaChannelDTO);

    /**
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 添加修改OTA平台
     * @return
     **/
    int addOtaChannel(OtaChannelDTO otaChannelDTO);

    /**
     * 查询所有合作的ota的平台并且是启用的
     * @return
     */
    List<OtaChannel> getOtaChannels();
}

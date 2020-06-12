package com.asianwallets.rights.dao;
import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * OTC平台管理表
 */
@Repository
public interface OtaChannelMapper extends  BaseMapper<OtaChannel> {

    /**
     * 根据ota平台名称查询otc平台管理信息
     * @param systemName
     * @return
     */
    OtaChannel selectBySystemName(@Param("systemName") String systemName);


    /**
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate OTA平台分页查询
     * @return
     **/
    List<OtaChannel> pageOtaChannel(OtaChannelDTO otaChannelDTO);

    /**
     * 获取系统所有启用的合作的OTA平台
     * @return
     */
    List<OtaChannel> getOtaChannelLists();
}

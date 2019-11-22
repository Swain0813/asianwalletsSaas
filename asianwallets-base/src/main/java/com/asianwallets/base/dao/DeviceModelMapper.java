package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.entity.DeviceModel;
import com.asianwallets.common.vo.DeviceModelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备型号
 */
@Repository
public interface DeviceModelMapper extends BaseMapper<DeviceModel> {


    /**
     * 查询设备型号信息
     *
     * @param deviceModelDTO
     * @return
     */
    List<DeviceModelVO> pageDeviceModel(DeviceModelDTO deviceModelDTO);

    /**
     * 通过型号id查询厂商个数
     *
     * @param modelId
     * @return
     */
    int selectByModelId(String modelId);

    /**
     * 查询指定的厂商ID下的未被禁用的型号数
     *
     * @param id
     * @param enabled
     * @return
     */
    int selectByVendorId(@Param("id") String id, @Param("enabled") boolean enabled);

    /**
     * 修改指定厂商下的型号启用禁用状态
     *
     * @param id
     * @param enabled
     * @return
     */
    int updateByVendorId(@Param("id") String id, @Param("enabled") boolean enabled);

    /**
     * 查询设备类型
     *
     * @param
     * @return
     */
    List<DeviceModelVO> queryModelCategory();
}
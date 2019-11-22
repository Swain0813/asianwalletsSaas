package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.entity.DeviceInfo;
import com.asianwallets.common.vo.DeviceInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 设备信息
 */
@Repository
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {

    /**
     * 查询设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    List<DeviceInfoVO> pageDeviceInfo(DeviceInfoDTO deviceInfoDTO);

    /**
     * 通过设备信息id查询设备的厂商、型号等信息
     *
     * @param id
     * @return
     */
    DeviceInfoVO selectModelAndVendorAndInfoById(@Param("id") String id);

    /**
     * 通过id更新设备绑定
     *
     * @param id
     * @param bindingStatus
     * @param updateTime
     * @param modifier
     * @return
     */
    int updateById(@Param("id") String id, @Param("bindingStatus") boolean bindingStatus, @Param("updateTime") Date updateTime, @Param("modifier") String modifier);

    /**
     * 通过厂商ID查询设备绑定个数
     *
     * @param id
     * @return
     */
    int selectByVendorId(@Param("id") String id);

    /**
     * 通过型号ID查询设备绑定个数
     *
     * @param id
     * @return
     */
    int selectByModelId(@Param("id") String id);

    /**
     * 通过设备ID查询设备是否被绑定
     *
     * @param id
     * @return
     */
    int selectByInfoId(@Param("id") String id);

    /**
     * 根据型号id与启用禁用状态查询信息个数
     *
     * @param id
     * @param enabled
     * @return
     */
    int selectByModelIdAndStatus(@Param("id") String id, @Param("enabled") boolean enabled);

    /**
     * 根据型号id去修改设备的状态
     *
     * @param id
     * @param enabled
     * @return
     */
    int updateByModelId(@Param("id") String id, @Param("enabled") Boolean enabled);

    /**
     * 判断IMEI号是否重复
     *
     * @param imei
     * @return
     */
    int selectByInfoIMEI(@Param("imei") String imei);
}
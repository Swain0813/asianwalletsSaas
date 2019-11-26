package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.entity.DeviceVendor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备厂商
 */

@Repository
public interface DeviceVendorMapper extends BaseMapper<DeviceVendor> {

    /**
     * 分页查询厂商信息
     *
     * @param deviceVendorDTO
     * @return
     */
    List<DeviceVendor> pageDeviceVendor(DeviceVendorDTO deviceVendorDTO);

    /**
     * 通过厂商id查询未禁用厂商个数
     *
     * @param vendorId
     * @return
     */
    int selectByVendorId(@Param("vendorId") String vendorId);

    /**
     * 查询类别
     *
     * @param
     * @return
     */
    List<DeviceVendor> queryVendorCategory();

    /**
     * 查询厂商的中英文名条数
     *
     * @param deviceVendor
     * @return
     */
    int selectCountByCnNameAndEnName(DeviceVendor deviceVendor);

    /**
     * 启用禁用厂商
     *
     * @param id
     * @param enabled
     * @return
     */
    int banDeviceVendorById(@Param("id") String id, @Param("enabled") Boolean enabled);

}
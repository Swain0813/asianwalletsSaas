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

    List<DeviceVendor> pageDeviceVendor(DeviceVendorDTO deviceVendorDTO);

    /**
     * 通过厂商id查询厂商个数
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
}
package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.DeviceVendor;
import org.springframework.stereotype.Repository;

/**
 * 设备厂商
 */

@Repository
public interface DeviceVendorMapper extends BaseMapper<DeviceVendor> {


    /**
     * 通过厂商名称查询ID
     *
     * @param name
     * @return
     */
    String selectByVendorName(String name);
}
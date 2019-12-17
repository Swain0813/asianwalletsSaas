package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.DeviceBinding;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 设备绑定
 */
@Repository
public interface DeviceBindingMapper extends BaseMapper<DeviceBinding> {

    DeviceBinding selectByMerchantIdAndImei(@Param("merchantId") String merchantId, @Param("imei") String imei);
}
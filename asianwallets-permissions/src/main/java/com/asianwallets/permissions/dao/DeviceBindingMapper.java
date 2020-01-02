package com.asianwallets.permissions.dao;

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

    /**
     * 查询当前机构code和设备号在设备绑定是否绑定
     *
     * @param institutionCode
     * @param imei
     * @return
     */
    @Select("select count(1) from device_binding where institution_code = #{institutionCode} and imei = #{imei} and enabled = 1")
    int selectCountByCodeAndImei(@Param("institutionCode") String institutionCode, @Param("imei") String imei);

    /**
     * 根据商户号和设备编号查询设备绑定信息
     * @param merchantId
     * @param imei
     * @return
     */
    DeviceBinding selectByMerchantIdAndImei(@Param("merchantId") String merchantId, @Param("imei") String imei);
}
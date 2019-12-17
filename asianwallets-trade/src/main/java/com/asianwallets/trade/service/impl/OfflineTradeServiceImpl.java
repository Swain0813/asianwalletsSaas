package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.entity.DeviceBinding;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.service.OfflineTradeService;
import com.asianwallets.trade.vo.CsbDynamicScanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 校验设备,签名信息
     *
     * @param obj 对象
     * @return
     */
//    private void checkDeviceAndSign(String merchantId,String imei,String operatorId) {
//        //获取属性名,属性值
//        Map<String, Object> map = ReflexClazzUtils.getFieldNames(obj);
//        String institutionCode = String.valueOf(map.get("institutionId"));
//        String deviceCode = String.valueOf(map.get("terminalId"));
//        String deviceOperator = String.valueOf(map.get("operatorId"));
//        //查询机构绑定设备
//        DeviceBinding deviceBinding = deviceBindingMapper.selectByInstitutionCodeAndImei(institutionCode, deviceCode);
//        if (deviceBinding == null) {
//            log.info("-----------------【线下业务接口】信息记录--------------【设备编号不合法】");
//            //设备编号不合法
//            throw new BusinessException(EResultEnum.DEVICE_CODE_INVALID.getCode());
//        }
//        //查询设备操作员
//        SysUser sysUser = sysUserMapper.selectByInstitutionCodeAndUserName(institutionCode.concat(deviceOperator));
//        if (sysUser == null) {
//            log.info("-----------------【线下业务接口】信息记录--------------【设备操作员不合法】");
//            //设备操作员不合法
//            throw new BusinessException(EResultEnum.DEVICE_OPERATOR_INVALID.getCode());
//        }
//        //验签
//        if (!commonService.checkOnlineSignMsgUseMD5(obj)) {
//            log.info("-----------------【线下业务接口】信息记录--------------【签名不匹配】");
//            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
//        }
//    }

    /**
     * 线下同机构CSB动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public CsbDynamicScanVO csbDynamicScan(OfflineTradeDTO offlineTradeDTO) {
        log.info("==================【线下CSB动态扫码】==================【请求参数】 offlineTradeDTO: {}", JSON.toJSONString(offlineTradeDTO));
        Orders orders = new Orders();
        orders.setMerchantId(offlineTradeDTO.getMerchantId());
        orders.setMerchantOrderId(offlineTradeDTO.getOrderNo());
        orders.setOrderCurrency(offlineTradeDTO.getOrderCurrency());
        orders.setOrderAmount(offlineTradeDTO.getOrderAmount());
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG(offlineTradeDTO.getOrderTime()));
        orders.setProductCode(offlineTradeDTO.getProductCode());
        orders.setImei(offlineTradeDTO.getImei());
        orders.setOperatorId(offlineTradeDTO.getOperatorId());
        orders.setCreateTime(new Date());
        orders.setCreator(offlineTradeDTO.getMerchantId());
        ordersMapper.insert(orders);
//        throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
//        int i = 1 / 0;
        log.info("==================【线下CSB动态扫码】==================【下单结束】");
        CsbDynamicScanVO csbDynamicScanVO = new CsbDynamicScanVO();
        csbDynamicScanVO.setOrderNo(orders.getMerchantOrderId());
        csbDynamicScanVO.setQrCodeUrl("www.baidu.com");
        csbDynamicScanVO.setDecodeType("0");
        return csbDynamicScanVO;
    }
}

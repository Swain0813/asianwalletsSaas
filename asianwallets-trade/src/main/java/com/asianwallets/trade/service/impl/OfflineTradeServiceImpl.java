package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.entity.DeviceBinding;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.DeviceBindingMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.SysUserMapper;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.OfflineTradeService;
import com.asianwallets.trade.vo.CsbDynamicScanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private DeviceBindingMapper deviceBindingMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 校验设备信息
     *
     * @param merchantId 商户ID
     * @param imei       设备号
     * @param operatorId 操作员ID
     */
    private void checkDevice(String merchantId, String imei, String operatorId) {
        //查询商户绑定设备
        DeviceBinding deviceBinding = deviceBindingMapper.selectByMerchantIdAndImei(merchantId, imei);
        if (deviceBinding == null) {
            log.info("================【线下业务接口】================【设备编号不合法】");
            //设备编号不合法
            throw new BusinessException(EResultEnum.DEVICE_CODE_INVALID.getCode());
        }
        //查询设备操作员
        SysUser sysUser = sysUserMapper.selectByUsername(operatorId.concat(merchantId));
        if (sysUser == null) {
            log.info("================【线下业务接口】================【设备操作员不合法】");
            //设备操作员不合法
            throw new BusinessException(EResultEnum.DEVICE_OPERATOR_INVALID.getCode());
        }
    }

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
        if (!commonBusinessService.repeatedRequests(offlineTradeDTO.getMerchantId(), offlineTradeDTO.getOrderNo())) {
            log.info("==================【线下CSB】下单信息记录==================【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        if (!commonBusinessService.checkOrderCurrency(offlineTradeDTO.getOrderCurrency(), offlineTradeDTO.getOrderAmount())) {
            log.info("==================【线下CSB】下单信息记录==================【订单金额不符合的当前币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
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

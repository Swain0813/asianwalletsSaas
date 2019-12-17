package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.OfflineTradeDTO;
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
    private OrdersMapper ordersMapper;

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

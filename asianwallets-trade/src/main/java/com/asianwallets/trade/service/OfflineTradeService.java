package com.asianwallets.trade.service;
import com.asianwallets.common.dto.PosQueryOrderListDTO;
import com.asianwallets.trade.dto.OfflineCheckOrdersDTO;
import com.asianwallets.trade.dto.OfflineLoginDTO;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.dto.PosGetMerProDTO;
import com.asianwallets.trade.vo.*;

import java.util.List;

public interface OfflineTradeService {

    /**
     * 线下登录
     *
     * @param offlineLoginDTO 线下登录实体
     * @return token
     */
    String login(OfflineLoginDTO offlineLoginDTO);

    /**
     * 线下同机构CSB动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    CsbDynamicScanVO csbDynamicScan(OfflineTradeDTO offlineTradeDTO);

    /**
     * 线下同机构BSC动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构BSC动态扫码输出实体
     */
    BscDynamicScanVO bscDynamicScan(OfflineTradeDTO offlineTradeDTO);

    /**
     * 线下查询订单列表【对外API】
     *
     * @param offlineCheckOrdersDTO 查询订单输入实体
     * @return 订单集合
     */
    List<OfflineCheckOrdersVO> checkOrder(OfflineCheckOrdersDTO offlineCheckOrdersDTO);

    /**
     * POS机查询商户产品,币种信息
     *
     * @param posGetMerProDTO POS机查询商户产品信息输入实体
     * @return POS机查询商户产品, 币种信息输出实体集合
     */
    PosMerProCurVO posGetMerPro(PosGetMerProDTO posGetMerProDTO);

    /**
     * POS机查询订单列表信息
     *
     * @param posQueryOrderListDTO POS机查询订单接口输入实体
     * @return 订单列表
     */
    List<PosQueryOrderListVO> posQueryOrderList(PosQueryOrderListDTO posQueryOrderListDTO);

    /**
     * POS机查询订单详情
     *
     * @param posQueryOrderListDTO POS机查询订单详情输入实体
     * @return 订单
     */
    PosQueryOrderListVO posQueryOrderDetail(PosQueryOrderListDTO posQueryOrderListDTO);
}

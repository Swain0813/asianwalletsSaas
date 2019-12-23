package com.asianwallets.trade.service;

import com.asianwallets.common.entity.*;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.vo.BasicInfoVO;

import java.math.BigDecimal;

/**
 * 通用业务接口
 */
public interface CommonBusinessService {

    /**
     * 线下
     * 校验MD5签名
     *
     * @param obj 验签对象
     * @return 布尔值
     */
    boolean checkSignByMd5(Object obj);

    /**
     * 通用签名校验
     *
     * @param obj
     * @return boolean
     */
    boolean checkUniversalSign(Object obj);

    /**
     * 换汇计算
     *
     * @param localCurrency   本币
     * @param foreignCurrency 外币
     * @param floatRate       浮动率
     * @param amount          金额
     * @return 换汇输出实体
     */
    CalcExchangeRateVO calcExchangeRate(String localCurrency, String foreignCurrency, BigDecimal floatRate, BigDecimal amount);

    /**
     * 换汇计算
     *
     * @param basicInfoVO 基础信息
     * @param orders
     * @return 换汇输出实体
     */
    void calcExchangeRateBak(BasicInfoVO basicInfoVO, Orders orders);

    /**
     * 校验重复请求【线上与线下下单】
     *
     * @param merchantId      商户编号
     * @param merchantOrderId 商户订单号
     * @return 布尔值
     */
    boolean repeatedRequests(String merchantId, String merchantOrderId);

    /**
     * 校验订单币种是否支持与默认值【线上与线下下单】
     *
     * @param orderCurrency 订单币种
     * @param orderAmount   订单金额
     * @return 布尔值
     */
    boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount);

    /**
     * 校验商户产品与通道的限额【线上与线下下单】
     *
     * @param orders          订单
     * @param merchantProduct 商户产品
     * @param channel         通道
     */
    void checkQuota(Orders orders, MerchantProduct merchantProduct, Channel channel);

    /**
     * 截取Url
     *
     * @param serverUrl 服务器回调地址
     * @param orders    订单
     */
    void getUrl(String serverUrl, Orders orders);

    /**
     * 计算手续费
     *
     * @param basicInfoVO
     * @param orders
     */
    void calculateCost(BasicInfoVO basicInfoVO, Orders orders);

    /**
     * 计算通道网关手续费
     *
     * @param orders  订单
     * @param channel 通道
     */
    void CalcGatewayFee(Orders orders, Channel channel);

    /**
     * 退款和撤销成功的场合
     *
     * @param orderRefund
     */
    void updateOrderRefundSuccess(OrderRefund orderRefund);

    /**
     * 退款和撤销失败的场合
     *
     * @param orderRefund
     */
    void updateOrderRefundFail(OrderRefund orderRefund);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate 创建调账单
     **/
    Reconciliation createReconciliation(String type, OrderRefund orderRefund, String remark);
}

package com.asianwallets.trade.service;

import com.asianwallets.common.entity.*;
import com.asianwallets.common.vo.CalcExchangeRateVO;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.CalcFeeVO;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * 通用业务接口
 */
public interface CommonBusinessService {

    /**
     * 使用机构对应平台的RSA私钥生成签名【回调时用】
     *
     * @param obj 对象
     * @return 签名
     */
    String generateSignatureUsePlatRSA(Object obj);

    /**
     * 使用机构对应平台的MD5生成签名【回调时用】
     *
     * @param obj 对象
     * @return 签名
     */
    String generateSignatureUsePlatMD5(Object obj);

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
     * @param obj 验签实体
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
     * 下单换汇【线上与线下下单】
     *
     * @param basicInfoVO 基础信息
     * @param orders      订单实体
     * @return 换汇输出实体
     */
    void swapRateByPayment(BasicInfoVO basicInfoVO, Orders orders);

    /**
     * 预授权下单的换汇输出实体
     * @param basicInfoVO
     * @param preOrders
     */
    void swapRateByPreOrders(BasicInfoVO basicInfoVO,PreOrders preOrders);

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
     * @param currency      币种
     * @return 布尔值
     */
    boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount, Currency currency);

    /**
     * 校验商户产品与通道的限额【线上与线下下单】
     *
     * @param orders          订单
     * @param merchantProduct 商户产品
     * @param channel         通道
     */
    void checkQuota(Orders orders, MerchantProduct merchantProduct, Channel channel);

    /**
     * 预授权下单时的校验商户产品与通道的限额
     * @param preOrders
     * @param merchantProduct
     * @param channel
     */
    void checkPreQuota(PreOrders preOrders,MerchantProduct merchantProduct, Channel channel);

    /**
     * 截取币种默认值
     *
     * @param orders   订单
     * @param currency 币种
     */
    void interceptDigit(Orders orders, Currency currency);

    /**
     * 预授权下单截取币种默认值
     * @param preOrders
     * @param currency
     */
    void interceptPreDigit(PreOrders preOrders,Currency currency);

    /**
     * 截取Url
     *
     * @param serverUrl 服务器回调地址
     * @param orders    订单
     */
    void getUrl(String serverUrl, Orders orders);

    /**
     * 预授权获取url
     * @param serverUrl
     * @param preOrders
     */
    void getPreUrl(String serverUrl, PreOrders preOrders);

    /**
     * 计算手续费
     *
     * @param basicInfoVO 基础信息实体
     * @param orders      订单实体
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
     * @param orderRefund 退款订单实体
     */
    void updateOrderRefundSuccess(OrderRefund orderRefund);

    /**
     * 退款和撤销失败的场合
     *
     * @param orderRefund 退款订单实体
     */
    void updateOrderRefundFail(OrderRefund orderRefund);

    /**
     * @return Reconciliation
     * @Author YangXu
     * @Date 2019/12/20
     * @Description 创建调账单
     **/
    Reconciliation createReconciliation(String type, OrderRefund orderRefund, String remark);

    /**
     * 创建商户对应币种的账户
     *
     * @param orders 订单
     */
    void createAccount(Orders orders);

    /**
     * 配置限额限次信息
     *
     * @param merchantId  商户编号
     * @param productCode 产品编号
     * @param amount      金额
     */
    void quota(String merchantId, Integer productCode, BigDecimal amount);

    /**
     * 回调时计算通道网关手续费【回调交易成功时收取】
     *
     * @param orders orders
     */
    void calcCallBackGatewayFeeSuccess(Orders orders);

    /**
     * 回调时计算通道网关手续费【回调交易失败时收取】     *
     *
     * @param orders orders
     */
    void calcCallBackGatewayFeeFailed(Orders orders);

    /**
     * 计算通道网关手续费【回调时用】
     *
     * @param amount  订单金额
     * @param channel 通道
     * @return CalcFeeVO  通道费用输出实体
     */
    CalcFeeVO calcChannelGatewayPoundage(BigDecimal amount, Channel channel);

    /**
     * 回调商户服务器地址【回调接口】
     *
     * @param orders 订单
     */
    void replyReturnUrl(Orders orders);

    /**
     * 调用商户浏览器回调接口
     * @param orders
     */
    void replyBrowserUrl(Orders orders);

    /**
     * 重定向用户jumpUrl
     *
     * @param orders
     * @param response
     */
    void replyJumpUrl(Orders orders, HttpServletResponse response);

    /**
     * 支付成功发送邮件给付款人
     *
     * @param orders 订单
     */
    void sendEmail(Orders orders);
    /**
     * @Author YangXu
     * @Date 2020/3/24
     * @Descripate 退还收单手续费的时候是否调分润
     * @return
     **/
    void refundShareBinifit(OrderRefund orderRefund);
}

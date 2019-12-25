package com.asianwallets.common.vo.clearing;
import com.asianwallets.common.entity.OrderRefund;
import lombok.Data;
import java.io.Serializable;

/**
 * 清结算系统资金冻结/解冻请求实体类
 */
@Data
public class FinancialFreezeDTO implements Serializable{
   private static final long serialVersionUID = 1L;

   /**
    * 系统编号
    */
   private String id;

   /**
    * 商户号
    */
   private String merchantId;

   /**
    * 商户订单号
    */
   private String merOrderNo;

   /**
    * 交易币种
    */
   private String txncurrency;

   /**
    * 交易金额
    */
   private double txnamount;

   /**
    * 状态：1加冻结，2解冻结
    */
   private int state;

   /**
    * 备注(state=1的时候表示加冻结描述，state=2的时候表示解冻结描述)
    */
   private String desc;

   /**
    * 签名信息
    */
   private String signMsg;

   /**
    * 应答code
    */
   private String respCode;

   /**
    * 应答消息
    */
   private String respMsg;

   public FinancialFreezeDTO() {
   }

   /**
    *退款用
    * @param state
    * @param orderRefund
    */
   public FinancialFreezeDTO(int state, OrderRefund orderRefund) {
      this.merchantId = orderRefund.getMerchantId();
      this.merOrderNo = orderRefund.getId();
      this.txncurrency = orderRefund.getOrderCurrency();
      this.txnamount = -1*orderRefund.getOrderAmount().add(orderRefund.getRefundFee()).subtract(orderRefund.getRefundOrderFee()).doubleValue();
      this.state = state;
      this.desc = "";
   }
}

package com.asianwallets.common.vo.clearing;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

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
    * 虚拟账户编号
    */
   private String mvaccountId;

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
    * 加冻结和解冻结用
    * @param reconciliation
    * @param account
    */
   public FinancialFreezeDTO(Reconciliation reconciliation, Account account) {
      this.id = reconciliation.getId();
      this.merchantId = reconciliation.getMerchantId();
      this.mvaccountId = account.getId();//账户 id
      this.desc = reconciliation.getReconciliationType() == 3 ? "加冻结" : "解冻结";
      this.merOrderNo = reconciliation.getId();
      this.txncurrency = reconciliation.getCurrency();
      //订单金额,2位
      this.txnamount =reconciliation.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
      //资金变动--3冻结 4 解冻 ||清结算--1:冻结2:解冻
      this.state = reconciliation.getReconciliationType() == 3 ? 1 : 2;
   }

   /**
    * 预约冻结用
    * @param tcsFrozenFundsLogs
    */
   public FinancialFreezeDTO(TcsFrozenFundsLogs tcsFrozenFundsLogs){
      this.id =tcsFrozenFundsLogs.getId();
      this.merchantId=tcsFrozenFundsLogs.getMerchantId();
      this.mvaccountId=tcsFrozenFundsLogs.getMvaccountId();
      this.desc = "预约冻结";
      this.merOrderNo =tcsFrozenFundsLogs.getMerOrderNo();
      this.txncurrency=tcsFrozenFundsLogs.getTxncurrency();
      this.txnamount=tcsFrozenFundsLogs.getTxnamount();
      //加冻结
      this.state=1;
   }


}

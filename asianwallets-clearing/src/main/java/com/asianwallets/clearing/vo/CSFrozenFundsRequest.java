package com.asianwallets.clearing.vo;
import lombok.Data;
import java.io.Serializable;

/**
* @Package :com.cbpay.bean.pojo.CSAPI
* @ClassName:CSFrozenFundsRequest
* @Author :admin
* @Date:2018年7月24日 下午1:51:42
* @Desc:清结算系统资金冻结/解冻请求实体类
*/
@Data
public class CSFrozenFundsRequest implements Serializable{
   private static final long serialVersionUID = 1L;

   private String version;//版本号(v1.0)

   private int inputCharset;//字符集(1:UTF-8)

   private int language;//语言(1:简体中文)

   private String id;//系统编号

   private String merchantId;//商户号

   private String merOrderNo;//商户订单号

   private String txncurrency;//交易币种

   private double txnamount;//交易金额

   private String mvaccountId;//虚拟账户编号

   private int state;//状态：1加冻结，2解冻结

   private String desc;//备注(state=1的时候表示加冻结描述，state=2的时候表示解冻结描述)

   private String signMsg;//签名信息

   private String respCode;//应答code

   private String respMsg;//应答消息


}

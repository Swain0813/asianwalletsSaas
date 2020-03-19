package com.asianwallets.trade.config;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * ad3配置参数读取类
 */
@Data
@Configuration("ad3ParamsConfig")
public class AD3ParamsConfig {

    @ApiModelProperty("AD3系统的url地址")
    @Value("${custom.ad3Url}")
    private String ad3Url;

    @ApiModelProperty("AD3系统商户号")
    @Value("${custom.merchantCode}")
    private String merchantCode;

    @ApiModelProperty("AD3系统操作员id")
    @Value("${custom.operatorId}")
    private String operatorId;

    @ApiModelProperty("AD3系统imei编号")
    @Value("${custom.imei}")
    private String imei;

    @ApiModelProperty("AD3系统登录密码")
    @Value("${custom.password}")
    private String password;

    @ApiModelProperty("AD3系统交易密码")
    @Value("${custom.tradePwd}")
    private String tradePassword;

    @ApiModelProperty("AD3系统私钥")
    @Value("${custom.platformProvidesPrivateKey}")
    private String platformProvidesPrivateKey;//私钥

    @ApiModelProperty("AD3签名方式")
    @Value("${custom.merchantSignType}")
    private String merchantSignType;//签名方式

    @ApiModelProperty("AD3回调地址")
    @Value("${custom.channelCallbackUrl}")
    private String channelCallbackUrl;

    @ApiModelProperty("亚洲钱包的支付成功页面的url")
    @Value("${custom.paySuccessUrl}")
    private String paySuccessUrl;

    @ApiModelProperty("ad3ItsUrl")
    @Value("${custom.ad3ItsUrl}")
    private String ad3ItsUrl;

    @ApiModelProperty("nextPosUrl")
    @Value("${custom.nextPosUrl}")
    private String nextPosUrl;

    @ApiModelProperty("AD3线下商户号")
    @Value("${custom.merchantCodeOffline}")
    private String merchantCodeOffline;

    @ApiModelProperty("AD3线下imei")
    @Value("${custom.imeiOffline}")
    private String imeiOffline;

    @ApiModelProperty("AD3线下操作员id")
    @Value("${custom.operatorIdOffline}")
    private String operatorIdOffline;

    @ApiModelProperty("AD3线下登录密码")
    @Value("${custom.passwordOffline}")
    private String passwordOffline;

    @ApiModelProperty("AD3线下交易密码")
    @Value("${custom.tradePwdOffline}")
    private String tradePwdOffline;
}


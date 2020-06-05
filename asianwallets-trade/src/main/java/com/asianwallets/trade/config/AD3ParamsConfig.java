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

    @ApiModelProperty("upi私钥")
    @Value("${custom.upi.privateKeyPath}")
    private String upiPrivateKeyPath;

    @ApiModelProperty("upi公钥")
    @Value("${custom.upi.publicKeyPath}")
    private String upiPublicKeyPath;

}


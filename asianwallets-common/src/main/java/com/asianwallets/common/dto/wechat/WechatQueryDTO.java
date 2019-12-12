package com.asianwallets.common.dto.wechat;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.utils.UUIDHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 微信查询请求实体
 * @author: XuWenQi
 * @create: 2019-06-28 10:48
 **/
@Data
@ApiModel(value = "微信查询请求实体", description = "微信查询请求实体")
public class WechatQueryDTO {

    @ApiModelProperty(value = "公众号id")
    private String appid;

    @ApiModelProperty(value = "")
    private String sign_type;

    @ApiModelProperty(value = "")
    private String mch_id;

    @ApiModelProperty(value = "")
    private String sub_mch_id;

    @ApiModelProperty(value = "")
    private String nonce_str;

    @ApiModelProperty(value = "")
    private String out_trade_no;

    @ApiModelProperty(value = "")
    private String md5KeyStr;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public WechatQueryDTO() {
    }

    public WechatQueryDTO(String id, Channel channel) {
        this.appid = "wx14e049b9320bccca";
        this.sign_type = "MD5";
        this.mch_id = channel.getChannelMerchantId();
        this.sub_mch_id = "66104046";
        this.nonce_str = UUIDHelper.getRandomString(32);
        this.out_trade_no = id;
        this.md5KeyStr = channel.getMd5KeyStr();
        this.channel = channel;
    }

    public static void main(String[] args) {
        System.out.println(UUIDHelper.getRandomString(32));
    }
}

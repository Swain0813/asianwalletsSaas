package com.asianwallets.common.dto.megapay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: nextPos通道请求实体
 * @author: YangXu
 * @create: 2019-06-12 13:47
 **/
@Data
@ApiModel(value = "nextPos通道请求实体", description = "nextPos通道请求实体")
public class NextPosRequestDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String einv;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "")
    private String return_url;

    @ApiModelProperty(value = "产品名")
    private String product;
    private String merRespPassword;
    private String merRespID;

    //以下不是上报通道参数
    @ApiModelProperty(value = "订单id")
    private String institutionOrderId;

    @ApiModelProperty(value = "ip")
    private String reqIp;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public NextPosRequestDTO() {
    }

    public NextPosRequestDTO(Orders orders, Channel channel, String retURL) {
        this.merID = channel.getChannelMerchantId();//商户号
        this.einv = orders.getId();//订单号
        //标价金额,外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
        this.amt = String.valueOf(orders.getTradeAmount());//订单金额
        this.return_url = retURL;
        this.product = orders.getCommodityName();
        this.institutionOrderId = orders.getInstitutionOrderId();
        this.reqIp = orders.getReqIp();
        this.merRespID = channel.getPayCode();
        this.merRespPassword = channel.getMd5KeyStr();
        this.channel = channel;
    }

//    public static void main(String[] args) throws IOException {
//        NextPosRequestDTO nextPosRequestDTO = new NextPosRequestDTO();
//        nextPosRequestDTO.setMerID("8758766");
//        nextPosRequestDTO.setEinv("O905991272750108674");
//        nextPosRequestDTO.setAmt("11");
//        nextPosRequestDTO.setReturn_url("http://192.168.124.31:9004/onlinecallback/nextPostCallback");
//        nextPosRequestDTO.setProduct("商户");
////        HttpResponse httpResponse = HttpClientUtils.reqPost("https://www.nextpos.asia/thaiqrstring/qrstring.aspx", nextPosRequestDTO, null);
////        System.out.println(httpResponse);
//        PostMethod post = new PostMethod("https://www.nextpos.asia/thaiqrstring/qrstring.aspx");
//        HttpClient httpClient = new HttpClient();
//        NameValuePair[] param = {
//                new NameValuePair("merID", "8758766"),//商户号
//                new NameValuePair("einv", "O905991272750108677"),//订单号
//                new NameValuePair("amt", "11.00"),//金额
//                new NameValuePair("product", "商户"),//产品名
//                new NameValuePair("return_url", "http://192.168.124.31:9004/onlinecallback/nextPostCallback")//接受异步通知的URL
//        };
//        post.setRequestBody(param);
//
//        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
//        int stats = 0;
//        stats = httpClient.executeMethod(post);
//        System.out.println(stats);
//        String respstr = new String(post.getResponseBody(), StandardCharsets.UTF_8);
//        System.out.println(respstr);
//    }
}

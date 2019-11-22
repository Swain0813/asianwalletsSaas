package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "Xe汇率查询接口输出实体", description = "Xe汇率查询接口输出实体")
public class XeRateVO {

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "terms")
    private String terms;

    @ApiModelProperty(value = "privacy")
    private String privacy;

    @ApiModelProperty(value = "本位币种")
    private String from;

    @ApiModelProperty(value = "备注")
    private List<XeRateResponseVO> to;

    @ApiModelProperty(value = "时间")
    private String timestamp;

//    public static void main(String[] args) {
//        String accountId = "personaltestuse17617711";
//        String apiKey = "cejpv7epaok9pqbtrne0pvtuml";
//
////        String historyRateUrl = "https://xecdapi.xe.com/v1/historic_rate/";
////        Map<String, Object> paramMap = new HashMap<>();
////        paramMap.put("from", "SGD");
////        paramMap.put("to", "USD");
////        paramMap.put("date", DateToolUtils.getReqDate());
////        Map<String, Object> headerMap = new HashMap<>();
////        org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
////        String encodedUsernamePassword = base64.encodeAsString((accountId + ":" + apiKey).getBytes());
////        headerMap.put("Authorization", "Basic " + encodedUsernamePassword);
////        JSONObject json = HttpClientUtils.reqGet(historyRateUrl, paramMap, headerMap);
////        System.out.println(json);
//
//
//        String convertRateUrl = "https://xecdapi.xe.com/v1/convert_from/";
//        StringBuilder sb = new StringBuilder();
//        sb.append(convertRateUrl).append("?").append("from=").append("CNY&");
//        List<String> toList = new ArrayList<>();
//        toList.add("SGD");
//        toList.add("USD");
//        toList.add("MYR");
//        sb.append("to=");
//        for (int i = 0; i < toList.size(); i++) {
//            if (i == toList.size() - 1) {
//                sb.append(toList.get(i));
//            } else {
//                sb.append(toList.get(i)).append(",");
//            }
//        }
//        String url = sb.toString();
//        System.out.println(url);
//        Map<String, Object> headerMap = new HashMap<>();
//        Base64 base64 = new Base64();
//        String encodedUsernamePassword = base64.encodeAsString((accountId + ":" + apiKey).getBytes());
//        headerMap.put("Authorization", "Basic " + encodedUsernamePassword);
//        headerMap.put("Content_Type", "application/json");
//        String json = HttpClientUtils.reqGetString(url, null, headerMap);
//        XeRateVO xeRateVO = JSONObject.parseObject(json, XeRateVO.class);
//        System.out.println(xeRateVO);
//    }
}

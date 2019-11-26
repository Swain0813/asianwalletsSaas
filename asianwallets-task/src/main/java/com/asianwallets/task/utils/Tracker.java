package com.asianwallets.task.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * trackingmore的配置和共通
 */

public class Tracker {
    /**
     * 国外服务器(xuwq@asianwallets.com)
     */
    private String Apikey = "ba2680a4-c0d6-4813-96d9-c0e8c864195e";
    /**
     * Json
     */
    public String orderOnlineByJson(String requestData, String urlStr, String type) {
        //---headerParams
        Map<String, String> headerparams = new HashMap<String, String>();
        headerparams.put("Trackingmore-Api-Key", Apikey);
        headerparams.put("Content-Type", "application/json");
        //---bodyParams
        List<String> bodyParams = new ArrayList<String>();
        String result = null;
        if (type.equals("post")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings/post";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("get")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings/get";
            String RelUrl = ReqURL + urlStr;
            result = sendPost(RelUrl, headerparams, bodyParams, "GET");

        } else if (type.equals("batch")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings/batch";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");
        } else if (type.equals("codeNumberGet")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings";
            String RelUrl = ReqURL + urlStr;
            result = sendGet(RelUrl, headerparams, "GET");
        } else if (type.equals("codeNumberPut")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings";
            bodyParams.add(requestData);
            String RelUrl = ReqURL + urlStr;
            result = sendPost(RelUrl, headerparams, bodyParams, "PUT");
        } else if (type.equals("codeNumberDelete")) {
            String ReqURL = "http://api.trackingmore.com/v2/trackings";
            String RelUrl = ReqURL + urlStr;
            result = sendGet(RelUrl, headerparams, "DELETE");

        } else if (type.equals("realtime")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/realtime";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("carriers")) {

            String ReqURL = "http://api.trackingmore.com/v2/carriers";
            result = sendGet(ReqURL, headerparams, "GET");

        } else if (type.equals("carriers/detect")) {

            String ReqURL = "http://api.trackingmore.com/v2/carriers/detect";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("update")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/update";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("getuserinfo")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/getuserinfo";
            result = sendGet(ReqURL, headerparams, "GET");

        } else if (type.equals("getstatusnumber")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/getstatusnumber";
            result = sendGet(ReqURL, headerparams, "GET");

        } else if (type.equals("notupdate")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/notupdate";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("remote")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/remote";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("costtime")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/costtime";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("delete")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/delete";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("updatemore")) {

            String ReqURL = "http://api.trackingmore.com/v2/trackings/updatemore";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        }
        return result;
    }


    private String sendPost(String url, Map<String, String> headerParams, List<String> bodyParams, String mothod) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.connect();

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            StringBuffer sbBody = new StringBuffer();
            for (String str : bodyParams) {
                sbBody.append(str);
            }
            out.write(sbBody.toString());

            out.flush();

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String sendGet(String url, Map<String, String> headerParams, String mothod) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();

            connection.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        //列出所有运输商以及在TrackingMore系统中相应运输商简码
//        String urlStr = null;
//        String requestData = null;
//        String result = new Tracker().orderOnlineByJson(requestData, urlStr, "carriers");
//        System.out.println(result);

        //Detect a carrier by tracking code
//        String urlStr = null;
//        String requestData = "{\"tracking_number\":\"EA152563251CN\"}";
//        String result = new Tracker().orderOnlineByJson(requestData, urlStr, "carriers/detect");
//        System.out.println(result);

        //获取多个运单号的物流信息
//        List<String> numbers = new ArrayList<>();
//        numbers.add("221562075186");
////        //numbers.add("70791996710609");
////        //numbers.add("70791996710609");
////        //String urlStr = "?page=1&limit=100&created_at_min=1521314361&created_at_max=1541314361";
////        //String urlStr = "?numbers=";
//        StringBuilder sb = new StringBuilder();
//        //sb.append("?status=notfound");
//        sb.append("?numbers=");
//        for (int i = 0; i < numbers.size(); i++) {
//            if (i < numbers.size() - 1) {
//                sb.append(numbers.get(i)).append(",");
//            } else {
//                sb.append(numbers.get(i));
//            }
//        }
//        String urlStr = sb.toString();
//        System.out.println(urlStr);
//        String requestData = null;
//        String result = new Tracker().orderOnlineByJson(requestData, urlStr, "get");
//        TrackingMoreVO trackingMoreVO = JSON.parseObject(result, TrackingMoreVO.class);
//        System.out.println(trackingMoreVO);

        //创建单个运单号
//        String urlStr =null;
//        String requestData= "{\"tracking_number\": \"EA152563254CN\",\"carrier_code\":\"china-ems\",\"title\":\"chase chen\",\"customer_name\":\"chase\",\"customer_email\":\"abc@qq.com\",\"order_id\":\"#123\",\"order_create_time\":\"2018-05-20 12:00\",\"destination_code\":\"IL\",\"tracking_ship_date\":\"1521314361\",\"tracking_postal_code\":\"13ES20\",\"lang\":\"en\",\"logistics_channel\":\"4PX page\"}";
//        String result = new Tracker().orderOnlineByJson(requestData,urlStr,"post");
//        TrackingMoreVO trackingMoreVO = JSON.parseObject(result, TrackingMoreVO.class);
//        System.out.println(result);
//        System.out.println(trackingMoreVO);

        //创建多个运单号
//        String urlStr = null;
//        String requestData = "[{\"tracking_number\": \"121112131312123\"}]";
//        String result = new Tracker().orderOnlineByJson(requestData, urlStr, "batch");
////        List<TrackingMoreVO> trackingMoreVOS = JSON.parseArray(result, TrackingMoreVO.class);
////        System.out.println(trackingMoreVOS);
//        System.out.println(result);
    }

}


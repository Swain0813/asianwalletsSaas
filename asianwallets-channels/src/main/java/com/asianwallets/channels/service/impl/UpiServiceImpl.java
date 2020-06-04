package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.service.UpiService;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.utils.CryptoUtil;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 14:35
 **/
@Service
@Slf4j
public class UpiServiceImpl implements UpiService {

    @Autowired
    private ChannelsConfig channelsConfig;


    @Override
    public BaseResponse upiPay(UpiDTO upiDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");

            log.info("===============【upi支付】===============【请求参数】 UpiDTO: {}", JSON.toJSONString(upiDTO.getUpiPayDTO()));
            String plainXML = JSON.toJSONString(upiDTO.getUpiPayDTO());
            byte[] plainBytes = plainXML.getBytes("UTF-8");
            String keyStr = getRandom(16);
            byte[] keyBytes = keyStr.getBytes("UTF-8");
            byte[] base64EncryptDataBytes = Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null));
            String encryptData = new String(base64EncryptDataBytes, "UTF-8");
            byte[] base64SingDataBytes = Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA"));
            String signData = new String(base64SingDataBytes, "UTF-8");
            byte[] base64EncyrptKeyBytes = Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding"));
            String encrtptKey = new String(base64EncyrptKeyBytes, "UTF-8");

            Map<String, String> map = new HashMap<String, String>();
            map.put("encryptData", encryptData);
            map.put("encryptKey", encrtptKey);
            map.put("agencyId", upiDTO.getChannel().getChannelMerchantId());
            map.put("signData", signData);
            log.info("===============【upi支付】===============【请求参数】 map: {}", JSON.toJSONString(map));
            HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getUpiPayUrl(), map, null);
            log.info("===============【upi支付】===============【请求参数】 httpResponse: {}", JSON.toJSONString(httpResponse));
            String result = respDecryption(httpResponse.getJsonObject(), hzfPriKey, yhPubKey);
            log.info("===============【upi支付】===============【请求参数】 result: {}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (httpResponse.getHttpStatus().equals(TradeConstant.HTTP_SUCCESS) && jsonObject.get("resp_code").equals("0000")) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(result);
            } else {
                //失败
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/3
     * @Descripate upi查询
     * @return
     **/
    @Override
    public BaseResponse upiQueery(UpiDTO upiDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");

            log.info("===============【upi查询】===============【请求参数】 UpiDTO: {}", JSON.toJSONString(upiDTO.getUpiPayDTO()));
            String plainXML = JSON.toJSONString(upiDTO.getUpiPayDTO());
            byte[] plainBytes = plainXML.getBytes("UTF-8");
            String keyStr = getRandom(16);
            byte[] keyBytes = keyStr.getBytes("UTF-8");
            byte[] base64EncryptDataBytes = Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null));
            String encryptData = new String(base64EncryptDataBytes, "UTF-8");
            byte[] base64SingDataBytes = Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA"));
            String signData = new String(base64SingDataBytes, "UTF-8");
            byte[] base64EncyrptKeyBytes = Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding"));
            String encrtptKey = new String(base64EncyrptKeyBytes, "UTF-8");

            Map<String, String> map = new HashMap<String, String>();
            map.put("encryptData", encryptData);
            map.put("encryptKey", encrtptKey);
            map.put("agencyId", upiDTO.getChannel().getChannelMerchantId());
            map.put("signData", signData);
            log.info("===============【upi查询】===============【请求参数】 map: {}", JSON.toJSONString(map));
            HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getUpiPayUrl(), map, null);
            log.info("===============【upi查询】===============【请求参数】 httpResponse: {}", JSON.toJSONString(httpResponse));
            String result = respDecryption(httpResponse.getJsonObject(), hzfPriKey, yhPubKey);
            log.info("===============【upi查询】===============【请求参数】 result: {}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (httpResponse.getHttpStatus().equals(TradeConstant.HTTP_SUCCESS) && jsonObject.get("resp_code").equals("0000")) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(result);
            } else {
                //失败
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/3
     * @Descripate 退款
     * @return
     **/
    @Override
    public BaseResponse upiRefund(UpiDTO upiDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");

            log.info("===============【upi退款】===============【请求参数】 UpiDTO: {}", JSON.toJSONString(upiDTO.getUpiRefundDTO()));
            String plainXML = JSON.toJSONString(upiDTO.getUpiRefundDTO());
            byte[] plainBytes = plainXML.getBytes("UTF-8");
            String keyStr = getRandom(16);
            byte[] keyBytes = keyStr.getBytes("UTF-8");
            byte[] base64EncryptDataBytes = Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null));
            String encryptData = new String(base64EncryptDataBytes, "UTF-8");
            byte[] base64SingDataBytes = Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA"));
            String signData = new String(base64SingDataBytes, "UTF-8");
            byte[] base64EncyrptKeyBytes = Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding"));
            String encrtptKey = new String(base64EncyrptKeyBytes, "UTF-8");

            Map<String, String> map = new HashMap<String, String>();
            map.put("encryptData", encryptData);
            map.put("encryptKey", encrtptKey);
            map.put("agencyId", upiDTO.getChannel().getChannelMerchantId());
            map.put("signData", signData);
            log.info("===============【upi退款】===============【请求参数】 map: {}", JSON.toJSONString(map));
            HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getUpiPayUrl(), map, null);
            log.info("===============【upi退款】===============【请求参数】 httpResponse: {}", JSON.toJSONString(httpResponse));
            String result = respDecryption(httpResponse.getJsonObject(), hzfPriKey, yhPubKey);
            log.info("===============【upi退款】===============【请求参数】 result: {}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (httpResponse.getHttpStatus().equals(TradeConstant.HTTP_SUCCESS) && jsonObject.get("resp_code").equals("0000")) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(result);
            } else {
                //失败
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/3
     * @Descripate 订单撤销
     * @return
     **/
    @Override
    public BaseResponse upiCancel(UpiDTO upiDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");

            log.info("===============【upi撤销】===============【请求参数】 UpiDTO: {}", JSON.toJSONString(upiDTO.getUpiRefundDTO()));
            String plainXML = JSON.toJSONString(upiDTO.getUpiRefundDTO());
            byte[] plainBytes = plainXML.getBytes("UTF-8");
            String keyStr = getRandom(16);
            byte[] keyBytes = keyStr.getBytes("UTF-8");
            byte[] base64EncryptDataBytes = Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null));
            String encryptData = new String(base64EncryptDataBytes, "UTF-8");
            byte[] base64SingDataBytes = Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA"));
            String signData = new String(base64SingDataBytes, "UTF-8");
            byte[] base64EncyrptKeyBytes = Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding"));
            String encrtptKey = new String(base64EncyrptKeyBytes, "UTF-8");

            Map<String, String> map = new HashMap<String, String>();
            map.put("encryptData", encryptData);
            map.put("encryptKey", encrtptKey);
            map.put("agencyId", upiDTO.getChannel().getChannelMerchantId());
            map.put("signData", signData);
            log.info("===============【upi撤销】===============【请求参数】 map: {}", JSON.toJSONString(map));
            HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getUpiPayUrl(), map, null);
            log.info("===============【upi撤销】===============【请求参数】 httpResponse: {}", JSON.toJSONString(httpResponse));
            String result = respDecryption(httpResponse.getJsonObject(), hzfPriKey, yhPubKey);
            log.info("===============【upi撤销】===============【请求参数】 result: {}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (httpResponse.getHttpStatus().equals(TradeConstant.HTTP_SUCCESS) && jsonObject.get("resp_code").equals("0000")) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(result);
            } else {
                //失败
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/3
     * @Descripate 下载对账文件
     * @return
     **/
    @Override
    public BaseResponse upiDownSettle(UpiDTO upiDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");

            log.info("===============【upi下载对账文件】===============【请求参数】 UpiDTO: {}", JSON.toJSONString(upiDTO.getUpiDownDTO()));
            String plainXML = JSON.toJSONString(upiDTO.getUpiDownDTO());
            byte[] plainBytes = plainXML.getBytes("UTF-8");
            String keyStr = getRandom(16);
            byte[] keyBytes = keyStr.getBytes("UTF-8");
            byte[] base64EncryptDataBytes = Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null));
            String encryptData = new String(base64EncryptDataBytes, "UTF-8");
            byte[] base64SingDataBytes = Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA"));
            String signData = new String(base64SingDataBytes, "UTF-8");
            byte[] base64EncyrptKeyBytes = Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding"));
            String encrtptKey = new String(base64EncyrptKeyBytes, "UTF-8");

            Map<String, String> map = new HashMap<String, String>();
            map.put("encryptData", encryptData);
            map.put("encryptKey", encrtptKey);
            map.put("agencyId", upiDTO.getChannel().getChannelMerchantId());
            map.put("signData", signData);
            log.info("===============【upi下载对账文件】===============【请求参数】 map: {}", JSON.toJSONString(map));
            HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getUpiPayUrl(), map, null);
            log.info("===============【upi下载对账文件】===============【请求参数】 httpResponse: {}", JSON.toJSONString(httpResponse));
            String result = respDecryption(httpResponse.getJsonObject(), hzfPriKey, yhPubKey);
            log.info("===============【upi下载对账文件】===============【请求参数】 result: {}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (httpResponse.getHttpStatus().equals(TradeConstant.HTTP_SUCCESS) && jsonObject.get("resp_code").equals("0000")) {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(result);
            } else {
                //失败
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    public static String getRandom(int length) {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            boolean isChar = (random.nextInt(2) % 2 == 0);// 输出字母还是数字
            if (isChar) { // 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                ret.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                ret.append(Integer.toString(random.nextInt(10)));
            }
        }
        return ret.toString();
    }

    public String respDecryption(JSONObject jsonObject, PrivateKey hzfPriKey, PublicKey yhPubKey) {
        String xmlData = "";
        try {
            byte[] encryptedBytes = Base64.decodeBase64(jsonObject.getString("encryptKey").toString().getBytes("UTF-8"));
            //			String encrtptKey = new String(encryptedBytes, "UTF-8");
            byte[] keyBytes = CryptoUtil.RSADecrypt(encryptedBytes, hzfPriKey, 2048, 11, "RSA/ECB/PKCS1Padding");
            byte[] plainBytes = Base64.decodeBase64(jsonObject.getString("encryptData").toString().getBytes("UTF-8"));
            byte[] xmlBytes = CryptoUtil.AESDecrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null);
            xmlData = new String(xmlBytes, "UTF-8");

            boolean signResult = CryptoUtil.verifyDigitalSign(xmlBytes, Base64.decodeBase64(jsonObject.getString("signData")), yhPubKey, "SHA1WithRSA");

            if (!signResult) {
                throw new Exception("sign error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlData;
    }
}

package com.asianwallets.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
public class ChannelsSignUtils {

    /**
     * 生成vtc回调签名
     *
     * @param object
     * @param md5KeyStr
     * @return
     */
    public static String getVtcSign(Object object, String md5KeyStr) {
        Map<String, Object> objectMap = ReflexClazzUtils.getFieldNames(object);
        HashMap<String, String> paramMap = new HashMap<>();
        for (String str : objectMap.keySet()) {
            paramMap.put(str, String.valueOf(objectMap.get(str)));
        }
        Map<String, String> map = new TreeMap<>(String::compareTo);
        Set<String> set = paramMap.keySet();
        for (String key : set) {
            map.put(key, paramMap.get(key));
        }
        map.put("institutionOrderId", null);
        map.put("reqIp", null);
        map.put("md5KeyStr", null);
        map.put("signature", null);
        StringBuffer sb = new StringBuffer();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String val = map.get(key);
            if (!StringUtils.isEmpty(val)) {
                sb.append(val.trim() + "|");
            }
        }
        String reSignStr = sb.toString() + md5KeyStr; //待签名的字符串
        log.info("签名前的明文:{}", reSignStr);
        String sign = Sha256Tools.encrypt(reSignStr).toUpperCase();
        return sign;
    }

}

package com.asianwallets.common.cache;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
public class CommonLanguageCacheService {
    private static final Log log = LogFactory.getLog(CommonLanguageCacheService.class);
    private Cache<String, HashMap> metaLanguageCache = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();

    private String path="data";

    public HashMap getLanguage(String language) {
        try {
            return metaLanguageCache.get(language, new Callable<HashMap>() {
                @Override
                public HashMap call() throws Exception {
                    String filePath = null;
                    switch (language) {
                        case "zh-cn":
                            filePath = path.concat("/").concat("ErrorMessage.json");
                            break;
                        case "zh-hk":
                            filePath = path.concat("/").concat("ErrorMessageHK.json");
                            break;
                        case "en-us":
                            filePath =  path.concat("/").concat("ErrorMessageEN.json");
                            break;
                        case "vn":
                            filePath = path.concat("/").concat("ErrorMessageYL.json");
                            break;
                        case "jp":
                            filePath = path.concat("/").concat("ErrorMessageJP.json");
                            break;
                        default:
                            filePath = path.concat("/").concat("ErrorMessage.json");
                            break;
                    }
                    return readData(filePath);
                }
            });
        } catch (Exception e) {
        }
        return null;
    }

    private HashMap readData(String path) {
        HashMap result = Maps.newHashMap();
        try {
            if (Objects.nonNull(path)) {
                StringBuffer buffer = new StringBuffer();
                InputStream resourceAsStream = CommonLanguageCacheService.class.getClassLoader().getResourceAsStream(path);
                InputStreamReader isr = new InputStreamReader(resourceAsStream, "UTF-8");
                BufferedReader in = new BufferedReader(isr);
                String line = "";

                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }

                String text = buffer.toString();
                result = JSON.parseObject(text, HashMap.class);


            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return result;
    }
}

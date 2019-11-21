package com.asianwallets.common.config;

import com.asianwallets.common.utils.GetIpAddr;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

@Component
public class AuditorProvider {
    /**
     * 获取当前请求的语言环境
     *
     * @return
     */
    public String getLanguage() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String language = "en-us";
        try {
            if (Objects.nonNull(request) && Objects.nonNull(request.getHeader("Content-Language"))) {
                language = request.getHeader("Content-Language");
            }
            if (Objects.nonNull(language)) {
                return language.split(",")[0];
            }
        } catch (Exception e) {
            return Locale.getDefault().getLanguage();
        }
        return language;
    }

    /**
     * 获取当前请求的ip
     *
     * @return
     */
    public String getReqIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return GetIpAddr.getIpAddr(request);
    }
}

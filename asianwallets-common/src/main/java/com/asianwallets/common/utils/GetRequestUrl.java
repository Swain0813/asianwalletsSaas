package com.asianwallets.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求的url
 */
public class GetRequestUrl {

    /**
     * 获取请求的url
     * @param request
     */
    public static String getRequstUrl(HttpServletRequest request){
       return request.getRequestURL().toString();
    }
}

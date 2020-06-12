package com.asianwallets.rights.service;

/**
 * 通用方法
 */
public interface CommonService {

    /**
     * 通用签名校验
     *
     * @param obj 验签实体
     * @return boolean
     */
    boolean checkUniversalSign(Object obj);
}

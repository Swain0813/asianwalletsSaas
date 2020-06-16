package com.asianwallets.rights.service;
import com.asianwallets.common.entity.RightsUserGrant;

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

    /**
     * 权益发送需要的用的短信或者邮件发送
     * @param rightsUserGrant
     */
    void sendMobileAndEmail(RightsUserGrant rightsUserGrant);
}

package com.asianwallets.rights.service.impl;

import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.rights.service.CommonRedisService;
import com.asianwallets.rights.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

/**
 * 通用方法
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonRedisService commonRedisService;

    /**
     * 通用签名校验
     *
     * @param obj 验签实体
     * @return boolean
     */
    @Override
    public boolean checkUniversalSign(Object obj) {
        log.info("===============【通用签名校验方法】===============【验签开始】");
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        String sign = map.get("sign");
        String signType = map.get("signType");
        String merchantId = map.get("merchantId");
        if (sign == null || "".equals(sign)) {
            throw new BusinessException(EResultEnum.SIGNATURE_CANNOT_BE_EMPTY.getCode());
        }
        Attestation attestation = commonRedisService.getAttestationInfo(merchantId);
        map.put("sign", null);
        if (signType.equals(TradeConstant.RSA)) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] signMsg = decoder.decode(sign);
            byte[] data = SignTools.getSignStr(map).getBytes();
            try {
                return RSAUtils.verify(data, signMsg, attestation.getPubkey());
            } catch (Exception e) {
                log.info("-----------  【通用签名校验】 RSA校验发生错误----------商户id:{},签名signMsg:{}", merchantId, signMsg);
            }
        } else if (signType.equals(TradeConstant.MD5)) {
            String str = SignTools.getSignStr(map) + attestation.getMd5key();
            log.info("----------【通用签名校验】 MD5加密前明文----------str:{}", str);
            String decryptSign = MD5Util.getMD5String(str);
            return sign.equalsIgnoreCase(decryptSign);
        }
        log.info("===============【通用签名校验方法】===============【验签结束】");
        return false;
    }
}

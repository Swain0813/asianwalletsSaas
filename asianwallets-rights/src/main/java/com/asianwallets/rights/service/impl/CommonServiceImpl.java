package com.asianwallets.rights.service.impl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.rights.feign.message.MessageFeign;
import com.asianwallets.rights.service.CommonRedisService;
import com.asianwallets.rights.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用方法
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonRedisService commonRedisService;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private AuditorProvider auditorProvider;

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
        String merchantId = map.get("institutionId");
        if (sign == null || "".equals(sign)) {
            throw new BusinessException(EResultEnum.SIGNATURE_CANNOT_BE_EMPTY.getCode());
        }
        Attestation attestation = commonRedisService.getAttestationInfo((map.get("institutionId")));
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

    /**
     *权益发送需要的用的短信或者邮件发送
     * @param rightsUserGrant
     */
    @Override
    @Async
    public void sendMobileAndEmail(RightsUserGrant rightsUserGrant) {
        log.info("*********************发邮件和短信 Start*************************************");
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            //获得时间
            String time = DateUtil.getCurrentDate() + " " + DateUtil.getCurrentTime();
            map.put("date", time);
            //票券编号
            map.put("ticketId",rightsUserGrant.getTicketId());
            //内容
            map.put("content",rightsUserGrant.getContent());
             if(!StringUtils.isEmpty(rightsUserGrant.getMobileNo())) {
                 //调用发送短信
                 messageFeign.sendSimple(rightsUserGrant.getMobileNo(),"恭喜你获得优惠券:"+rightsUserGrant.getTicketId()+"/n"+rightsUserGrant.getContent());
             }else {
                 //调用发送邮件
                 messageFeign.sendTemplateMail(rightsUserGrant.getEmail(), auditorProvider.getLanguage(), Status._4, map);
             }
        }catch (Exception e){
            //发生异常向固定手机号发短信
            messageFeign.sendSimple("18800330943","权益业务调用邮件和短信发生异常");
            log.error("权益业务发短信和邮件服务发生异常：{}==={}==={}==={}===",e);
        }
        log.info("*********************发邮件和短信 End*************************************");
    }

}

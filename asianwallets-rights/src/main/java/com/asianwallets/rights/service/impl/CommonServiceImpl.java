package com.asianwallets.rights.service.impl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.RightsConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.entity.ShortUrl;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import com.asianwallets.rights.dao.ShortUrlMapper;
import com.asianwallets.rights.feign.message.MessageFeign;
import com.asianwallets.rights.service.CommonRedisService;
import com.asianwallets.rights.service.CommonService;
import com.asianwallets.rights.utils.QrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

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

    public static final String IMAGES_DIR = "/imagesaas/";

    @Value("${file.http.server}")
    private String fileHttpServer;

    @Value("${file.upload.path}")
    private String fileUploadPath;


    @Value("${file.http.rghServer}")
    private String rghServer;

    @Autowired
    private ShortUrlMapper shortUrlMapper;

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
            //商户名称
            map.put("merchantName", rightsUserGrant.getMerchantName());
            //活动主题
            map.put("activityTheme", rightsUserGrant.getActivityTheme());
            //活动内容
            if(rightsUserGrant.getRightsType()== RightsConstant.FULL_DISCOUNT){
                //满减的场合
                if(rightsUserGrant.getCapAmount()!=null && rightsUserGrant.getCapAmount().compareTo(BigDecimal.ZERO)==1){
                    map.put("content", "满"+rightsUserGrant.getFullReductionAmount()+"减"+rightsUserGrant.getTicketAmount()+","+rightsUserGrant.getCapAmount()+"封顶");
                }else {
                    map.put("content", "满"+rightsUserGrant.getFullReductionAmount()+"减"+rightsUserGrant.getTicketAmount()+","+"上不封顶");
                }
            }else if(rightsUserGrant.getRightsType()==RightsConstant.DISCOUNT){
                //折扣的场合
                if(rightsUserGrant.getDiscount()!=null && rightsUserGrant.getDiscount().compareTo(BigDecimal.ZERO)==1){
                    map.put("content", rightsUserGrant.getDiscount().multiply(new BigDecimal(10))+"折优惠,"+rightsUserGrant.getCapAmount()+"封顶");
                }else {
                    map.put("content", rightsUserGrant.getDiscount().multiply(new BigDecimal(10))+"折优惠,"+rightsUserGrant.getCapAmount()+"上不封顶");
                }
            }else if(rightsUserGrant.getRightsType()==RightsConstant.PACKAGE){
                //套餐的场合
                map.put("content", rightsUserGrant.getPackageValue()+"套餐");
            }else {
               //定额
                map.put("content", rightsUserGrant.getTicketAmount()+"优惠券");
            }
            //票券的二维码
            String fileName= IMAGES_DIR.concat(DateUtil.getCurrentDate()).concat("/").concat(UUID.randomUUID().toString()).concat(".png");
            String imagePath = fileUploadPath.concat(fileName);
            createDir(imagePath);
            QrCodeUtil.generateQrCodeAndSave(rightsUserGrant.getTicketId(),"png",350,350,imagePath);
            //票券的二维码真正存放的服务器地址
            String qrCodeUrl= fileHttpServer.concat(fileName);
            //票券编号
            map.put("ticketId",rightsUserGrant.getTicketId());
            //可用时间
            map.put("startTime",DateToolUtils.formatTimestamp.format(rightsUserGrant.getStartTime()));
            map.put("endTime",DateToolUtils.formatTimestamp.format(rightsUserGrant.getEndTime()));
            //不可用时间
            map.put("unusableTime",rightsUserGrant.getExt4());
            //使用规则
            map.put("ruleDescription",rightsUserGrant.getRuleDescription());
            //商家地址
            map.put("shopAddresses",rightsUserGrant.getShopAddresses());
             if(!StringUtils.isEmpty(rightsUserGrant.getMobileNo())) {
                 //长链接和短链接的存库
                 ShortUrl shortUrl = new  ShortUrl();
                 shortUrl.setId(IDS.uniqueID().toString());
                 //长链接
                 shortUrl.setUrl(qrCodeUrl);
                 String ss = ShortUrlUtil.getShortUrl(qrCodeUrl);
                 String sUrl = rghServer.concat(ss);
                 shortUrl.setShortUrl(ss);
                 shortUrl.setCreateTime(new Date());
                 int result = shortUrlMapper.insert(shortUrl);
                 if(result>0){
                     //调用发送短信
                     map.put("ticketQrCode",sUrl);
                     messageFeign.sendSimpleTemplate(auditorProvider.getLanguage(),Status._4,rightsUserGrant.getMobileNo(),map);
                 }
             }else {
                 //调用发送邮件
                 map.put("ticketQrCode", qrCodeUrl);
                 messageFeign.sendTemplateMail(rightsUserGrant.getEmail(), auditorProvider.getLanguage(), Status._4, map);
             }
        }catch (Exception e){
            //发生异常向固定手机号发短信
            messageFeign.sendSimple("18800330943","权益业务调用邮件和短信发生异常");
            log.error("权益业务发短信和邮件服务发生异常：{}==={}==={}==={}===",e);
        }
        log.info("*********************发邮件和短信 End*************************************");
    }

    /**
     * 创建目录
     * @param path
     */
    private void createDir(String path) {
        File fileDir = new File(path);
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
    }
}

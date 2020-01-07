package com.asianwallets.trade;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.AttestationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.feign.ClearingFeign;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;


@SpringBootTest(classes = TradeApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TradeApplicationTests {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private ClearingFeign clearingFeign;

    @Autowired
    private AttestationMapper attestationMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Test
    public void test() {
//        channelsFeign.xenditPay(null);
//        clearingFeign.intoAndOutMerhtAccount(new FundChangeDTO());
//        messageFeign.sendSimple("", "");
        rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, "O981694484956463");
    }

    @Test
    public void test666() {
        String content = "test中文";
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String s = new String(key);
        System.out.println(s);
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);

        //加密
        byte[] encrypt = aes.encrypt(content);
        //解密
        byte[] decrypt = aes.decrypt(encrypt);

        //加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        System.out.println("encryptHex = " + encryptHex);
        //解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println("decryptStr = " + decryptStr);
    }

    @Test
    public void test1() {
        Attestation attestation = new Attestation();
        attestation.setId(IDS.uuid2());//id
        Map<String, String> rsaMap;
        try {
            rsaMap = RSAUtils.initKey();
        } catch (Exception e) {
//            log.info("---------生成RSA公私钥错误---------");
            throw new BusinessException(EResultEnum.KEY_GENERATION_FAILED.getCode());
        }
        attestation.setInstitutionId("sdfasdfadf");
        attestation.setMerchantId("sfasfdasdfa");
        attestation.setPubkey(rsaMap.get("publicKey"));
        attestation.setPrikey(rsaMap.get("privateKey"));
        attestation.setEnabled(true);
        attestation.setMd5key(IDS.uuid2());
        attestation.setCreator("fasdfadfs");
        attestation.setCreateTime(new Date());
        attestationMapper.insert(attestation);
    }


}

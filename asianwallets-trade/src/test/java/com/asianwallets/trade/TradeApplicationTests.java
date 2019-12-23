package com.asianwallets.trade;

import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.trade.dao.AttestationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.feign.ClearingFeign;
import com.asianwallets.trade.feign.MessageFeign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TradeApplication.class)
public class TradeApplicationTests {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private ClearingFeign clearingFeign;

    @Autowired
    private AttestationMapper attestationMapper;

    @Test
    public void test() {
        channelsFeign.xenditPay(null);
        clearingFeign.intoAndOutMerhtAccount(null);
        messageFeign.sendSimple("", "");
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

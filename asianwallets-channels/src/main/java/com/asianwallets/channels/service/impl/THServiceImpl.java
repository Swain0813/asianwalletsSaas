package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.service.THService;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ISO8583Util;
import com.asianwallets.common.dto.th.ISO8583.NumberStringUtil;
import com.asianwallets.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-07 11:01
 **/
@Slf4j
@Service
public class THServiceImpl implements THService {

    @Autowired
    private ChannelsConfig channelsConfig;

    /**
     * 通华CSB
     *
     * @param iso8583DTO
     * @return
     */
    @Override
    public BaseResponse thCSB(ISO8583DTO iso8583DTO) {
        return null;
    }

    /**
     * 通华BSC
     *
     * @param iso8583DTO
     * @return
     */
    @Override
    public BaseResponse thBSC(ISO8583DTO iso8583DTO) {
        return null;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华退款
     **/
    @Override
    public BaseResponse thRefund(ISO8583DTO iso8583DTO) {
        BaseResponse response = new BaseResponse();
        log.info("===============【通华退款接口】===============【请求参数】 iso8583DTO:{}", JSON.toJSONString(iso8583DTO));
        String tdpu = channelsConfig.getThTDPU();
        String header = channelsConfig.getThHeader();
        //商户号
        String merchNum = "852999958120501";
        //终端号
        String terminalNum = "00018644";
        //机构号
        String institutionNum = "000000008600005";
        //业务类型
        String businessTypes = "00000000";
        try {
            String sendMsg = tdpu + header + NumberStringUtil.str2HexStr(merchNum + terminalNum + institutionNum + businessTypes + merchNum)
                    + ISO8583Util.packISO8583DTO(iso8583DTO);
            //计算报文长度
            String strHex2 = String.format("%04x",sendMsg.length()/2);
            sendMsg = strHex2 + sendMsg;
            log.info("===============【通华退款接口】===============【请求报文参数】 sendMsg:{}",sendMsg);
            Map<String, String> respMap = ISO8583Util.sendTCPRequest(channelsConfig.getThIp(), channelsConfig.getThPort(), NumberStringUtil.str2Bcd(sendMsg));
            String result = respMap.get("respData");
            log.info("===============【通华退款接口】===============【返回报文参数】 result:{}",result);
            //解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(result);
            log.info("===============【通华退款接口】===============【返回参数】 iso8583DTO1281:{}", JSON.toJSONString(iso8583DTO1281));

        } catch (Exception e) {
            log.info("===============【通华退款接口】===============【异常】 e:{}", e);
        }
        return response;
    }
}

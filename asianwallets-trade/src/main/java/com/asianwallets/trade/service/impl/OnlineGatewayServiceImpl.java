package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.entity.InstitutionRequestParameters;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.OnlineGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
public class OnlineGatewayServiceImpl implements OnlineGatewayService {

    @Autowired
    private Help2PayService help2PayService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    @Override
    public OnlineTradeVO gateway(OnlineTradeDTO onlineTradeDTO) {

        //判断
        if (!StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {
            //直连
            return directConnection(onlineTradeDTO);
        }
        //间连
        return indirectConnection(onlineTradeDTO);

        /*help2PayService.onlinePay(null,null);

        try {
            ChannelsAbstract channelsAbstract = (Help2PayServiceImpl)Class.forName("com.asianwallets.trade.channels.help2pay.impl.Help2PayServiceImpl").newInstance();

            channelsAbstract.offlineBSC(null,null,null);
        }catch (Exception e) {
            e.printStackTrace();
        }


        if (StringUtils.isEmpty(onlineTradeDTO.getIssuerId())) {

        }
        return null;*/
    }

    /**
     * 间连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private OnlineTradeVO indirectConnection(OnlineTradeDTO onlineTradeDTO) {
        return null;
    }

    /**
     * 直连
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    private OnlineTradeVO directConnection(OnlineTradeDTO onlineTradeDTO) {
        //信息落地
        log.info("---------------【线上直连收单输入实体】---------------OnlineTradeDTO:{}", JSON.toJSONString(onlineTradeDTO));
        //重复请求
        if (!commonBusinessService.repeatedRequests(onlineTradeDTO.getMerchantId(), onlineTradeDTO.getOrderNo())) {
            log.info("-----------------【线上直连】下单信息记录--------------【重复请求】");
            throw new BusinessException(EResultEnum.REPEAT_ORDER_REQUEST.getCode());
        }
        //检查币种默认值
        if (!commonBusinessService.checkOrderCurrency(onlineTradeDTO.getOrderCurrency(), onlineTradeDTO.getOrderAmount())) {
            log.info("-----------------【线上直连】下单信息记录--------------【订单金额不符合的当前币种默认值】");
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }

        //可选参数校验


        //签名校验
        if (!commonBusinessService.checkUniversalSign(onlineTradeDTO)) {
            log.info("-----------------【线上直连】下单信息记录--------------【签名错误】");
            throw new BusinessException(EResultEnum.SIGNATURE_ERROR.getCode());
        }
        //获取商户信息

        return null;
    }


    /**
     * 校验请求参数
     *
     * @param onlineTradeDTO
     * @param institutionRequestParameters 机构请求参数实体
     */
    private void checkRequestParameters(OnlineTradeDTO onlineTradeDTO, InstitutionRequestParameters institutionRequestParameters) {
        if (institutionRequestParameters.getBrowserUrl() && StringUtils.isEmpty(onlineTradeDTO.getBrowserUrl())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductName() && StringUtils.isEmpty(onlineTradeDTO.getProductName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getProductDescription() && StringUtils.isEmpty(onlineTradeDTO.getProductDescription())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerName() && StringUtils.isEmpty(onlineTradeDTO.getPayerName())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerPhone() && StringUtils.isEmpty(onlineTradeDTO.getPayerPhone())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerEmail() && StringUtils.isEmpty(onlineTradeDTO.getPayerEmail())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getPayerBank() && StringUtils.isEmpty(onlineTradeDTO.getPayerBank())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getLanguage() && StringUtils.isEmpty(onlineTradeDTO.getLanguage())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark1() && StringUtils.isEmpty(onlineTradeDTO.getRemark1())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark2() && StringUtils.isEmpty(onlineTradeDTO.getRemark2())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (institutionRequestParameters.getRemark3() && StringUtils.isEmpty(onlineTradeDTO.getRemark3())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
    }
}

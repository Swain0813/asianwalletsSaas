package com.asianwallets.trade.service.impl;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ProductMapper;
import com.asianwallets.trade.dao.ShareBenefitLogsMapper;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.ShareBenefitService;
import com.asianwallets.trade.vo.BasicInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sun.management.resources.agent;

import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-03 14:47
 **/
@Slf4j
@Service
@Transactional
public class ShareBenefitServiceImpl implements ShareBenefitService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ShareBenefitLogsMapper shareBenefitLogsMapper;
    @Autowired
    private CommonRedisDataService commonRedisDataService;
    @Autowired
    private ProductMapper productMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/8/23
     * @Descripate 插入分润流水
     **/
    @Override
    public void insertShareBenefitLogs(String orderId) {
        log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== orderId: 【{}】", orderId);
        try {
            Integer type;
            if (orderId.startsWith("O")) {
                //订单号以O开头是收款单
                type = 1;
            } else {
                type = 2;
            }
            //商户代理商户号
            String merchantAgencyCode = null;
            //通道代理商户号
            String channelAgencyCode = null;
            //产品code
            Integer productCode = null;
            Orders orders = null;
            //OrderPayment orderPayment = null;

            if (type == 1) {
                /***************************************** 收款单的场合 ****************************************/
                orders = ordersMapper.selectByPrimaryKey(orderId);
                merchantAgencyCode = orders.getAgentCode();
                channelAgencyCode = orders.getRemark8();
                productCode = orders.getProductCode();

            } else {
                /***************************************** 付款单的场合 ****************************************/

                //TODO

            }

            /********************************************* 商户代理分润 ****************************************/
            if (StringUtils.isEmpty(merchantAgencyCode)) {
                Merchant merchantAgency = commonRedisDataService.getMerchantById(merchantAgencyCode);
                //查询分润流水是否存在当前订单信息
                int count = shareBenefitLogsMapper.selectCountByOrderId(orderId, "2");
                if (count > 0) {
                    log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== 【商户分润记录已存在】orderId: 【{}】", orderId);
                    return;
                }
                BasicInfoVO basicInfoVO = this.getBasicInfo(merchantAgency, productCode);
                //创建流水对象
                ShareBenefitLogs shareBenefitLogs = this.createShareBenefitLogs(type, "2", orders, null, basicInfoVO);


            }
            /********************************************* 通道代理分润 ****************************************/
            if (StringUtils.isEmpty(channelAgencyCode)) {
                Merchant channelAgency = commonRedisDataService.getMerchantById(channelAgencyCode);
                //查询分润流水是否存在当前订单信息
                int count = shareBenefitLogsMapper.selectCountByOrderId(orderId, "1");
                if (count > 0) {
                    log.error("================== 【insertShareBenefitLogs 通道代理分润】=================== 【通道分润记录已存在】orderId: 【{}】", orderId);
                    return;
                }
                BasicInfoVO basicInfoVO = this.getBasicInfo(channelAgency, productCode);
                //创建流水对象
                ShareBenefitLogs shareBenefitLogs = this.createShareBenefitLogs(type, "1", orders, null, basicInfoVO);
            }



        } catch (Exception e) {
            log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== 【异常】 orderId: 【{}】,Exception :【{}】", orderId, e);
            //rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orderId);
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 创建流水对象
     **/
    private ShareBenefitLogs createShareBenefitLogs(Integer type, String agentType, Orders orders, Object object, BasicInfoVO basicInfoVO) {
        ShareBenefitLogs shareBenefitLogs = new ShareBenefitLogs();
        shareBenefitLogs.setId("SL" + IDS.uniqueID());
        if (type.equals("1")) {
            shareBenefitLogs.setOrderId(orders.getId());
            shareBenefitLogs.setInstitutionId(orders.getInstitutionId());
            shareBenefitLogs.setInstitutionName(orders.getInstitutionName());
            shareBenefitLogs.setMerchantName(orders.getMerchantName());
            shareBenefitLogs.setMerchantId(orders.getMerchantId());
            shareBenefitLogs.setChannelCode(basicInfoVO.getChannel().getChannelCode());
            shareBenefitLogs.setChannelName(basicInfoVO.getChannel().getChannelCnName());
            shareBenefitLogs.setTradeCurrency(orders.getTradeCurrency());
            shareBenefitLogs.setTradeAmount(orders.getTradeAmount());
        } else {
            //TODO
            shareBenefitLogs.setOrderId("");
            shareBenefitLogs.setInstitutionId("");
            shareBenefitLogs.setInstitutionName("");
            shareBenefitLogs.setMerchantName("");
            shareBenefitLogs.setMerchantId("");
            shareBenefitLogs.setChannelCode("");
            shareBenefitLogs.setChannelName("");
            shareBenefitLogs.setTradeCurrency("");
            shareBenefitLogs.setTradeAmount(BigDecimal.ZERO);

        }
        shareBenefitLogs.setAgentId(basicInfoVO.getMerchant().getId());
        shareBenefitLogs.setAgentName(basicInfoVO.getMerchant().getCnName());
        shareBenefitLogs.setAgentType(agentType);
        shareBenefitLogs.setOrderType(type);

        //shareBenefitLogs.setFee(new BigDecimal("0"));
        //shareBenefitLogs.setShareBenefit(new BigDecimal("0"));

        shareBenefitLogs.setIsShare(TradeConstant.SHARE_BENEFIT_WAIT);
        shareBenefitLogs.setDividedMode(basicInfoVO.getMerchantProduct().getDividedMode());
        shareBenefitLogs.setDividedRatio(basicInfoVO.getMerchantProduct().getDividedRatio());
        shareBenefitLogs.setCreateTime(new Date());
        return shareBenefitLogs;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 查询基础信息
     **/
    private BasicInfoVO getBasicInfo(Merchant agency, Integer productCode) {
        log.error("================== 【getBasicInfo 查询基础信息】=================== agency:【{}】，productCode: 【{}】", JSON.toJSONString(agency), productCode);
        BasicInfoVO basicInfoVO = new BasicInfoVO();
        basicInfoVO.setMerchant(agency);
        //根据productCode，机构id以及订单收付类型查询产品信息
        Product product = commonRedisDataService.getProductByCode(productCode);
        basicInfoVO.setProduct(product);
        MerchantProduct merchantProduct = commonRedisDataService.getMerProByMerIdAndProId(agency.getId(), product.getId());
        basicInfoVO.setMerchantProduct(merchantProduct);
        log.error("================== 【getBasicInfo 查询基础信息】=================== basicInfoVO:【{}】", JSON.toJSONString(basicInfoVO));
        return basicInfoVO;
    }


}

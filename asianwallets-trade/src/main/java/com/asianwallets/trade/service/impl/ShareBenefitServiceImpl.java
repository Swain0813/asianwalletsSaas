package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ShareBenefitLogsMapper;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.ShareBenefitService;
import com.asianwallets.trade.vo.BasicInfoVO;
import com.asianwallets.trade.vo.CalcFeeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-03 14:47
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ShareBenefitServiceImpl implements ShareBenefitService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ShareBenefitLogsMapper shareBenefitLogsMapper;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Value("${custom.warning.email}")
    private String developerEmail;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private RabbitMQSender rabbitMQSender;


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
            int count = shareBenefitLogsMapper.selectCountByOrderId(orderId, "2");
            log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== 商户代理分润 count: 【{}】,merchantAgencyCode:【{}】", count, merchantAgencyCode);
            if (count == 0 && StringUtils.isNotEmpty(merchantAgencyCode)) {
                Merchant merchantAgency = commonRedisDataService.getMerchantById(merchantAgencyCode);
                BasicInfoVO basicInfoVO = this.getBasicInfo(merchantAgency, productCode);
                //创建流水对象
                ShareBenefitLogs shareBenefitLogs = this.createShareBenefitLogs(type, "2", orders, null, basicInfoVO);
                //计算分润
                CalcFeeVO calcFeeVO = this.calculateShareBenefit(type, orders, null, basicInfoVO.getMerchantProduct(), shareBenefitLogs);
                if (calcFeeVO.getChargeStatus().equals(TradeConstant.CHARGE_STATUS_FALID)) {
                    messageFeign.sendSimpleMail(developerEmail, "代理商产品算费失败 预警", "代理商商户号 ：{ " + merchantAgencyCode + " } ，订单号 ：{ " + orderId + " } 代理商产品算费失败");
                    throw new BusinessException(EResultEnum.ERROR.getCode());
                }
                //分润金额
                shareBenefitLogs.setShareBenefit(calcFeeVO.getFee());
                shareBenefitLogsMapper.insert(shareBenefitLogs);
            }
            /********************************************* 通道代理分润 ****************************************/
            int count1 = shareBenefitLogsMapper.selectCountByOrderId(orderId, "1");
            log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== 通道代理分润 count1: 【{}】,channelAgencyCode:【{}】", count1, channelAgencyCode);
            if (count1 == 0 && StringUtils.isNotEmpty(channelAgencyCode)) {
                Merchant channelAgency = commonRedisDataService.getMerchantById(channelAgencyCode);
                //查询分润流水是否存在当前订单信息
                BasicInfoVO basicInfoVO = this.getBasicInfo(channelAgency, productCode);
                //创建流水对象
                ShareBenefitLogs shareBenefitLogs = this.createShareBenefitLogs(type, "1", orders, null, basicInfoVO);
                //计算分润
                CalcFeeVO calcFeeVO = this.calculateShareBenefit(type, orders, null, basicInfoVO.getMerchantProduct(), shareBenefitLogs);
                if (calcFeeVO.getChargeStatus().equals(TradeConstant.CHARGE_STATUS_FALID)) {
                    messageFeign.sendSimpleMail(developerEmail, "代理商产品算费失败 预警", "代理商商户号 ：{ " + channelAgencyCode + " } ，订单号 ：{ " + orderId + " } 代理商产品算费失败");
                    throw new BusinessException(EResultEnum.ERROR.getCode());
                }
                //分润金额
                shareBenefitLogs.setShareBenefit(calcFeeVO.getFee());
                shareBenefitLogsMapper.insert(shareBenefitLogs);
            }

        } catch (Exception e) {
            log.error("================== 【insertShareBenefitLogs 插入分润流水】=================== 【异常】 orderId: 【{}】,Exception :【{}】", orderId, e);
            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orderId);
            //回滚
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/6
     * @Descripate 计算分润
     **/
    private CalcFeeVO calculateShareBenefit(Integer type, Orders orders, Object object, MerchantProduct merchantProduct, ShareBenefitLogs shareBenefitLogs) {
        log.error("================== 【calculateShareBenefit 计算分润】====================");
        CalcFeeVO calcFeeVO = new CalcFeeVO();
        calcFeeVO.setChargeStatus(TradeConstant.CHARGE_STATUS_FALID);
        //单笔费率
        BigDecimal poundage = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal orderFee = BigDecimal.ZERO;
        if (type == 1) {
            amount = orders.getTradeAmount();
            orderFee = orders.getFee();
        } else if (type == 2) {
            //    TODO
        }
        if (merchantProduct.getRateType().equals(TradeConstant.FEE_TYPE_RATE)) {
            //手续费=订单金额*单笔费率+附加值
            poundage = amount.multiply(merchantProduct.getRate());
            //判断手续费是否小于最小值，大于最大值
            if (merchantProduct.getMinTate() != null && poundage.compareTo(merchantProduct.getMinTate()) == -1) {
                poundage = merchantProduct.getMinTate();
            }
            if (merchantProduct.getMaxTate() != null && poundage.compareTo(merchantProduct.getMaxTate()) == 1) {
                poundage = merchantProduct.getMaxTate();
            }
        } else if (merchantProduct.getRateType().equals(TradeConstant.FEE_TYPE_QUOTA)) {
            //手续费=单笔定额值+附加值
            poundage = merchantProduct.getRate().add(merchantProduct.getAddValue());
        } else {
            log.info("================== 【calculateShareBenefit 计算分润】=================== 【手续费模式异常】 calcFeeVO: {} ", JSON.toJSONString(calcFeeVO));
            return calcFeeVO;
        }
        if (type == 1 && !orders.getOrderCurrency().equals(orders.getTradeCurrency())) {
            if (org.springframework.util.StringUtils.isEmpty(orders.getTradeForOrderRate())) {
                log.info("==================【calculateShareBenefit 计算分润】===================【换汇异常】 orders.getTradeForOrderRate()为null");
                return calcFeeVO;
            }
            poundage = poundage.multiply(orders.getTradeForOrderRate());
        }
        //若是汇款单需要把代理商手续费换汇
        if (type == 2) {
            //   TODO
        }
        //手续费
        poundage = poundage.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.info("==================【calculateShareBenefit 计算分润】==================== poundage:{}", poundage);
        shareBenefitLogs.setFee(poundage);
        //计算分润
        BigDecimal benefit = BigDecimal.ZERO;
        if (merchantProduct.getDividedRatio() != null) {
            benefit = orderFee.subtract(poundage).multiply(merchantProduct.getDividedRatio());
        } else {
            log.info("==================【calculateShareBenefit 计算分润】=================== 【分润模式异常】 calcFeeVO: {} ", JSON.toJSONString(calcFeeVO));
            return calcFeeVO;
        }

        //算费成功
        log.info("==================【calculateShareBenefit 计算分润】==================== benefit:{}", benefit);
        calcFeeVO.setChargeStatus(TradeConstant.CHARGE_STATUS_SUCCESS);
        calcFeeVO.setFee(benefit.setScale(2, BigDecimal.ROUND_HALF_UP));
        calcFeeVO.setChargeTime(new Date());
        log.info("==================【calculateShareBenefit 计算分润】=================== calcFeeVO: {} ", JSON.toJSONString(calcFeeVO));
        return calcFeeVO;
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
        if (type == 1) {
            shareBenefitLogs.setOrderId(orders.getId());
            shareBenefitLogs.setInstitutionId(orders.getInstitutionId());
            shareBenefitLogs.setInstitutionName(orders.getInstitutionName());
            shareBenefitLogs.setMerchantName(orders.getMerchantName());
            shareBenefitLogs.setMerchantId(orders.getMerchantId());
            shareBenefitLogs.setChannelCode(orders.getChannelCode());
            shareBenefitLogs.setChannelName(orders.getChannelName());
            shareBenefitLogs.setTradeCurrency(orders.getTradeCurrency());
            shareBenefitLogs.setTradeAmount(orders.getTradeAmount());
            shareBenefitLogs.setMerchantOrderId(orders.getMerchantOrderId());
            shareBenefitLogs.setExtend3(orders.getChannelCode());//通道编号
            shareBenefitLogs.setExtend4(orders.getChannelName());//通道名称
            shareBenefitLogs.setExtend5(orders.getMerchantOrderId());//商户流水号
            shareBenefitLogs.setExtend6(orders.getChannelNumber());//通道流水号
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
            shareBenefitLogs.setMerchantOrderId("");
            shareBenefitLogs.setExtend3("");//通道编号
            shareBenefitLogs.setExtend4("");//通道名称
            shareBenefitLogs.setExtend5("");//商户流水号
            shareBenefitLogs.setExtend6("");//通道流水号
        }
        shareBenefitLogs.setAgentId(basicInfoVO.getMerchant().getId());
        shareBenefitLogs.setAgentName(basicInfoVO.getMerchant().getCnName());
        shareBenefitLogs.setAgentType(agentType);
        shareBenefitLogs.setOrderType(type);
        shareBenefitLogs.setExtend1(basicInfoVO.getProduct().getProductCode().toString());//产品编号
        shareBenefitLogs.setExtend2(basicInfoVO.getProduct().getProductName());//产品名称
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

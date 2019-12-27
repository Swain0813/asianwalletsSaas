package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.SettleOrderMapper;
import com.asianwallets.base.service.ReconciliationService;
import com.asianwallets.base.service.SettleOrderService;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReviewSettleDTO;
import com.asianwallets.common.dto.ReviewSettleInfoDTO;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 结算交易
 */
@Transactional
@Service
@Slf4j
public class SettleOrderServiceImpl implements SettleOrderService {

    @Autowired
    private SettleOrderMapper settleOrderMapper;

    @Autowired
    private ReconciliationService reconciliationService;

    /**
     * 结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrder(settleOrderDTO));

    }

    /**
     * 结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrderDetail(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrderDetail(settleOrderDTO));

    }

    /**
     * 结算导出
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO) {
        return settleOrderMapper.exportSettleOrderInfo(settleOrderDTO);
    }

    /**
     * 结算审核
     * @param reviewSettleDTO
     * @return
     */
    @Override
    public int reviewSettlement(ReviewSettleDTO reviewSettleDTO) {
        log.info("------------结算审核开始------------settleOrderDTO:{}", JSON.toJSON(reviewSettleDTO));
        if (StringUtils.isEmpty(reviewSettleDTO.getReviewStatus())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        List<ReviewSettleInfoDTO> rsInfos = reviewSettleDTO.getReviewSettleInfoDTOS();
        ArrayList<SettleOrder> soList = new ArrayList<>();
        BigDecimal allFee = BigDecimal.ZERO;
        for (ReviewSettleInfoDTO rsInfo : rsInfos) {
            //判断汇率是否填写
            if (StringUtils.isEmpty(rsInfo.getRate())) {
                throw new BusinessException(EResultEnum.RATE_IS_NULL.getCode());
            }
            SettleOrder sOrder = settleOrderMapper.selectByPrimaryKey(rsInfo.getId());
            //判断是否为结算中
            if (sOrder == null || !sOrder.getTradeStatus().equals(TradeConstant.AUDIT_WAIT)) {
                throw new BusinessException(EResultEnum.TRADE_STATUS_IS_ERROR.getCode());
            }
            sOrder.setModifier(reviewSettleDTO.getModifier());//修改人
            sOrder.setUpdateTime(new Date());//更新时间
            sOrder.setTradeFee(reviewSettleDTO.getTradeFee());//批次交易手续费
            sOrder.setRemark(reviewSettleDTO.getRemark());//备注
            sOrder.setRate(rsInfo.getRate());//汇率
            sOrder.setSettleChannel(reviewSettleDTO.getSettleChannel());//结算通道
            //手续费币种
            if (StringUtils.isEmpty(reviewSettleDTO.getFeeCurrency())) {
                sOrder.setFeeCurrency(sOrder.getTxncurrency());
            } else {
                sOrder.setFeeCurrency(reviewSettleDTO.getFeeCurrency());
            }
            sOrder.setSettleAmount(sOrder.getTxnamount().multiply(rsInfo.getRate()));//单条记录的计费
            allFee = allFee.add(sOrder.getSettleAmount());//总金额
            soList.add(sOrder);
        }
        final BigDecimal allFees = reviewSettleDTO.getTotalSettleAmount();//总结算金额
        //金额为负
        if (allFees.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(EResultEnum.SETTLEMENT_AMOUNT_TOO_SMALL.getCode());
        }
        soList.forEach(so -> so.setTotalSettleAmount(allFees));
        int tag = 0;
        if (String.valueOf(reviewSettleDTO.getReviewStatus()).equals(String.valueOf(TradeConstant.AUDIT_FAIL))) {
            log.info("------------审核失败------------settleOrderList:{}", JSON.toJSON(soList));
            tag = auditFailure(reviewSettleDTO, soList);

        } else if (String.valueOf(reviewSettleDTO.getReviewStatus()).equals(String.valueOf(TradeConstant.AUDIT_SUCCESS))) {
            log.info("------------审核成功------------");
            soList.forEach(s -> s.setTradeStatus(TradeConstant.AUDIT_SUCCESS));//设置成功状态
            log.info("------------审核成功 更新参数------------settleOrderList:{}", JSON.toJSON(soList));
            for (SettleOrder settleOrder : soList) {
                tag += settleOrderMapper.updateByPrimaryKey(settleOrder);
            }
            if (tag != soList.size()) {
                log.info("-------------更新失败 后台数据未更新完全-------------");
                throw new BusinessException(EResultEnum.UPDATE_FAILED.getCode());
            }
        }
        return tag;
    }

    /**
     * 失败情况下的处理
     *
     * @param settleOrderDTO
     * @param list
     * @return
     */
    private int auditFailure(ReviewSettleDTO settleOrderDTO, List<SettleOrder> list) {
        list.forEach(s -> s.setTradeStatus(TradeConstant.AUDIT_FAIL));//设置失败状态
        int tag = 0;
        for (SettleOrder settleOrder : list) {
            ReconOperDTO reconOperDTO = new ReconOperDTO();
            reconOperDTO.setId("T" + IDS.uniqueID());
            reconOperDTO.setAmount(settleOrder.getTxnamount());
            reconOperDTO.setMerchantId(settleOrder.getMerchantId());
            reconOperDTO.setCurrency(settleOrder.getTxncurrency());
            reconOperDTO.setType(1);//调入
            reconOperDTO.setAccountType(TradeConstant.OTHER_ACCOUNT);//其他账户
            reconOperDTO.setChangeType(TradeConstant.TRANSFER);//资金变动类型
            reconOperDTO.setRemark("结算审核失败，系统自动调账");
            log.info("------------审核失败 调账开始------------reconOperDTO:{}", JSON.toJSON(reconOperDTO));
            //调账
            String flag = reconciliationService.doReconciliation(settleOrderDTO.getModifier(), reconOperDTO);
            if (flag.equals("success")) {
                log.info("------------审核失败 过审开始------------settleOrderDTO:{}", JSON.toJSON(settleOrderDTO));
                //过审
                reconciliationService.auditReconciliation(settleOrderDTO.getModifier(), reconOperDTO.getId(), true, "结算审核失败，系统自动调账，系统自动审核");
            }
            tag += settleOrderMapper.updateByPrimaryKey(settleOrder);
        }
        if (list.size() != tag) {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        return tag;
    }

}

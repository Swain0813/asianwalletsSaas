package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.SettleCheckAccountDetailMapper;
import com.asianwallets.base.dao.SettleCheckAccountMapper;
import com.asianwallets.base.dao.TcsStFlowMapper;
import com.asianwallets.base.service.SettleCheckAccountService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.entity.SettleCheckAccountDetail;
import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ExportSettleCheckAccountVO;
import com.asianwallets.common.vo.SettleCheckAccountDetailVO;
import com.asianwallets.common.vo.SettleCheckAccountVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 结算对账管理的实现类
 */
@Slf4j
@Service
public class SettleCheckAccountServiceImpl extends BaseServiceImpl<SettleCheckAccount> implements SettleCheckAccountService {


    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    @Autowired
    private SettleCheckAccountMapper settleCheckAccountMapper;

    @Autowired
    private SettleCheckAccountDetailMapper settleCheckAccountDetailMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询商户结算对账
     **/
    @Override
    public PageInfo<SettleCheckAccountVO> pageSettleAccountCheck(TradeCheckAccountDTO tradeCheckAccountDTO) {
        return new PageInfo<SettleCheckAccountVO>(settleCheckAccountMapper.pageSettleAccountCheck(tradeCheckAccountDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 分页查询商户结算对账详情
     **/
    @Override
    public PageInfo<SettleCheckAccountDetail> pageSettleAccountCheckDetail(TradeCheckAccountDTO tradeCheckAccountDTO) {
        return new PageInfo<SettleCheckAccountDetail>(settleCheckAccountDetailMapper.pageSettleAccountCheckDetail(tradeCheckAccountDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 导出商户结算对账
     **/
    @Override
    public Map<String, Object> exportSettleAccountCheck(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Statement", settleCheckAccountMapper.exportSettleAccountCheck(tradeCheckAccountDTO));
        List<ExportSettleCheckAccountVO> list = settleCheckAccountDetailMapper.exportSettleAccountCheckDetail(tradeCheckAccountDTO);
        for (ExportSettleCheckAccountVO e : list) {
            map.put(e.getCurrency(), e.getList());
        }
        for (String key : map.keySet()) {
            double balance = 0;
            if (!StringUtils.isEmpty(tradeCheckAccountDTO.getStartDate())) {
                Date date = DateToolUtils.getDateByStr(tradeCheckAccountDTO.getStartDate());
                BigDecimal bigDecimal = settleCheckAccountMapper.getBalanceByTimeAndCurrencyAndInstitutionCode(date, key, tradeCheckAccountDTO.getMerchantId());
                balance = bigDecimal == null ? 0 : bigDecimal.doubleValue();
            }
            double afterBalance = 0;
            if (!key.equals("Statement")) {
                List<SettleCheckAccountDetailVO> list1 = (List<SettleCheckAccountDetailVO>) map.get(key);
                for (int i = list1.size() - 1; i >= 0; i--) {
                    SettleCheckAccountDetailVO settleCheckAccountDetail = list1.get(i);
                    afterBalance = balance + settleCheckAccountDetail.getTxnamount().doubleValue() - settleCheckAccountDetail.getFee().doubleValue()+settleCheckAccountDetail.getRefundOrderFee().doubleValue();
                    settleCheckAccountDetail.setBalance(balance);
                    settleCheckAccountDetail.setAfterBalance(afterBalance);
                    balance = afterBalance;
                }
            }
        }
        return map;
    }

    /**
     * 结算信息对账
     *
     * @return
     */
    @Override
    public int settleAccountCheck(Date time) {
        List<TcsStFlow> tcsStFlowList = tcsStFlowMapper.selectTcsStFlow(DateToolUtils.addDay(time, -1));
        List<SettleCheckAccountDetail> settleCheckAccountDetails = new ArrayList<>();
        for (TcsStFlow tcsStFlow : tcsStFlowList) {
            SettleCheckAccountDetail settleCheckAccountDetail = new SettleCheckAccountDetail();
            BeanUtils.copyProperties(tcsStFlow, settleCheckAccountDetail);
            settleCheckAccountDetail.setId(IDS.uuid2());
            settleCheckAccountDetail.setCreateTime(new Date());
            settleCheckAccountDetails.add(settleCheckAccountDetail);
        }
        if (settleCheckAccountDetails.size() == 0) {
            log.info("------------- 统计结算单 ------------ settleCheckAccountDetails.size = {}", settleCheckAccountDetails.size());
            return 0;
        }
        settleCheckAccountDetailMapper.insertList(settleCheckAccountDetails);

        List<SettleCheckAccount> settleCheckAccounts = settleCheckAccountMapper.statistical(DateToolUtils.addDay(time, -1));
        for (SettleCheckAccount settleCheckAccount : settleCheckAccounts) {
            SettleCheckAccount s = settleCheckAccountMapper.selectByCurrencyAndInstitutionCode(settleCheckAccount.getCurrency(), settleCheckAccount.getMerchantId());
            if (s != null) {
                settleCheckAccount.setId(IDS.uuid2());
                settleCheckAccount.setInitialAmount(s.getFinalAmount());
                BigDecimal finalAmount = settleCheckAccount.getAmount().subtract(settleCheckAccount.getFee()).add(settleCheckAccount.getRefundOrderFee()).add(s.getFinalAmount());
                settleCheckAccount.setFinalAmount(finalAmount);
                settleCheckAccount.setCheckTime(DateToolUtils.addDay(time, -1));
                settleCheckAccount.setCreateTime(new Date());
            } else {
                settleCheckAccount.setId(IDS.uuid2());
                settleCheckAccount.setInitialAmount(BigDecimal.ZERO);
                BigDecimal finalAmount = settleCheckAccount.getAmount().subtract(settleCheckAccount.getFee()).add(settleCheckAccount.getRefundOrderFee());
                settleCheckAccount.setFinalAmount(finalAmount);
                settleCheckAccount.setCheckTime(DateToolUtils.addDay(time, -1));
                settleCheckAccount.setCreateTime(new Date());
            }
        }
        return settleCheckAccountMapper.insertList(settleCheckAccounts);
    }
}

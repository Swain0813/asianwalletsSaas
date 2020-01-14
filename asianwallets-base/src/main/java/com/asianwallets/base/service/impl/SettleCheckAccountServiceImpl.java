package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.SettleCheckAccountDetailMapper;
import com.asianwallets.base.dao.SettleCheckAccountMapper;
import com.asianwallets.base.dao.TcsStFlowMapper;
import com.asianwallets.base.service.SettleCheckAccountService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.entity.SettleCheckAccountDetail;
import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 机构结算单表 服务实现类
 * </p>
 *
 * @author yx
 * @since 2020-01-14
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
     * 机构结算信息对账
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

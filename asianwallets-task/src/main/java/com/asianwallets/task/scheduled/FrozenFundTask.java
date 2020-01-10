package com.asianwallets.task.scheduled;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.task.dao.AccountMapper;
import com.asianwallets.task.dao.ReconciliationMapper;
import com.asianwallets.task.dao.TcsFrozenFundsLogsMapper;
import com.asianwallets.task.feign.MessageFeign;
import com.asianwallets.task.service.ClearingService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@Slf4j
@Api(value = "预约资金冻结定时任务")
public class FrozenFundTask {

    @Autowired
    private TcsFrozenFundsLogsMapper tcsFrozenFundsLogsMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Value("${custom.developer.mobile}")
    private String developerMobile;

    @Value("${custom.developer.email}")
    private String developerEmail;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private AccountMapper accountMapper;


    /**
     * 预约资金冻结定时任务
     * 15分钟跑一次
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    //@Scheduled(cron = "0/10 * * * * ? ")//每10秒执行一次 测试用
    @Transactional
    public void clearOperLog(){
        log.info("*********开始预约资金冻结定时任务********************");
        try {
            List<TcsFrozenFundsLogs> tcsFrozenFundsLogsLists = tcsFrozenFundsLogsMapper.getTcsFrozenFundsLogs();
            if (ArrayUtil.isEmpty(tcsFrozenFundsLogsLists)) {
                log.info("=============【预约资金冻结定时任务】=============【预约冻结记录为空】");
                return;
            }
            for(TcsFrozenFundsLogs tcsFrozenFundsLogsList:tcsFrozenFundsLogsLists){
                //获取账户信息
                Account account = accountMapper.getAccount(tcsFrozenFundsLogsList.getMvaccountId());
                if (account == null) {
                    //当前商户不存在该币种的账户
                    log.info("*******************预约资金冻结定时任务***************************【当前商户不存在该币种的账户】");
                }
                //预约冻结金额>结算户金额-冻结金额则不下次预约冻结
                if (tcsFrozenFundsLogsList.getTxnamount()>account.getSettleBalance().subtract(account.getFreezeBalance()).doubleValue()) {
                    continue;
                }
                //调用清结算的资金冻结和解冻接口
                FinancialFreezeDTO ffd = new FinancialFreezeDTO(tcsFrozenFundsLogsList);
                BaseResponse response = clearingService.freezingFunds(ffd);
                if (response.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //更新系统冻结资金记录
                    tcsFrozenFundsLogsMapper.updateTcsFrozenFundsLogsById(tcsFrozenFundsLogsList.getId());
                } else {//请求失败
                    reconciliationMapper.updateStatusById(tcsFrozenFundsLogsList.getMerOrderNo(), TradeConstant.FREEZE_FALID, "预约资金冻结定时任务", "预约冻结失败");
                }
            }
        } catch (Exception e) {
            log.error("预约资金冻结定时任务发生异常==={}", e);
            messageFeign.sendSimple(developerMobile, "预约资金冻结定时任务发生异常!");
            messageFeign.sendSimpleMail(developerEmail, "预约资金冻结定时任务发生异常", "预约资金冻结定时任务发生异常");
        }
        log.info("***********结束预约资金冻结定时任务******************");
    }
}

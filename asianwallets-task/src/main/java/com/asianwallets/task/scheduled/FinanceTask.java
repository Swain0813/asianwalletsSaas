package com.asianwallets.task.scheduled;
import com.asianwallets.task.service.SettleCheckAccountService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Component
@Slf4j
@Api(value = "saas账务定时任务")
@Transactional
public class FinanceTask {

    @Autowired
    private SettleCheckAccountService settleCheckAccountService;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate saas定时生成机构对账结算单数据
     **/
    @Scheduled(cron = "0 0 1 ? * *")//每天早上1点执行一次
    //@Scheduled(cron = "0/10 * * * * ? ")//每10秒执行一次 测试用
    public void settleAccountCheck() {
        log.info("************saas定时生成机构对账结算单数据开始****************");
        settleCheckAccountService.settleAccountCheck(new Date());
        log.info("************saas定时生成机构对账结算单数据结束****************");
    }

}

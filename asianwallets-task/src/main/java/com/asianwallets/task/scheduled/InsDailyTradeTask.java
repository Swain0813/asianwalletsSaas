package com.asianwallets.task.scheduled;

import com.asianwallets.common.entity.InsDailyTrade;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.InsDailyTradeVO;
import com.asianwallets.task.dao.InsDailyTradeMapper;
import com.asianwallets.task.dao.OrdersMapper;
import com.asianwallets.task.feign.MessageFeign;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@Api(value = "机构日交易汇总表定时任务")
public class InsDailyTradeTask {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private InsDailyTradeMapper insDailyTradeMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Value("${custom.developer.mobile}")
    private String developerMobile;

    @Value("${custom.developer.email}")
    private String developerEmail;

    /**
     * 机构日交易汇总表定时任务
     */
    @Scheduled(cron = "0 0 1 ? * *")
    //@Scheduled(cron = "0/10 * * * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public void insDailyTrade() {
        log.info("=============【机构日交易汇总表定时任务】=============【开始执行】");
        try {
            //获取昨日日期
            String yesterday = DateToolUtils.getYesterday();
            List<InsDailyTradeVO> insDailyTradeVOList = ordersMapper.insDailyTradeReport(yesterday);
            if (ArrayUtil.isEmpty(insDailyTradeVOList)) {
                log.info("=============【机构日交易汇总表定时任务】=============【昨日交易数据为空】");
                return;
            }
            List<InsDailyTrade> dailyTradeList = new ArrayList<>();
            for (InsDailyTradeVO insDailyTradeVO : insDailyTradeVOList) {
                InsDailyTrade insDailyTrade = new InsDailyTrade();
                insDailyTrade.setId(IDS.uuid2());
                insDailyTrade.setInstitutionId(insDailyTradeVO.getInstitutionId());
                insDailyTrade.setInstitutionName(insDailyTradeVO.getInstitutionName());
                insDailyTrade.setChannelCode(insDailyTradeVO.getChannelCode());
                insDailyTrade.setChannelName(insDailyTradeVO.getChannelName());
                insDailyTrade.setOrderCurrency(insDailyTradeVO.getOrderCurrency());
                insDailyTrade.setTotalCount(insDailyTradeVO.getTotalCount());
                insDailyTrade.setTotalAmount(insDailyTradeVO.getTotalAmount());
                insDailyTrade.setTotalFee(insDailyTradeVO.getTotalFee());
                insDailyTrade.setTradeTime(DateToolUtils.getDateByStr(yesterday));
                insDailyTrade.setCreateTime(new Date());
                insDailyTrade.setCreator("定时任务");
                dailyTradeList.add(insDailyTrade);
            }
            int insertNum = insDailyTradeMapper.insertList(dailyTradeList);
            log.info("=============【机构日交易汇总表定时任务】=============【结束执行】 insertNum: {}", insertNum);
        } catch (Exception e) {
            log.info("=============【机构日交易汇总表定时任务】=============【定时任务异常】", e);
            messageFeign.sendSimple(developerMobile, "机构日交易汇总表定时任务异常!");
            messageFeign.sendSimpleMail(developerEmail, "机构日交易汇总表定时任务异常!", "机构日交易汇总表定时任务异常!");
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }
}
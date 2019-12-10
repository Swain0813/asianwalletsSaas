package com.asianwallets.base.job;
import com.asianwallets.base.dao.MerchantProductAuditMapper;
import com.asianwallets.base.dao.MerchantProductHistoryMapper;
import com.asianwallets.base.dao.MerchantProductMapper;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.utils.IDS;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description: 产品审核生效job
 * @author: YangXu
 * @create: 2019-03-04 17:03
 **/
@Slf4j
@Transactional
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ProductInfoJob implements BaseJob{
    @Autowired
    private MerchantProductMapper merchantProductMapper;

    @Autowired
    private MerchantProductHistoryMapper merchantProductHistoryMapper;

    @Autowired
    private MerchantProductAuditMapper merchantProductAuditMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        log.info("---------------------ProductInfoJob--------------------------------------- jobDataMap :{}", jobDataMap);
        if(jobDataMap.get("insProductId") == null) return;

        String insProductId = jobDataMap.get("insProductId").toString();

    }
}

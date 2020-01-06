package com.asianwallets.clearing.scheduled;

import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.service.ClearService;
import com.asianwallets.clearing.service.DrawService;
import com.asianwallets.clearing.service.SettleService;
import com.asianwallets.clearing.service.ShareBenefitService;
import com.asianwallets.common.redis.RedisService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: 清结算定时任务入口
 * @author: YangXu
 * @create: 2019-07-26 17:46
 **/
@Slf4j
@Component
@Api(value = "清结算定时任务入口")
public class TaskScheduled {

    @Autowired
    private ClearService clearService;

    @Autowired
    private SettleService settleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DrawService drawService;

    @Autowired
    private ShareBenefitService shareBenefitService;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 批次清算定时任务入口
     **/
    //@Scheduled(cron = "0 0/1 * * * ?")//测试用
    @Scheduled(cron = "0 0/5 * * * ?")//生产环境配置
    public void ClearForGroupBatch() {
        String key = "ClearForGroupBatch_CLEARING_KEY";
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            try {
                log.info("***************** get lock success key :【{}】 **************  ", key);
                log.info("************************** 开始执行清算任务 *********************");
                clearService.ClearForGroupBatch();
                log.info("************************** 结束执行清算任务 *********************");
            } catch (Exception e) {
                log.info("*************** 批次清算 **************** Exception：{}", e);
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 批次结算定时任务入口
     **/
    //@Scheduled(cron = "0 0/2 * * * ?")//测试用
    @Scheduled(cron = "0 0/10 * * * ?")//生产环境配置
    public void SettlementForBatch() {
        String key = "SettlementForBatch_CLEARING_KEY";
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            try {
                log.info("***************** get lock success key :【{}】 **************  ", key);
                log.info("************************** 开始执行结算任务 *********************");
                settleService.SettlementForBatch();
                log.info("************************** 结束执行结算任务 *********************");
            } catch (Exception e) {
                log.info("*************** 批次结算 **************** Exception：{}", e);
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
        }
    }

    /**
     * 分润跑批定时任务
     */
    //@Scheduled(cron = "0/10 * * * * ? ")//每10秒执行一次 测试用
    @Scheduled(cron = "0 0 0/2 * * ?")//每两小时执行一次 生产环境配置
    public void ShareBenefitLogsBatch() {
        String key = "ShareBenefitLogsBatch_CLEARING_KEY";
        log.info("************ CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            try {
                log.info("***************** get lock success key :【{}】 **************  ", key);
                log.info("************************** 开始执行分润跑批任务 *********************");
                shareBenefitService.ShareBenefitForBatch();
                log.info("************************** 结束执行分润跑批任务 *********************");
            } catch (Exception e) {
                log.info("*************** 批次分润跑批 **************** Exception：{}", e);
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
        }

    }


    /**
     * 自动提款跑批定时任务
     */
    //@Scheduled(cron = "0/10 * * * * ? ")//每10秒执行一次 测试用
    @Scheduled(cron = "0 0 7 ? * *")//每天早上7点跑批一次 生产环境用
    public void DrawForBatch() {
        String key = "DrawForBatch_CLEARING_KEY";
        log.info("************ DrawForBatch_CLEARING_KEY *************** key:{}", key);
        if (redisService.lock(key, Const.Redis.expireTime)) {
            try {
                log.info("***************** get lock success key :【{}】 **************  ", key);
                log.info("************************** 开始执行自动提款跑批任务 *********************");
                drawService.DrawForBatch();
                log.info("************************** 结束执行自动提款跑批任务 *********************");
            } catch (Exception e) {
                log.info("*************** 批次结算 **************** Exception：{}", e);
            } finally {
                while (!redisService.releaseLock(key)) {
                    log.info("******************* release lock failed ******************** ：{} ", key);
                }
                log.info("********************* release lock success ******************** : {}", key);
            }
        } else {
            log.info("********************* get lock failed ******************** : {} : " + key);
        }
    }
}

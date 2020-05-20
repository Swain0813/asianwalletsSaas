package com.asianwallets.base.job;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.MerchantProductAuditMapper;
import com.asianwallets.base.dao.MerchantProductHistoryMapper;
import com.asianwallets.base.dao.MerchantProductMapper;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.*;
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
public class ProductInfoJob implements BaseJob {
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
        if (jobDataMap.get("merProId") == null) {
            return;
        }
        String merProId = jobDataMap.get("merProId").toString();

        MerchantProductAudit oldMercahntProductAudit = merchantProductAuditMapper.selectByPrimaryKey(merProId);
        MerchantProduct oldMerchantProduct = merchantProductMapper.selectByPrimaryKey(merProId);
        //将原纪录移动到历史表
        MerchantProductHistory institutionProductHistory = new MerchantProductHistory();
        BeanUtils.copyProperties(oldMerchantProduct, institutionProductHistory);
        institutionProductHistory.setId(IDS.uuid2());
        institutionProductHistory.setMerchantProductId(oldMerchantProduct.getId());
        institutionProductHistory.setCreateTime(oldMerchantProduct.getCreateTime());
        institutionProductHistory.setCreator(oldMerchantProduct.getCreator());
        institutionProductHistory.setModifier(oldMerchantProduct.getModifier());
        institutionProductHistory.setUpdateTime(oldMerchantProduct.getUpdateTime());
        merchantProductHistoryMapper.insert(institutionProductHistory);

        //将审核表信息更新到主表
        MerchantProduct institutionProduct = new MerchantProduct();
        BeanUtils.copyProperties(oldMercahntProductAudit, institutionProduct);
        institutionProduct.setUpdateTime(new Date());
        institutionProduct.setEnabled(true);
        institutionProduct.setAuditStatus(TradeConstant.AUDIT_SUCCESS);
        merchantProductMapper.updateByPrimaryKeySelective(institutionProduct);
        //删除审核表记录
        merchantProductAuditMapper.deleteByPrimaryKey(merProId);
        //审核通过后将新增和修改的机构产品信息添加的redis里
        try {
            redisService.set(AsianWalletConstant.MERCHANTPRODUCT_CACHE_KEY.concat("_").concat(oldMercahntProductAudit.getMerchantId().concat("_").concat(oldMercahntProductAudit.getProductId())),
                    JSON.toJSONString(merchantProductMapper.selectByPrimaryKey(merProId)));
        } catch (Exception e) {
            log.error("审核通过后将新增和修改的机构产品信息添加的redis里：" + e.getMessage());
        }
    }
}

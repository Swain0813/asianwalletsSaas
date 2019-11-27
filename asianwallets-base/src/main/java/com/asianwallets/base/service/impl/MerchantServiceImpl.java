package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.MerchantAuditMapper;
import com.asianwallets.base.dao.MerchantHistoryMapper;
import com.asianwallets.base.dao.MerchantMapper;
import com.asianwallets.base.service.MerchantService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Service
@Slf4j
public class MerchantServiceImpl extends BaseServiceImpl<Merchant> implements MerchantService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantAuditMapper merchantAuditMapper;

    @Autowired
    private MerchantHistoryMapper merchantHistoryMapper;

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加商户
     * @return
     **/
    @Override
    public int addMerchant(String name, MerchantDTO merchantDTO) {

        //判断机构名称是否存在
        if (merchantMapper.selectCountByInsName(merchantDTO.getCnName()) > 0) {
            throw new BusinessException(EResultEnum.NAME_EXIST.getCode());
        }
        if (merchantMapper.selectCountByInsName(merchantDTO.getEnName()) > 0) {
            throw new BusinessException(EResultEnum.NAME_EXIST.getCode());
        }

        Merchant merchant = new Merchant();
        MerchantAudit merchantAudit = new MerchantAudit();
        BeanUtils.copyProperties(merchantDTO,merchant);
        BeanUtils.copyProperties(merchantDTO,merchantAudit);

        String id = "M"+ IDS.uniqueID().toString();

        merchant.setId(id);
        merchant.setCreateTime(new Date());
        merchant.setCreator(name);
        merchant.setAuditStatus(TradeConstant.AUDIT_WAIT);
        merchant.setEnabled(false);

        merchantAudit.setId(id);
        merchantAudit.setCreateTime(new Date());
        merchantAudit.setCreator(name);
        merchantAudit.setAuditStatus(TradeConstant.AUDIT_WAIT);
        merchantAudit.setEnabled(false);

        merchantMapper.insert(merchant);

        return merchantAuditMapper.insert(merchantAudit);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 修改商户
     * @return
     **/
    @Override
    public int updateMerchant(String name, MerchantDTO merchantDTO) {
        //机构主体信息
        //若有审核失败数据删除
        MerchantAudit oldMerchantAudit = merchantAuditMapper.selectByPrimaryKey(merchantDTO.getMerchantId());
        if (oldMerchantAudit != null && TradeConstant.AUDIT_WAIT.equals(oldMerchantAudit.getAuditStatus())) {
            throw new BusinessException(EResultEnum.AUDIT_INFO_EXIENT.getCode());
        } else if (oldMerchantAudit != null && TradeConstant.AUDIT_FAIL.equals(oldMerchantAudit.getAuditStatus())) {
            merchantAuditMapper.deleteByPrimaryKey(merchantDTO.getMerchantId());
        }
        MerchantAudit merchantAudit = new MerchantAudit();
        BeanUtils.copyProperties(merchantDTO, merchantAudit);
        merchantAudit.setId(merchantDTO.getMerchantId());
        //创建时间
        merchantAudit.setCreateTime(new Date());
        //创建人
        merchantAudit.setCreator(name);
        merchantAudit.setAuditStatus(TradeConstant.AUDIT_WAIT);
        return merchantAuditMapper.insert(merchantAudit);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户信息列表
     * @return
     **/
    @Override
    public PageInfo<Merchant> pageFindMerchant(MerchantDTO merchantDTO) {
        return new PageInfo<>(merchantMapper.pageFindMerchant(merchantDTO));
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户审核信息列表
     * @return
     **/
    @Override
    public PageInfo<MerchantAudit> pageFindMerchantAudit(MerchantDTO merchantDTO) {
        return new PageInfo<>(merchantAuditMapper.pageFindMerchantAudit(merchantDTO));
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户信息详情
     * @return
     **/
    @Override
    public Merchant getMerchantInfo(String id) {
        return merchantMapper.getMerchantInfo(id);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户审核信息详情
     * @return
     **/
    @Override
    public MerchantAudit getMerchantAuditInfo(String id) {
        return merchantAuditMapper.getMerchantAuditInfo(id);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 审核商户信息接口
     * @return
     **/
    @Override
    public int auditMerchant(String username, String merchantId, Boolean enabled, String remark) {
        int num;
        Merchant oleMerchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (TradeConstant.AUDIT_SUCCESS.equals(oleMerchant.getAuditStatus())) {
            if (enabled) {
                //审核通过
                //将审核表信息移动到主题表
                MerchantAudit merchantAudit = merchantAuditMapper.selectByPrimaryKey(merchantId);
                //查询主题表原机构信息.把原机构信息存放历史表
                MerchantHistory merchantHistory = new MerchantHistory();
                BeanUtils.copyProperties(oleMerchant, merchantHistory);
                merchantHistory.setId(IDS.uuid2());
                merchantHistory.setInstitutionId(merchantId);
                merchantHistory.setEnabled(enabled);
                merchantHistoryMapper.insert(merchantHistory);
                merchantMapper.deleteByPrimaryKey(merchantId);
                //将审核表信息移动到主题表
                Merchant merchant = new Merchant();
                BeanUtils.copyProperties(merchantAudit, merchant);
                merchant.setAuditStatus(TradeConstant.AUDIT_SUCCESS);
                //创建时间
                merchant.setCreateTime(oleMerchant.getCreateTime());
                //创建人
                merchant.setCreator(oleMerchant.getCreator());
                merchant.setUpdateTime(new Date());
                merchant.setModifier(username);
                merchant.setRemark(remark);
                merchant.setEnabled(enabled);
                merchantMapper.insert(merchant);
                merchantAuditMapper.deleteByPrimaryKey(merchantId);
                try {
                    //审核通过后将新增和修改的机构信息添加的redis里
                    redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
                } catch (Exception e) {
                    log.error("审核通过后将商户信息同步到redis里发生错误：", e.getMessage());
                }
                num = 0;
            } else {
                //审核不通过
                MerchantAudit merchantAudit = new MerchantAudit();
                merchantAudit.setAuditStatus(TradeConstant.AUDIT_FAIL);
                merchantAudit.setId(merchantId);
                merchantAudit.setModifier(username);
                merchantAudit.setUpdateTime(new Date());
                merchantAudit.setRemark(remark);
                merchantAudit.setEnabled(enabled);
                num =merchantAuditMapper.updateByPrimaryKeySelective(merchantAudit);
            }
        } else {
            //初次添加
            if (enabled) {
                //审核通过
                //查询主题表原机构信息.把原机构信息存放历史表
                merchantMapper.deleteByPrimaryKey(merchantId);
                //将审核表信息移动到主题表
                MerchantAudit merchantAudit = merchantAuditMapper.selectByPrimaryKey(merchantId);
                Merchant merchant = new Merchant();
                BeanUtils.copyProperties(merchantAudit, merchant);
                merchant.setAuditStatus(TradeConstant.AUDIT_SUCCESS);
                //创建时间
                merchant.setCreateTime(oleMerchant.getCreateTime());
                //创建人
                merchant.setCreator(oleMerchant.getCreator());
                merchant.setUpdateTime(new Date());
                merchant.setModifier(username);
                merchant.setEnabled(enabled);
                merchantMapper.insert(merchant);
                try {
                    //审核通过后将新增和修改的机构信息添加的redis里
                    redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
                } catch (Exception e) {
                    log.error("审核通过后将商户信息同步到redis里发生错误：", e.getMessage());
                }
                num = merchantAuditMapper.deleteByPrimaryKey(merchant);
            } else {
                Merchant merchant = new Merchant();
                merchant.setId(merchantId);
                merchant.setAuditStatus(TradeConstant.AUDIT_FAIL);
                merchant.setModifier(username);
                merchant.setUpdateTime(new Date());
                merchant.setRemark(remark);
                merchant.setEnabled(enabled);
                merchantMapper.updateByPrimaryKeySelective(merchant);
                //审核不通过
                MerchantAudit merchantAudit = new MerchantAudit();
                merchantAudit.setAuditStatus(TradeConstant.AUDIT_FAIL);
                merchantAudit.setId(merchantId);
                merchantAudit.setModifier(username);
                merchantAudit.setUpdateTime(new Date());
                merchantAudit.setRemark(remark);
                merchantAudit.setEnabled(enabled);
                num = merchantAuditMapper.updateByPrimaryKeySelective(merchantAudit);
            }

        }
        return num;
    }
}

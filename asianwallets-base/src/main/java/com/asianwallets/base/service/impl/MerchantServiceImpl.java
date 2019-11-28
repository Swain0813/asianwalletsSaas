package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.*;
import com.asianwallets.base.service.MerchantService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
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
    @Autowired
    private AuditorProvider auditorProvider;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加商户
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
        BeanUtils.copyProperties(merchantDTO, merchant);
        BeanUtils.copyProperties(merchantDTO, merchantAudit);
        //商户编号
        String id = "M"+IDS.uniqueID();
        String merchantId = DateToolUtils.getReqDateE().concat(id.substring(id.length() - 4));
        merchant.setId(merchantId);
        merchant.setCreateTime(new Date());
        merchant.setCreator(name);
        merchant.setAuditStatus(TradeConstant.AUDIT_WAIT);
        merchant.setEnabled(false);

        merchantAudit.setId(merchantId);
        merchantAudit.setCreateTime(new Date());
        merchantAudit.setCreator(name);
        merchantAudit.setAuditStatus(TradeConstant.AUDIT_WAIT);
        merchantAudit.setEnabled(false);
        if (merchantMapper.insert(merchant) > 0) {
            //账号信息
            SysUser sysUser = new SysUser();
            String userId = IDS.uuid2();
            sysUser.setId(userId);
            sysUser.setUsername("admin"+id);
            sysUser.setPassword(encryptPassword("123456"));
            sysUser.setTradePassword(encryptPassword("123456"));//交易密码
            sysUser.setSysId(id);
            if (merchantDTO.getMerchantType().equals(AsianWalletConstant.MERCHANT_USER)) {
                //普通商户
                sysUser.setPermissionType(AsianWalletConstant.MERCHANT);
                sysUser.setSysType(AsianWalletConstant.MERCHANT_USER);
            } else if (merchantDTO.getMerchantType().equals(AsianWalletConstant.AGENCY_USER)) {
                //代理商户
                sysUser.setPermissionType(AsianWalletConstant.AGENCY);
                sysUser.setSysType(AsianWalletConstant.AGENCY_USER);
            } else if (merchantDTO.getMerchantType().equals(AsianWalletConstant.GROUP_USER)) {
                //集团商户
                sysUser.setPermissionType(AsianWalletConstant.MERCHANT);
                sysUser.setSysType(AsianWalletConstant.GROUP_USER);
            }
            sysUser.setName("admin");
            sysUser.setCreateTime(new Date());
            sysUser.setCreator(name);
            sysUser.setEnabled(true);
            sysUserMapper.insert(sysUser);

            if (merchantDTO.getMerchantType().equals(AsianWalletConstant.MERCHANT_USER)) {
                //分配普通商户角色
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(sysUserRoleMapper.getInstitutionRoleId());
                sysUserRole.setUserId(userId);
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setCreator(name);
                sysUserRoleMapper.insert(sysUserRole);
            } else if (merchantDTO.getMerchantType().equals(AsianWalletConstant.AGENCY_USER)) {
                //分配代理商户角色
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(sysUserRoleMapper.getAgencyRoleId());
                sysUserRole.setUserId(userId);
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setCreator(name);
                sysUserRoleMapper.insert(sysUserRole);
            } else if (merchantDTO.getMerchantType().equals(AsianWalletConstant.GROUP_USER)) {
                //分配集团商户角色
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(sysUserRoleMapper.getGroupRoleId());
                sysUserRole.setUserId(userId);
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setCreator(name);
                sysUserRoleMapper.insert(sysUserRole);
            }
        }

        return merchantAuditMapper.insert(merchantAudit);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户信息列表
     **/
    @Override
    public PageInfo<Merchant> pageFindMerchant(MerchantDTO merchantDTO) {
        merchantDTO.setLanguage(auditorProvider.getLanguage());
        return new PageInfo<>(merchantMapper.pageFindMerchant(merchantDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 修改商户
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
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户审核信息列表
     **/
    @Override
    public PageInfo<MerchantAudit> pageFindMerchantAudit(MerchantDTO merchantDTO) {
        merchantDTO.setLanguage(auditorProvider.getLanguage());
        return new PageInfo<>(merchantAuditMapper.pageFindMerchantAudit(merchantDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户信息详情
     **/
    @Override
    public Merchant getMerchantInfo(String id) {
        return merchantMapper.getMerchantInfo(id, auditorProvider.getLanguage());
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户审核信息详情
     **/
    @Override
    public MerchantAudit getMerchantAuditInfo(String id) {
        return merchantAuditMapper.getMerchantAuditInfo(id, auditorProvider.getLanguage());
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 审核商户信息接口
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
                num = merchantAuditMapper.updateByPrimaryKeySelective(merchantAudit);
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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 代理商下拉框
     **/
    @Override
    public List<Merchant> getAllAgent(String merchantType) {
        return merchantMapper.getAllAgent(merchantType);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
     * @return
     **/
    @Override
    public List<Merchant> exportMerchant(MerchantDTO merchantDTO) {
        merchantDTO.setLanguage(auditorProvider.getLanguage());//设置语言
        return merchantMapper.exportMerchant(merchantDTO);
    }

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 禁用启用商户
     * @return
     **/
    @Override
    public int banMerchant(String username, String merchantId, Boolean enabled) {
        int num;
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {//商户信息不存在
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        merchant.setId(merchantId);
        merchant.setEnabled(enabled);
        merchant.setModifier(username);
        merchant.setUpdateTime(new Date());
        num = merchantMapper.updateByPrimaryKeySelective(merchant);
        try {
            //更新机构信息后添加的redis里
            redisService.set(AsianWalletConstant.MERCHANT_CACHE_KEY.concat("_").concat(merchant.getId()), JSON.toJSONString(merchant));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }
}

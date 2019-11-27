package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.InstitutionAuditMapper;
import com.asianwallets.base.dao.InstitutionHistoryMapper;
import com.asianwallets.base.dao.InstitutionMapper;
import com.asianwallets.base.dao.SysUserMapper;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.base.service.InstitutionService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.entity.InstitutionAudit;
import com.asianwallets.common.entity.InstitutionHistory;
import com.asianwallets.common.entity.SysUser;
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

/**
 * <p>
 * 机构表 服务实现类
 * </p>
 *
 * @author yx
 * @since 2019-11-22
 */
@Service
@Slf4j
public class InstitutionServiceImpl extends BaseServiceImpl<Institution> implements InstitutionService {

    @Autowired
    private InstitutionMapper institutionMapper;
    @Autowired
    private InstitutionAuditMapper institutionAuditMapper;
    @Autowired
    private InstitutionHistoryMapper institutionHistoryMapper;
    @Autowired
    private RedisService redisService;

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
     * @Descripate 添加机构
     **/
    @Override
    public int addInstitution(String name, InstitutionDTO institutionDTO) {
        //判断机构名称是否存在
        if (institutionMapper.selectCountByInsName(institutionDTO.getCnName()) > 0) {
            throw new BusinessException(EResultEnum.NAME_EXIST.getCode());
        }
        if (institutionMapper.selectCountByInsName(institutionDTO.getEnName()) > 0) {
            throw new BusinessException(EResultEnum.NAME_EXIST.getCode());
        }
        //机构编号
        String str = IDS.uniqueID().toString();
        String institutionId = DateToolUtils.getReqDateE().concat(str.substring(str.length() - 4));

        Institution institution = new Institution();
        InstitutionAudit institutionAudit = new InstitutionAudit();

        BeanUtils.copyProperties(institutionDTO, institution);
        BeanUtils.copyProperties(institutionDTO, institutionAudit);

        institution.setId(institutionId);
        institution.setCreateTime(new Date());
        institution.setCreator(name);
        institution.setAuditStatus(TradeConstant.AUDIT_WAIT);
        institution.setEnabled(false);

        institutionAudit.setId(institutionId);
        institutionAudit.setCreateTime(new Date());
        institutionAudit.setCreator(name);
        institutionAudit.setAuditStatus(TradeConstant.AUDIT_WAIT);
        institutionAudit.setEnabled(false);

        if (institutionMapper.insert(institution) > 0) {
            //账号信息
            SysUser sysUser = new SysUser();
            String userId = IDS.uuid2();
            sysUser.setId(userId);
            sysUser.setUsername(institutionId + "admin");
            sysUser.setPassword(encryptPassword("123456"));
            sysUser.setTradePassword(encryptPassword("123456"));//交易密码
            sysUser.setSysId(institutionId);
            sysUser.setPermissionType(AsianWalletConstant.INSTITUTION);
            sysUser.setSysType(AsianWalletConstant.INSTITUTION_USER);
            sysUser.setName("admin");
            sysUser.setCreateTime(new Date());
            sysUser.setCreator(name);
            sysUserMapper.insert(sysUser);

        }

        return institutionAuditMapper.insert(institutionAudit);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 修改机构
     **/
    @Override
    public int updateInstitution(String name, InstitutionDTO institutionDTO) {
        //机构主体信息
        //若有审核失败数据删除
        InstitutionAudit oldInstitutionAudit = institutionAuditMapper.selectByPrimaryKey(institutionDTO.getInstitutionId());
        if (oldInstitutionAudit != null && TradeConstant.AUDIT_WAIT.equals(oldInstitutionAudit.getAuditStatus())) {
            throw new BusinessException(EResultEnum.AUDIT_INFO_EXIENT.getCode());
        } else if (oldInstitutionAudit != null && TradeConstant.AUDIT_FAIL.equals(oldInstitutionAudit.getAuditStatus())) {
            institutionAuditMapper.deleteByPrimaryKey(institutionDTO.getInstitutionId());
        }
        InstitutionAudit institutionAudit = new InstitutionAudit();
        BeanUtils.copyProperties(institutionDTO, institutionAudit);
        institutionAudit.setId(institutionDTO.getInstitutionId());
        //创建时间
        institutionAudit.setCreateTime(new Date());
        //创建人
        institutionAudit.setCreator(name);
        institutionAudit.setAuditStatus(TradeConstant.AUDIT_WAIT);
        return institutionAuditMapper.insert(institutionAudit);
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 查询机构信息
     **/
    @Override
    public PageInfo<Institution> pageFindInstitution(InstitutionDTO institutionDTO) {
        return new PageInfo(institutionMapper.pageFindInstitution(institutionDTO));
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询机构审核信息列表
     **/
    @Override
    public PageInfo<InstitutionAudit> pageFindInstitutionAudit(InstitutionDTO institutionDTO) {
        return new PageInfo(institutionAuditMapper.pageFindInstitutionAudit(institutionDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据机构Id查询机构信息详情
     **/
    @Override
    public Institution getInstitutionInfo(String id) {
        return institutionMapper.getInstitutionInfo(id);
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据机构Id查询机构信息详情
     **/
    @Override
    public InstitutionAudit getInstitutionInfoAudit(String id) {
        return institutionAuditMapper.getInstitutionInfoAudit(id);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 审核机构信息
     **/
    @Override
    public int auditInstitution(String username, String institutionId, Boolean enabled, String remark) {
        int num;
        Institution oleInstitution = institutionMapper.selectByPrimaryKey(institutionId);
        if (TradeConstant.AUDIT_SUCCESS.equals(oleInstitution.getAuditStatus())) {
            if (enabled) {
                //审核通过
                //将审核表信息移动到主题表
                InstitutionAudit institutionAudit = institutionAuditMapper.selectByPrimaryKey(institutionId);
                //查询主题表原机构信息.把原机构信息存放历史表
                InstitutionHistory institutionHistory = new InstitutionHistory();
                BeanUtils.copyProperties(oleInstitution, institutionHistory);
                institutionHistory.setId(IDS.uuid2());
                institutionHistory.setInstitutionId(institutionId);
                institutionHistory.setEnabled(enabled);
                institutionHistoryMapper.insert(institutionHistory);
                institutionMapper.deleteByPrimaryKey(institutionId);
                //将审核表信息移动到主题表
                Institution institution = new Institution();
                BeanUtils.copyProperties(institutionAudit, institution);
                institution.setAuditStatus(TradeConstant.AUDIT_SUCCESS);
                //创建时间
                institution.setCreateTime(oleInstitution.getCreateTime());
                //创建人
                institution.setCreator(oleInstitution.getCreator());
                institution.setUpdateTime(new Date());
                institution.setModifier(username);
                institution.setRemark(remark);
                institution.setEnabled(enabled);
                institutionMapper.insert(institution);
                institutionAuditMapper.deleteByPrimaryKey(institutionId);
                try {
                    //审核通过后将新增和修改的机构信息添加的redis里
                    redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
                } catch (Exception e) {
                    log.error("审核通过后将机构信息同步到redis里发生错误：", e.getMessage());
                }
                num = 0;
            } else {
                //审核不通过
                InstitutionAudit institutionAudit = new InstitutionAudit();
                institutionAudit.setAuditStatus(TradeConstant.AUDIT_FAIL);
                institutionAudit.setId(institutionId);
                institutionAudit.setModifier(username);
                institutionAudit.setUpdateTime(new Date());
                institutionAudit.setRemark(remark);
                institutionAudit.setEnabled(enabled);
                num = institutionAuditMapper.updateByPrimaryKeySelective(institutionAudit);
            }
        } else {
            //初次添加
            if (enabled) {
                //审核通过
                //查询主题表原机构信息.把原机构信息存放历史表
                institutionMapper.deleteByPrimaryKey(institutionId);
                //将审核表信息移动到主题表
                InstitutionAudit institutionAudit = institutionAuditMapper.selectByPrimaryKey(institutionId);
                Institution institution = new Institution();
                BeanUtils.copyProperties(institutionAudit, institution);
                institution.setAuditStatus(TradeConstant.AUDIT_SUCCESS);
                //创建时间
                institution.setCreateTime(oleInstitution.getCreateTime());
                //创建人
                institution.setCreator(oleInstitution.getCreator());
                institution.setUpdateTime(new Date());
                institution.setModifier(username);
                institution.setEnabled(enabled);
                institutionMapper.insert(institution);
                try {
                    //审核通过后将新增和修改的机构信息添加的redis里
                    redisService.set(AsianWalletConstant.INSTITUTION_CACHE_KEY.concat("_").concat(institution.getId()), JSON.toJSONString(institution));
                } catch (Exception e) {
                    log.error("审核通过后将机构信息同步到redis里发生错误：", e.getMessage());
                }
                num = institutionAuditMapper.deleteByPrimaryKey(institutionId);
            } else {
                Institution institution = new Institution();
                institution.setId(institutionId);
                institution.setAuditStatus(TradeConstant.AUDIT_FAIL);
                institution.setModifier(username);
                institution.setUpdateTime(new Date());
                institution.setRemark(remark);
                institution.setEnabled(enabled);
                institutionMapper.updateByPrimaryKeySelective(institution);
                //审核不通过
                InstitutionAudit institutionAudit = new InstitutionAudit();
                institutionAudit.setAuditStatus(TradeConstant.AUDIT_FAIL);
                institutionAudit.setId(institutionId);
                institutionAudit.setModifier(username);
                institutionAudit.setUpdateTime(new Date());
                institutionAudit.setRemark(remark);
                institutionAudit.setEnabled(enabled);
                num = institutionAuditMapper.updateByPrimaryKeySelective(institutionAudit);
            }

        }
        return num;
    }
}
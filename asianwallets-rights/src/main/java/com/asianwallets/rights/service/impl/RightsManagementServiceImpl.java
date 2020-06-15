package com.asianwallets.rights.service.impl;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.asianwallets.common.constant.RightsConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.vo.InstitutionRightsApiVO;
import com.asianwallets.common.vo.InstitutionRightsInfoVO;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.rights.dao.InstitutionRightsMapper;
import com.asianwallets.rights.service.CommonRedisService;
import com.asianwallets.rights.service.CommonService;
import com.asianwallets.rights.service.RightsManagementService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 权益管理
 */
@Service
@Transactional
@Slf4j
public class RightsManagementServiceImpl implements RightsManagementService {

    @Autowired
    private InstitutionRightsMapper institutionRightsMapper;

    @Autowired
    private CommonRedisService commonRedisService;

    @Autowired
    private CommonService commonService;

    /**
     * 新增权益
     *
     * @param institutionRightsDTO DTO
     * @param creator
     * @return int
     */
    @Override
    public int addRights(InstitutionRightsDTO institutionRightsDTO, String creator) {
        //检查参数
        checkInstitutionRightsDTO(institutionRightsDTO);
        List<String> merchantIdList = institutionRightsDTO.getMerchantId();
        StringBuilder merchantIdStr = new StringBuilder();
        StringBuilder merchantNameStr = new StringBuilder();
        for (int i = 0; i < merchantIdList.size(); i++) {
            //根据商户号获取商户信息
            Merchant merchant = commonRedisService.getMerchant(merchantIdList.get(i));
            if (!merchant.getInstitutionId().equals(institutionRightsDTO.getInstitutionId())) {
                throw new BusinessException(EResultEnum.INSTITUTION_AND_MERCHANT_NOT_RELATED.getCode());
            }
            merchantIdStr.append(merchant.getId());
            merchantNameStr.append(merchant.getCnName());
            if (i != merchantIdList.size() - 1) {
                merchantIdStr.append(",");
                merchantNameStr.append(",");
            }
        }
        if (institutionRightsMapper.selectByActivityTheme(institutionRightsDTO.getActivityTheme()) != null) {
            throw new BusinessException(EResultEnum.DUPLICATE_EVENT_THEME.getCode());
        }
        InstitutionRights institutionRights = new InstitutionRights();
        BeanUtils.copyProperties(institutionRightsDTO, institutionRights);
        String id = IDS.uniqueID().toString();
        String batchNo = "B" + DateToolUtils.getReqDateE().concat(id.substring(id.length() - 4));
        institutionRights.setMerchantId(merchantIdStr.toString());
        institutionRights.setMerchantName(merchantNameStr.toString());
        institutionRights.setInstitutionName(institutionRightsDTO.getInstitutionName());
        institutionRights.setTicketAmount(institutionRightsDTO.getTicketAmount());
        //剩余数量
        institutionRights.setSurplusAmount(institutionRightsDTO.getActivityAmount());
        institutionRights.setStartTime(institutionRightsDTO.getStartTime());
        institutionRights.setEndTime(institutionRightsDTO.getEndTime());
        institutionRights.setGetLimit(Byte.valueOf(institutionRightsDTO.getGetLimit()));
        String unusableTime = StrUtil.removeSuffix(StrUtil.removePrefix(Arrays.toString(institutionRightsDTO.getUnusableTime().toArray()), "["), "]").replace(" ", "");
        institutionRights.setExtend1(unusableTime);
        institutionRights.setCreator(creator);
        institutionRights.setBatchNo(batchNo);
        institutionRights.setId(id);
        institutionRights.setInstitutionRequestTime(new Date());
        institutionRights.setEnabled(true);
        institutionRights.setCreateTime(new Date());
        return institutionRightsMapper.insert(institutionRights);
    }

    /**
     * 对外的权益新增
     *
     * @param institutionRightsApiDTO 对外的权益新增DTO
     * @return int
     */
    @Override
    public int addRightsApi(RightsApiDTO institutionRightsApiDTO) {
        //检查参数
        checkInstitutionRightsApiDTO(institutionRightsApiDTO);
        List<String> merchantIdList = institutionRightsApiDTO.getMerchantId();
        StringBuilder merchantIdStr = new StringBuilder();
        StringBuilder merchantNameStr = new StringBuilder();
        String institutionName = null;
        for (int i = 0; i < merchantIdList.size(); i++) {
            //根据商户号获取商户信息
            Merchant merchant = commonRedisService.getMerchant(merchantIdList.get(i));
            if (!merchant.getInstitutionId().equals(institutionRightsApiDTO.getInstitutionId())) {
                throw new BusinessException(EResultEnum.INSTITUTION_AND_MERCHANT_NOT_RELATED.getCode());
            }
            institutionName = commonRedisService.getInstitutionInfo(institutionRightsApiDTO.getInstitutionId()).getCnName();
            merchantIdStr.append(merchant.getId());
            merchantNameStr.append(merchant.getCnName());
            if (i != merchantIdList.size() - 1) {
                merchantIdStr.append(",");
                merchantNameStr.append(",");
            }
        }
        if (institutionRightsMapper.selectByActivityTheme(institutionRightsApiDTO.getActivityTheme()) != null) {
            throw new BusinessException(EResultEnum.DUPLICATE_EVENT_THEME.getCode());
        }
        InstitutionRights institutionRights = new InstitutionRights();
        BeanUtils.copyProperties(institutionRightsApiDTO, institutionRights);
        String id = IDS.uniqueID().toString();
        String batchNo = "B" + DateToolUtils.getReqDateE().concat(id.substring(id.length() - 4));
        institutionRights.setMerchantId(merchantIdStr.toString());
        institutionRights.setMerchantName(merchantNameStr.toString());
        institutionRights.setInstitutionName(institutionName);
        institutionRights.setTicketAmount(institutionRightsApiDTO.getTicketAmount());
        //剩余数量
        institutionRights.setSurplusAmount(institutionRightsApiDTO.getNumberOfActivities());
        institutionRights.setStartTime(institutionRightsApiDTO.getActivityStartTime());
        institutionRights.setEndTime(institutionRightsApiDTO.getActivityEndTime());
        institutionRights.setGetLimit(institutionRightsApiDTO.getUseLimit());
        String unusableTime = StrUtil.removeSuffix(StrUtil.removePrefix(Arrays.toString(institutionRightsApiDTO.getUnusableTime().toArray()), "["), "]").replace(" ", "");
        institutionRights.setExtend1(unusableTime);
        institutionRights.setBatchNo(batchNo);
        institutionRights.setId(id);
        institutionRights.setInstitutionRequestTime(new Date());
        institutionRights.setEnabled(true);
        institutionRights.setCreateTime(new Date());
        institutionRights.setCreator("对外API新增记录");
        return institutionRightsMapper.insert(institutionRights);
    }

    /**
     * 分页查询
     *
     * @param institutionRightsDTO 查询DTO
     * @return PageInfo<InstitutionRightsVO>
     * @since 200000
     */
    @Override
    public PageInfo<InstitutionRightsVO> pageRightsInfo(InstitutionRightsPageDTO institutionRightsDTO) {
        List<InstitutionRightsVO> institutionRightsVOS = institutionRightsMapper.pageRightsInfo(institutionRightsDTO);
        return new PageInfo<>(institutionRightsVOS);
    }

    /**
     * 查询详情
     *
     * @param institutionRightsDTO 查询DTO
     * @return List<InstitutionRights>
     */
    @Override
    public InstitutionRightsInfoVO selectRightsInfo(InstitutionRightsDTO institutionRightsDTO) {
        InstitutionRights institutionRights = institutionRightsMapper.selectRightsInfo(institutionRightsDTO);
        InstitutionRightsInfoVO institutionRightsInfoVO = new InstitutionRightsInfoVO();
        BeanUtils.copyProperties(institutionRights, institutionRightsInfoVO);
        institutionRightsInfoVO.setMerchantIds(Arrays.asList(institutionRights.getMerchantId().split(",")));
        institutionRightsInfoVO.setMerchantNames(Arrays.asList(institutionRights.getMerchantName().split(",")));
        institutionRightsInfoVO.setUnusableTime(Arrays.asList(institutionRights.getExtend1().split(",")));
        institutionRightsInfoVO.setSetImages(Arrays.asList(institutionRights.getSetImages().split(",")));
        return institutionRightsInfoVO;
    }

    /**
     * 导出
     *
     * @param institutionRightsDTO 查询DTO
     * @return List<InstitutionRightsVO>
     */
    @Override
    public List<InstitutionRightsVO> exportRightsInfo(InstitutionRightsExportDTO institutionRightsDTO) {
        return institutionRightsMapper.exportRightsInfo(institutionRightsDTO);
    }

    /**
     * 修改权益
     *
     * @param institutionRightsDTO 修改DTO
     * @param updateName
     * @return INT
     */
    @Override
    public int updateRightsInfo(InstitutionRightsDTO institutionRightsDTO, String updateName) {
        InstitutionRights institutionRights = institutionRightsMapper.selectByPrimaryKey(institutionRightsDTO.getId());
        if (institutionRights == null) {
            // 权益不存在
            throw new BusinessException(EResultEnum.EQUITY_DOES_NOT_EXIST.getCode());
        }
        BeanUtils.copyProperties(institutionRightsDTO, institutionRights, ReflexClazzUtils.getNullPropertyNames(institutionRightsDTO));
        institutionRights.setModifier(updateName);
        institutionRights.setUpdateTime(new Date());
        return institutionRightsMapper.updateByPrimaryKeySelective(institutionRights);
    }

    /**
     * 导入数据
     *
     * @param institutionRights 导入实体
     * @return INT
     */
    @Override
    public int importRightsInfo(List<InstitutionRights> institutionRights) {
        return institutionRightsMapper.insertList(institutionRights);
    }

    /**
     * 对外的查询方法
     *
     * @param institutionRightsInfoApiDTO 查询DTO
     * @return List<InstitutionRights>
     */
    @Override
    public List<InstitutionRightsApiVO> getRightsInfo(InstitutionRightsInfoApiDTO institutionRightsInfoApiDTO) {
         if (!commonService.checkUniversalSign(institutionRightsInfoApiDTO)) {
             throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
         }
        //判断商户和机构有没有关联关系
        Merchant merchant = commonRedisService.getMerchant(institutionRightsInfoApiDTO.getMerchantId());
        if (!merchant.getInstitutionId().equals(merchant.getInstitutionId())) {
            throw new BusinessException(EResultEnum.INSTITUTION_AND_MERCHANT_NOT_RELATED.getCode());
        }
        List<InstitutionRightsApiVO> rightsInfos = institutionRightsMapper.getRightsInfo(institutionRightsInfoApiDTO);
        rightsInfos.forEach(n -> {
            n.setMerchantIds(Arrays.asList(n.getMerchantId()));
            n.setUnusableTime(Arrays.asList(n.getExtend1()));
        });
        return rightsInfos;

    }

    /**
     * 检查参数
     *
     * @param institutionRightsDTO 权益新增
     */
    private void checkInstitutionRightsDTO(InstitutionRightsDTO institutionRightsDTO) {
        //优惠类型 1-满减 2-折扣 3-套餐 4-定额
        Byte rightsType = institutionRightsDTO.getRightsType();
        // 1-满减
        if (rightsType.equals(RightsConstant.FULL_DISCOUNT)) {
            //套餐(票券)金额
            if (institutionRightsDTO.getTicketAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //满减金额
            if (institutionRightsDTO.getFullReductionAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //封顶金额
            if (institutionRightsDTO.getCapAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //叠加
            if (institutionRightsDTO.getOverlay() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        }
        // 2-折扣
        if (rightsType.equals(RightsConstant.DISCOUNT)) {
            //扣率
            if (institutionRightsDTO.getDiscount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //叠加 折扣不可叠加
            institutionRightsDTO.setOverlay(false);
        }
        // 3-套餐
        if (rightsType.equals(RightsConstant.PACKAGE)) {
            //叠加
            if (institutionRightsDTO.getOverlay() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            if (institutionRightsDTO.getPackageValue() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //套餐文字
            if (StringUtils.isBlank(institutionRightsDTO.getSetText()) || StringUtils.isBlank(institutionRightsDTO.getSetImages())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        } // 4-定额
        if (rightsType.equals(RightsConstant.QUOTA)) {
            //叠加
            if (institutionRightsDTO.getOverlay() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //套餐(票券)金额
            if (institutionRightsDTO.getTicketAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        }

        //---------------------通用判断---------------------
        //活动数量
        if (institutionRightsDTO.getActivityAmount() <= 0) {
            throw new BusinessException(EResultEnum.ILLEGAL_NUMBER_OF_ACTIVITIES.getCode());
        }

        //活动时间
        if (institutionRightsDTO.getStartTime() != null && institutionRightsDTO.getEndTime() != null) {
            if (institutionRightsDTO.getStartTime().getTime() - DateUtil.beginOfDay(new Date()).getTime() < 0) {
                throw new BusinessException(EResultEnum.EVENT_START_TIME_IS_ILLEGAL.getCode());
            }
            if (institutionRightsDTO.getEndTime().getTime() - institutionRightsDTO.getStartTime().getTime() <= 0) {
                throw new BusinessException(EResultEnum.EVENT_END_TIME_IS_ILLEGAL.getCode());
            }
        }
        //不可用时间
        List<String> unusableTimes = institutionRightsDTO.getUnusableTime();
        if (unusableTimes.size() > 0) {
            Collections.sort(unusableTimes);
            DateTime start = DateUtil.parse(unusableTimes.get(0), "yyyy-MM-dd");
            DateTime end = DateUtil.parse(unusableTimes.get(unusableTimes.size() - 1), "yyyy-MM-dd");
            if (institutionRightsDTO.getStartTime() != null && institutionRightsDTO.getEndTime() != null) {
                if (start.getTime() - DateUtil.beginOfDay(institutionRightsDTO.getStartTime()).getTime() < 0) {
                    throw new BusinessException(EResultEnum.UNAVAILABLE_TIME_IS_ILLEGAL.getCode());
                }
                if (DateUtil.beginOfDay(institutionRightsDTO.getEndTime()).getTime() - end.getTime() < 0) {
                    throw new BusinessException(EResultEnum.UNAVAILABLE_TIME_IS_ILLEGAL.getCode());
                }
            }
        }

    }

    /**
     * 检查参数
     *
     * @param institutionRightsApiDTO 权益新增
     */
    private void checkInstitutionRightsApiDTO(RightsApiDTO institutionRightsApiDTO) {
        //优惠类型 1-满减 2-折扣 3-套餐 4-定额
        Byte rightsType = institutionRightsApiDTO.getPreferentialType();
        // 1-满减
        if (rightsType.equals(RightsConstant.FULL_DISCOUNT)) {
            //票券金额
            if (institutionRightsApiDTO.getTicketAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //满减金额
            if (institutionRightsApiDTO.getConsumptionAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //封顶金额
            if (institutionRightsApiDTO.getMaximumDiscountAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //叠加
            if (institutionRightsApiDTO.getStackUsing() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        }
        // 2-折扣
        if (rightsType.equals(RightsConstant.DISCOUNT)) {
            //扣率
            if (institutionRightsApiDTO.getDiscount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //叠加 折扣不可叠加
            institutionRightsApiDTO.setStackUsing(false);
        }
        // 3-套餐
        if (rightsType.equals(RightsConstant.PACKAGE)) {
            //叠加
            if (institutionRightsApiDTO.getStackUsing() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            if (institutionRightsApiDTO.getPackagePrice() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //套餐文字
            if (StringUtils.isBlank(institutionRightsApiDTO.getPackageDetails()) || StringUtils.isBlank(institutionRightsApiDTO.getPackagePicture())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        } // 4-定额
        if (rightsType.equals(RightsConstant.QUOTA)) {
            //叠加
            if (institutionRightsApiDTO.getStackUsing() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //票券金额
            if (institutionRightsApiDTO.getTicketAmount() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
        }

        //---------------------通用判断---------------------
        //活动数量
        if (institutionRightsApiDTO.getNumberOfActivities() <= 0) {
            throw new BusinessException(EResultEnum.ILLEGAL_NUMBER_OF_ACTIVITIES.getCode());
        }

        //活动时间
        if (institutionRightsApiDTO.getActivityStartTime() != null && institutionRightsApiDTO.getActivityEndTime() != null) {
            if (institutionRightsApiDTO.getActivityStartTime().getTime() - DateUtil.beginOfDay(new Date()).getTime() < 0) {
                throw new BusinessException(EResultEnum.EVENT_START_TIME_IS_ILLEGAL.getCode());
            }
            if (institutionRightsApiDTO.getActivityEndTime().getTime() - institutionRightsApiDTO.getActivityStartTime().getTime() <= 0) {
                throw new BusinessException(EResultEnum.EVENT_END_TIME_IS_ILLEGAL.getCode());
            }
        }
        //不可用时间
        List<String> unusableTimes = institutionRightsApiDTO.getUnusableTime();
        if (unusableTimes.size() > 0) {
            Collections.sort(unusableTimes);
            DateTime start = DateUtil.parse(unusableTimes.get(0), "yyyy-MM-dd");
            DateTime end = DateUtil.parse(unusableTimes.get(unusableTimes.size() - 1), "yyyy-MM-dd");
            if (institutionRightsApiDTO.getActivityStartTime() != null && institutionRightsApiDTO.getActivityEndTime() != null) {
                if (start.getTime() - DateUtil.beginOfDay(institutionRightsApiDTO.getActivityStartTime()).getTime() < 0) {
                    throw new BusinessException(EResultEnum.UNAVAILABLE_TIME_IS_ILLEGAL.getCode());
                }
                if (institutionRightsApiDTO.getActivityEndTime().getTime() - end.getTime() < 0) {
                    throw new BusinessException(EResultEnum.UNAVAILABLE_TIME_IS_ILLEGAL.getCode());
                }
            }
        }
    }

    /**
     * 查询当前请求机构权益信息还在活动周期的有效机构权益信息
     *
     * @return List<InstitutionRights>
     */
    @Override
    public List<InstitutionRights> getRightsInfoLists() {
        return institutionRightsMapper.getRightsInfoLists();
    }


    /**
     * 新增查询机构权益信息下拉框用
     *
     * @param institutionRightsDTO
     * @return
     */
    @Override
    public PageInfo<InstitutionRights> pageRightsInfoList(InstitutionRightsQueryDTO institutionRightsDTO) {
        List<InstitutionRights> institutionRightsVOS = institutionRightsMapper.pageRightsInfoList(institutionRightsDTO);
        return new PageInfo<>(institutionRightsVOS);
    }
}


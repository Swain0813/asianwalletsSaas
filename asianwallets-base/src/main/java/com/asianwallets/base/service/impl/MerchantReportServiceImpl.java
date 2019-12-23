package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.InstitutionMapper;
import com.asianwallets.base.dao.MerchantMapper;
import com.asianwallets.base.dao.MerchantReportMapper;
import com.asianwallets.base.service.MerchantReportService;
import com.asianwallets.common.dto.MerchantReportDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.MerchantReportVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.asianwallets.common.utils.ReflexClazzUtils.getNullPropertyNames;

@Service
@Transactional
public class MerchantReportServiceImpl implements MerchantReportService {

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantReportMapper merchantReportMapper;

    /**
     * 添加报备
     *
     * @param merchantReportDTO
     * @return
     */
    @Override
    public int addReport(MerchantReportDTO merchantReportDTO) {
        //判断必要参数
        if (StringUtils.isBlank(merchantReportDTO.getChannelCode()) || StringUtils.isBlank(merchantReportDTO.getInstitutionId()) || StringUtils.isBlank(merchantReportDTO.getMerchantId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //判断机构 商户 通道 是否存在
        Institution institution = institutionMapper.selectByPrimaryKey(merchantReportDTO.getInstitutionId());
        if (institution == null) {
            throw new BusinessException(EResultEnum.INSTITUTION_DOES_NOT_EXIST.getCode());
        }
        Channel channel = channelMapper.selectByChannelCode(merchantReportDTO.getChannelCode());
        if (channel == null) {
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantReportDTO.getMerchantId());
        if (merchant == null) {
            throw new BusinessException(EResultEnum.MERCHANT_DOES_NOT_EXIST.getCode());
        }
        //shopCode  不可以重复
        if (merchantReportMapper.selectByShopCode(merchantReportDTO.getShopCode()) != null) {
            throw new BusinessException(EResultEnum.SHOP_CODE_EXIST.getCode());
        }
        //检查重复添加
        if (merchantReportMapper.selectByChannelCodeAndMerchantId(merchantReportDTO.getChannelCode(), merchantReportDTO.getMerchantId()) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        //检查该商户是否属于此机构
        if (!StringUtils.isBlank(merchant.getInstitutionId()) && !merchant.getInstitutionId().equals(institution.getId())) {
            //YES
            throw new BusinessException(EResultEnum.SHOP_CODE_EXIST.getCode());
        }
        //Assignment
        MerchantReport merchantReport = new MerchantReport();
        merchantReport.setMerchantId(merchant.getId());
        merchantReport.setChannelCode(channel.getChannelCode());
        merchantReport.setInstitutionId(institution.getId());
        merchantReport.setInstitutionName(institution.getCnName());
        merchantReport.setMerchantName(merchant.getCnName());
        merchantReport.setChannelName(channel.getChannelCnName());
        merchantReport.setSubMerchantCode(merchantReportDTO.getSubMerchantCode());
        merchantReport.setSubMerchantName(merchantReportDTO.getSubMerchantName());
        merchantReport.setShopName(merchantReportDTO.getShopName());
        merchantReport.setShopCode(merchantReportDTO.getShopCode());
        merchantReport.setSubAppid(merchantReportDTO.getSubAppid());
        merchantReport.setEnabled(true);
        merchantReport.setExtend1(merchantReportDTO.getExtend1());
        merchantReport.setExtend2(merchantReportDTO.getExtend2());
        merchantReport.setId(IDS.uniqueID().toString());
        merchantReport.setCreateTime(new Date());
        merchantReport.setCreator(merchantReportDTO.getCreator());
        return merchantReportMapper.insert(merchantReport);
    }

    /**
     * @param merchantReportDTO DTO
     * @return PageInfo<MerchantReportVO>
     * @Description 查询
     */
    @Override
    public PageInfo<MerchantReportVO> pageReport(MerchantReportDTO merchantReportDTO) {
        return new PageInfo<>(merchantReportMapper.pageReport(merchantReportDTO));
    }

    /**
     * 修改报备信息
     *
     * @param merchantReportDTO
     * @return
     */
    @Override
    public int updateReport(MerchantReportDTO merchantReportDTO) {
        //Determine the necessary parameters
        if (StringUtils.isBlank(merchantReportDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        MerchantReport merchantReport = new MerchantReport();
        BeanUtils.copyProperties(merchantReportDTO, merchantReport, getNullPropertyNames(merchantReportDTO));
        //change the data
        return merchantReportMapper.updateByPrimaryKeySelective(merchantReport);
    }

    /**
     * 启用禁用报备信息
     *
     * @param merchantReportDTO
     * @return
     */
    @Override
    public int banReport(MerchantReportDTO merchantReportDTO) {
        //Determine the necessary parameters
        if (StringUtils.isBlank(merchantReportDTO.getId()) || merchantReportDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //Change the data
        return merchantReportMapper.banReport(merchantReportDTO.getId(), merchantReportDTO.getEnabled(), merchantReportDTO.getModifier());
    }

    /**
     * Export data information
     *
     * @param merchantReportDTO
     * @return
     */
    @Override
    public List<MerchantReportVO> exportReport(MerchantReportDTO merchantReportDTO) {
        List<MerchantReportVO> merchantReportVOS = merchantReportMapper.pageReport(merchantReportDTO);
        List<MerchantReportVO> collect = merchantReportVOS.parallelStream().sorted(Comparator.comparing(MerchantReportVO::getCreateTime).reversed()).collect(Collectors.toList());
        for (MerchantReportVO merchantReportVO : collect) {
            if (merchantReportVO.getEnabled()) {
                merchantReportVO.setEnabledStr("启用");
            } else {
                merchantReportVO.setEnabledStr("禁用");
            }
        }
        return collect;
    }
}

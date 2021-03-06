package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.InstitutionChannelMapper;
import com.asianwallets.base.dao.InstitutionProductMapper;
import com.asianwallets.base.dao.ProductChannelMapper;
import com.asianwallets.base.service.InstitutionProductChannelService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.InstitutionChannelQueryDTO;
import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.dto.InstitutionProductDTO;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionChannel;
import com.asianwallets.common.entity.InstitutionProduct;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.InstitutionChannelQueryVO;
import com.asianwallets.common.vo.InstitutionProductChannelVO;
import com.asianwallets.common.vo.InstitutionProductVO;
import com.asianwallets.common.vo.ProductChannelVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class InstitutionProductChannelServiceImpl implements InstitutionProductChannelService {

    @Autowired
    private InstitutionProductMapper institutionProductMapper;

    @Autowired
    private InstitutionChannelMapper institutionChannelMapper;

    @Autowired
    private ProductChannelMapper productChannelMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 新增机构关联产品通道信息
     *
     * @param username                         用户名
     * @param institutionProductChannelDTOList 机构产品通道输入实体集合
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addInstitutionProductChannel(String username, List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        if (institutionProductMapper.selectCountByInstitutionId(institutionProductChannelDTOList.get(0).getInstitutionId()) > 0) {
            log.info("==========【新增机构关联产品通道信息】==========【信息已存在】");
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        for (InstitutionProductChannelDTO institutionProductChannelDTO : institutionProductChannelDTOList) {
            //通道不选择的场合 报错误信息输入参数不能为空
            if (ArrayUtil.isEmpty(institutionProductChannelDTO.getChannelIdList()) || StringUtils.isEmpty(institutionProductChannelDTO.getInstitutionName())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //机构产品信息
            InstitutionProduct institutionProduct = new InstitutionProduct();
            String insProId = IDS.uuid2();
            institutionProduct.setId(insProId);
            institutionProduct.setInstitutionId(institutionProductChannelDTO.getInstitutionId());
            institutionProduct.setProductId(institutionProductChannelDTO.getProductId());
            institutionProduct.setInstitutionName(institutionProductChannelDTO.getInstitutionName());
            institutionProduct.setProductAbbrev(institutionProductChannelDTO.getProductAbbrev());
            institutionProduct.setProductImg(institutionProductChannelDTO.getProductImg());
            institutionProduct.setProductDetailsLogo(institutionProductChannelDTO.getProductDetailsLogo());
            institutionProduct.setProductPrintLogo(institutionProductChannelDTO.getProductPrintLogo());
            institutionProduct.setRank(institutionProductChannelDTO.getRank());
            institutionProduct.setCreateTime(new Date());
            institutionProduct.setCreator(username);
            institutionProductMapper.insert(institutionProduct);
            if (!ArrayUtil.isEmpty(institutionProductChannelDTO.getChannelIdList())) {
                //机构通道信息
                List<InstitutionChannel> institutionChannelList = Lists.newArrayList();
                for (String channelId : institutionProductChannelDTO.getChannelIdList()) {
                    InstitutionChannel institutionChannel = new InstitutionChannel();
                    institutionChannel.setId(IDS.uuid2());
                    institutionChannel.setInsProId(insProId);
                    institutionChannel.setChannelId(channelId);
                    institutionChannel.setCreateTime(new Date());
                    institutionChannel.setCreator(username);
                    institutionChannelList.add(institutionChannel);
                }
                institutionChannelMapper.insertList(institutionChannelList);
            }
        }
        return 1;
    }

    /**
     * 修改机构关联产品通道信息
     *
     * @param username                         用户名
     * @param institutionProductChannelDTOList 机构产品通道输入实体集合
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateInsProChaByInsId(String username, List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        //查询机构产品中间表ID
        List<String> insProIdList = institutionProductMapper.selectIdListByInstitutionId(institutionProductChannelDTOList.get(0).getInstitutionId());
        if (!ArrayUtil.isEmpty(insProIdList)) {
            //删除机构通道中间表信息
            institutionChannelMapper.deleteByInsProIdList(insProIdList);
        }
        //删除机构产品中间表信息
        institutionProductMapper.deleteByInstitutionId(institutionProductChannelDTOList.get(0).getInstitutionId());
        for (InstitutionProductChannelDTO institutionProductChannelDTO : institutionProductChannelDTOList) {
            //通道不选择的场合 报错误信息输入参数不能为空
            if (ArrayUtil.isEmpty(institutionProductChannelDTO.getChannelIdList()) || StringUtils.isEmpty(institutionProductChannelDTO.getInstitutionName())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //机构产品信息
            InstitutionProduct institutionProduct = new InstitutionProduct();
            String insProId = IDS.uuid2();
            institutionProduct.setId(insProId);
            institutionProduct.setInstitutionId(institutionProductChannelDTO.getInstitutionId());
            institutionProduct.setProductId(institutionProductChannelDTO.getProductId());
            institutionProduct.setRank(institutionProductChannelDTO.getRank());
            institutionProduct.setInstitutionName(institutionProductChannelDTO.getInstitutionName());
            institutionProduct.setProductAbbrev(institutionProductChannelDTO.getProductAbbrev());
            institutionProduct.setProductImg(institutionProductChannelDTO.getProductImg());
            institutionProduct.setProductDetailsLogo(institutionProductChannelDTO.getProductDetailsLogo());
            institutionProduct.setProductPrintLogo(institutionProductChannelDTO.getProductPrintLogo());
            institutionProduct.setCreateTime(new Date());
            institutionProduct.setUpdateTime(new Date());
            institutionProduct.setCreator(username);
            institutionProduct.setModifier(username);
            institutionProductMapper.insert(institutionProduct);
            if (!ArrayUtil.isEmpty(institutionProductChannelDTO.getChannelIdList())) {
                List<InstitutionChannel> institutionChannelList = Lists.newArrayList();
                for (String channelId : institutionProductChannelDTO.getChannelIdList()) {
                    InstitutionChannel institutionChannel = new InstitutionChannel();
                    institutionChannel.setId(IDS.uuid2());
                    institutionChannel.setInsProId(insProId);
                    institutionChannel.setChannelId(channelId);
                    institutionChannel.setCreateTime(new Date());
                    institutionChannel.setUpdateTime(new Date());
                    institutionChannel.setCreator(username);
                    institutionChannel.setModifier(username);
                    institutionChannelList.add(institutionChannel);
                }
                institutionChannelMapper.insertList(institutionChannelList);
            }
        }
        return 1;
    }

    /**
     * 根据机构ID查询机构关联产品通道信息
     *
     * @param institutionId 机构ID
     * @param merchantId    商户ID
     * @return 机构产品通道输出实体集合
     */
    @Override
    public List<InstitutionProductChannelVO> getInsProChaByInsId(String institutionId, String merchantId) {
        return institutionProductMapper.selectRelevantInfoByInstitutionId(institutionId, merchantId, auditorProvider.getLanguage());
    }

    /**
     * 查询所有产品关联通道信息
     *
     * @return 产品通道集合
     */
    @Override
    public List<ProductChannelVO> getAllProCha() {
        return productChannelMapper.getAllProCha(auditorProvider.getLanguage());
    }

    /**
     * 分页查询机构参数设置
     *
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public PageInfo<InstitutionProduct> pageInstitutionRequests(InstitutionRequestDTO institutionRequestDTO) {
        return new PageInfo(institutionProductMapper.pageInstitutionRequests(institutionRequestDTO));
    }

    /**
     * 分页查询机构产品信息
     * @param institutionProductDTO
     * @return
     */
    @Override
    public PageInfo<InstitutionProductVO> pageInstitutionPro(InstitutionProductDTO institutionProductDTO){
        //当前请求语言
        institutionProductDTO.setLanguage(auditorProvider.getLanguage());
        institutionProductDTO.setSort("ip.rank");
        institutionProductDTO.setSort("ip.create_time");
        return new PageInfo(institutionProductMapper.pageInstitutionPro(institutionProductDTO));
    }

    /**
     * 分页查询机构通道信息
     * @param institutionChannelQueryDTO
     * @return
     */
    @Override
    public PageInfo<InstitutionChannelQueryVO> pageInstitutionCha(InstitutionChannelQueryDTO institutionChannelQueryDTO){
        return new PageInfo(institutionProductMapper.pageInstitutionCha(institutionChannelQueryDTO));
    }

}

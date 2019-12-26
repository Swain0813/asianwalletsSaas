package com.asianwallets.base.service;

import com.asianwallets.common.dto.InstitutionChannelQueryDTO;
import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.dto.InstitutionProductDTO;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionProduct;
import com.asianwallets.common.vo.InstitutionChannelQueryVO;
import com.asianwallets.common.vo.InstitutionProductChannelVO;
import com.asianwallets.common.vo.InstitutionProductVO;
import com.asianwallets.common.vo.ProductChannelVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface InstitutionProductChannelService {


    /**
     * 新增机构关联产品通道信息
     *
     * @param username                         用户名
     * @param institutionProductChannelDTOList 机构产品通道输入实体集合
     * @return 修改条数
     */
    int addInstitutionProductChannel(String username, List<InstitutionProductChannelDTO> institutionProductChannelDTOList);

    /**
     * 修改机构关联产品通道信息
     *
     * @param username                         用户名
     * @param institutionProductChannelDTOList 机构产品通道输入实体集合
     * @return 修改条数
     */
    int updateInsProChaByInsId(String username, List<InstitutionProductChannelDTO> institutionProductChannelDTOList);

    /**
     * 根据机构ID查询机构关联产品通道信息
     *
     * @param institutionId 机构ID
     * @param merchantId    商户ID
     * @return 机构产品通道输出实体集合
     */
    List<InstitutionProductChannelVO> getInsProChaByInsId(String institutionId, String merchantId);

    /**
     * 查询所有产品关联通道信息
     *
     * @return 产品通道集合
     */
    List<ProductChannelVO> getAllProCha();

    /**
     * 分页查询机构参数设置
     *
     * @param institutionRequestDTO
     * @return
     */
    PageInfo<InstitutionProduct> pageInstitutionRequests(InstitutionRequestDTO institutionRequestDTO);

    /**
     * 分页查询机构产品信息
     * @param institutionProductDTO
     * @return
     */
    PageInfo<InstitutionProductVO> pageInstitutionPro(InstitutionProductDTO institutionProductDTO);

    /**
     * 分页查询机构通道信息
     * @param institutionChannelQueryDTO
     * @return
     */
    PageInfo<InstitutionChannelQueryVO> pageInstitutionCha(InstitutionChannelQueryDTO institutionChannelQueryDTO);

}

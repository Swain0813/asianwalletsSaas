package com.asianwallets.base.service;


import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.entity.ProductChannel;
import com.asianwallets.common.vo.InstitutionProductChannelVO;

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
     * @param insId 机构ID
     * @return 机构产品通道输出实体集合
     */
    List<InstitutionProductChannelVO> getInsProChaByInsId(String insId);

    /**
     * 查询所有产品关联通道信息
     *
     * @return 产品通道集合
     */
    List<ProductChannel> getAllProCha();

}

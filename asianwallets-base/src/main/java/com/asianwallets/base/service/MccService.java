package com.asianwallets.base.service;

import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.vo.MccVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * mcc
 */
public interface MccService {

    /**
     * 新增Mcc
     *
     * @param payTypeDTO
     * @return
     */
    int addMcc(MccDTO payTypeDTO);


    /**
     * 修改Mcc
     *
     * @param payTypeDTO
     * @return
     */
    int updateMcc(MccDTO payTypeDTO);

    /**
     * 分页查询
     *
     * @param payTypeDTO
     * @return
     */
    PageInfo<MccVO> pageMcc(MccDTO payTypeDTO);

    /**
     * 禁用Mcc
     *
     * @param payTypeDTO
     * @return
     */
    int banMcc(MccDTO payTypeDTO);

    /**
     * @param language
     * @return
     */
    List<MccVO> inquireAllMcc(String language);

    /**
     * 导入MCC
     *
     * @param list
     * @return
     */
    int importMcc(List<Mcc> list);

    /**
     * 导出
     *
     * @param mccDto
     * @return
     */
    List<MccVO> exportMcc(MccDTO mccDto);
}

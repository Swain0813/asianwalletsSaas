package com.asianwallets.base.service;

import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.entity.PayType;
import com.asianwallets.common.vo.PayTypeVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 支付方式
 */
public interface PayTypeService {
    /**
     * 新增支付方式
     *
     * @param payTypeDTO
     * @return
     */
    int addPayType(PayTypeDTO payTypeDTO);


    /**
     * 修改支付方式
     *
     * @param payTypeDTO
     * @return
     */
    int updatePayType(PayTypeDTO payTypeDTO);

    /**
     * 分页查询
     *
     * @param payTypeDTO
     * @return
     */
    PageInfo<PayTypeVO> pagePayType(PayTypeDTO payTypeDTO);

    /**
     * 禁用支付方式
     *
     * @param payTypeDTO
     * @return
     */
    int banPayType(PayTypeDTO payTypeDTO);

    /**
     * @return
     */
    List<PayType> inquireAllPaytype();
}

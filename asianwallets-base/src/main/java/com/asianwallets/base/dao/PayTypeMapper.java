package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.entity.PayType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付方式
 */
@Repository
public interface PayTypeMapper extends BaseMapper<PayType> {
    /**
     * 通过支付方式中英文名查找
     *
     * @param payTypeDTO
     * @return
     */
    PayType selectByCnOrEnName(PayTypeDTO payTypeDTO);

    /**
     * 分页查询
     *
     * @param payTypeDTO
     * @return
     */
    List<PayType> pagePayType(PayTypeDTO payTypeDTO);
    /*int deleteByPrimaryKey(String id);

    int insert(PayType record);

    int insertSelective(PayType record);

    PayType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayType record);

    int updateByPrimaryKey(PayType record);*/
}
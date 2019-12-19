package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据产品编码获取产品
     *
     * @param productCode 产品编码
     * @return 产品
     */
    Product selectByProductCode(Integer productCode);
}

package com.asianwallets.trade.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Product;
import org.apache.ibatis.annotations.Param;
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


    /**
     * @Author YangXu
     * @Date 2019/8/23
     * @Descripate
     * @return 根据机构ID和订单币种和产品类型查询产品
     **/
    Product selectByCodeAndType(@Param("productCode") Integer productCode, @Param("merchantId") String merchantId, @Param("type") Byte type);

    /**
     * 根据产品名称获取产品信息
     * @param productName
     * @return
     */
    Product selectByProductName(String productName);
}

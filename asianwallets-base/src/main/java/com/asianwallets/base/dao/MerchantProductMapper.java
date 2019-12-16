package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.MerchantProductDTO;
import com.asianwallets.common.entity.MerchantProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Repository
public interface MerchantProductMapper extends  BaseMapper<MerchantProduct> {


    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 查询商户是否已经分配产品
     * @return
     **/
    @Select("select count(1) from merchant_product where merchant_id = #{merchantId} and product_id = #{productId}")
    int selectCountbyMerIdProId(@Param("merchantId") String merchantId, @Param("productId") String productId);


    MerchantProduct getMerchantProductByMerIdAndProId(@Param("merchantId") String merchantId, @Param("productId") String productId);

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户产品信息
     * @return
     **/
    List<MerchantProduct> pageFindMerProduct(MerchantProductDTO merchantProductDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户产品信息
     * @return
     **/
    List<MerchantProduct> exportMerProduct(MerchantProductDTO merchantProductDTO);
}

package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.vo.ProductVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 产品表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-12-05
 */
@Repository
public interface ProductMapper extends  BaseMapper<Product> {

   /**
    * @Author YangXu
    * @Date 2019/12/5
    * @Descripate 分页查询产品
    * @return
    **/
    List<ProductVO> pageProduct(ProductDTO productDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 查询产品
     * @return
     **/
    List<ProductVO> selectProduct(ProductDTO productDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/1/29
     * @Descripate 根据支付方式查询所有产品
     **/
    @Select("select  p.id as id ,concat(pt.name,'-',p.currency) as payType from product p, pay_type pt where p.pay_type = #{payType} and p.pay_type = pt.extend1  and pt.language =#{language} ")
    List<Product> selectProductByPayType(@Param("payType") String payType, @Param("language") String language);
}

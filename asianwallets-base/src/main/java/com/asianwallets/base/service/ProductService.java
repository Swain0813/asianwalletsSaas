package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.vo.ProductVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 * 产品表 服务类
 * </p>
 *
 * @author yx
 * @since 2019-12-05
 */
public interface ProductService extends BaseService<Product> {


    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 添加产品
     * @return
     **/
    int addProduct(String name, ProductDTO productDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 更新产品
     * @return
     **/
    int updateProduct(String username, ProductDTO productDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/1/29
     * @Descripate 分页查询产品
     **/
    PageInfo<ProductVO> pageProduct(ProductDTO productDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/6
     * @Descripate 查询产品
     * @return
     **/
    List<ProductVO> selectProduct(ProductDTO productDTO);
}

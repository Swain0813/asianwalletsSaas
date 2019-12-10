package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.AuaditProductDTO;
import com.asianwallets.common.dto.MerProDTO;
import com.asianwallets.common.dto.MerchantProductDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.response.BaseResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
public interface MerchantProductService extends BaseService<MerchantProduct> {


    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 添加商户产品
     * @return
     **/
    int addMerchantProduct(String name, List<MerchantProductDTO> merchantProductDTOs);

    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 商户分配通道
     * @return
     **/
    int allotMerProductChannel(String username, MerProDTO merProDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 修改商户产品
     * @return
     **/
    int updateMerchantProduct(String name, MerchantProductDTO merchantProductDTO);

   /**
    * @Author YangXu
    * @Date 2019/12/10
    * @Descripate 批量审核商户产品
    * @return
    **/
   BaseResponse auditMerchantProduct(String username, AuaditProductDTO auaditProductDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户产品信息
     * @return
     **/
    PageInfo<MerchantProduct> pageFindMerProduct(MerchantProductDTO merchantProductDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户审核产品信息
     * @return
     **/
    PageInfo<MerchantProductAudit> pageFindMerProductAudit(MerchantProductDTO merchantProductDTO);
}

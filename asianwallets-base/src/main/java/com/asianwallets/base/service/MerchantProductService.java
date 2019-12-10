package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.MerChannelVO;
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

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 根据产品Id查询商户产品详情
     * @return
     **/
    MerchantProduct getMerProductById(String merProductId);

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 根据Id查询商户产品审核详情
     * @return
     **/
    MerchantProductAudit getMerProductAuditById(String merProductId);


    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户产品通道管理信息
     * @return
     **/
    PageInfo<MerChannelVO> pageFindMerProChannel(SearchChannelDTO searchChannelDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 修改机构通道
     * @return
     **/
    int updateMerchantChannel(String username, BatchUpdateSortDTO batchUpdateSort);
}

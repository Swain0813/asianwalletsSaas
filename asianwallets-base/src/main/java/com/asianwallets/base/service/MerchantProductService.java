package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.entity.MerchantProductAudit;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.MerChannelVO;
import com.asianwallets.common.vo.MerchantProductVO;
import com.asianwallets.common.vo.MerchantRelevantVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
public interface MerchantProductService extends BaseService<MerchantProduct> {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 添加商户产品
     **/
    int addMerchantProduct(String name, List<MerchantProductDTO> merchantProductDTOs);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 商户分配通道
     **/
    int allotMerProductChannel(String username, MerProDTO merProDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 修改商户产品
     **/
    int updateMerchantProduct(String name, MerchantProductDTO merchantProductDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 批量审核商户产品
     **/
    BaseResponse auditMerchantProduct(String username, AuaditProductDTO auaditProductDTO);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户产品信息
     **/
    PageInfo<MerchantProductVO> pageFindMerProduct(MerchantProductDTO merchantProductDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户审核产品信息
     **/
    PageInfo<MerchantProductAudit> pageFindMerProductAudit(MerchantProductDTO merchantProductDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 根据产品Id查询商户产品详情
     **/
    MerchantProductVO getMerProductById(String merProductId);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 根据Id查询商户产品审核详情
     **/
    MerchantProductAudit getMerProductAuditById(String merProductId);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户产品通道管理信息
     **/
    PageInfo<MerChannelVO> pageFindMerProChannel(SearchChannelDTO searchChannelDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 修改机构通道
     **/
    int updateMerchantChannel(String username, List<BatchUpdateSortDTO> batchUpdateSort);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 查询商户分配通道关联关系
     **/
    List<MerchantRelevantVO> getRelevantInfo(String merchantId);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户产品信息
     **/
    List<MerchantProduct> exportMerProduct(MerchantProductDTO merchantProductDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/11
     * @Descripate 根据商户通道Id查询商户通道详情
     **/
    MerChannelVO getMerChannelInfoById(String merChannelId);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/12
     * @Descripate 导出商户通道信息
     **/
    List<MerChannelVO> exportMerChannel(SearchChannelDTO searchChannelDTO);
}

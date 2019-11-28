package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantAudit;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
public interface MerchantService extends BaseService<Merchant> {

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加商户
     * @return
     **/
    int addMerchant(String name, MerchantDTO merchantDTO);


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 修改商户
     * @return
     **/
    int updateMerchant(String name, MerchantDTO merchantDTO);


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户信息列表
     * @return
     **/
    PageInfo<Merchant> pageFindMerchant(MerchantDTO merchantDTO);

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户审核信息列表
     * @return
     **/
    PageInfo<MerchantAudit> pageFindMerchantAudit(MerchantDTO merchantDTO);

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户信息详情
     * @return
     **/
    Merchant getMerchantInfo(String id);

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户审核信息详情
     * @return
     **/
    MerchantAudit getMerchantAuditInfo(String id);

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 审核商户信息接口
     * @return
     **/
    int auditMerchant(String username, String merchantId, Boolean enabled, String remark);

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 代理商下拉框
     * @return
     **/
    List<Merchant> getAllAgent(String merchantType);

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
     * @return
     **/
    List<Merchant> exportMerchant(MerchantDTO merchantDTO);
}

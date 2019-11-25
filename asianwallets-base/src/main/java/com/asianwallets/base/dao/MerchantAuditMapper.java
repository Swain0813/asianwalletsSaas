package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantAudit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface MerchantAuditMapper extends  BaseMapper<MerchantAudit> {

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户审核信息列表
     * @return
     **/
    List<MerchantAudit> pageFindMerchantAudit(MerchantDTO merchantDTO);


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户审核信息详情
     * @return
     **/
    MerchantAudit getMerchantAuditInfo(@Param("id") String id);
}

package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.MerchantProductDTO;
import com.asianwallets.common.entity.MerchantProductAudit;
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
public interface MerchantProductAuditMapper extends  BaseMapper<MerchantProductAudit> {

    /**
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 分页查询商户审核产品信息
     * @return
     **/
    List<MerchantProductAudit> pageFindMerProductAudit(MerchantProductDTO merchantProductDTO);
}

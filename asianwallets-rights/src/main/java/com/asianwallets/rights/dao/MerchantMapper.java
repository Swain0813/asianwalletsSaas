package com.asianwallets.rights.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * 根据商户编号获取商户信息
     *
     * @param code
     * @return
     */
    Merchant getMerchant(@Param("code") String code);

}

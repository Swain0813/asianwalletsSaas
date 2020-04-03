package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.PayType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 支付方式
 */
@Repository
public interface PayTypeMapper extends BaseMapper<PayType> {
    /**
     * 使用extend1与语言查询数据
     *
     * @param extend1
     * @param language
     * @return
     */
    PayType selectByExtend1AndLanguage(@Param("extend1") String extend1, @Param("language") String language);

}
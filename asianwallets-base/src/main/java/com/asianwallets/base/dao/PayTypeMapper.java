package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.entity.PayType;
import com.asianwallets.common.vo.PayTypeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付方式
 */
@Repository
public interface PayTypeMapper extends BaseMapper<PayType> {
    /**
     * 通过支付方式中英文名查找
     *
     * @param name
     * @param language
     * @return
     */
    PayType selectByNameAndLanguage(@Param("name") String name, @Param("language") String language);

    /**
     * 分页查询
     *
     * @param payTypeDTO
     * @return
     */
    List<PayTypeVO> pagePayType(PayTypeDTO payTypeDTO);

    /**
     * 使用extend1与语言查询数据
     *
     * @param extend1
     * @param language
     * @return
     */
    PayType selectByExtend1AndLanguage(@Param("extend1") String extend1, @Param("language") String language);

    /**
     * 查询所有支付方式
     *
     * @param language
     * @return
     */
    List<PayTypeVO> inquireAllPaytype(@Param("language") String language);

    /**
     * 使用extend1查询数据
     *
     * @param extend1
     * @return
     */
    List<PayType> selectByExtend1(@Param("extend1") String extend1);
}
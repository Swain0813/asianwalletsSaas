package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.vo.MccVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MCC数据
 */
@Repository
public interface MccMapper extends BaseMapper<Mcc> {
    /**
     * 通过支付方式中英文名查找
     *
     * @param mccDTO
     * @return
     */
    Mcc selectByName(MccDTO mccDTO);

    /**
     * 分页查询
     *
     * @param mccDTO
     * @return
     */
    List<MccVO> pageMcc(MccDTO mccDTO);

    /**
     * 使用extend1与语言查询数据
     *
     * @param extend1
     * @param language
     * @return
     */
    Mcc selectByExtend1AndLanguage(@Param("extend1") String extend1, @Param("language") String language);

    /**
     * 查询所有支付方式
     *
     * @param language
     * @return
     */
    List<MccVO> inquireAllMcc(@Param("language") String language);

    /**
     * 使用extend1查询数据
     *
     * @param extend1
     * @return
     */
    List<Mcc> selectByExtend1(@Param("extend1") String extend1);

    /**
     * 通过CODE查询不属于此ID的MCC数据
     *
     * @param mccDTO
     * @return
     */
    Mcc selectByCode(MccDTO mccDTO);

    /**
     * 依据ID修改所有的code
     *
     * @param mccDTO
     * @return
     */
    int updateById(MccDTO mccDTO);

    /**
     * 通过CODE修改
     *
     * @param mcc
     * @return
     */
    int updateByCode(Mcc mcc);
}
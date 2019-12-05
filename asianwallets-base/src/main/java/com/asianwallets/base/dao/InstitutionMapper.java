package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.vo.InstitutionExportVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 机构表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-22
 */
@Repository
public interface InstitutionMapper extends  BaseMapper<Institution> {

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 判断机构名称是否存在
     * @return
     **/
    @Select("select count(1) from institution where cn_name = #{InstitutionName} or en_name = #{InstitutionName}")
    int selectCountByInsName(@Param("InstitutionName") String InstitutionName);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询机构信息
     **/
    List<Institution>  pageFindInstitution(InstitutionDTO institutionDTO);

    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据机构Id查询机构信息详情
     * @return
     **/
    Institution getInstitutionInfo(@Param("id") String id, @Param("language") String language);

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 机构下拉框
     * @return
     **/
    @Select("select id as id ,cn_name as cnName ,en_name as enName from institution where enabled=1")
    List<Institution> getAllInstitution();

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 导出机构
     **/
    List<InstitutionExportVO> exportInstitution(InstitutionDTO institutionDTO);
}

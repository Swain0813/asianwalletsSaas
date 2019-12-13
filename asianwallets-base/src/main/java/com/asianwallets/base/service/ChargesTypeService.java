package com.asianwallets.base.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.ChargesTypeDTO;
import com.asianwallets.common.entity.Charges;
import com.asianwallets.common.vo.ChargesTypeVO;
import com.github.pagehelper.PageInfo;


/**
 * @Auther: shenxinran
 * @Date: 2019/1/22 15:40
 * @Description: 费率管理业务层接口
 */
public interface ChargesTypeService extends BaseService<Charges> {
    /**
     * 功能描述:添加费率
     *
     * @param:
     * @return:
     * @auther: shenxinran
     * @date: 2019/1/22 17:36
     */
    int addChargesType(ChargesTypeDTO chargesTypeDTO);

    /**
     * 功能描述: 更新费率
     *
     * @param:
     * @return:
     * @auther: shenxinran
     * @date: 2019/1/23 9:30
     */
    int updateChargesType(ChargesTypeDTO chargesTypeDTO);

    /**
     * 删除费率
     * @param enabled
     * @param id
     * @param modifier
     */
    int banChargesType(boolean enabled, String id, String modifier);

    /**
     * 功能描述: 条件查询费率
     *
     * @param: chargesTypeDTO 费率管理输入实体
     * @return: 查询到的费率
     * @auther: shenxinran
     * @date: 2019/1/25 11:45
     */
    PageInfo<ChargesTypeVO> pageChargesCondition(ChargesTypeDTO chargesTypeDTO);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Charges getChargesInfo(String id);
}

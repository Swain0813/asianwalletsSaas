package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.ChargesMapper;
import com.asianwallets.base.service.ChargesTypeService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.ChargesTypeDTO;
import com.asianwallets.common.entity.Charges;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ChargesTypeVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/1/22 15:40
 * @Description: 费率管理业务层实现类
 */
@Service
@Transactional
public class ChargesTypeServiceImpl extends BaseServiceImpl<Charges> implements ChargesTypeService {


    @Autowired
    private ChargesMapper chargesMapper;


    /**
     * 功能描述:添加费率
     *
     * @param chargesTypeDTO 输入实体
     * @return: void
     * @auther: shenxinran
     * @date: 2019/1/22 17:36
     */
    @Override
    public int addChargesType(ChargesTypeDTO chargesTypeDTO) {
        Charges charges = new Charges();
        chargesTypeDTO.setId(IDS.uuid2());
        chargesTypeDTO.setEnabled(true);
        chargesTypeDTO.setCreateTime(new Date());
        BeanUtils.copyProperties(chargesTypeDTO, charges);
        return chargesMapper.insert(charges);
    }

    /**
     * 功能描述: 更新费率
     *
     * @param chargesTypeDTO
     * @return: void
     * @auther: shenxinran
     * @date: 2019/1/23 9:30
     */
    @Override
    public int updateChargesType(ChargesTypeDTO chargesTypeDTO) {
        Charges charges = new Charges();
        BeanUtils.copyProperties(chargesTypeDTO, charges);
        charges.setUpdateTime(new Date());
        return chargesMapper.updateByPrimaryKeySelective(charges);
    }

    /**
     * 删除费率
     *
     * @param enabled
     * @param id
     * @param modifier
     */
    @Override
    public int banChargesType(boolean enabled, String id, String modifier) {
        Charges charges = new Charges();
        charges.setId(id);
        charges.setUpdateTime(new Date());
        charges.setModifier(modifier);
        charges.setEnabled(enabled);
        return chargesMapper.updateByPrimaryKeySelective(charges);
    }


    /**
     * 功能描述: 条件查询费率
     *
     * @param chargesTypeDTO
     * @param: chargesTypeDTO 费率管理输入实体
     * @return: 查询到的费率
     * @auther: shenxinran
     * @date: 2019/1/25 11:45
     */
    @Override
    public PageInfo<ChargesTypeVO> pageChargesCondition(ChargesTypeDTO chargesTypeDTO) {
        return new PageInfo<ChargesTypeVO>(chargesMapper.pageChargesCondition(chargesTypeDTO));
    }


    /**
     * 根据据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Charges getChargesInfo(String id) {
        return chargesMapper.selectByPrimaryKey(id);
    }
}

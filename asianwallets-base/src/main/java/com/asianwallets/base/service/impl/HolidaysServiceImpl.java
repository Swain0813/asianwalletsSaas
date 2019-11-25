package com.asianwallets.base.service.impl;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.entity.Holidays;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.HolidaysVO;
import com.asianwallets.base.dao.HolidaysMapper;
import com.asianwallets.base.service.HolidaysService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.List;

/**
 * 节假日接口实现类
 */
@Service
@Transactional
public class HolidaysServiceImpl extends BaseServiceImpl<Holidays> implements HolidaysService {

    @Autowired
    private HolidaysMapper holidaysMapper;

    /**
     * 添加节假日信息
     *
     * @param holidaysDTO 节假日输入实体
     * @param name        添加者姓名
     * @return 添加条数
     */
    @Override
    public int addHolidays(HolidaysDTO holidaysDTO, String name) {
        //非空check 国家编号
        if (StringUtils.isEmpty(holidaysDTO.getCountryName())) {
            throw new BusinessException(EResultEnum.HOLIDAYS_COUNTRY_IS_NOT_NULL.getCode());
        }
        //国家名称
        if (StringUtils.isEmpty(holidaysDTO.getCountryName())) {
            throw new BusinessException(EResultEnum.HOLIDAYS_COUNTRY_IS_NOT_NULL.getCode());
        }
        //日期
        if (holidaysDTO.getDate() == null) {
            throw new BusinessException(EResultEnum.HOLIDAYS_DATE_IS_NOT_NULL.getCode());
        }
        //节假日名称
        if (StringUtils.isEmpty(holidaysDTO.getName())) {
            throw new BusinessException(EResultEnum.HOLIDAYS_NAME_IS_NOT_NULL.getCode());
        }
        //判断添加的时间是否过期
        if (DateToolUtils.compareTime(holidaysDTO.getDate(), new Date())) {
            throw new BusinessException(EResultEnum.HOLIDAYS_ADD_TIME_EXPIRED.getCode());
        }
        //判断节假日信息是否存在
        if (holidaysMapper.selectByCountryAndDate(holidaysDTO.getCountryName(), holidaysDTO.getDate()) > 0) {
            throw new BusinessException(EResultEnum.HOLIDAYS_INFO_EXIST.getCode());
        }
        Holidays holidays = new Holidays();
        BeanUtils.copyProperties(holidaysDTO, holidays);
        holidays.setId(IDS.uuid2());
        holidays.setEnabled(true);
        holidays.setCreateTime(new Date());
        holidays.setCreator(name);
        return holidaysMapper.insert(holidays);
    }

    /**
     * 禁用节假日信息
     *
     * @param holidaysDTO 节假日id
     * @param name        修改者姓名
     * @return 修改条数
     */
    @Override
    public int banHolidays(HolidaysDTO holidaysDTO, String name) {
        //非空check 节假日id
        if (StringUtils.isEmpty(holidaysDTO.getId())) {
            throw new BusinessException(EResultEnum.HOLIDAYS_ID_IS_NOT_NULL.getCode());
        }
        //节假日状态
        if (holidaysDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Holidays holidays = new Holidays();
        holidays.setId(holidaysDTO.getId());
        holidays.setEnabled(holidaysDTO.getEnabled());
        holidays.setUpdateTime(new Date());
        holidays.setModifier(name);
        return holidaysMapper.updateByPrimaryKeySelective(holidays);
    }


    /**
     * 分页多条件查询节假日信息
     *
     * @param holidaysDTO 节假日输入实体
     * @return 节假日输出实体集合
     */
    @Override
    public PageInfo<HolidaysVO> getByMultipleConditions(HolidaysDTO holidaysDTO) {
        return new PageInfo(holidaysMapper.pageMultipleConditions(holidaysDTO));
    }


    /**
     * 导入节假日信息
     *
     * @param fileList
     * @return
     */
    @Override
    public int uploadFiles(List<Holidays> fileList) {
        return holidaysMapper.insertList(fileList);
    }
}

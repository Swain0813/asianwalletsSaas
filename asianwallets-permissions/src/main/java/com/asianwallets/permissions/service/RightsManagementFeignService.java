package com.asianwallets.permissions.service;

import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.vo.InstitutionRightsVO;

import java.util.List;

/**
 * 机构权益导出相关功能
 */
public interface RightsManagementFeignService {

    /**
     * 机构权益导出
     *
     * @param list
     * @param clazz
     * @param language
     * @return
     */
    ExcelWriter exportRights(List<InstitutionRightsVO> list, Class clazz, String language);
}

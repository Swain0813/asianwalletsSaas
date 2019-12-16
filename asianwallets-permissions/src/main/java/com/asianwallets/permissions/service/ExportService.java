package com.asianwallets.permissions.service;

import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.vo.*;

import java.util.ArrayList;
import java.util.List;

public interface ExportService {

    /**
     * Excel 导出机构
     *
     * @param institutions 对象集合
     * @param clazz        类名Class对象
     * @return ExcelWriter writer
     */
    ExcelWriter getInstitutionExcel(List<InstitutionExportVO> institutions, Class clazz);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
     **/
    ExcelWriter getMerchantExcel(ArrayList<MerchantExportVO> merchantExportVOS, Class clazz);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户产品
     **/
    ExcelWriter getMerchantProductExcel(ArrayList<MerchantProductExportVO> merchantProExportVOS, Class clazz);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户通道
     **/
    ExcelWriter getMerchantChannelExcel(ArrayList<MerChannelExportVO> merchantChannelExportVOS, Class clazz);

    /**
     * 导出订单信息
     *
     * @param exportOrdersVOList  订单集合
     * @param exportOrdersVOClass 订单导出class
     * @param writer
     * @return
     */
    ExcelWriter exportOrders(List<ExportOrdersVO> exportOrdersVOList, Class<ExportOrdersVO> exportOrdersVOClass, ExcelWriter writer);
}

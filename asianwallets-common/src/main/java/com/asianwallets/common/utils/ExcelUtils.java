package com.asianwallets.common.utils;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.constant.AsianWalletConstant;

import java.util.*;

public class ExcelUtils<T> {

    /**
     * 导出Excel文件
     *
     * @param dataList 数据集合
     * @param clazz    字节码对象
     * @return Excel输出流
     */
    public ExcelWriter exportExcel(List<T> dataList, Class<T> clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        //获取属性值字段和注释
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        //数据集合
        List<Object> sourceList = new ArrayList<>();
        //注释集合
        Set<Object> commentSet = new LinkedHashSet<>();
        for (T data : dataList) {
            //对象属性名对应属性值的Map
            Map<String, Object> objMap = BeanToMapUtil.beanToMap(data);
            List<Object> tempList = new ArrayList<>();
            for (int i = 0; i < property.length; i++) {
                for (String propertyName : objMap.keySet()) {
                    if (propertyName.equals(property[i])) {
                        commentSet.add(comment[i]);
                        tempList.add(objMap.get(propertyName));
                    }
                }
            }
            sourceList.add(tempList);
        }
        sourceList.add(0, commentSet);
        writer.write(sourceList);
        return writer;
    }
}

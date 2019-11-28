package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.vo.InstitutionExportVO;
import com.asianwallets.common.vo.MerchantExportVO;
import com.asianwallets.permissions.service.ExportService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-28 13:49
 **/
@Service
public class ExportServiceImpl implements ExportService {

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出机构
     * @return
     **/
    @Override
    public ExcelWriter getInstitutionExcel(List<InstitutionExportVO> institutionExportVOS, Class clazz) {

        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (InstitutionExportVO institutionExportVO : institutionExportVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(institutionExportVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int p = 0; p < property.length; p++) {
                for (String s : keySet) {
                    if (s.equals(property[p])) {
                        oSet1.add(comment[p]);
                        if (s.equals("auditStatus")) {
                            if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                oList2.add("待审核");
                            } else if ((String.valueOf((oMap.get(s))).equals("2"))) {
                                oList2.add("审核通过");
                            } else if ((String.valueOf((oMap.get(s))).equals("3"))) {
                                oList2.add("审核不通过");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("enabled")) {
                            if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                oList2.add("启用");
                            } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                oList2.add("禁用");
                            }
                        } else {
                            oList2.add(oMap.get(s));
                        }

                    }
                }
            }
            oList1.add(oList2);
        }
        oList1.add(0, oSet1);
        writer.write(oList1);
        return writer;



    }

    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
     * @return
     **/
    @Override
    public ExcelWriter getMerchantExcel(ArrayList<MerchantExportVO> merchantExportVOS, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (MerchantExportVO merchantExportVO : merchantExportVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(merchantExportVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int p = 0; p < property.length; p++) {
                for (String s : keySet) {
                    if (s.equals(property[p])) {
                        oSet1.add(comment[p]);
                        if (s.equals("auditStatus")) {
                            if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                oList2.add("待审核");
                            } else if ((String.valueOf((oMap.get(s))).equals("2"))) {
                                oList2.add("审核通过");
                            } else if ((String.valueOf((oMap.get(s))).equals("3"))) {
                                oList2.add("审核不通过");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("enabled")) {
                            if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                oList2.add("启用");
                            } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                oList2.add("禁用");
                            }
                        } else if (s.equals("merchantType")) {
                            if ((String.valueOf((oMap.get(s)))).equals("3")) {
                                oList2.add("普通商户");
                            } else if ((String.valueOf((oMap.get(s)))).equals("4")) {
                                oList2.add("代理商");
                            } else if((String.valueOf((oMap.get(s)))).equals("5")){
                                oList2.add("集团商户");
                            }else {
                                oList2.add("");
                            }
                        } else {
                            oList2.add(oMap.get(s));
                        }

                    }
                }
            }
            oList1.add(oList2);
        }
        oList1.add(0, oSet1);
        writer.write(oList1);
        return writer;

    }
}

package com.asianwallets.permissions.service.impl;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.permissions.service.RightsManagementFeignService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RightsManagementFeignServiceImpl implements RightsManagementFeignService {


    /**
     * 导出机构权益
     *
     * @param list
     * @param clazz
     * @param language
     * @return
     */
    @Override
    public ExcelWriter exportRights(List<InstitutionRightsVO> list, Class clazz, String language) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (InstitutionRightsVO institutionRightsVO : list) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(institutionRightsVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (TradeConstant.ZH_CN.equals(language)) {
                            if (s.equals("rightsType")) {
                                if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                    oList2.add("满减");
                                } else if ((String.valueOf((oMap.get(s))).equals("2"))) {
                                    oList2.add("折扣");
                                } else if ((String.valueOf((oMap.get(s))).equals("3"))) {
                                    oList2.add("套餐");
                                } else if ((String.valueOf((oMap.get(s))).equals("4"))) {
                                    oList2.add("定额");
                                } else {
                                    oList2.add("");
                                }
                            } else if (s.equals("enabled")) {
                                if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                    oList2.add("启用");
                                } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                    oList2.add("禁用");
                                } else {
                                    oList2.add("");
                                }
                            } else if (s.equals("getLimit")) {
                                if ((String.valueOf((oMap.get(s)))).equals("1")) {
                                    oList2.add("不限");
                                } else if ((String.valueOf((oMap.get(s)))).equals("2")) {
                                    oList2.add("每人/张/天");
                                } else if ((String.valueOf((oMap.get(s)))).equals("3")) {
                                    oList2.add("仅限1张/人");
                                } else {
                                    oList2.add("");
                                }
                            } else {
                                oList2.add(oMap.get(s));
                            }
                        } else {
                            if (s.equals("rightsType")) {
                                if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                    oList2.add("Full Reduction");
                                } else if ((String.valueOf((oMap.get(s))).equals("2"))) {
                                    oList2.add("Discount");
                                } else if ((String.valueOf((oMap.get(s))).equals("3"))) {
                                    oList2.add("Package");
                                } else if ((String.valueOf((oMap.get(s))).equals("4"))) {
                                    oList2.add("Quota");
                                } else {
                                    oList2.add("");
                                }
                            } else if (s.equals("enabled")) {
                                if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                    oList2.add("Enable");
                                } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                    oList2.add("Disable");
                                } else {
                                    oList2.add("");
                                }
                            } else if (s.equals("getLimit")) {
                                if ((String.valueOf((oMap.get(s)))).equals("1")) {
                                    oList2.add("Unlimited");
                                } else if ((String.valueOf((oMap.get(s)))).equals("2")) {
                                    oList2.add("Per Person / Sheet / Day");
                                } else if ((String.valueOf((oMap.get(s)))).equals("3")) {
                                    oList2.add("Only 1 Piece / Person");
                                } else {
                                    oList2.add("");
                                }
                            } else {
                                oList2.add(oMap.get(s));
                            }
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

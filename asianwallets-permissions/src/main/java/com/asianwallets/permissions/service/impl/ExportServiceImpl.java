package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.entity.TradeCheckAccount;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.vo.*;
import com.asianwallets.permissions.service.ExportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 导出excel的实现类
 */
@Service
public class ExportServiceImpl implements ExportService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
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
                            } else if ((String.valueOf((oMap.get(s)))).equals("5")) {
                                oList2.add("集团商户");
                            } else {
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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户产品
     **/
    @Override
    public ExcelWriter getMerchantProductExcel(ArrayList<MerchantProductExportVO> merchantProExportVOS, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (MerchantProductExportVO merchantProductExportVO : merchantProExportVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(merchantProductExportVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int p = 0; p < property.length; p++) {
                for (String s : keySet) {
                    if (s.equals(property[p])) {
                        oSet1.add(comment[p]);
                        if (s.equals("tradeDirection")) {
                            if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                oList2.add("线上");
                            } else if ((String.valueOf((oMap.get(s))).equals("2"))) {
                                oList2.add("线下");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("feePayer")) {
                            if ((String.valueOf((oMap.get(s)))).equals("1")) {
                                oList2.add("商户");
                            } else if ((String.valueOf((oMap.get(s)))).equals("2")) {
                                oList2.add("用户");
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
                        } else if (s.equals("refundDefault")) {
                            if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                oList2.add("收");
                            } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                oList2.add("不收");
                            } else {
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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/12
     * @Descripate 导出商户通道
     **/
    @Override
    public ExcelWriter getMerchantChannelExcel(ArrayList<MerChannelExportVO> merchantChannelExportVOS, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (MerChannelExportVO merChannelExportVO : merchantChannelExportVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(merChannelExportVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int p = 0; p < property.length; p++) {
                for (String s : keySet) {
                    if (s.equals(property[p])) {
                        oSet1.add(comment[p]);
                        if (s.equals("enabled")) {
                            if ((String.valueOf((oMap.get(s)))).equals("true")) {
                                oList2.add("启用");
                            } else if ((String.valueOf((oMap.get(s)))).equals("false")) {
                                oList2.add("禁用");
                            } else {
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

    /**
     * 导出订单信息
     *
     * @param exportOrdersVOList 订单集合
     * @param clazz              订单导出class
     * @param writer
     * @return
     */
    @Override
    public ExcelWriter exportOrders(List<ExportOrdersVO> exportOrdersVOList, Class<ExportOrdersVO> clazz, ExcelWriter writer) {
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        BigDecimal totalTradeAmount = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        BigDecimal totalChannelFee = BigDecimal.ZERO;
        for (ExportOrdersVO order : exportOrdersVOList) {
            totalOrderAmount = totalOrderAmount.add(order.getOrderAmount());
            totalTradeAmount = totalTradeAmount.add(order.getTradeAmount());
            totalFee = totalFee.add(order.getFee());
            totalChannelFee = totalChannelFee.add(order.getChannelFee());
        }
        //设置列宽
        writer.setColumnWidth(-1, 40);
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        Set<Object> oSet1 = new LinkedHashSet<>();
        for (ExportOrdersVO orderTradeVO : exportOrdersVOList) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(orderTradeVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("tradeDirection")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("线上");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("线下");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("tradeStatus")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("待支付 ");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("交易中");
                            } else if (String.valueOf(oMap.get(s)).equals("3")) {
                                oList2.add("交易成功");
                            } else if (String.valueOf(oMap.get(s)).equals("4")) {
                                oList2.add("交易失败");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("cancelStatus")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("撤销中 ");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("撤销成功");
                            } else if (String.valueOf(oMap.get(s)).equals("3")) {
                                oList2.add("撤销失败");
                            } else if(String.valueOf(oMap.get(s)).equals("4")){
                                oList2.add("冲正中");
                            }else if(String.valueOf(oMap.get(s)).equals("5")){
                                oList2.add("冲正成功");
                            }else if(String.valueOf(oMap.get(s)).equals("6")){
                                oList2.add("冲正失败");
                            }else {
                                oList2.add("");
                            }
                        } else if (s.equals("refundStatus")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("退款中 ");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("部分退款成功");
                            } else if (String.valueOf(oMap.get(s)).equals("3")) {
                                oList2.add("退款成功");
                            } else if (String.valueOf(oMap.get(s)).equals("4")) {
                                oList2.add("退款失败");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("tradeType")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("收");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("付");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("deliveryStatus")) {
                            if (String.valueOf(oMap.get(s)).equals("1")) {
                                oList2.add("未发货");
                            } else if (String.valueOf(oMap.get(s)).equals("2")) {
                                oList2.add("已发货");
                            } else {
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
        int count = 0;
        List<Object> statistics = new ArrayList<>();
        statistics.add("金额总计");
        for (Object o : oSet1) {
            count++;
            switch (String.valueOf(o)) {
                case "订单金额":
                    statistics.add(count - 1, totalOrderAmount);
                    break;
                case "手续费":
                    statistics.add(count - 1, totalFee);
                    break;
                case "通道手续费":
                    statistics.add(count - 1, totalChannelFee);
                    break;
                case "交易金额":
                    statistics.add(count - 1, totalTradeAmount);
                    break;
                default:
                    statistics.add(count, "");
                    break;
            }
        }
        //为了移除表头多余一格
        statistics.remove(statistics.size() - 1);
        oList1.add(0, statistics);
        oList1.add(1, oSet1);
        writer.write(oList1);
        return writer;
    }

    /**
     * 导出结算户余额流水详情
     * 导出清算金额详情
     * 导出冻结金额详情
     *
     * @param list
     * @param clazz
     * @return
     */
    @Override
    public ExcelWriter getTmMerChTvAcctBalanceWriter(List<TmMerChTvAcctBalanceVO> list, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (TmMerChTvAcctBalanceVO tmMerChTvAcctBalance : list) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(tmMerChTvAcctBalance);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("tradetype")) {
                            if ((String.valueOf((oMap.get(s))).equals("AA"))) {
                                oList2.add("调账");
                            } else if ((String.valueOf((oMap.get(s))).equals("ST"))) {
                                oList2.add("收单");
                            } else if ((String.valueOf((oMap.get(s))).equals("RV"))) {
                                oList2.add("撤销");
                            } else if ((String.valueOf((oMap.get(s))).equals("RF"))) {
                                oList2.add("退款");
                            } else if ((String.valueOf((oMap.get(s))).equals("WD"))) {
                                oList2.add("提款");
                            } else if ((String.valueOf((oMap.get(s))).equals("NT"))) {
                                oList2.add("收单");
                            } else if ((String.valueOf((oMap.get(s))).equals("FZ"))) {
                                oList2.add("冻结");
                            } else if ((String.valueOf((oMap.get(s))).equals("TW"))) {
                                oList2.add("解冻");
                            } else if ((String.valueOf((oMap.get(s))).equals("PM"))) {
                                oList2.add("付款");
                            } else if ((String.valueOf((oMap.get(s))).equals("RA"))) {
                                oList2.add("调账");
                            } else if ((String.valueOf((oMap.get(s))).equals("SP"))) {
                                oList2.add("分润");
                            } else {
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

    /**
     * 机构后台分润导出
     *
     * @param queryAgencyShareBenefitVOS
     * @param clazz
     * @return
     */
    @Override
    public ExcelWriter exportAgencyShareBenefit(List<QueryAgencyShareBenefitVO> queryAgencyShareBenefitVOS, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (QueryAgencyShareBenefitVO dto : queryAgencyShareBenefitVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(dto);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if ("isShare".equals(s)) {
                            if ("1".equals(String.valueOf((oMap.get(s))))) {
                                oList2.add("Stay Share Benefit");
                            } else if ("2".equals(String.valueOf((oMap.get(s))))) {
                                oList2.add("Has Share Benefit");
                            } else {
                                oList2.add("");
                            }
                        } else if ("agent_type".equals(s)) {
                            if ("1".equals(String.valueOf((oMap.get(s))))) {
                                oList2.add("Channel Agent");
                            } else if ("2".equals(String.valueOf((oMap.get(s))))) {
                                oList2.add("Merchants Agent");
                            } else {
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

    /**
     * 导出账户信息
     *
     * @param list
     * @param clazz
     * @return
     */
    @Override
    public ExcelWriter exportAccount(List<AccountListVO> list, Class clazz) {
        ExcelWriter writer = ExcelUtil.getBigWriter();
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (AccountListVO tmMerChTvAcctBalance : list) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(tmMerChTvAcctBalance);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("merchantType")) {
                            if ((String.valueOf((oMap.get(s))).equals("3"))) {
                                oList2.add("普通商户");
                            } else if ((String.valueOf((oMap.get(s))).equals("4"))) {
                                oList2.add("代理商户");
                            } else if ((String.valueOf((oMap.get(s))).equals("5"))) {
                                oList2.add("集团商户");
                            } else {
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

    /**
     * Excel 导出结算单1
     *
     * @param insPros 对象集合
     * @param clazz   类名Class对象
     * @return ExcelWriter writer
     */
    @Override
    public ExcelWriter getSettleCheckAccountsWriter(ExcelWriter writer, String language, List<SettleCheckAccount> insPros, Class clazz) {
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();

        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (SettleCheckAccount settleCheckAccount : insPros) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(settleCheckAccount);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("checkTime")) {
                            oList2.add(DateToolUtils.SHORT_DATE_FORMAT.format(oMap.get(s)));
                        } else {
                            oList2.add(oMap.get(s));
                        }
                    }
                }
            }
            oList1.add(oList2);
        }
        oList1.add(0, oSet1);
        writer.setColumnWidth(-1, 20);
        writer.passRows(1);
        if (AsianWalletConstant.EN_US.equals(language)) {
            writer.merge(0, 0, 0, 7, "Institutional statement: all transactions affecting changes in balance during the previous settlement period", true);
        } else {
            writer.merge(0, 0, 0, 7, "机构结算单:上一个结算周期内影响余额变动的所有交易", true);
        }
        writer.write(oList1);
        return writer;
    }

    /**
     * Excel 导出结算单2
     *
     * @param insPros 对象集合
     * @param clazz   类名Class对象
     * @return ExcelWriter writer
     */
    @Override
    public ExcelWriter getSettleCheckAccountDetailWriter(ExcelWriter writer, List<ExportSettleCheckAccountDetailVO> insPros, Class clazz) {
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(clazz);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (ExportSettleCheckAccountDetailVO settleCheckAccountDetail : insPros) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(settleCheckAccountDetail);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("balancetype")) {
                            if ((String.valueOf((oMap.get(s)))).equals("1")) {
                                oList2.add("Normal Money");
                            } else if ((String.valueOf((oMap.get(s)))).equals("2")) {
                                oList2.add("Freeze Funds");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("tradetype")) {
                            if ((String.valueOf((oMap.get(s)))).equals("RF")) {
                                oList2.add("refund");
                            } else if ((String.valueOf((oMap.get(s)))).equals("WD")) {
                                oList2.add("withdrawals");
                            } else if ((String.valueOf((oMap.get(s)))).equals("ST")) {
                                oList2.add("acquire");
                            } else if ((String.valueOf((oMap.get(s)))).equals("PM")) {
                                oList2.add("payment");
                            } else if ((String.valueOf((oMap.get(s)))).equals("AA")) {
                                oList2.add("reconciliation");
                            } else if ((String.valueOf((oMap.get(s)))).equals("RV")) {
                                oList2.add("reverse");
                            } else if ((String.valueOf((oMap.get(s)))).equals("FZ")) {
                                oList2.add("freeze");
                            } else if ((String.valueOf((oMap.get(s)))).equals("TW")) {
                                oList2.add("unfreeze");
                            } else if ((String.valueOf((oMap.get(s)))).equals("PM")) {
                                oList2.add("payment");
                            } else {
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
        writer.setColumnWidth(-1, 20);
        writer.write(oList1);
        return writer;
    }

    /**
     * 导出商户交易对账单
     */
    @Override
    public ExcelWriter exportTradeCheckAccount(ExportTradeAccountVO exportTradeAccountVO, String language, Class clazz1, Class clazz2) {
        //获取属性名的名称与注释Map
        Map<String, String[]> totalResult = ReflexClazzUtils.getFiledStructMap(clazz1);
        Map<String, String[]> detailResult = ReflexClazzUtils.getFiledStructMap(clazz2);
        //总表注释信息
        String[] totalComment = totalResult.get(AsianWalletConstant.EXCEL_TITLES);
        //总表属性名信息
        String[] totalProperty = totalResult.get(AsianWalletConstant.EXCEL_ATTRS);
        //详细表注释信息
        String[] detailComment = detailResult.get(AsianWalletConstant.EXCEL_TITLES);
        //详细表属性名信息
        String[] detailProperty = detailResult.get(AsianWalletConstant.EXCEL_ATTRS);
        ExcelWriter writer = ExcelUtil.getBigWriter();
        writer.renameSheet("Deals Total Table");
        //总表信息
        List<TradeCheckAccount> totals = exportTradeAccountVO.getTradeCheckAccounts();
        //详细表信息
        List<TradeAccountDetailVO> details = exportTradeAccountVO.getTradeAccountDetailVOS();
        //总表数据集合
        List<Object> totalDataList = new ArrayList<>();
        //总表注释名称集合
        LinkedHashSet<Object> totalCommentSet = new LinkedHashSet<>();
        //总表
        for (TradeCheckAccount tradeCheckAccount : totals) {
            //将对象的属性名与属性值转换成Map
            HashMap<String, Object> entityMap = BeanToMapUtil.beanToMap(tradeCheckAccount);
            //属性名称集合
            Set<String> propertyNameSet = entityMap.keySet();
            //属性值集合
            ArrayList<Object> attrValueList = new ArrayList<>();
            for (int i = 0; i < totalProperty.length; i++) {
                for (String property : propertyNameSet) {
                    //属性名称相等时
                    if (property.equals(totalProperty[i])) {
                        //添加注释名称信息
                        totalCommentSet.add(totalComment[i]);
                        //添加对应属性名称的属性值
                        attrValueList.add(entityMap.get(property));
                    }
                }
            }
            //添加属性值集合到Excel数据集合中
            totalDataList.add(attrValueList);
        }
        //添加总表注释名称信息
        totalDataList.add(0, totalCommentSet);
        if (AsianWalletConstant.EN_US.equals(language)) {
            writer.merge(10, "Statement: a breakdown of all successful transactions from the previous day (including transactions & refunds)", true);
            writer.setColumnWidth(-1, 25);
        } else {
            writer.merge(10, "对账单:统计前一天所有成功交易的明细（包含交易&退款）", true);
            writer.setColumnWidth(-1, 17);
        }
        writer.write(totalDataList);


        //详细表
        for (TradeAccountDetailVO tradeAccountDetailVO : details) {
            //详细表数据集合
            List<Object> detailDataList = new ArrayList<>();
            //详细表注释名称集合
            LinkedHashSet<Object> detailCommentSet = new LinkedHashSet<>();
            //添加详细表注释名称信息
            detailDataList.add(detailCommentSet);
            //设置新Sheet
            writer.setSheet(tradeAccountDetailVO.getOrderCurrency());
            for (TradeCheckAccountDetailVO tradeCheckAccountDetailVO : tradeAccountDetailVO.getTradeCheckAccountDetailVOS()) {
                List<Object> attrValueList = new ArrayList<>();
                //将对象的属性名与属性值转换成Map
                HashMap<String, Object> entityMap = BeanToMapUtil.beanToMap(tradeCheckAccountDetailVO);
                Set<String> propertySet = entityMap.keySet();
                for (int i = 0; i < detailProperty.length; i++) {
                    for (String property : propertySet) {
                        //属性名称相同
                        if (property.equals(detailProperty[i])) {
                            detailCommentSet.add(detailComment[i]);
                            attrValueList.add(entityMap.get(property));
                        }
                    }
                }
                detailDataList.add(attrValueList);
            }
            writer.setColumnWidth(-1, 20);
            writer.write(detailDataList);
        }
        return writer;
    }
}

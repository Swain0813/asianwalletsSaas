package com.asianwallets.common.utils;

import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.vo.MerchantReportVO;

import javax.servlet.ServletOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ErrorExcelUtils {
    /**
     * excel导出错误处理
     *
     * @param writer
     * @param lists
     * @param out
     * @param errCode
     * @param language
     * @return
     */
    public static boolean errorExportExcel(ExcelWriter writer, List<MerchantReportVO> lists, ServletOutputStream out, String errCode, String language) {
        if (ArrayUtil.isEmpty(lists)) {
            //数据不存在的场合
            HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(language);
            writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(errCode))));
            writer.flush(out);
            return true;
        }
        return false;
    }
}

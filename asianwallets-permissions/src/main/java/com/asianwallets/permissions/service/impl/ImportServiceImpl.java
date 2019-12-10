package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.feign.base.BankFeign;
import com.asianwallets.permissions.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ImportServiceImpl implements ImportService {

    @Autowired
    private BankFeign bankFeign;

    /**
     * 导入银行信息
     *
     * @param username 用户名
     * @param file     文件
     * @return 银行集合
     */
    @Override
    public List<Bank> importBank(String username, MultipartFile file) {
        List<Bank> h = new ArrayList<>();
        String fileName = file.getOriginalFilename();
        // 判断格式0
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            // 当excel内的格式不正确时
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        List<List<Object>> read = reader.read();
        //判断是否超过上传限制
        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
        }
        if (read.size() <= 0) {
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        for (int i = 1; i < read.size(); i++) {
            List<Object> objects = read.get(i);
            Bank ol = new Bank();
            //判断传入的excel的格式是否符合约定
            if (StringUtils.isEmpty(objects.get(0))
                    || StringUtils.isEmpty(objects.get(1))
                    || StringUtils.isEmpty(objects.get(2))
                    || objects.size() != 4
                    || StringUtils.isEmpty(objects.get(3))) {
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            try {
                ol.setBankName(objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
                ol.setBankCurrency(objects.get(1).toString().replaceAll("\\s*", ""));
                ol.setBankCountry(objects.get(2).toString().replaceAll("\\s*", ""));
                ol.setIssuerId(objects.get(3).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            } catch (Exception e) {
                // 当excel内的格式不正确时
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            ol.setId(IDS.uuid2());
            ol.setBankCode("" + IDS.uniqueID());
            ol.setCreator(username);
            ol.setCreateTime(new Date());
            ol.setEnabled(true);
            BankDTO bankDTO = new BankDTO();
            bankDTO.setBankName(ol.getBankName());
            bankDTO.setBankCurrency(ol.getBankCurrency());
            if (bankFeign.getByBankNameAndCurrency(bankDTO) != null) {
                continue;
            }
            h.add(ol);
        }
        for (int i = 0; i < h.size() - 1; i++) {
            for (int j = h.size() - 1; j > i; j--) {
                if (h.get(j).getBankName().equals(h.get(i).getBankName()) && h.get(j).getBankCurrency().equals(h.get(i).getBankCurrency())) {
                    h.remove(j);
                }
            }
        }
        if (h.size() == 0) {
            throw new BusinessException(EResultEnum.IMPORT_REPEAT_ERROR.getCode());
        }
        return h;
    }
}

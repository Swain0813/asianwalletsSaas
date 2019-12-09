package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.dao.MccMapper;
import com.asianwallets.permissions.service.MccFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:26
 * @Description: 设备信息
 */
@Service
@Transactional
public class MccFeignServiceImpl implements MccFeignService {

    @Autowired
    private MccMapper mccMapper;


    @Override
    public List<Mcc> uploadMcc(MultipartFile file, String createName) {
        ArrayList<Mcc> h = new ArrayList<>();
        // 判断格式0
        if (!file.getOriginalFilename().matches("^.+\\.(?i)(xls)$") && !file.getOriginalFilename().matches("^.+\\.(?i)(xlsx)$")) {
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
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
            //判断传入的excel的格式是否符合约定
            if (StringUtils.isEmpty(objects.get(0))
                    || StringUtils.isEmpty(objects.get(1))
                    || StringUtils.isEmpty(objects.get(2))
                    || objects.size() != 3
            ) {
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            Mcc mcc = new Mcc();
            Mcc m = mccMapper.selectByCode(objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            if (m != null && !StringUtils.isEmpty(m.getExtend1())) {
                mcc.setExtend1(m.getExtend1());
            } else {
                mcc.setExtend1(IDS.uniqueID().toString());
            }
            checkName(objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            mcc.setId(IDS.uniqueID().toString());
            mcc.setName(objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            mcc.setCode(objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            mcc.setLanguage(objects.get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            mcc.setCreateTime(new Date());
            mcc.setCreator(createName);
            mcc.setEnabled(true);
            h.add(mcc);
        }
        if (h.size() == 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Set<Mcc> set = new HashSet<>(h);
        if (set.size() != h.size()) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        return h;
    }

    /**
     * 校验CODE
     *
     * @param code
     * @return
     */
    private void checkCode(String code) {
        if (mccMapper.selectByCode(code) != null) {
            throw new BusinessException(EResultEnum.MCC_EXIST.getCode());
        }
    }

    /**
     * 检验名
     *
     * @param name
     * @return
     */
    private void checkName(String name) {
        if (mccMapper.selectByName(name) != null) {
            throw new BusinessException(EResultEnum.MCC_EXIST.getCode());
        }
    }


}

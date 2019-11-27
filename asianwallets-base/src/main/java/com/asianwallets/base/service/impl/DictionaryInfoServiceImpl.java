package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.DictionaryMapper;
import com.asianwallets.base.dao.DictionaryTypeMapper;
import com.asianwallets.base.service.DictionaryInfoService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DictionaryInfoAllDTO;
import com.asianwallets.common.dto.DictionaryInfoDTO;
import com.asianwallets.common.entity.Dictionary;
import com.asianwallets.common.entity.DictionaryType;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * @author shenxinran
 * @Date: 2019/1/30 14:38
 * @Description: 字典类别与信息管理业务层
 */
@Service
@Transactional
public class DictionaryInfoServiceImpl implements DictionaryInfoService {

    @Autowired
    private DictionaryTypeMapper dictionaryTypeMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;


    /**
     * 添加信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public int addDictionaryInfo(DictionaryInfoDTO dictionaryInfoDTO) {
        //判断语言是否填写
        if (StringUtils.isEmpty(dictionaryInfoDTO.getLanguage())) {
            throw new BusinessException(EResultEnum.DICINFO_LANGUAGE_IS_NULL.getCode());
        }
        //判断名称是否填写
        if (StringUtils.isEmpty(dictionaryInfoDTO.getName())) {
            throw new BusinessException(EResultEnum.DICINFO_NAME_IS_NULL.getCode());
        }

        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(dictionaryInfoDTO, dictionary);
        dictionary.setEnabled(true);
        dictionary.setCreateTime(new Date());
        dictionary.setDictypeCode(AsianWalletConstant.DICTIONARY_TYPE_CODE);
        //检验类型id与数据id字段是否填写
        if (StringUtils.isEmpty(dictionaryInfoDTO.getChoseType()) && StringUtils.isEmpty(dictionaryInfoDTO.getType())) {
            throw new BusinessException(EResultEnum.DICINFO_TYPE_IS_NULL.getCode());
        }
        //1.判断类型名称是否存在
        if (addDictionaryType(dictionaryInfoDTO, dictionary)) {
            return dictionaryMapper.insertSelective(dictionary);
        }
        //检查参数
        if (StringUtils.isNotBlank(dictionaryInfoDTO.getType()) && StringUtils.isNotBlank(dictionaryInfoDTO.getChoseType())) {
            throw new BusinessException(EResultEnum.DICINFO_ILLEGAL_PARAMETER_EXIST.getCode());
        }
        return addDictionary(dictionaryInfoDTO, dictionary);
    }

    /**
     * 添加字典类型
     *
     * @param dictionaryInfoDTO
     * @param dictionary
     * @return
     */
    private boolean addDictionaryType(DictionaryInfoDTO dictionaryInfoDTO, Dictionary dictionary) {
        if (StringUtils.isNotBlank(dictionaryInfoDTO.getType()) && StringUtils.isBlank(dictionaryInfoDTO.getChoseType())) {
            //添加类型信息
            DictionaryType dictionaryType = new DictionaryType();
            BeanUtils.copyProperties(dictionaryInfoDTO, dictionaryType);
            dictionaryType.setDicValue(dictionaryInfoDTO.getType());
            //检验类型是否重复添加
            if (StringUtils.isNotBlank(dictionaryMapper.getNameAndLanguage(dictionaryInfoDTO.getName(), dictionaryInfoDTO.getLanguage()))) {
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
            //添加不同语言的同种类型
            String dictTypeCode = dictionaryTypeMapper.findDicTypeCodeByValue(dictionaryType.getDicValue());
            if (StringUtils.isNotBlank(dictTypeCode)) {
                dictionary.setCode(dictTypeCode);
                return true;
            }
            dictionaryType.setCreateTime(new Date());
            dictionaryType.setEnabled(true);
            String size = dictionaryTypeMapper.findCount();
            if (StringUtils.isBlank(size)) {
                DictionaryType dt = new DictionaryType();
                dt.setId(IDS.uuid2());
                dt.setDicCode(AsianWalletConstant.DICTIONARY_TYPE_CODE);
                dt.setDicValue(AsianWalletConstant.DICTIONARY_TYPE_CODE);
                dt.setCreateTime(new Date());
                dt.setEnabled(false);
                dictionaryTypeMapper.insertSelective(dt);
            }
            dictionaryType.setDicCode(AsianWalletConstant.DIC + (Integer.valueOf(size) + 1));
            if (dictionaryTypeMapper.insertSelective(dictionaryType) > 0) {
                dictionary.setDictypeCode(AsianWalletConstant.DICTIONARY_TYPE_CODE);
                dictionary.setCode(dictionaryType.getDicCode());
                return true;
            }
        }
        return false;
    }

    /**
     * 添加字典数据
     *
     * @param dictionaryInfoDTO
     * @param dictionary
     * @return
     */
    private int addDictionary(DictionaryInfoDTO dictionaryInfoDTO, Dictionary dictionary) {
        if (dictionaryInfoDTO.getChoseType().equals(AsianWalletConstant.CURRENCY_CODE) || dictionaryInfoDTO.getChoseType().equals(AsianWalletConstant.SETTLEMENT_CODE)) {
            if (StringUtils.isEmpty(dictionaryInfoDTO.getCode())) {
                throw new BusinessException(EResultEnum.DICINFO_CODE_IS_NULL.getCode());
            }
        }

        //判断类型名称是否存在 此时的 dictionaryInfoDTO.getType() 为字典类型索引的dic_code
        String dicVlue = dictionaryTypeMapper.findDicTypeCode(dictionaryInfoDTO.getChoseType());
        if (StringUtils.isBlank(dicVlue)) {
            throw new BusinessException(EResultEnum.DICINFO_TYPE_VALUE_NOT_EXIST.getCode());
        }
        //当插入币种数据时，要判断币种默认值是否存在
        if (AsianWalletConstant.CURRENCY_CODE.equals(dictionaryInfoDTO.getChoseType())) {
            if (StringUtils.isEmpty(dictionaryInfoDTO.getDefaultValue())) {
                throw new BusinessException(EResultEnum.DICINFO_CURRENCY_DEFAULT_IS_NULL.getCode());
            }
        }
        // 校验是否重复添加
        if (dictionaryMapper.checkValue(dictionaryInfoDTO.getCode(), dictionaryInfoDTO.getName(), dictionaryInfoDTO.getChoseType(), dictionaryInfoDTO.getLanguage()) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        //插入到 dictionary（字典数据）表中的基础数据dictype_code字段
        BeanUtils.copyProperties(dictionaryInfoDTO, dictionary);
        if (!dictionaryInfoDTO.getChoseType().equals(AsianWalletConstant.CURRENCY_CODE)) {
            String latestCode = dictionaryMapper.getLatestCodeBydictypeCodeAndLanguage(dictionaryInfoDTO.getChoseType(), null);
            //使得CODE自增
            Integer code = null;
            if (StringUtils.isBlank(latestCode)) {
                code = 1;
            } else {
                code = Integer.valueOf(latestCode) + 1;
            }
            dictionary.setCode(dictionaryInfoDTO.getChoseType() + "_" + code);
            dictionary.setEnabled(true);
        } else {
            dictionary.setCode(dictionaryInfoDTO.getCode());
            dictionary.setEnabled(dictionaryInfoDTO.getEnabled());
        }
        dictionary.setDictypeCode(dictionaryInfoDTO.getChoseType());
        if (checkRepeat(dictionary)) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        dictionary.setCreateTime(new Date());
        return dictionaryMapper.insertSelective(dictionary);
    }


    /**
     * 启用禁用字典类型
     *
     * @param modifierName
     * @param typeId
     * @param enabled
     * @return
     */
    @Override
    public int banDictionaryType(String modifierName, String typeId, Boolean enabled) {
        if (StringUtils.isEmpty(typeId)) {
            throw new BusinessException(EResultEnum.DICINFO_TYPEID_IS_NULL.getCode());
        }
        if (enabled == null) {
            throw new BusinessException(EResultEnum.ENABLE_IS_NULL.getCode());
        }
        //禁用当前类型ID下的所有数据
        if (dictionaryMapper.banDictionaryWithType(modifierName, typeId, enabled) < 0) {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        //禁用类型ID
        return dictionaryTypeMapper.banDictionaryType(modifierName, typeId, enabled);
    }

    /**
     * 启用禁用字典数据
     *
     * @param modifierName
     * @param id
     * @param enabled
     * @return
     */
    @Override
    public int banDictionary(String modifierName, String id, Boolean enabled) {
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException(EResultEnum.DICINFO_ID_IS_NULL.getCode());
        }
        if (enabled == null) {
            throw new BusinessException(EResultEnum.ENABLE_IS_NULL.getCode());
        }
        Dictionary dictionary = new Dictionary();
        dictionary.setModifier(modifierName);
        dictionary.setId(id);
        dictionary.setEnabled(enabled);
        dictionary.setUpdateTime(new Date());
        return dictionaryMapper.updateByPrimaryKeySelective(dictionary);
    }

    /**
     * 更新字典类型
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public int updateDictionaryType(DictionaryInfoDTO dictionaryInfoDTO) {

        return update(dictionaryInfoDTO);
    }

    /**
     * 更新数据
     *
     * @param dictionaryInfoDTO
     * @return
     */
    private int update(DictionaryInfoDTO dictionaryInfoDTO) {
        if (StringUtils.isEmpty(dictionaryInfoDTO.getId())) {
            throw new BusinessException(EResultEnum.DICINFO_ID_IS_NULL.getCode());
        }
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(dictionaryInfoDTO, dictionary);
        dictionary.setUpdateTime(new Date());
        return dictionaryMapper.updateByPrimaryKeySelective(dictionary);
    }

    /**
     * 更新字典数据
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public int updateDictionary(DictionaryInfoDTO dictionaryInfoDTO) {
        return update(dictionaryInfoDTO);
    }

    /**
     * 查询类型信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public PageInfo<Dictionary> pageDicTypeInfo(DictionaryInfoDTO dictionaryInfoDTO) {
        //币种的场合，没有多语言
        if (AsianWalletConstant.CURRENCY_CODE.equals(dictionaryInfoDTO.getDictypeCode())) {
            dictionaryInfoDTO.setLanguage("");// "dic_2"-币种的场合语言设置为空
        }
        return new PageInfo(dictionaryMapper.pageDictionaryInfo(dictionaryInfoDTO));
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public Dictionary getDictionaryInfo(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException(EResultEnum.DICINFO_ID_IS_NULL.getCode());
        }
        return dictionaryMapper.selectByPrimaryKey(id);
    }

    /**
     * 检查重复数据
     *
     * @param dictionary
     * @return
     */
    private boolean checkRepeat(Dictionary dictionary) {
        return dictionaryMapper.selectOne(dictionary) != null;
    }

    /**
     * 新增语言
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public int addOtherLanguage(DictionaryInfoDTO dictionaryInfoDTO) {
        Dictionary dictionary = new Dictionary();

        if (addDicTypeLanguage(dictionaryInfoDTO, dictionary)) {
            //类别
            return dictionaryMapper.insert(dictionary);
        }
        return addDicInfoLanguage(dictionaryInfoDTO, dictionary);
    }

    public int addDicInfoLanguage(DictionaryInfoDTO dictionaryInfoDTO, Dictionary dictionary) {
        //字典数据添加
        if (StringUtils.isEmpty(dictionaryInfoDTO.getChoseType()) || StringUtils.isEmpty(dictionaryInfoDTO.getCode()) || StringUtils.isEmpty(dictionaryInfoDTO.getLanguage())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (dictionaryMapper.selectDicCodeAndLanguage(dictionaryInfoDTO.getCode(), dictionaryInfoDTO.getLanguage()) != 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        dictionary.setId(IDS.uuid2());
        dictionary.setName(dictionaryInfoDTO.getName());
        dictionary.setEnabled(true);
        if (dictionaryInfoDTO.getIcon() != null) {
            dictionary.setIcon(dictionaryInfoDTO.getIcon());
        }
        dictionary.setDictypeCode(dictionaryInfoDTO.getChoseType());
        dictionary.setCode(dictionaryInfoDTO.getCode());
        dictionary.setCreator(dictionaryInfoDTO.getCreator());
        dictionary.setLanguage(dictionaryInfoDTO.getLanguage());
        dictionary.setCreateTime(new Date());
        return dictionaryMapper.insert(dictionary);
    }


    public boolean addDicTypeLanguage(DictionaryInfoDTO dictionaryInfoDTO, Dictionary dictionary) {
        if (StringUtils.isEmpty(dictionaryInfoDTO.getChoseType())) {
            //字典类型添加
            if (StringUtils.isEmpty(dictionaryInfoDTO.getType()) || StringUtils.isEmpty(dictionaryInfoDTO.getLanguage()) || StringUtils.isEmpty(dictionaryInfoDTO.getName())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            if (dictionaryMapper.selectDicCodeAndLanguage(dictionaryInfoDTO.getType(), dictionaryInfoDTO.getLanguage()) != 0) {
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
            dictionary.setId(IDS.uuid2());
            dictionary.setCreateTime(new Date());
            dictionary.setCreator(dictionaryInfoDTO.getCreator());
            dictionary.setCode(dictionaryInfoDTO.getCode());
            dictionary.setLanguage(dictionaryInfoDTO.getLanguage());
            dictionary.setDictypeCode(AsianWalletConstant.DICTIONARY_TYPE_CODE);
            dictionary.setEnabled(true);
            dictionary.setName(dictionaryInfoDTO.getName());
            return true;
        }

        return false;
    }

    /**
     * 查询全部数据字典信息
     *
     * @param dictionaryInfoDTO
     * @return
     */
    @Override
    public PageInfo<Dictionary> pageDictionaryInfos(DictionaryInfoAllDTO dictionaryInfoDTO) {
        //币种的场合，没有多语言
        if (AsianWalletConstant.CURRENCY_CODE.equals(dictionaryInfoDTO.getDictypeCode())) {
            dictionaryInfoDTO.setLanguage("");// "dic_2"-币种的场合语言设置为空
        }
        return new PageInfo(dictionaryMapper.pageDictionaryInfos(dictionaryInfoDTO));
    }

}

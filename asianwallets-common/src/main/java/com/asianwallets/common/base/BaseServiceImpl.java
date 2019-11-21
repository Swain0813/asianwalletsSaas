package com.asianwallets.common.base;

import com.asianwallets.common.constant.AsianWalletConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: (service基础实现类)
 * @createTime 2018年6月29日 上午11:01:17
 * @copyright: 上海众哈网络技术有限公司
 */
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {


	@Autowired
	public BaseMapper<T> baseMapper;

	public void setMapper(BaseMapper<T> baseMapper) {
		this.baseMapper = baseMapper;
	}

	@Override
	public int save(T entity) {
		entity.setCreateTime(new Date());
		return baseMapper.insert(entity);
	}

	@Override
	public int batchSave(List<T> recordList) {
		return baseMapper.insertList(recordList);
	}

	@Override
	public int delete(String id) {

		return baseMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int delete(T entity) {

		return baseMapper.delete(entity);
	}

	@Override
	public int updateByPrimaryKey(T entity) {
		entity.setUpdateTime(new Date());
		return baseMapper.updateByPrimaryKey(entity);
	}

	@Override
	public int update(T entity) {
		entity.setUpdateTime(new Date());
		return baseMapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public T get(String id) {

		return baseMapper.selectByPrimaryKey(id);
	}

	@Override
	public T selectOne(T entity) {

		return baseMapper.selectOne(entity);
	}

	@Override
	public List<T> getAll() {

		return baseMapper.selectAll();
	}

	@Override
	public List<T> select(T entity) {

		return baseMapper.select(entity);
	}

	@Override
	public int setUnavailableByExample(List<String> ids, T entity) {

		Example example = new Example(entity.getClass());
		Criteria criteria = example.createCriteria();
		criteria.andIn(AsianWalletConstant.FIELD_ID_PARAM, new HashSet<String>(ids));
		criteria.andEqualTo(AsianWalletConstant.FIELD_IS_AVAILABLE_PARAM, Boolean.TRUE);
		return baseMapper.updateByExampleSelective(entity, example);
	}

	@Override
	public T getAccountByExample(String fieldName, String fieldData, String currency, Class<T> targetClass) {

		Example example = new Example(targetClass);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo(fieldName, fieldData);
		criteria.andEqualTo("currency", currency);
		List<T> tempList = baseMapper.selectByExample(example);
		if (!CollectionUtils.isEmpty(tempList)) {
			return tempList.get(AsianWalletConstant.ZERO);
		}

		return null;
	}

	@Override
	public int deleteByExample(List<String> ids, Class<T> targetClass) {

		Example example = new Example(targetClass);
		Criteria criteria = example.createCriteria();
		criteria.andIn(AsianWalletConstant.FIELD_ID_PARAM, new HashSet<String>(ids));
		return baseMapper.deleteByExample(example);
	}

}

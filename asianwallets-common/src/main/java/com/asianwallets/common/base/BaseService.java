package com.asianwallets.common.base;

import java.util.List;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: (service基础类)
 * @createTime 2018年6月29日 上午10:58:47
 * @copyright: 上海众哈网络技术有限公司
 */
public interface BaseService<T> {

    public int save(T entity);// 保存

    public int batchSave(List<T> recordList);// 批量保存

    public int delete(String id);// 删除

    public int delete(T entity);// 删除

    //通过主键更新，只是更新新的model中不为空的字段，空值对应字段不作处理
    public int update(T entity);

    //通过主键更新全部字段 根据主键更新实体全部字段，会将为空的字段在数据库中置为NULL
    public int updateByPrimaryKey(T entity);// 更新所有信息

    public T get(String id);// 查找

    public T selectOne(T entity);// 查找

    public List<T> getAll();// 查找所有

    public List<T> select(T entity);// 查找所有

    public T getAccountByExample(String fieldName, String fieldData, String currency, Class<T> targetClass);
    
    public int setUnavailableByExample(List<String> ids, T entity);// 根据Example修改

    public int deleteByExample(List<String> ids, Class<T> targetClass);// 根据Example删除

} 
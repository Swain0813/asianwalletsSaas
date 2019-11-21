package com.asianwallets.common.base;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.UpdateProvider;
import java.util.List;

/**
 * 批量插入和批量更新功能
 * @param <T>
 */
public interface IdInserListMapper<T> {
    /**
     * 批量插入功能修改
     * @param recordList
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @InsertProvider(type = IdSpecialProvider.class, method = "dynamicSQL")
    int insertList(List<T> recordList);

    /**
     * 批量更新功能
     * @param recordList
     * @return
     */
    @UpdateProvider(type = IdSpecialProvider.class, method = "dynamicSQL")
    int updateBatchList(List<T> recordList);

}


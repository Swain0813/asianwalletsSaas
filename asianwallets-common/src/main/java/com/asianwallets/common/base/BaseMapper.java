package com.asianwallets.common.base;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: (mybatis基础类)
 * @createTime 2018年6月29日 上午10:55:50
 * @copyright: 上海众哈网络技术有限公司
 */
@Repository
public interface BaseMapper<T> extends Mapper<T>, IdInserListMapper<T>{


}

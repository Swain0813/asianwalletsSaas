package com.asianwallets.common.base;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;


/**
 * @classDesc: 功能描述: 批量插入功能修改
 * @author Wu, Hua-Zheng
 * @createTime 2018年6月29日 上午10:54:26
 * @version v1.0.0
 */
public class IdSpecialProvider extends MapperTemplate{

    public IdSpecialProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }


    /**
     * 批量插入，支持自定义主键批量插入
     */
    public String insertList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if ( column.isInsertable()) {
                sql.append(column.getColumnHolder("record") + ",");
            }
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }


    public String insertUseGeneratedKeys(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(SqlHelper.insertValuesColumns(entityClass, false, false, false));
        return sql.toString();
    }

    /**
     * 拼update sql, 使用case when方式，id为主键
     *
     * @param ms
     * @return
     */
    public String updateBatchList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"set\" suffixOverrides=\",\">");

        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                sql.append("  <trim prefix=\""+column.getColumn()+" =case\" suffix=\"end,\">");
                sql.append("    <foreach collection=\"list\" item=\"i\" index=\"index\">");
                sql.append("      <if test=\"i."+column.getProperty()+"!=null\">");
                sql.append("         when id=#{i.id} then #{i."+column.getProperty()+"}");
                sql.append("      </if>");
                sql.append("    </foreach>");
                sql.append("  </trim>");
            }
        }
        sql.append("</trim>");
        sql.append("WHERE");
        sql.append(" id IN ");
        sql.append("<trim prefix=\"(\" suffix=\")\">");
        sql.append("<foreach collection=\"list\" separator=\", \" item=\"i\" index=\"index\" >");
        sql.append("#{i.id}");
        sql.append("</foreach>");
        sql.append("</trim>");
        return sql.toString();
    }

}

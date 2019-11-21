package com.asianwallets.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: Bean和Map相互转换服务
 * @createTime 2018/8/3 下午6:23
 * @copyright: 上海众哈网络技术有限公司
 */
public class BeanConversionUtils {


    /**
     * @classDesc: 功能描述: 转换简单类型的属性，复杂类型的对象直接会过滤掉
     * @author Wu, Hua-Zheng
     * @createTime 2018/8/3 下午6:26
     * @version v1.0.0
     * @copyright: 上海众哈网络技术有限公司
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, value);
        }

        return map;
    }

}

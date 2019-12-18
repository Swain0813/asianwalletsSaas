package com.asianwallets.common.utils;

import com.asianwallets.common.constant.AsianWalletConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @classDesc: 功能描述: 反射工具类
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
public class ReflexClazzUtils {

    /**
     * @methodDesc: 功能描述: 获取属性值字段和注释
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月22日 下午11:16:23
     * @version v1.0.0
     */
    public static String getAllFiledItems(Class<?> targetClass, Object target) {

        StringBuffer sb = new StringBuffer();
        Field[] fields = targetClass.getDeclaredFields();
        ApiModelProperty apiModel = null;
        for (Field field : fields) {
            apiModel = field.getAnnotation(ApiModelProperty.class); // 获取指定类型注解

            if (ValidatorToolUtils.isNullOrEmpty(apiModel) || ValidatorToolUtils.isNullOrEmpty(apiModel.value())) {
                continue;
            }

            // 添加字段注释和属性字段名称
            sb.append("【" + apiModel.value() + "(" + field.getName());
            try {
                // 增加属性值
                sb.append(")】----------<" + String.valueOf(field.get(target)) + ">\r\n");
            } catch (IllegalArgumentException e) {
                log.error("[ReflexClazzUtils-getAllFiledItems异常]-{}", e);
            } catch (IllegalAccessException e) {
                log.error("[ReflexClazzUtils-getAllFiledItems异常]-{}", e);
            }

        }
        return sb.toString();
    }


    /**
     * @methodDesc: 功能描述: 获取属性值字段和注释
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月22日 下午11:16:23
     * @version v1.0.0
     */
    public static Map<String, String[]> getFiledStructMap(Class<?> targetClass) {

        Map<String, String[]> result = new HashMap<String, String[]>();
        List<String> fieldList = new LinkedList<String>();
        List<String> commentList = new LinkedList<String>();

        Field[] fields = targetClass.getDeclaredFields();
        ApiModelProperty apiModel = null;
        for (Field field : fields) {
            apiModel = field.getAnnotation(ApiModelProperty.class); // 获取指定类型注解

            if (ValidatorToolUtils.isNullOrEmpty(apiModel) || ValidatorToolUtils.isNullOrEmpty(apiModel.value())) {
                continue;
            }
            // 添加字段注释和属性字段名称
            //commentList.add(apiModel.value() + "[" + field.getName() + "]");
            commentList.add(apiModel.value());
            fieldList.add(field.getName());
        }

        String[] titleArray = new String[commentList.size()];
        titleArray = commentList.toArray(titleArray);

        String[] attrArray = new String[commentList.size()];
        attrArray = fieldList.toArray(attrArray);

        //字段
        result.put(AsianWalletConstant.EXCEL_ATTRS, attrArray);

        //注释
        result.put(AsianWalletConstant.EXCEL_TITLES, titleArray);

        return result;
    }


    /**
     * @methodDesc: 功能描述: 获取属性值字段和注释
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月22日 下午11:16:23
     * @version v1.0.0
     */
    public static Object setFiledValue(String fieldName, String value, Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ReflexClazzUtils-setFiledValue异常]-{}", e);
        }
        return obj;
    }


    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    public String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return (String) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ReflexClazzUtils-getFieldValueByFieldName异常]-{}", e);
            return null;
        }
    }

    /**
     * 根据属性名获取属性元素，包括各种安全范围和所有父类
     *
     * @param fieldName
     * @param object
     * @return
     */
    public Field getFieldByClasss(String fieldName, Object object) {
        Field field = null;
        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                // 这里甚么都不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会进入
                e.printStackTrace();
                log.error("[ReflexClazzUtils-getFieldByClasss异常]-{}", e);
                return null;

            }
        }
        return field;

    }

    /**
     * 获取对象中的属性名与属性值(包括父类)
     *
     * @param f 对象
     * @return 属性名与属性值对应的Map
     */
    public static Map<String, Object> getFieldNames(Object f) {
        Map<String, Object> map = new LinkedHashMap<>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = f.getClass().getDeclaredFields();
        Field[] declaredFields = f.getClass().getSuperclass().getDeclaredFields();
        //遍历子类
        for (Field field : fields) {
            try {
                // 对于每个属性，获取属性名
                String varName = field.getName();
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object obj = field.get(f);
                map.put(varName, obj);
                // 恢复访问控制权限
                field.setAccessible(accessFlag);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        //遍历父类
        for (Field declaredField : declaredFields) {
            try {
                // 对于每个属性，获取属性名
                String varName = declaredField.getName();
                // 获取原来的访问控制权限
                boolean accessFlag = declaredField.isAccessible();
                // 修改访问控制权限
                declaredField.setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object obj = declaredField.get(f);
                map.put(varName, obj);
                // 恢复访问控制权限
                declaredField.setAccessible(accessFlag);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return map;
    }

    /**
     * 获取对象中的属性名与属性值
     *
     * @param f 对象
     * @return 属性名与属性值对应的Map
     */
    public static Map<String, Object> getCommonFieldNames(Object f) {
        Map<String, Object> map = new LinkedHashMap<>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = f.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                // 对于每个属性，获取属性名
                String varName = field.getName();
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object obj = field.get(f);
                map.put(varName, obj);
                // 恢复访问控制权限
                field.setAccessible(accessFlag);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return map;
    }

    /**
     * 获取对象中的属性名与属性值
     *
     * @param f 对象
     * @return 属性名与属性值对应的Map
     */
    public static Map<String, String> getFieldForStringValue(Object f) {
        Map<String, String> map = new LinkedHashMap<>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = f.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                //对于每个属性，获取属性名
                String varName = field.getName();
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object obj = field.get(f);
                map.put(varName, String.valueOf(obj));
                //恢复访问控制权限
                field.setAccessible(accessFlag);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return map;
    }

    /**
     * 获取对象中的空值属性
     *
     * @param source 原对象
     * @return 空值数组
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 将对象转换为Map<String,String>类型
     *
     * @param oldMap
     * @return
     */
    public static Map<String, String> objectMapToStringMap(Map<String, Object> oldMap) {
        HashMap<String, String> newMap = new HashMap<>();
        Set<String> set = oldMap.keySet();
        for (String s : set) {
            newMap.put(s, String.valueOf(oldMap.get(s)));
        }
        return newMap;
    }


}

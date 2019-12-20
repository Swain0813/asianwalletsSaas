package com.asianwallets.trade.utils;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;

import java.util.Map;

@SuppressWarnings("unchecked")
public class HandlerContext {

    private Map<String, Class> handlerMap;

    public HandlerContext(Map<String, Class> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 根据不同的Type获取不同类型AbstractHandler的实例
     *
     * @param type 实例类型
     * @return 抽象处理器
     */
    public AbstractHandler getInstance(String type) {
        Class clazz = handlerMap.get(type);
        if (clazz == null) {
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        return (AbstractHandler) BeanTool.getBean(clazz);
    }

}

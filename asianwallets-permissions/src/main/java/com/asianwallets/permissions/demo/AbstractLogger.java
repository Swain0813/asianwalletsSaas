package com.asianwallets.permissions.demo;

import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:01
 **/
@Component
public abstract  class AbstractLogger {

    //public static String FirstProcess = "FirstProcess";
    //public static String SecondProcess = "SecondProcess";
    //public static String ThreeProcess = "ThreeProcess";
    //
    //protected String level;

    //责任链中的下一个元素
    protected AbstractLogger nextLogger;

    public void setNextLogger(AbstractLogger nextLogger){
        this.nextLogger = nextLogger;
    }

    public void logMessage(String message){
        ResultVO vo  = write(message);
        if(nextLogger !=null && vo.status){
            nextLogger.logMessage(vo.getObject().toString());
        }
    }

    abstract protected ResultVO write(String message);
}

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

    public ResultVO logMessage(ResultVO resultVO){
        ResultVO vo  = write(resultVO);
        if(nextLogger !=null && vo.status){
            nextLogger.logMessage(resultVO);
        }
        return vo;
    }

    abstract protected ResultVO write(ResultVO resultVO);
}

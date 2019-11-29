package com.asianwallets.base.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:02
 **/
public class FirstProcess extends AbstractLogger {

    public FirstProcess(String level) {
        this.level = level;
    }
    public FirstProcess(String level,AbstractLogger abstractLogger) {
        this.level = level;
        this.nextLogger = abstractLogger;
    }
    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- FirstProcess --------------------------------");
        ResultVO resultVO = new ResultVO();
        System.out.println(" ================= FirstProcess : " + message);
        resultVO.setStatus(true);
        return resultVO;
    }
}

package com.asianwallets.base.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:03
 **/
public class SecondProcess extends AbstractLogger {

    public SecondProcess(String level) {
        this.level = level;
    }
    public SecondProcess(String level,AbstractLogger abstractLogger) {
        this.level = level;
        this.nextLogger = abstractLogger;
    }

    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- SecondProcess --------------------------------");
        ResultVO resultVO = new ResultVO();
        System.out.println(" ================= SecondProcess : " + message);
        resultVO.setStatus(true);
        return resultVO;

    }
}

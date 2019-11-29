package com.asianwallets.base.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:03
 **/
public class ThreeProcess extends AbstractLogger{

    public ThreeProcess(String level){
        this.level = level;
    }

    public ThreeProcess(String level,AbstractLogger abstractLogger){
        this.level = level;
        this.nextLogger = abstractLogger;
    }

    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- ThreeProcess --------------------------------");
        ResultVO resultVO = new ResultVO();
        System.out.println(" ================= ThreeProcess : " + message);
        resultVO.setStatus(true);
        return resultVO;
    }
}

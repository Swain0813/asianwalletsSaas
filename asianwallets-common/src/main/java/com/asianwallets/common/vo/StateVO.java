package com.asianwallets.common.vo;

import lombok.Data;

/**
 * @ClassName StateVO
 * @Description TODO
 * @Author abc
 * @Date 2019/11/26 15:05
 * @Version 1.0
 */

@Data
public class StateVO {
    private String stateId;
    private String stateParentId;
    private String cnStateName;
    private String enStateName;
//    private List<CityVO> cityVOS;
}
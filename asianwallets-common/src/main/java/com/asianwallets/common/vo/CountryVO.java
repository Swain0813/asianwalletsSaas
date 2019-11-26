package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CountryVO
 * @Description 国家输出VO
 * @Author abc
 * @Date 2019/11/25 14:23
 * @Version 1.0
 */
@Data
@ApiModel(value = "国家输出VO", description = "国家输出VO")
public class CountryVO {

    private String countryId;
    private String cnCountryName;
    private String enCountryName;
    private String areaCode;
    private List<StateVO> stateVOS;
}

/*@Data
class StateVO {
    private String stateId;
    private String stateParentId;
    private String cnStateName;
    private String enStateName;
//    private List<CityVO> cityVOS;
}*/

/*@Data
class CityVO {
    private String cityId;
    private String parentId;
    private String cnCityName;
    private String enCityName;
}*/


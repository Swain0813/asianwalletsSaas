package com.asianwallets.common.vo;

import com.asianwallets.common.entity.Country;
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
    List<List<List<Country>>> i;

}

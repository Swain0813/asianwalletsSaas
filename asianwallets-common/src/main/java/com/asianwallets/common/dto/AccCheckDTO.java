package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Map;


@Data
@ApiModel(value = "对账单导入实体", description = "对账单导入实体")
public class AccCheckDTO {

    Map<String, AD3CheckAccountDTO> ad3SDMap;
    Map<String, AD3CheckAccountDTO> ad3TKMap;
}

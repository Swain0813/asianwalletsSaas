package com.asianwallets.common.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;


@Data
@ApiModel(value = "pos机交易打印查询输出实体", description = "pos机交易打印查询输出实体")
public class PosSearchLogVO {

    @ApiModelProperty(value = "付款方式")
    private String payMethod;

    @ApiModelProperty(value = "pos机交易打印查询输出实体")
    private List<PosSearchVO> posSearchVO;
}

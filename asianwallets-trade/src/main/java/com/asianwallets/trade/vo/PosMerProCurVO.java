package com.asianwallets.trade.vo;

import com.asianwallets.common.vo.CurrencyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "POS机查询商户产品,币种信息输出实体", description = "POS机查询商户产品,币种信息输出实体")
public class PosMerProCurVO {

    @ApiModelProperty(value = "AW支持的所有币种")
    private List<PosCurrencyVO> currencies;

    @ApiModelProperty(value = "商户产品信息")
    private List<PosMerProVO> merProList;

    public PosMerProCurVO() {
    }

    public PosMerProCurVO(List<PosCurrencyVO> currencies, List<PosMerProVO> merProList) {
        this.currencies = currencies;
        this.merProList = merProList;
    }
}

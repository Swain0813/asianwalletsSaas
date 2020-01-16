package com.asianwallets.common.vo;

import com.asianwallets.common.entity.TradeCheckAccount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "商户交易对账导出实体", description = "机构交易对账导出实体")
public class ExportTradeAccountVO {

    @ApiModelProperty("商户交易对账总表")
    private List<TradeCheckAccount> tradeCheckAccounts;

    @ApiModelProperty("商户交易对账详细表")
    private List<TradeAccountDetailVO> tradeAccountDetailVOS;
}

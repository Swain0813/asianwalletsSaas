package com.asianwallets.common.entity;

import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 结算控制表
 * </p>
 *
 * @author yx
 * @since 2019-12-10
 */
@Data
@Entity
@Table(name =  "settle_control")
public class SettleControl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
	@ApiModelProperty(value = "账户ID")
	@Column(name ="account_id")
	private String accountId;
    /**
     * 最小起结金额
     */
	@ApiModelProperty(value = "最小起结金额")
	@Column(name ="min_settle_amount")
	private BigDecimal minSettleAmount;
    /**
     * 自动结算结算开关
     */
	@ApiModelProperty(value = "自动结算结算开关")
	@Column(name ="settle_switch")
	private Boolean settleSwitch;
    /**
     * 启用禁用
     */
	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;

}

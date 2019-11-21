package com.asianwallets.common.exception;
import lombok.Getter;
import lombok.Setter;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 自定义异常
 * @createTime 2018年7月2日 下午4:03:09
 * @copyright: 上海众哈网络技术有限公司
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 7780291076367953235L;

	private String code;

	// 自定义验证失败
	public BusinessException(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public BusinessException(String code) {
		this.code = code;
	}


}

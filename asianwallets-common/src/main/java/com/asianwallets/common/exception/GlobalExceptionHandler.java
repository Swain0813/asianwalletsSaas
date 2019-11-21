package com.asianwallets.common.exception;

import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 全局统一异常处理
 * @createTime 2018年6月29日 下午2:39:57
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public BaseResponse goalException(Exception e) {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String language = this.getLanguage(request);
		if(language==null){
			language="en-us";
		}
		HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(language);
		if (e instanceof BusinessException) {
			BusinessException businessException = (BusinessException) e;
			log.info("【全局异常捕获】-error-[ businessException 业务异常信息]:[{}]", e.getMessage());
			return ResultUtil.error(businessException.getCode(), errorMsgMap.get(String.valueOf(businessException.getCode())).toString());
		}else if (e instanceof MethodArgumentNotValidException) {
			BindingResult result = ((MethodArgumentNotValidException) e).getBindingResult();
			StringBuffer message = new StringBuffer();
			if (result.hasErrors()) {
				message = printBindException(result);
			}
			log.info("【全局异常捕获】-error-[ MethodArgumentNotValidException 业务异常信息]:[{}]",errorMsgMap.get(message.toString()));
			return ResultUtil.error(message.toString(), errorMsgMap.get(message.toString()).toString());
		}else if(e instanceof MissingServletRequestParameterException) {
			log.error("【全局异常捕获】-error-[ MissingServletRequestParameterException 系统异常信息]:[{}]",e);
			return ResultUtil.error(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode())).toString());
		}else if(e instanceof HttpMessageNotReadableException) {
			log.error("【全局异常捕获】-error-[ HttpMessageNotReadableException 系统异常信息]:[{}]",e);
			return ResultUtil.error(EResultEnum.PARAMETER_IS_NOT_INVALID.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.PARAMETER_IS_NOT_INVALID.getCode())).toString());
		}else if(e instanceof Exception) {
			log.error("【全局异常捕获】-error-[ Exception 系统异常信息]:[{}]",e);
            if(StringUtils.isNotBlank( e.getMessage()) && e.getMessage().equals("Bad credentials")){
                return ResultUtil.error(EResultEnum.USER_OR_PASSWORD_INCORRECT.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.USER_OR_PASSWORD_INCORRECT.getCode())).toString());
			} else if (StringUtils.isNotBlank( e.getMessage()) && e.getMessage().equals("User is disabled")){
				return ResultUtil.error(EResultEnum.USER_NOT_ENABLE.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.USER_NOT_ENABLE.getCode())).toString());
			}
			return ResultUtil.error(EResultEnum.ERROR.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.ERROR.getCode())).toString());
		}else {
			return ResultUtil.error(EResultEnum.REQUEST_REMOTE_ERROR.getCode(), errorMsgMap.get(String.valueOf(EResultEnum.REQUEST_REMOTE_ERROR.getCode())).toString());
		}
	}

	/**
	 * 获取当前请求的语言
	 * @param request
	 * @return
	 */
	private String getLanguage(HttpServletRequest request) {
		String language = null;
		try {
			if (Objects.nonNull(request) && Objects.nonNull(request.getHeader("Content-Language"))) {
				language = request.getHeader("Content-Language");
			}
			if (Objects.nonNull(language)) {
				return language.split(",")[0];
			}
		} catch (Exception e) {
			return Locale.getDefault().getLanguage();
		}
		return language;
	}

	private StringBuffer printBindException(BindingResult result) {
		StringBuffer message = new StringBuffer();
		List<ObjectError> allErrors = result.getAllErrors();
		message.append(allErrors.get(0).getDefaultMessage());//获取第一个error
		return message;
	}


}

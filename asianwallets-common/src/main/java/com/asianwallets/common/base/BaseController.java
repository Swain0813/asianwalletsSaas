package com.asianwallets.common.base;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.OperationLogDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.GetIpAddr;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.utils.ValidatorToolUtils;
import com.asianwallets.common.vo.SysUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 功能描述:(控制器类父类)
 * @createTime 2018年6月29日 上午10:54:07
 * @copyright: 上海众哈网络技术有限公司
 */
public class BaseController {

    @Autowired
    private RedisService redisService;

    /**
     * @methodDesc: 功能描述: 功能描述:(使用valid注解进行字段校验)
     * @createTime 2018年6月29日 上午10:54:45
     * @version v1.0.0
     */
    protected String valid(String msgPrefix, BindingResult bindingResult) {

        //错误信息拼接
        StringBuilder errors = new StringBuilder();
        if (bindingResult.hasErrors()) {
            if (bindingResult.getErrorCount() > 0) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    if (StringUtils.isNoneBlank(errors)) {
                        errors.append(",");
                    }
                    errors.append(error.getField()).append(":").append(error.getDefaultMessage());
                }
            }
        }

        //错误信息异常抛出
        if (ValidatorToolUtils.isNotNullOrEmpty(errors.toString())) {
            throw new BusinessException(msgPrefix + errors.toString());
        }

        return null;
    }

    /**
     * @param req
     * @methodDesc: 功能描述: 功能描述:(获取项目根目录地址)
     * @createTime 2018年6月29日 上午10:54:26
     * @version v1.0.0
     */
    protected String getRootUrl(HttpServletRequest req) {
        StringBuffer url = req.getRequestURL();
        String contextPath = req.getContextPath();
        return url.substring(0, url.indexOf(contextPath) + contextPath.length());
    }

    /**
     * 获取request
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public String getUserName() {
        HttpServletRequest request = getRequest();
        String token = request.getHeader(AsianWalletConstant.tokenHeader);
        if (redisService.get(token) == null) {
            throw new BusinessException(EResultEnum.USER_IS_NOT_LOGIN.getCode());
        }
        return redisService.get(token);
    }

    /**
     * 获取用户ip
     *
     * @return 用户ip
     */
    public String getReqIp() {
        HttpServletRequest request = getRequest();
        return GetIpAddr.getIpAddr(request);
    }

    /**
     * 添加操作日志的对象
     *
     * @param userName         用户名
     * @param operationType    操作类型
     * @param operationContext 操作内容
     * @param functionPoint    功能点
     * @return
     */
    public OperationLogDTO setOperationLog(String userName, Byte operationType, String operationContext, String functionPoint) {
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        operationLogDTO.setUserName(userName);//用户名
        operationLogDTO.setOperationIp(GetIpAddr.getIpAddr(this.getRequest()));//操作ip
        operationLogDTO.setOperationType(operationType);//操作类型
        operationLogDTO.setOperationContext(operationContext);//操作内容
        operationLogDTO.setFunctionPoint(functionPoint);//功能点
        operationLogDTO.setCreator(userName);//创建人
        return operationLogDTO;
    }

    /**
     * 根据errcode返回message
     * @param code
     * @return
     */
    public String getErrorMsgMap(String code){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String language = this.getLanguage();
        HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(language);
        return  String.valueOf(errorMsgMap.get(code));
    }

    /**
     * 获取当前的语言
     * @param
     * @return
     */
    public String getLanguage() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String language = "en-us";
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
}

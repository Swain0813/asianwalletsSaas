package com.asianwallets.permissions.service;

import com.asianwallets.common.vo.SysUserVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SysUserVoService extends UserDetailsService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/1/21
     * @Descripate 得到用户的具体信息
     **/
    SysUserVO getSysUser(String userName, String sysId, Integer permissionType);


}

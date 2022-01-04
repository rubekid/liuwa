package com.liuwa.web.controller.system;

import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.entity.SysMenu;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.core.domain.model.LoginBody;
import com.liuwa.common.utils.SecurityUtils;
import com.liuwa.framework.security.authens.OauthAccessToken;
import com.liuwa.framework.web.service.SysLoginService;
import com.liuwa.framework.web.service.SysPermissionService;
import com.liuwa.framework.web.service.TokenService;
import com.liuwa.system.service.SysMenuService;
import com.liuwa.web.vo.SelfInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 * 
 * @author liuwa
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public OauthAccessToken login(@RequestBody LoginBody loginBody)
    {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        OauthAccessToken accessToken = tokenService.wrapper(token);
        return accessToken;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public SelfInfoVo getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        SelfInfoVo selfInfo = new SelfInfoVo();
        selfInfo.setUser(user);
        selfInfo.setRoles(roles);
        selfInfo.setPermissions(permissions);
        return selfInfo;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Result.ItemsVo getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.items(menuService.buildMenus(menus));
    }
}

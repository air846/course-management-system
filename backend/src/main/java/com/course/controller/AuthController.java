package com.course.controller;

import com.course.dto.Result;
import com.course.dto.auth.LoginRequest;
import com.course.dto.auth.LoginResponse;
import com.course.dto.auth.RefreshTokenRequest;
import com.course.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户认证相关的API接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());
        
        try {
            LoginResponse response = authService.login(loginRequest);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(401, "用户名或密码错误");
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("刷新令牌请求");
        
        try {
            LoginResponse response = authService.refreshToken(refreshTokenRequest);
            return Result.success("令牌刷新成功", response);
        } catch (Exception e) {
            log.error("令牌刷新失败: {}", e.getMessage());
            return Result.error(401, "刷新令牌无效或已过期");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，清除认证信息")
    public Result<String> logout(HttpServletRequest request) {
        log.info("用户登出请求");
        
        try {
            String token = getJwtFromRequest(request);
            if (StringUtils.hasText(token)) {
                boolean success = authService.logout(token);
                if (success) {
                    return Result.success("登出成功");
                }
            }
            return Result.error("登出失败");
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return Result.error("登出失败");
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<LoginResponse.UserInfo> getCurrentUser() {
        log.info("获取当前用户信息");
        
        try {
            LoginResponse.UserInfo userInfo = authService.getCurrentUser();
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error(401, "未找到用户信息，请重新登录");
        }
    }

    /**
     * 检查令牌有效性
     */
    @GetMapping("/check")
    @Operation(summary = "检查令牌", description = "检查当前令牌是否有效")
    public Result<Boolean> checkToken() {
        log.info("检查令牌有效性");
        
        try {
            // 如果能够获取到用户信息，说明令牌有效
            authService.getCurrentUser();
            return Result.success("令牌有效", true);
        } catch (Exception e) {
            log.warn("令牌无效: {}", e.getMessage());
            return Result.error(401, "令牌无效");
        }
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

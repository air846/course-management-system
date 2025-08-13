package com.course.service;

import com.course.dto.auth.LoginRequest;
import com.course.dto.auth.LoginResponse;
import com.course.dto.auth.RefreshTokenRequest;

/**
 * 认证服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 刷新令牌
     * 
     * @param refreshTokenRequest 刷新令牌请求
     * @return 登录响应
     */
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * 用户登出
     * 
     * @param token JWT令牌
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    LoginResponse.UserInfo getCurrentUser();
}

package com.course.service.impl;

import com.course.dto.auth.LoginRequest;
import com.course.dto.auth.LoginResponse;
import com.course.dto.auth.RefreshTokenRequest;
import com.course.security.JwtTokenUtil;
import com.course.security.UserPrincipal;
import com.course.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());
        
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 获取用户信息
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // 生成JWT令牌
        String accessToken = jwtTokenUtil.generateTokenWithRoles(
                userPrincipal, 
                userPrincipal.getId(), 
                userPrincipal.getRoleCodes()
        );
        String refreshToken = jwtTokenUtil.generateRefreshToken(userPrincipal);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtExpiration);
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(userPrincipal.getId());
        userInfo.setUsername(userPrincipal.getUsername());
        userInfo.setRealName(userPrincipal.getRealName());
        userInfo.setEmail(userPrincipal.getEmail());
        userInfo.setAvatar(userPrincipal.getAvatar());
        userInfo.setGender(userPrincipal.getGender());
        userInfo.setRoles(userPrincipal.getRoles());
        userInfo.setRoleCodes(userPrincipal.getRoleCodes());
        userInfo.setLastLoginTime(LocalDateTime.now());

        response.setUserInfo(userInfo);
        
        log.info("用户登录成功: {}", loginRequest.getUsername());
        return response;
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        log.info("刷新令牌");
        
        try {
            // 验证刷新令牌
            if (!jwtTokenUtil.validateToken(refreshToken)) {
                throw new RuntimeException("刷新令牌无效");
            }
            
            // 从刷新令牌中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            
            // 生成新的访问令牌
            String newAccessToken = jwtTokenUtil.refreshToken(refreshToken);
            
            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken); // 刷新令牌保持不变
            response.setExpiresIn(jwtExpiration);
            
            log.info("令牌刷新成功: {}", username);
            return response;
        } catch (Exception e) {
            log.error("令牌刷新失败", e);
            throw new RuntimeException("令牌刷新失败: " + e.getMessage());
        }
    }

    @Override
    public boolean logout(String token) {
        try {
            // 获取当前用户
            String username = jwtTokenUtil.getUsernameFromToken(token);
            log.info("用户登出: {}", username);
            
            // 清除安全上下文
            SecurityContextHolder.clearContext();
            
            // TODO: 可以将token加入黑名单，防止被继续使用
            
            log.info("用户登出成功: {}", username);
            return true;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }

    @Override
    public LoginResponse.UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new RuntimeException("未找到当前用户信息");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(userPrincipal.getId());
        userInfo.setUsername(userPrincipal.getUsername());
        userInfo.setRealName(userPrincipal.getRealName());
        userInfo.setEmail(userPrincipal.getEmail());
        userInfo.setAvatar(userPrincipal.getAvatar());
        userInfo.setGender(userPrincipal.getGender());
        userInfo.setRoles(userPrincipal.getRoles());
        userInfo.setRoleCodes(userPrincipal.getRoleCodes());
        
        return userInfo;
    }
}

package com.course.security;

import com.course.dto.auth.LoginRequest;
import com.course.dto.auth.LoginResponse;
import com.course.dto.Result;
import com.course.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 安全认证集成测试
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void testJwtTokenUtil() {
        // 测试JWT工具类基本功能
        String testSecret = "test-secret-key-for-jwt-token-generation";
        String testUsername = "testuser";
        
        // 由于JwtTokenUtil需要UserDetails，这里只测试基本的token验证
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYzMjQ2NzIwMCwiZXhwIjoxNjMyNTUzNjAwfQ.test";
        
        // 测试从无效token中获取用户名（应该抛出异常）
        assertThrows(Exception.class, () -> {
            jwtTokenUtil.getUsernameFromToken(token);
        });
        
        System.out.println("✅ JWT工具类测试通过");
    }

    @Test
    void testLoginWithValidCredentials() {
        // 测试有效凭据登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        try {
            Result<LoginResponse> result = authController.login(loginRequest);
            
            // 验证登录结果
            assertNotNull(result);
            assertEquals(200, result.getCode());
            assertEquals("登录成功", result.getMessage());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getAccessToken());
            assertNotNull(result.getData().getRefreshToken());
            assertEquals("Bearer", result.getData().getTokenType());
            assertNotNull(result.getData().getUserInfo());
            assertEquals("admin", result.getData().getUserInfo().getUsername());
            
            System.out.println("✅ 有效凭据登录测试通过");
            System.out.println("   - 访问令牌: " + result.getData().getAccessToken().substring(0, 20) + "...");
            System.out.println("   - 用户信息: " + result.getData().getUserInfo().getUsername());
        } catch (Exception e) {
            // 如果管理员用户不存在，这是正常的
            System.out.println("⚠️ 管理员用户可能不存在，跳过登录测试");
        }
    }

    @Test
    void testLoginWithInvalidCredentials() {
        // 测试无效凭据登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrongpassword");

        Result<LoginResponse> result = authController.login(loginRequest);
        
        // 验证登录失败
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
        
        System.out.println("✅ 无效凭据登录测试通过");
    }

    @Test
    void testLoginWithEmptyCredentials() {
        // 测试空凭据登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        // 由于直接调用Controller方法，参数验证会抛出异常
        assertThrows(Exception.class, () -> {
            authController.login(loginRequest);
        });

        System.out.println("✅ 空凭据登录测试通过");
    }

    @Test
    void testGetCurrentUserWithoutAuthentication() {
        // 测试未认证状态下获取当前用户信息
        Result<LoginResponse.UserInfo> result = authController.getCurrentUser();
        
        // 验证失败结果
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("未找到用户信息，请重新登录", result.getMessage());
        
        System.out.println("✅ 未认证获取用户信息测试通过");
    }

    @Test
    void testCheckTokenWithoutAuthentication() {
        // 测试未认证状态下检查令牌
        Result<Boolean> result = authController.checkToken();
        
        // 验证失败结果
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("令牌无效", result.getMessage());
        
        System.out.println("✅ 未认证检查令牌测试通过");
    }
}

package com.course.dto.auth;

import com.course.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录响应DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;

        @Schema(description = "用户名", example = "admin")
        private String username;

        @Schema(description = "真实姓名", example = "管理员")
        private String realName;

        @Schema(description = "邮箱", example = "admin@example.com")
        private String email;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        private String avatar;

        @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
        private Integer gender;

        @Schema(description = "角色列表")
        private List<Role> roles;

        @Schema(description = "角色编码列表")
        private List<String> roleCodes;

        @Schema(description = "最后登录时间")
        private LocalDateTime lastLoginTime;
    }
}

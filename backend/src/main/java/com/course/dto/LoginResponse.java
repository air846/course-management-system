package com.course.dto;

import com.course.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户登录响应类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户登录响应")
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 用户信息内部类
     */
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 用户ID
         */
        @Schema(description = "用户ID", example = "1")
        private Long id;

        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "admin")
        private String username;

        /**
         * 真实姓名
         */
        @Schema(description = "真实姓名", example = "系统管理员")
        private String realName;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱", example = "admin@course.com")
        private String email;

        /**
         * 手机号
         */
        @Schema(description = "手机号", example = "13800000001")
        private String phone;

        /**
         * 头像
         */
        @Schema(description = "头像", example = "http://example.com/avatar.jpg")
        private String avatar;

        /**
         * 性别：0-未知，1-男，2-女
         */
        @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
        private Integer gender;

        /**
         * 角色编码列表
         */
        @Schema(description = "角色编码列表", example = "[\"ADMIN\"]")
        private List<String> roleCodes;

        /**
         * 权限列表
         */
        @Schema(description = "权限列表")
        private List<String> permissions;

        /**
         * 从User实体转换
         */
        public static UserInfo fromUser(User user) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setGender(user.getGender());
            userInfo.setRoleCodes(user.getRoleCodes());
            return userInfo;
        }
    }
}

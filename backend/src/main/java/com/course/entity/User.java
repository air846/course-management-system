package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @TableField("username")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @TableField("password")
    @JsonIgnore
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @TableField("real_name")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;

    /**
     * 头像URL
     */
    @TableField("avatar")
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    /**
     * 用户角色列表（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "用户角色列表")
    private List<Role> roles;

    /**
     * 角色编码列表（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "角色编码列表")
    private List<String> roleCodes;
}

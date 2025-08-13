package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 角色实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("roles")
@Schema(description = "角色信息")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    @TableField("role_name")
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    @TableField("role_code")
    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    @TableField("description")
    @Schema(description = "角色描述", example = "系统管理员，拥有所有权限")
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;
}

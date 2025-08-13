package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.dto.Result;
import com.course.dto.user.*;
import com.course.entity.User;
import com.course.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户相关的API接口")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户: {}", request.getUsername());
        
        // 转换为实体对象
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        // 创建用户
        User createdUser = userService.createUser(user);
        
        // 转换为响应对象
        UserResponse response = convertToResponse(createdUser);
        
        return Result.success("用户创建成功", response);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public Result<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("获取用户详情: {}", id);
        
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        
        UserResponse response = convertToResponse(user);
        return Result.success(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    public Result<UserResponse> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户信息: {}", id);
        
        // 设置用户ID
        request.setId(id);
        
        // 转换为实体对象
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        // 更新用户
        User updatedUser = userService.updateUser(user);
        
        // 转换为响应对象
        UserResponse response = convertToResponse(updatedUser);
        
        return Result.success("用户信息更新成功", response);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "逻辑删除用户")
    public Result<String> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("删除用户: {}", id);
        
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success("用户删除成功");
        } else {
            return Result.error("用户删除失败");
        }
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public Result<IPage<UserResponse>> getUserPage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "角色编码")
            @RequestParam(required = false) String roleCode,
            @Parameter(description = "用户状态")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询用户: current={}, size={}, keyword={}", current, size, keyword);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setKeyword(keyword);
        
        IPage<User> userPage = userService.getUserPage(pageRequest, roleCode, status);
        
        // 转换为响应对象
        IPage<UserResponse> responsePage = userPage.convert(this::convertToResponse);
        
        return Result.success(responsePage);
    }

    /**
     * 修改密码
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "修改用户密码")
    public Result<String> changePassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id,
            @Valid @RequestBody PasswordChangeRequest request) {
        log.info("修改用户密码: {}", id);
        
        // 验证确认密码
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Result.error(400, "新密码和确认密码不一致");
        }
        
        boolean success = userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/password/reset")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public Result<String> resetPassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "新密码", required = true)
            @RequestParam @NotNull String newPassword) {
        log.info("重置用户密码: {}", id);
        
        boolean success = userService.resetPassword(id, newPassword);
        if (success) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    public Result<String> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "状态：0-禁用，1-启用", required = true)
            @RequestParam @NotNull Integer status) {
        log.info("更新用户状态: {} -> {}", id, status);
        
        boolean success = userService.updateUserStatus(id, status);
        if (success) {
            return Result.success("用户状态更新成功");
        } else {
            return Result.error("用户状态更新失败");
        }
    }

    /**
     * 根据角色查询用户列表
     */
    @GetMapping("/role/{roleCode}")
    @Operation(summary = "根据角色查询用户", description = "根据角色编码查询用户列表")
    public Result<List<UserResponse>> getUsersByRole(
            @Parameter(description = "角色编码", required = true)
            @PathVariable String roleCode) {
        log.info("根据角色查询用户: {}", roleCode);
        
        List<User> users = userService.getUsersByRole(roleCode);
        List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public Result<Boolean> checkUsername(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,
            @Parameter(description = "排除的用户ID")
            @RequestParam(required = false) Long excludeId) {
        log.info("检查用户名: {}", username);
        
        boolean exists = userService.isUsernameExists(username, excludeId);
        return Result.success(!exists); // 返回是否可用
    }

    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}

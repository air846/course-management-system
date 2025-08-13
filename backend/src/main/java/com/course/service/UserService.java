package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.PageRequest;
import com.course.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface UserService {

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    User createUser(User user);

    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户（包含角色信息）
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(User user);

    /**
     * 删除用户（逻辑删除）
     * 
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 分页查询用户列表
     * 
     * @param pageRequest 分页请求参数
     * @param roleCode 角色编码（可选）
     * @param status 用户状态（可选）
     * @return 用户分页列表
     */
    IPage<User> getUserPage(PageRequest pageRequest, String roleCode, Integer status);

    /**
     * 根据角色编码查询用户列表
     * 
     * @param roleCode 角色编码
     * @return 用户列表
     */
    List<User> getUsersByRole(String roleCode);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isUsernameExists(String username, Long excludeId);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isEmailExists(String email, Long excludeId);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isPhoneExists(String phone, Long excludeId);

    /**
     * 重置用户密码
     * 
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(Long id, String newPassword);

    /**
     * 修改用户密码
     * 
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 启用/禁用用户
     * 
     * @param id 用户ID
     * @param status 状态（0-禁用，1-启用）
     * @return 是否操作成功
     */
    boolean updateUserStatus(Long id, Integer status);

    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色编码列表
     * 
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoleCodes(Long userId);
}

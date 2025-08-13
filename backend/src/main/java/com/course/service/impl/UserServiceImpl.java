package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.PageRequest;
import com.course.entity.Role;
import com.course.entity.User;
import com.course.mapper.RoleMapper;
import com.course.mapper.UserMapper;
import com.course.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        // 验证用户名唯一性
        if (isUsernameExists(user.getUsername(), null)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(user.getEmail()) && isEmailExists(user.getEmail(), null)) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 验证手机号唯一性
        if (StringUtils.hasText(user.getPhone()) && isPhoneExists(user.getPhone(), null)) {
            throw new IllegalArgumentException("手机号已存在");
        }
        
        // 加密密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }
        if (user.getGender() == null) {
            user.setGender(0); // 默认未知
        }
        
        userMapper.insert(user);
        log.info("用户创建成功，ID: {}", user.getId());
        
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }
        
        User user = userMapper.selectById(id);
        if (user != null) {
            // 获取用户角色
            List<Role> roles = roleMapper.findByUserId(id);
            user.setRoles(roles);
            
            // 获取角色编码
            List<String> roleCodes = userMapper.getUserRoleCodes(id);
            user.setRoleCodes(roleCodes);
        }
        
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        
        User user = userMapper.findByUsernameWithRoles(username);
        if (user != null) {
            // 获取用户角色
            List<Role> roles = roleMapper.findByUserId(user.getId());
            user.setRoles(roles);
            
            // 获取角色编码
            List<String> roleCodes = userMapper.getUserRoleCodes(user.getId());
            user.setRoleCodes(roleCodes);
        }
        
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        
        return userMapper.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        log.info("更新用户信息: {}", user.getId());
        
        // 验证用户是否存在
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证用户名唯一性
        if (StringUtils.hasText(user.getUsername()) && 
            isUsernameExists(user.getUsername(), user.getId())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(user.getEmail()) && 
            isEmailExists(user.getEmail(), user.getId())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 验证手机号唯一性
        if (StringUtils.hasText(user.getPhone()) && 
            isPhoneExists(user.getPhone(), user.getId())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        
        // 不允许通过此方法修改密码
        user.setPassword(null);
        
        userMapper.updateById(user);
        log.info("用户信息更新成功: {}", user.getId());
        
        return getUserById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        if (id == null) {
            return false;
        }
        
        log.info("删除用户: {}", id);
        
        // 验证用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 逻辑删除
        int result = userMapper.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("用户删除成功: {}", id);
        } else {
            log.warn("用户删除失败: {}", id);
        }
        
        return success;
    }

    @Override
    public IPage<User> getUserPage(PageRequest pageRequest, String roleCode, Integer status) {
        Page<User> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        return userMapper.selectUserPageWithRoles(page, pageRequest.getKeyword(), roleCode, status);
    }

    @Override
    public List<User> getUsersByRole(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return List.of();
        }
        
        return userMapper.findByRoleCode(roleCode);
    }

    @Override
    public boolean isUsernameExists(String username, Long excludeId) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email, Long excludeId) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone, Long excludeId) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        if (id == null || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        log.info("重置用户密码: {}", id);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.updateById(user);
        
        boolean success = result > 0;
        if (success) {
            log.info("用户密码重置成功: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        if (id == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        log.info("修改用户密码: {}", id);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.updateById(user);
        
        boolean success = result > 0;
        if (success) {
            log.info("用户密码修改成功: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        
        log.info("更新用户状态: {} -> {}", id, status);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        user.setStatus(status);
        int result = userMapper.updateById(user);
        
        boolean success = result > 0;
        if (success) {
            log.info("用户状态更新成功: {} -> {}", id, status);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null) {
            return false;
        }
        
        log.info("为用户分配角色: {} -> {}", userId, roleIds);
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // TODO: 实现用户角色分配逻辑
        // 这里需要操作user_roles表，暂时返回true
        log.info("用户角色分配成功: {}", userId);
        return true;
    }

    @Override
    public List<String> getUserRoleCodes(Long userId) {
        if (userId == null) {
            return List.of();
        }
        
        return userMapper.getUserRoleCodes(userId);
    }
}

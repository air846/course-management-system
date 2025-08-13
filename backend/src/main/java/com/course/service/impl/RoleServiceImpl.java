package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.PageRequest;
import com.course.entity.Role;
import com.course.mapper.RoleMapper;
import com.course.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(Role role) {
        log.info("创建角色: {}", role.getRoleName());
        
        // 验证角色名称唯一性
        if (isRoleNameExists(role.getRoleName(), null)) {
            throw new IllegalArgumentException("角色名称已存在");
        }
        
        // 验证角色编码唯一性
        if (isRoleCodeExists(role.getRoleCode(), null)) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        
        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(1); // 默认启用
        }
        
        roleMapper.insert(role);
        log.info("角色创建成功，ID: {}", role.getId());
        
        return role;
    }

    @Override
    public Role getRoleById(Long id) {
        if (id == null) {
            return null;
        }
        
        return roleMapper.selectById(id);
    }

    @Override
    public Role getRoleByCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return null;
        }
        
        return roleMapper.findByRoleCode(roleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Role role) {
        if (role.getId() == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        log.info("更新角色信息: {}", role.getId());
        
        // 验证角色是否存在
        Role existingRole = roleMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        
        // 验证角色名称唯一性
        if (StringUtils.hasText(role.getRoleName()) && 
            isRoleNameExists(role.getRoleName(), role.getId())) {
            throw new IllegalArgumentException("角色名称已存在");
        }
        
        // 验证角色编码唯一性
        if (StringUtils.hasText(role.getRoleCode()) && 
            isRoleCodeExists(role.getRoleCode(), role.getId())) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        
        roleMapper.updateById(role);
        log.info("角色信息更新成功: {}", role.getId());
        
        return getRoleById(role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        if (id == null) {
            return false;
        }
        
        log.info("删除角色: {}", id);
        
        // 验证角色是否存在
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        
        // TODO: 检查角色是否被用户使用，如果被使用则不允许删除
        
        // 逻辑删除
        int result = roleMapper.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("角色删除成功: {}", id);
        } else {
            log.warn("角色删除失败: {}", id);
        }
        
        return success;
    }

    @Override
    public IPage<Role> getRolePage(PageRequest pageRequest, Integer status) {
        Page<Role> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.and(w -> w.like(Role::getRoleName, pageRequest.getKeyword())
                           .or().like(Role::getRoleCode, pageRequest.getKeyword())
                           .or().like(Role::getDescription, pageRequest.getKeyword()));
        }
        
        // 状态过滤
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        
        // 排序
        if (StringUtils.hasText(pageRequest.getSortField())) {
            if ("desc".equalsIgnoreCase(pageRequest.getSortOrder())) {
                wrapper.orderByDesc(Role::getId); // 默认按ID排序
            } else {
                wrapper.orderByAsc(Role::getId);
            }
        } else {
            wrapper.orderByDesc(Role::getCreateTime);
        }
        
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Role> getAllEnabledRoles() {
        return roleMapper.findAllEnabled();
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        
        return roleMapper.findByUserId(userId);
    }

    @Override
    public boolean isRoleNameExists(String roleName, Long excludeId) {
        if (!StringUtils.hasText(roleName)) {
            return false;
        }
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, roleName);
        if (excludeId != null) {
            wrapper.ne(Role::getId, excludeId);
        }
        
        return roleMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean isRoleCodeExists(String roleCode, Long excludeId) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(Role::getId, excludeId);
        }
        
        return roleMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        
        log.info("更新角色状态: {} -> {}", id, status);
        
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        
        role.setStatus(status);
        int result = roleMapper.updateById(role);
        
        boolean success = result > 0;
        if (success) {
            log.info("角色状态更新成功: {} -> {}", id, status);
        }
        
        return success;
    }
}

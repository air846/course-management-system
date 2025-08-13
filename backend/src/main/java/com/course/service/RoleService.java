package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface RoleService {

    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @return 创建的角色
     */
    Role createRole(Role role);

    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long id);

    /**
     * 根据角色编码获取角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(String roleCode);

    /**
     * 更新角色信息
     * 
     * @param role 角色信息
     * @return 更新后的角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色（逻辑删除）
     * 
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long id);

    /**
     * 分页查询角色列表
     * 
     * @param pageRequest 分页请求参数
     * @param status 角色状态（可选）
     * @return 角色分页列表
     */
    IPage<Role> getRolePage(PageRequest pageRequest, Integer status);

    /**
     * 获取所有启用的角色
     * 
     * @return 角色列表
     */
    List<Role> getAllEnabledRoles();

    /**
     * 根据用户ID获取角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 检查角色名称是否存在
     * 
     * @param roleName 角色名称
     * @param excludeId 排除的角色ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isRoleNameExists(String roleName, Long excludeId);

    /**
     * 检查角色编码是否存在
     * 
     * @param roleCode 角色编码
     * @param excludeId 排除的角色ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isRoleCodeExists(String roleCode, Long excludeId);

    /**
     * 启用/禁用角色
     * 
     * @param id 角色ID
     * @param status 状态（0-禁用，1-启用）
     * @return 是否操作成功
     */
    boolean updateRoleStatus(Long id, Integer status);
}

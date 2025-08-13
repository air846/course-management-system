package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE role_code = #{roleCode} AND deleted = 0")
    Role findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据用户ID查询用户的角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 查询所有启用的角色
     * 
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE status = 1 AND deleted = 0 ORDER BY id")
    List<Role> findAllEnabled();
}

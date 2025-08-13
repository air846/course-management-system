package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包含角色信息）
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT u.*, GROUP_CONCAT(r.role_code) as role_codes " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.username = #{username} AND u.deleted = 0 " +
            "GROUP BY u.id")
    User findByUsernameWithRoles(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(@Param("phone") String phone);

    /**
     * 分页查询用户列表（包含角色信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param roleCode 角色编码
     * @param status 用户状态
     * @return 用户分页列表
     */
    IPage<User> selectUserPageWithRoles(Page<User> page, 
                                       @Param("keyword") String keyword,
                                       @Param("roleCode") String roleCode,
                                       @Param("status") Integer status);

    /**
     * 根据角色编码查询用户列表
     * 
     * @param roleCode 角色编码
     * @return 用户列表
     */
    @Select("SELECT u.* FROM users u " +
            "INNER JOIN user_roles ur ON u.id = ur.user_id " +
            "INNER JOIN roles r ON ur.role_id = r.id " +
            "WHERE r.role_code = #{roleCode} AND u.deleted = 0 AND u.status = 1")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 获取用户的角色编码列表
     * 
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT r.role_code FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> getUserRoleCodes(@Param("userId") Long userId);
}

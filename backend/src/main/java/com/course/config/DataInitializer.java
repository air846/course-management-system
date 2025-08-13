package com.course.config;

import com.course.entity.Role;
import com.course.entity.User;
import com.course.service.RoleService;
import com.course.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化系统数据...");
        
        // 初始化角色
        initRoles();
        
        // 初始化管理员用户
        initAdminUser();
        
        log.info("系统数据初始化完成");
    }

    /**
     * 初始化角色
     */
    private void initRoles() {
        // 检查是否已存在角色
        if (roleService.getRoleByCode("ADMIN") != null) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        // 创建管理员角色
        Role adminRole = new Role();
        adminRole.setRoleName("管理员");
        adminRole.setRoleCode("ADMIN");
        adminRole.setDescription("系统管理员，拥有所有权限");
        adminRole.setStatus(1);
        roleService.createRole(adminRole);
        log.info("创建管理员角色成功");

        // 创建教师角色
        Role teacherRole = new Role();
        teacherRole.setRoleName("教师");
        teacherRole.setRoleCode("TEACHER");
        teacherRole.setDescription("教师，可以管理课程和查看学生信息");
        teacherRole.setStatus(1);
        roleService.createRole(teacherRole);
        log.info("创建教师角色成功");

        // 创建学生角色
        Role studentRole = new Role();
        studentRole.setRoleName("学生");
        studentRole.setRoleCode("STUDENT");
        studentRole.setDescription("学生，可以选课和查看课程信息");
        studentRole.setStatus(1);
        roleService.createRole(studentRole);
        log.info("创建学生角色成功");
    }

    /**
     * 初始化管理员用户
     */
    private void initAdminUser() {
        // 检查是否已存在管理员用户
        User existingAdmin = userService.getUserByUsername("admin");
        if (existingAdmin != null) {
            log.info("管理员用户已存在，重置密码为123456");
            // 重置密码
            boolean success = userService.resetPassword(existingAdmin.getId(), "123456");
            if (success) {
                log.info("管理员密码重置完成");
            } else {
                log.error("管理员密码重置失败");
            }
            return;
        }

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("123456"); // 会被自动加密
        adminUser.setEmail("admin@course.com");
        adminUser.setPhone("13800000000");
        adminUser.setRealName("系统管理员");
        adminUser.setGender(0);
        adminUser.setStatus(1);
        
        userService.createUser(adminUser);
        log.info("创建管理员用户成功 - 用户名: admin, 密码: 123456");

        // TODO: 为管理员分配角色（需要实现用户角色关联）
    }
}

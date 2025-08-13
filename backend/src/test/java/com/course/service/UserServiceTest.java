package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService测试类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        // 创建测试用户
        User user = new User();
        user.setUsername("servicetest");
        user.setPassword("password123");
        user.setEmail("service@test.com");
        user.setPhone("13800000001");
        user.setRealName("服务测试用户");
        user.setGender(1);

        // 创建用户
        User createdUser = userService.createUser(user);
        
        // 验证创建结果
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("servicetest", createdUser.getUsername());
        assertEquals("service@test.com", createdUser.getEmail());
        assertEquals("服务测试用户", createdUser.getRealName());
        assertEquals(1, createdUser.getStatus()); // 默认启用
        
        // 验证密码已加密
        assertNotEquals("password123", createdUser.getPassword());
        assertTrue(createdUser.getPassword().startsWith("$2a$"));
        
        System.out.println("✅ 用户创建测试通过 - 用户ID: " + createdUser.getId());
    }

    @Test
    void testCreateUserWithDuplicateUsername() {
        // 先创建一个用户
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password123");
        user1.setEmail("user1@test.com");
        user1.setRealName("用户1");
        
        userService.createUser(user1);

        // 尝试创建相同用户名的用户
        User user2 = new User();
        user2.setUsername("duplicate");
        user2.setPassword("password456");
        user2.setEmail("user2@test.com");
        user2.setRealName("用户2");

        // 应该抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user2);
        });
        
        assertEquals("用户名已存在", exception.getMessage());
        System.out.println("✅ 用户名重复检查测试通过");
    }

    @Test
    void testGetUserById() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("getbyid");
        user.setPassword("password123");
        user.setEmail("getbyid@test.com");
        user.setRealName("根据ID查询用户");
        
        User createdUser = userService.createUser(user);
        Long userId = createdUser.getId();

        // 根据ID查询用户
        User foundUser = userService.getUserById(userId);
        
        // 验证查询结果
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("getbyid", foundUser.getUsername());
        assertEquals("getbyid@test.com", foundUser.getEmail());
        
        System.out.println("✅ 根据ID查询用户测试通过 - 用户: " + foundUser.getRealName());
    }

    @Test
    void testGetUserByUsername() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("getbyusername");
        user.setPassword("password123");
        user.setEmail("getbyusername@test.com");
        user.setRealName("根据用户名查询用户");
        
        userService.createUser(user);

        // 根据用户名查询用户
        User foundUser = userService.getUserByUsername("getbyusername");
        
        // 验证查询结果
        assertNotNull(foundUser);
        assertEquals("getbyusername", foundUser.getUsername());
        assertEquals("getbyusername@test.com", foundUser.getEmail());
        
        System.out.println("✅ 根据用户名查询用户测试通过 - 用户: " + foundUser.getRealName());
    }

    @Test
    void testUpdateUser() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("updatetest");
        user.setPassword("password123");
        user.setEmail("update@test.com");
        user.setRealName("更新测试用户");
        
        User createdUser = userService.createUser(user);
        Long userId = createdUser.getId();

        // 更新用户信息
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setRealName("已更新的用户");
        updateUser.setPhone("13900000000");
        updateUser.setGender(2);
        
        User updatedUser = userService.updateUser(updateUser);
        
        // 验证更新结果
        assertNotNull(updatedUser);
        assertEquals("已更新的用户", updatedUser.getRealName());
        assertEquals("13900000000", updatedUser.getPhone());
        assertEquals(2, updatedUser.getGender());
        // 用户名和邮箱应该保持不变
        assertEquals("updatetest", updatedUser.getUsername());
        assertEquals("update@test.com", updatedUser.getEmail());
        
        System.out.println("✅ 用户更新测试通过 - 新姓名: " + updatedUser.getRealName());
    }

    @Test
    void testDeleteUser() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("deletetest");
        user.setPassword("password123");
        user.setEmail("delete@test.com");
        user.setRealName("删除测试用户");
        
        User createdUser = userService.createUser(user);
        Long userId = createdUser.getId();

        // 删除用户
        boolean result = userService.deleteUser(userId);
        
        // 验证删除结果
        assertTrue(result);
        
        // 验证用户已被逻辑删除
        User deletedUser = userService.getUserById(userId);
        assertNull(deletedUser);
        
        System.out.println("✅ 用户删除测试通过 - 用户已被逻辑删除");
    }

    @Test
    void testGetUserPage() {
        // 先创建几个测试用户
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("pagetest" + i);
            user.setPassword("password123");
            user.setEmail("page" + i + "@test.com");
            user.setRealName("分页测试用户" + i);
            user.setStatus(i % 2); // 交替设置状态
            
            userService.createUser(user);
        }

        // 分页查询用户
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(1);
        pageRequest.setSize(3);
        pageRequest.setKeyword("分页测试");
        
        IPage<User> userPage = userService.getUserPage(pageRequest, null, null);
        
        // 验证分页结果
        assertNotNull(userPage);
        assertTrue(userPage.getTotal() >= 5);
        assertTrue(userPage.getRecords().size() <= 3);
        
        System.out.println("✅ 用户分页查询测试通过 - 总数: " + userPage.getTotal() + 
                          ", 当前页数据: " + userPage.getRecords().size());
    }

    @Test
    void testIsUsernameExists() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("existstest");
        user.setPassword("password123");
        user.setEmail("exists@test.com");
        user.setRealName("存在性测试用户");
        
        User createdUser = userService.createUser(user);

        // 测试用户名存在性检查
        assertTrue(userService.isUsernameExists("existstest", null));
        assertFalse(userService.isUsernameExists("notexists", null));
        
        // 测试排除自己的情况
        assertFalse(userService.isUsernameExists("existstest", createdUser.getId()));
        
        System.out.println("✅ 用户名存在性检查测试通过");
    }

    @Test
    void testChangePassword() {
        // 先创建一个用户
        User user = new User();
        user.setUsername("passwordtest");
        user.setPassword("oldpassword");
        user.setEmail("password@test.com");
        user.setRealName("密码测试用户");
        
        User createdUser = userService.createUser(user);
        Long userId = createdUser.getId();

        // 修改密码
        boolean result = userService.changePassword(userId, "oldpassword", "newpassword");
        
        // 验证修改结果
        assertTrue(result);
        
        // 验证新密码生效（通过再次修改密码来验证）
        boolean result2 = userService.changePassword(userId, "newpassword", "newerpassword");
        assertTrue(result2);
        
        // 验证旧密码不能使用
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(userId, "oldpassword", "anotherpassword");
        });
        assertEquals("原密码不正确", exception.getMessage());
        
        System.out.println("✅ 密码修改测试通过");
    }
}

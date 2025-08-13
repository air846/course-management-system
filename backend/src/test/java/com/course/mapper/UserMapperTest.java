package com.course.mapper;

import com.course.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapper测试类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsertUser() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhone("13800000000");
        user.setRealName("测试用户");
        user.setGender(1);
        user.setStatus(1);

        // 插入用户
        int result = userMapper.insert(user);
        
        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(user.getId());
        
        System.out.println("✅ 用户插入测试通过 - 用户ID: " + user.getId());
    }

    @Test
    void testSelectById() {
        // 先插入一个测试用户
        User user = new User();
        user.setUsername("selecttest");
        user.setPassword("password123");
        user.setEmail("select@example.com");
        user.setRealName("查询测试用户");
        user.setStatus(1);
        
        userMapper.insert(user);
        Long userId = user.getId();

        // 根据ID查询用户
        User foundUser = userMapper.selectById(userId);
        
        // 验证查询结果
        assertNotNull(foundUser);
        assertEquals("selecttest", foundUser.getUsername());
        assertEquals("select@example.com", foundUser.getEmail());
        assertEquals("查询测试用户", foundUser.getRealName());
        
        System.out.println("✅ 根据ID查询用户测试通过 - 用户名: " + foundUser.getUsername());
    }

    @Test
    void testFindByUsername() {
        // 先插入一个测试用户
        User user = new User();
        user.setUsername("usernametest");
        user.setPassword("password123");
        user.setEmail("username@example.com");
        user.setRealName("用户名测试");
        user.setStatus(1);
        
        userMapper.insert(user);

        // 根据用户名查询
        User foundUser = userMapper.findByUsernameWithRoles("usernametest");
        
        // 验证查询结果
        assertNotNull(foundUser);
        assertEquals("usernametest", foundUser.getUsername());
        assertEquals("username@example.com", foundUser.getEmail());
        
        System.out.println("✅ 根据用户名查询测试通过 - 用户: " + foundUser.getRealName());
    }

    @Test
    void testFindByEmail() {
        // 先插入一个测试用户
        User user = new User();
        user.setUsername("emailtest");
        user.setPassword("password123");
        user.setEmail("email@test.com");
        user.setRealName("邮箱测试用户");
        user.setStatus(1);
        
        userMapper.insert(user);

        // 根据邮箱查询
        User foundUser = userMapper.findByEmail("email@test.com");
        
        // 验证查询结果
        assertNotNull(foundUser);
        assertEquals("emailtest", foundUser.getUsername());
        assertEquals("email@test.com", foundUser.getEmail());
        
        System.out.println("✅ 根据邮箱查询测试通过 - 用户: " + foundUser.getRealName());
    }

    @Test
    void testUpdateUser() {
        // 先插入一个测试用户
        User user = new User();
        user.setUsername("updatetest");
        user.setPassword("password123");
        user.setEmail("update@example.com");
        user.setRealName("更新测试用户");
        user.setStatus(1);
        
        userMapper.insert(user);
        Long userId = user.getId();

        // 更新用户信息
        user.setRealName("已更新的用户");
        user.setPhone("13900000000");
        
        int result = userMapper.updateById(user);
        
        // 验证更新结果
        assertEquals(1, result);
        
        // 重新查询验证
        User updatedUser = userMapper.selectById(userId);
        assertEquals("已更新的用户", updatedUser.getRealName());
        assertEquals("13900000000", updatedUser.getPhone());
        
        System.out.println("✅ 用户更新测试通过 - 新姓名: " + updatedUser.getRealName());
    }

    @Test
    void testDeleteUser() {
        // 先插入一个测试用户
        User user = new User();
        user.setUsername("deletetest");
        user.setPassword("password123");
        user.setEmail("delete@example.com");
        user.setRealName("删除测试用户");
        user.setStatus(1);
        
        userMapper.insert(user);
        Long userId = user.getId();

        // 删除用户（逻辑删除）
        int result = userMapper.deleteById(userId);
        
        // 验证删除结果
        assertEquals(1, result);
        
        // 验证逻辑删除（用户仍存在但deleted=1）
        User deletedUser = userMapper.selectById(userId);
        // 由于MyBatis Plus的逻辑删除，这里应该返回null
        assertNull(deletedUser);
        
        System.out.println("✅ 用户删除测试通过 - 用户已被逻辑删除");
    }

    @Test
    void testSelectAll() {
        // 插入几个测试用户
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("listtest" + i);
            user.setPassword("password123");
            user.setEmail("list" + i + "@example.com");
            user.setRealName("列表测试用户" + i);
            user.setStatus(1);
            
            userMapper.insert(user);
        }

        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        
        // 验证查询结果
        assertNotNull(users);
        assertTrue(users.size() >= 3);
        
        System.out.println("✅ 查询所有用户测试通过 - 用户数量: " + users.size());
        users.forEach(u -> System.out.println("   - " + u.getUsername() + " (" + u.getRealName() + ")"));
    }
}

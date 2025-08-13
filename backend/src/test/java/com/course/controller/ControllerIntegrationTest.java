package com.course.controller;

import com.course.dto.user.UserCreateRequest;
import com.course.dto.course.CourseCreateRequest;
import com.course.dto.Result;
import com.course.dto.user.UserResponse;
import com.course.dto.course.CourseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Controller层集成测试
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ControllerIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private CourseController courseController;

    @Test
    void testUserControllerCreateUser() {
        // 创建用户请求
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setPhone("13800138000");
        request.setRealName("测试用户");
        request.setGender(1);

        // 调用Controller方法
        Result<UserResponse> result = userController.createUser(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("用户创建成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("test@example.com", result.getData().getEmail());
        assertEquals("测试用户", result.getData().getRealName());

        System.out.println("✅ 用户创建Controller测试通过");
    }

    @Test
    void testUserControllerGetById() {
        // 先创建一个用户
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername("getuser");
        createRequest.setPassword("password123");
        createRequest.setEmail("get@example.com");
        createRequest.setRealName("获取测试用户");

        Result<UserResponse> createResult = userController.createUser(createRequest);
        Long userId = createResult.getData().getId();

        // 根据ID获取用户
        Result<UserResponse> result = userController.getUserById(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("getuser", result.getData().getUsername());

        System.out.println("✅ 根据ID获取用户Controller测试通过");
    }

    @Test
    void testUserControllerCheckUsername() {
        // 检查用户名是否存在
        Result<Boolean> result = userController.checkUsername("nonexistentuser", null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData()); // 用户名可用

        System.out.println("✅ 用户名检查Controller测试通过");
    }

    @Test
    void testCourseControllerCreateCourse() {
        // 创建课程请求
        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseCode("TEST001");
        request.setCourseName("测试课程");
        request.setCategory("测试分类");
        request.setCredits(new BigDecimal("3.0"));
        request.setHours(48);
        request.setDescription("这是一个测试课程");
        request.setTeacherId(1L);
        request.setMaxStudents(50);
        request.setSemester("2024春季");

        // 调用Controller方法
        Result<CourseResponse> result = courseController.createCourse(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("课程创建成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("TEST001", result.getData().getCourseCode());
        assertEquals("测试课程", result.getData().getCourseName());
        assertEquals("测试分类", result.getData().getCategory());

        System.out.println("✅ 课程创建Controller测试通过");
    }

    @Test
    void testCourseControllerGetById() {
        // 先创建一个课程
        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseCode("GET001");
        createRequest.setCourseName("获取测试课程");
        createRequest.setCredits(new BigDecimal("3.0"));
        createRequest.setTeacherId(1L);
        createRequest.setMaxStudents(50);
        createRequest.setSemester("2024春季");

        Result<CourseResponse> createResult = courseController.createCourse(createRequest);
        Long courseId = createResult.getData().getId();

        // 根据ID获取课程
        Result<CourseResponse> result = courseController.getCourseById(courseId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("GET001", result.getData().getCourseCode());

        System.out.println("✅ 根据ID获取课程Controller测试通过");
    }

    @Test
    void testCourseControllerCheckCode() {
        // 检查课程编码是否存在
        Result<Boolean> result = courseController.checkCourseCode("NONEXISTENT", null);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData()); // 课程编码可用

        System.out.println("✅ 课程编码检查Controller测试通过");
    }

    @Test
    void testCourseControllerGetCategories() {
        // 获取课程分类列表
        Result result = courseController.getCategoryList();

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());

        System.out.println("✅ 获取课程分类Controller测试通过");
    }
}

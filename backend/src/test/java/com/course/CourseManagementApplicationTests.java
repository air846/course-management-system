package com.course;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 课程管理系统应用测试类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
class CourseManagementApplicationTests {

    /**
     * 测试应用上下文加载
     */
    @Test
    void contextLoads() {
        // 这个测试用于验证Spring Boot应用上下文能够正常加载
        System.out.println("课程管理系统应用上下文加载测试通过！");
    }

    /**
     * 测试应用启动
     */
    @Test
    void applicationStarts() {
        // 验证应用能够正常启动
        System.out.println("课程管理系统应用启动测试通过！");
    }
}

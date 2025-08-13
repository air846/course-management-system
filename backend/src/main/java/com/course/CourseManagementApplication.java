package com.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 课程管理系统主启动类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.course.mapper")
public class CourseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
        System.out.println("=================================");
        System.out.println("课程管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================");
    }
}

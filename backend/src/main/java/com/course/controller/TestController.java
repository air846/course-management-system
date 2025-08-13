package com.course.controller;

import com.course.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证应用是否正常运行
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试系统是否正常运行的接口")
public class TestController {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "课程管理系统运行正常");
        data.put("version", "1.0.0");
        
        return Result.success("系统运行正常", data);
    }

    /**
     * 欢迎页面
     */
    @GetMapping("/welcome")
    @Operation(summary = "欢迎页面", description = "系统欢迎信息")
    public Result<String> welcome() {
        return Result.success("欢迎使用课程管理系统！");
    }

    /**
     * 系统信息
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息", description = "获取系统基本信息")
    public Result<Map<String, Object>> info() {
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("applicationName", "课程管理系统");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("description", "基于Spring Boot + Vue 3的现代化课程管理系统");
        systemInfo.put("author", "课程管理系统开发团队");
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("springBootVersion", "2.7.14");
        systemInfo.put("buildTime", LocalDateTime.now());
        
        return Result.success("获取系统信息成功", systemInfo);
    }
}

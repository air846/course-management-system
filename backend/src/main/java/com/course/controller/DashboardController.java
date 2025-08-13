package com.course.controller;

import com.course.dto.Result;
import com.course.service.CourseService;
import com.course.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "仪表盘统计数据接口")
public class DashboardController {

    private final UserService userService;
    private final CourseService courseService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计数据", description = "获取仪表盘的统计数据")
    public Result<Map<String, Object>> getStats() {
        log.info("获取仪表盘统计数据");
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 统计总用户数（排除已删除的）
            com.course.dto.PageRequest userPageRequest = new com.course.dto.PageRequest();
            userPageRequest.setCurrent(1);
            userPageRequest.setSize(1);
            long totalUsers = userService.getUserPage(userPageRequest, null, null).getTotal();

            // 统计总课程数（排除已删除的）
            com.course.dto.PageRequest coursePageRequest = new com.course.dto.PageRequest();
            coursePageRequest.setCurrent(1);
            coursePageRequest.setSize(1);
            long totalCourses = courseService.getCoursePage(coursePageRequest, null, null, null, null).getTotal();

            // 统计在线用户数（这里简化为活跃用户数，实际应该根据session或token统计）
            com.course.dto.PageRequest onlinePageRequest = new com.course.dto.PageRequest();
            onlinePageRequest.setCurrent(1);
            onlinePageRequest.setSize(1);
            long onlineUsers = userService.getUserPage(onlinePageRequest, null, 1).getTotal();
            
            // 系统消息数（这里暂时设为0，后续可以添加消息系统）
            long systemMessages = 0;
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalCourses", totalCourses);
            stats.put("onlineUsers", onlineUsers);
            stats.put("systemMessages", systemMessages);
            
            log.info("统计数据获取成功: {}", stats);
            return Result.success("获取统计数据成功", stats);
            
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }
    
    /**
     * 获取用户角色分布统计
     */
    @GetMapping("/user-role-stats")
    @Operation(summary = "获取用户角色分布", description = "获取用户角色分布统计")
    public Result<Map<String, Object>> getUserRoleStats() {
        log.info("获取用户角色分布统计");
        
        try {
            Map<String, Object> roleStats = new HashMap<>();
            
            // 统计各角色用户数
            long adminCount = userService.getUsersByRole("ADMIN").size();
            long teacherCount = userService.getUsersByRole("TEACHER").size();
            long studentCount = userService.getUsersByRole("STUDENT").size();
            
            roleStats.put("adminCount", adminCount);
            roleStats.put("teacherCount", teacherCount);
            roleStats.put("studentCount", studentCount);
            
            log.info("用户角色分布统计获取成功: {}", roleStats);
            return Result.success("获取用户角色分布成功", roleStats);
            
        } catch (Exception e) {
            log.error("获取用户角色分布失败", e);
            return Result.error("获取用户角色分布失败");
        }
    }
}

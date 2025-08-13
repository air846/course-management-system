package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.dto.Result;
import com.course.dto.course.*;
import com.course.entity.Course;
import com.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程管理控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Validated
@Tag(name = "课程管理", description = "课程相关的API接口")
public class CourseController {

    private final CourseService courseService;

    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "创建新课程")
    public Result<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        log.info("创建课程: {}", request.getCourseName());
        
        // 转换为实体对象
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        
        // 创建课程
        Course createdCourse = courseService.createCourse(course);
        
        // 转换为响应对象
        CourseResponse response = convertToResponse(createdCourse);
        
        return Result.success("课程创建成功", response);
    }

    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情", description = "根据课程ID获取课程详细信息")
    public Result<CourseResponse> getCourseById(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("获取课程详情: {}", id);
        
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Result.error(404, "课程不存在");
        }
        
        CourseResponse response = convertToResponse(course);
        return Result.success(response);
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程信息", description = "更新课程基本信息")
    public Result<CourseResponse> updateCourse(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long id,
            @Valid @RequestBody CourseUpdateRequest request) {
        log.info("更新课程信息: {}", id);
        
        // 设置课程ID
        request.setId(id);
        
        // 转换为实体对象
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        
        // 更新课程
        Course updatedCourse = courseService.updateCourse(course);
        
        // 转换为响应对象
        CourseResponse response = convertToResponse(updatedCourse);
        
        return Result.success("课程信息更新成功", response);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "逻辑删除课程")
    public Result<String> deleteCourse(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("删除课程: {}", id);
        
        boolean success = courseService.deleteCourse(id);
        if (success) {
            return Result.success("课程删除成功");
        } else {
            return Result.error("课程删除失败");
        }
    }

    /**
     * 分页查询课程列表
     */
    @GetMapping
    @Operation(summary = "分页查询课程", description = "分页查询课程列表")
    public Result<IPage<CourseResponse>> getCoursePage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "课程分类")
            @RequestParam(required = false) String category,
            @Parameter(description = "教师ID")
            @RequestParam(required = false) Long teacherId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "课程状态")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询课程: current={}, size={}, keyword={}", current, size, keyword);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setKeyword(keyword);
        
        IPage<Course> coursePage = courseService.getCoursePage(pageRequest, category, teacherId, semester, status);
        
        // 转换为响应对象
        IPage<CourseResponse> responsePage = coursePage.convert(this::convertToResponse);
        
        return Result.success(responsePage);
    }

    /**
     * 根据学期查询课程列表
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询课程", description = "根据学期查询课程列表")
    public Result<List<CourseResponse>> getCoursesBySemester(
            @Parameter(description = "学期", required = true)
            @PathVariable String semester) {
        log.info("根据学期查询课程: {}", semester);
        
        List<Course> courses = courseService.getCoursesBySemester(semester);
        List<CourseResponse> responses = courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询可选课程列表
     */
    @GetMapping("/available")
    @Operation(summary = "查询可选课程", description = "查询指定学期的可选课程列表")
    public Result<List<CourseResponse>> getAvailableCourses(
            @Parameter(description = "学期", required = true)
            @RequestParam String semester) {
        log.info("查询可选课程: {}", semester);
        
        List<Course> courses = courseService.getAvailableCourses(semester);
        List<CourseResponse> responses = courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 根据教师查询课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "根据教师查询课程", description = "根据教师ID查询课程列表")
    public Result<List<CourseResponse>> getCoursesByTeacher(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long teacherId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("根据教师查询课程: teacherId={}, semester={}", teacherId, semester);
        
        List<Course> courses = courseService.getCoursesByTeacher(teacherId, semester);
        List<CourseResponse> responses = courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 更新课程状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新课程状态", description = "启用或停用课程")
    public Result<String> updateCourseStatus(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "状态：0-停课，1-正常", required = true)
            @RequestParam @NotNull Integer status) {
        log.info("更新课程状态: {} -> {}", id, status);
        
        boolean success = courseService.updateCourseStatus(id, status);
        if (success) {
            return Result.success("课程状态更新成功");
        } else {
            return Result.error("课程状态更新失败");
        }
    }

    /**
     * 复制课程到新学期
     */
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制课程", description = "复制课程到新学期")
    public Result<CourseResponse> copyCourseToNewSemester(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "新学期", required = true)
            @RequestParam @NotNull String newSemester) {
        log.info("复制课程到新学期: {} -> {}", id, newSemester);
        
        Course copiedCourse = courseService.copyCourseToNewSemester(id, newSemester);
        CourseResponse response = convertToResponse(copiedCourse);
        
        return Result.success("课程复制成功", response);
    }

    /**
     * 获取课程分类列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取课程分类", description = "获取所有课程分类列表")
    public Result<List<String>> getCategoryList() {
        log.info("获取课程分类列表");
        
        List<String> categories = courseService.getCategoryList();
        return Result.success(categories);
    }

    /**
     * 检查课程编码是否存在
     */
    @GetMapping("/check/code")
    @Operation(summary = "检查课程编码", description = "检查课程编码是否已存在")
    public Result<Boolean> checkCourseCode(
            @Parameter(description = "课程编码", required = true)
            @RequestParam String courseCode,
            @Parameter(description = "排除的课程ID")
            @RequestParam(required = false) Long excludeId) {
        log.info("检查课程编码: {}", courseCode);
        
        boolean exists = courseService.isCourseCodeExists(courseCode, excludeId);
        return Result.success(!exists); // 返回是否可用
    }

    /**
     * 统计教师课程数量
     */
    @GetMapping("/teacher/{teacherId}/count")
    @Operation(summary = "统计教师课程数量", description = "统计教师的课程数量")
    public Result<Integer> countCoursesByTeacher(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long teacherId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("统计教师课程数量: teacherId={}, semester={}", teacherId, semester);
        
        Integer count = courseService.countCoursesByTeacher(teacherId, semester);
        return Result.success(count);
    }

    /**
     * 转换为响应对象
     */
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        BeanUtils.copyProperties(course, response);
        return response;
    }
}

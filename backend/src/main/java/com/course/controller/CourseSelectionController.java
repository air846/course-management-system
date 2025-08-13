package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.CourseSelectionRequest;
import com.course.dto.CourseSelectionResponse;
import com.course.dto.PageRequest;
import com.course.dto.Result;
import com.course.entity.CourseSelection;
import com.course.service.CourseSelectionService;
import com.course.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选课管理控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/course-selections")
@RequiredArgsConstructor
@Tag(name = "选课管理", description = "选课管理相关接口")
public class CourseSelectionController {

    private final CourseSelectionService courseSelectionService;

    /**
     * 学生选课
     */
    @PostMapping
    @Operation(summary = "学生选课", description = "学生选择课程")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public Result<CourseSelectionResponse> selectCourse(@Valid @RequestBody CourseSelectionRequest request) {
        log.info("学生选课: {}", request.getCourseId());
        
        // 获取当前用户ID，如果请求中没有指定学生ID，则使用当前用户ID
        Long studentId = request.getStudentId();
        if (studentId == null) {
            studentId = SecurityUtils.getCurrentUserId();
        }
        
        // 如果不是管理员，只能为自己选课
        if (!SecurityUtils.hasRole("ADMIN") && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能为自己选课");
        }
        
        CourseSelection selection = courseSelectionService.selectCourse(studentId, request.getCourseId());
        CourseSelectionResponse response = convertToResponse(selection);
        
        return Result.success("选课成功", response);
    }

    /**
     * 学生退课
     */
    @DeleteMapping("/{courseId}")
    @Operation(summary = "学生退课", description = "学生退选课程")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public Result<Void> dropCourse(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "学生ID")
            @RequestParam(required = false) Long studentId) {
        log.info("学生退课: courseId={}, studentId={}", courseId, studentId);
        
        // 获取当前用户ID，如果请求中没有指定学生ID，则使用当前用户ID
        if (studentId == null) {
            studentId = SecurityUtils.getCurrentUserId();
        }
        
        // 如果不是管理员，只能为自己退课
        if (!SecurityUtils.hasRole("ADMIN") && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能为自己退课");
        }
        
        boolean success = courseSelectionService.dropCourse(studentId, courseId);
        if (success) {
            return Result.<Void>success("退课成功", null);
        } else {
            return Result.error("退课失败");
        }
    }

    /**
     * 查询学生的选课记录
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学生选课记录", description = "查询指定学生的选课记录")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<CourseSelectionResponse>> getStudentSelections(
            @Parameter(description = "学生ID", required = true)
            @PathVariable @NotNull Long studentId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("查询学生选课记录: studentId={}, semester={}, status={}", studentId, semester, status);
        
        // 如果不是管理员或教师，只能查看自己的选课记录
        if (!SecurityUtils.hasRole("ADMIN") && !SecurityUtils.hasRole("TEACHER") 
            && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能查看自己的选课记录");
        }
        
        List<CourseSelection> selections = courseSelectionService.getStudentSelections(studentId, semester, status);
        List<CourseSelectionResponse> responses = selections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询我的选课记录
     */
    @GetMapping("/my")
    @Operation(summary = "查询我的选课记录", description = "查询当前用户的选课记录")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<CourseSelectionResponse>> getMySelections(
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("查询我的选课记录: semester={}, status={}", semester, status);
        
        Long studentId = SecurityUtils.getCurrentUserId();
        List<CourseSelection> selections = courseSelectionService.getStudentSelections(studentId, semester, status);
        List<CourseSelectionResponse> responses = selections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询课程的选课学生列表
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程选课学生", description = "查询指定课程的选课学生列表")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<CourseSelectionResponse>> getCourseSelections(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("查询课程选课学生: courseId={}, status={}", courseId, status);
        
        List<CourseSelection> selections = courseSelectionService.getCourseSelections(courseId, status);
        List<CourseSelectionResponse> responses = selections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 分页查询选课记录
     */
    @GetMapping
    @Operation(summary = "分页查询选课记录", description = "分页查询选课记录列表")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<CourseSelectionResponse>> getSelectionPage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询选课记录: current={}, size={}, keyword={}", current, size, keyword);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setKeyword(keyword);
        
        IPage<CourseSelection> selectionPage = courseSelectionService.getSelectionPage(pageRequest, courseId, semester, status);
        
        // 转换为响应对象
        IPage<CourseSelectionResponse> responsePage = selectionPage.convert(this::convertToResponse);
        
        return Result.success(responsePage);
    }

    /**
     * 检查是否可以选课
     */
    @GetMapping("/check/{courseId}")
    @Operation(summary = "检查是否可以选课", description = "检查学生是否可以选择指定课程")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public Result<Boolean> canSelectCourse(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "学生ID")
            @RequestParam(required = false) Long studentId) {
        log.info("检查是否可以选课: courseId={}, studentId={}", courseId, studentId);
        
        // 获取当前用户ID，如果请求中没有指定学生ID，则使用当前用户ID
        if (studentId == null) {
            studentId = SecurityUtils.getCurrentUserId();
        }
        
        // 如果不是管理员，只能检查自己是否可以选课
        if (!SecurityUtils.hasRole("ADMIN") && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能检查自己是否可以选课");
        }
        
        boolean canSelect = courseSelectionService.canSelectCourse(studentId, courseId);
        return Result.success(canSelect);
    }

    /**
     * 统计学生选课数量
     */
    @GetMapping("/count/student/{studentId}")
    @Operation(summary = "统计学生选课数量", description = "统计指定学生的选课数量")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Integer> countStudentSelections(
            @Parameter(description = "学生ID", required = true)
            @PathVariable @NotNull Long studentId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("统计学生选课数量: studentId={}, semester={}, status={}", studentId, semester, status);
        
        // 如果不是管理员或教师，只能查看自己的选课数量
        if (!SecurityUtils.hasRole("ADMIN") && !SecurityUtils.hasRole("TEACHER") 
            && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能查看自己的选课数量");
        }
        
        Integer count = courseSelectionService.countStudentSelections(studentId, semester, status);
        return Result.success(count);
    }

    /**
     * 统计课程选课数量
     */
    @GetMapping("/count/course/{courseId}")
    @Operation(summary = "统计课程选课数量", description = "统计指定课程的选课数量")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Integer> countCourseSelections(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "选课状态")
            @RequestParam(required = false) Integer status) {
        log.info("统计课程选课数量: courseId={}, status={}", courseId, status);
        
        Integer count = courseSelectionService.countCourseSelections(courseId, status);
        return Result.success(count);
    }

    /**
     * 转换为响应对象
     */
    private CourseSelectionResponse convertToResponse(CourseSelection selection) {
        CourseSelectionResponse response = new CourseSelectionResponse();
        BeanUtils.copyProperties(selection, response);
        
        // 设置状态描述
        if (selection.getStatus() != null) {
            response.setStatusText(selection.getStatus() == 1 ? "已选课" : "已退课");
        }
        
        return response;
    }
}

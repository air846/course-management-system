package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.GradeRequest;
import com.course.dto.GradeResponse;
import com.course.dto.GradeStatistics;
import com.course.dto.PageRequest;
import com.course.dto.Result;
import com.course.entity.Grade;
import com.course.service.GradeService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩管理控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "成绩管理", description = "成绩管理相关接口")
public class GradeController {

    private final GradeService gradeService;

    /**
     * 录入或更新成绩
     */
    @PostMapping
    @Operation(summary = "录入或更新成绩", description = "教师录入或更新学生成绩")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<GradeResponse> saveOrUpdateGrade(@Valid @RequestBody GradeRequest request) {
        log.info("录入或更新成绩: studentId={}, courseId={}", request.getStudentId(), request.getCourseId());
        
        Grade grade = gradeService.saveOrUpdateGrade(request);
        GradeResponse response = convertToResponse(grade);
        
        return Result.success("成绩保存成功", response);
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/batch")
    @Operation(summary = "批量录入成绩", description = "批量录入学生成绩")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Integer> batchSaveGrades(@Valid @RequestBody List<GradeRequest> requests) {
        log.info("批量录入成绩: 数量={}", requests.size());
        
        int count = gradeService.batchSaveGrades(requests);
        
        return Result.success("批量录入成功", count);
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩", description = "删除指定成绩记录")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Void> deleteGrade(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("删除成绩: id={}", id);
        
        boolean success = gradeService.deleteGrade(id);
        if (success) {
            return Result.<Void>success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 查询成绩详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询成绩详情", description = "根据ID查询成绩详情")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<GradeResponse> getGradeById(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("查询成绩详情: id={}", id);
        
        Grade grade = gradeService.getGradeById(id);
        if (grade == null) {
            return Result.error("成绩记录不存在");
        }
        
        // 权限检查：学生只能查看自己的成绩
        if (SecurityUtils.isStudent() && !grade.getStudentId().equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("无权查看该成绩");
        }
        
        GradeResponse response = convertToResponse(grade);
        return Result.success(response);
    }

    /**
     * 查询学生成绩列表
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学生成绩", description = "查询指定学生的成绩列表")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<GradeResponse>> getStudentGrades(
            @Parameter(description = "学生ID", required = true)
            @PathVariable @NotNull Long studentId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("查询学生成绩: studentId={}, semester={}", studentId, semester);
        
        // 权限检查：学生只能查看自己的成绩
        if (SecurityUtils.isStudent() && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能查看自己的成绩");
        }
        
        List<Grade> grades = gradeService.getStudentGrades(studentId, semester);
        List<GradeResponse> responses = grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询我的成绩
     */
    @GetMapping("/my")
    @Operation(summary = "查询我的成绩", description = "查询当前用户的成绩列表")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<GradeResponse>> getMyGrades(
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("查询我的成绩: semester={}", semester);
        
        Long studentId = SecurityUtils.getCurrentUserId();
        List<Grade> grades = gradeService.getStudentGrades(studentId, semester);
        List<GradeResponse> responses = grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询课程成绩列表
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程成绩", description = "查询指定课程的成绩列表")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<GradeResponse>> getCourseGrades(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "学期", required = true)
            @RequestParam @NotNull String semester) {
        log.info("查询课程成绩: courseId={}, semester={}", courseId, semester);
        
        List<Grade> grades = gradeService.getCourseGrades(courseId, semester);
        List<GradeResponse> responses = grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 分页查询成绩列表
     */
    @GetMapping
    @Operation(summary = "分页查询成绩", description = "分页查询成绩列表")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<GradeResponse>> getGradePage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "学生ID")
            @RequestParam(required = false) Long studentId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "成绩等级")
            @RequestParam(required = false) String gradeLevel) {
        log.info("分页查询成绩: current={}, size={}, keyword={}", current, size, keyword);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setKeyword(keyword);
        
        IPage<Grade> gradePage = gradeService.getGradePage(pageRequest, courseId, studentId, semester, gradeLevel);
        
        // 转换为响应对象
        IPage<GradeResponse> responsePage = gradePage.convert(this::convertToResponse);
        
        return Result.success(responsePage);
    }

    /**
     * 查询课程成绩统计
     */
    @GetMapping("/statistics/course/{courseId}")
    @Operation(summary = "课程成绩统计", description = "查询指定课程的成绩统计信息")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<GradeStatistics> getCourseStatistics(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "学期", required = true)
            @RequestParam @NotNull String semester) {
        log.info("查询课程成绩统计: courseId={}, semester={}", courseId, semester);
        
        GradeStatistics statistics = gradeService.calculateCourseStatistics(courseId, semester);
        if (statistics == null) {
            return Result.error("暂无成绩数据");
        }
        
        return Result.success(statistics);
    }

    /**
     * 查询学生平均成绩
     */
    @GetMapping("/average/student/{studentId}")
    @Operation(summary = "学生平均成绩", description = "查询学生的平均成绩")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Double> getStudentAverage(
            @Parameter(description = "学生ID", required = true)
            @PathVariable @NotNull Long studentId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("查询学生平均成绩: studentId={}, semester={}", studentId, semester);
        
        // 权限检查：学生只能查看自己的成绩
        if (SecurityUtils.isStudent() && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能查看自己的成绩");
        }
        
        Double average = gradeService.calculateStudentAverage(studentId, semester);
        return Result.success(average);
    }

    /**
     * 查询课程成绩分布
     */
    @GetMapping("/distribution/course/{courseId}")
    @Operation(summary = "课程成绩分布", description = "查询课程的成绩分布统计")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> getCourseGradeDistribution(
            @Parameter(description = "课程ID", required = true)
            @PathVariable @NotNull Long courseId,
            @Parameter(description = "学期", required = true)
            @RequestParam @NotNull String semester) {
        log.info("查询课程成绩分布: courseId={}, semester={}", courseId, semester);
        
        List<Map<String, Object>> distribution = gradeService.getCourseGradeDistribution(courseId, semester);
        return Result.success(distribution);
    }

    /**
     * 查询学生排名
     */
    @GetMapping("/ranking/student/{studentId}")
    @Operation(summary = "学生排名", description = "查询学生的学期排名")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<Map<String, Object>> getStudentRanking(
            @Parameter(description = "学生ID", required = true)
            @PathVariable @NotNull Long studentId,
            @Parameter(description = "学期", required = true)
            @RequestParam @NotNull String semester) {
        log.info("查询学生排名: studentId={}, semester={}", studentId, semester);
        
        // 权限检查：学生只能查看自己的排名
        if (SecurityUtils.isStudent() && !studentId.equals(SecurityUtils.getCurrentUserId())) {
            return Result.error("只能查看自己的排名");
        }
        
        Map<String, Object> ranking = gradeService.getStudentRanking(studentId, semester);
        return Result.success(ranking);
    }

    /**
     * 转换为响应对象
     */
    private GradeResponse convertToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        BeanUtils.copyProperties(grade, response);
        
        // 设置是否及格
        if (grade.getTotalScore() != null) {
            response.setIsPass(grade.getTotalScore().doubleValue() >= 60);
        }
        
        return response;
    }
}

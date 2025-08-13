package com.course.controller;

import com.course.dto.ChartData;
import com.course.dto.Result;
import com.course.dto.StatisticsOverview;
import com.course.dto.TrendData;
import com.course.service.StatisticsService;
import com.course.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计分析", description = "数据统计分析相关接口")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final ExportService exportService;

    /**
     * 获取统计概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取统计概览", description = "获取系统整体统计概览数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<StatisticsOverview> getStatisticsOverview() {
        log.info("获取统计概览");
        
        StatisticsOverview overview = statisticsService.getStatisticsOverview();
        return Result.success(overview);
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/users")
    @Operation(summary = "获取用户统计", description = "获取用户相关统计数据")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StatisticsOverview.UserStatistics> getUserStatistics() {
        log.info("获取用户统计数据");
        
        StatisticsOverview.UserStatistics userStats = statisticsService.getUserStatistics();
        return Result.success(userStats);
    }

    /**
     * 获取课程统计数据
     */
    @GetMapping("/courses")
    @Operation(summary = "获取课程统计", description = "获取课程相关统计数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<StatisticsOverview.CourseStatistics> getCourseStatistics() {
        log.info("获取课程统计数据");
        
        StatisticsOverview.CourseStatistics courseStats = statisticsService.getCourseStatistics();
        return Result.success(courseStats);
    }

    /**
     * 获取成绩统计数据
     */
    @GetMapping("/grades")
    @Operation(summary = "获取成绩统计", description = "获取成绩相关统计数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<StatisticsOverview.GradeStatistics> getGradeStatistics() {
        log.info("获取成绩统计数据");
        
        StatisticsOverview.GradeStatistics gradeStats = statisticsService.getGradeStatistics();
        return Result.success(gradeStats);
    }

    /**
     * 获取公告统计数据
     */
    @GetMapping("/announcements")
    @Operation(summary = "获取公告统计", description = "获取公告相关统计数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<StatisticsOverview.AnnouncementStatistics> getAnnouncementStatistics() {
        log.info("获取公告统计数据");
        
        StatisticsOverview.AnnouncementStatistics announcementStats = statisticsService.getAnnouncementStatistics();
        return Result.success(announcementStats);
    }

    /**
     * 获取用户增长趋势
     */
    @GetMapping("/trends/users")
    @Operation(summary = "获取用户增长趋势", description = "获取指定时间范围内的用户增长趋势")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<TrendData> getUserGrowthTrend(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-06-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "时间范围", example = "month")
            @RequestParam(defaultValue = "month") String timeRange) {
        log.info("获取用户增长趋势: startDate={}, endDate={}, timeRange={}", startDate, endDate, timeRange);
        
        TrendData trendData = statisticsService.getUserGrowthTrend(startDate, endDate, timeRange);
        return Result.success(trendData);
    }

    /**
     * 获取课程选课趋势
     */
    @GetMapping("/trends/course-selections")
    @Operation(summary = "获取课程选课趋势", description = "获取指定时间范围内的课程选课趋势")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<TrendData> getCourseSelectionTrend(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-06-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "时间范围", example = "month")
            @RequestParam(defaultValue = "month") String timeRange) {
        log.info("获取课程选课趋势: startDate={}, endDate={}, timeRange={}", startDate, endDate, timeRange);
        
        TrendData trendData = statisticsService.getCourseSelectionTrend(startDate, endDate, timeRange);
        return Result.success(trendData);
    }

    /**
     * 获取成绩分布图表
     */
    @GetMapping("/charts/grade-distribution")
    @Operation(summary = "获取成绩分布图表", description = "获取成绩分布的图表数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<ChartData> getGradeDistributionChart(
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("获取成绩分布图表: courseId={}, semester={}", courseId, semester);
        
        ChartData chartData = statisticsService.getGradeDistributionChart(courseId, semester);
        return Result.success(chartData);
    }

    /**
     * 获取课程类别分布图表
     */
    @GetMapping("/charts/course-category")
    @Operation(summary = "获取课程类别分布图表", description = "获取课程类别分布的图表数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<ChartData> getCourseCategoryChart() {
        log.info("获取课程类别分布图表");
        
        ChartData chartData = statisticsService.getCourseCategoryChart();
        return Result.success(chartData);
    }

    /**
     * 获取用户角色分布图表
     */
    @GetMapping("/charts/user-role")
    @Operation(summary = "获取用户角色分布图表", description = "获取用户角色分布的图表数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<ChartData> getUserRoleChart() {
        log.info("获取用户角色分布图表");
        
        ChartData chartData = statisticsService.getUserRoleChart();
        return Result.success(chartData);
    }

    /**
     * 获取公告类型分布图表
     */
    @GetMapping("/charts/announcement-type")
    @Operation(summary = "获取公告类型分布图表", description = "获取公告类型分布的图表数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<ChartData> getAnnouncementTypeChart() {
        log.info("获取公告类型分布图表");
        
        ChartData chartData = statisticsService.getAnnouncementTypeChart();
        return Result.success(chartData);
    }

    /**
     * 获取月度活跃数据图表
     */
    @GetMapping("/charts/monthly-activity")
    @Operation(summary = "获取月度活跃数据图表", description = "获取指定年份的月度活跃数据图表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<ChartData> getMonthlyActivityChart(
            @Parameter(description = "年份", example = "2024")
            @RequestParam(defaultValue = "2024") Integer year) {
        log.info("获取月度活跃数据图表: year={}", year);
        
        ChartData chartData = statisticsService.getMonthlyActivityChart(year);
        return Result.success(chartData);
    }

    /**
     * 获取热门课程排行
     */
    @GetMapping("/rankings/popular-courses")
    @Operation(summary = "获取热门课程排行", description = "获取热门课程排行榜")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<List<Map<String, Object>>> getPopularCourses(
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门课程排行: limit={}", limit);
        
        List<Map<String, Object>> popularCourses = statisticsService.getPopularCourses(limit);
        return Result.success(popularCourses);
    }

    /**
     * 获取优秀学生排行
     */
    @GetMapping("/rankings/top-students")
    @Operation(summary = "获取优秀学生排行", description = "获取优秀学生排行榜")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<List<Map<String, Object>>> getTopStudents(
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取优秀学生排行: semester={}, limit={}", semester, limit);
        
        List<Map<String, Object>> topStudents = statisticsService.getTopStudents(semester, limit);
        return Result.success(topStudents);
    }

    /**
     * 获取教师课程统计
     */
    @GetMapping("/teachers/course-statistics")
    @Operation(summary = "获取教师课程统计", description = "获取教师的课程统计数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<List<Map<String, Object>>> getTeacherCourseStatistics(
            @Parameter(description = "教师ID")
            @RequestParam(required = false) Long teacherId) {
        log.info("获取教师课程统计: teacherId={}", teacherId);
        
        List<Map<String, Object>> teacherStats = statisticsService.getTeacherCourseStatistics(teacherId);
        return Result.success(teacherStats);
    }

    /**
     * 获取学期对比数据
     */
    @GetMapping("/comparisons/semester")
    @Operation(summary = "获取学期对比数据", description = "获取两个学期的对比数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Map<String, Object>> getSemesterComparison(
            @Parameter(description = "学期1", example = "2024春季")
            @RequestParam String semester1,
            @Parameter(description = "学期2", example = "2024秋季")
            @RequestParam String semester2) {
        log.info("获取学期对比数据: semester1={}, semester2={}", semester1, semester2);
        
        Map<String, Object> comparison = statisticsService.getSemesterComparison(semester1, semester2);
        return Result.success(comparison);
    }

    /**
     * 获取系统使用情况统计
     */
    @GetMapping("/system-usage")
    @Operation(summary = "获取系统使用情况统计", description = "获取指定时间范围内的系统使用情况统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getSystemUsageStatistics(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-06-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("获取系统使用情况统计: startDate={}, endDate={}", startDate, endDate);
        
        Map<String, Object> usageStats = statisticsService.getSystemUsageStatistics(startDate, endDate);
        return Result.success(usageStats);
    }

    /**
     * 导出统计概览报表
     */
    @GetMapping("/export/overview")
    @Operation(summary = "导出统计概览报表", description = "导出系统整体统计概览Excel报表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportStatisticsOverview() {
        log.info("导出统计概览报表");

        byte[] excelData = exportService.exportStatisticsOverview();

        String filename = "统计概览报表_" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * 导出用户统计报表
     */
    @GetMapping("/export/users")
    @Operation(summary = "导出用户统计报表", description = "导出用户统计Excel报表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUserStatistics(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam(defaultValue = "2024-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-12-31")
            @RequestParam(defaultValue = "2024-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("导出用户统计报表: startDate={}, endDate={}", startDate, endDate);

        byte[] excelData = exportService.exportUserStatistics(startDate, endDate);

        String filename = "用户统计报表_" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * 导出课程统计报表
     */
    @GetMapping("/export/courses")
    @Operation(summary = "导出课程统计报表", description = "导出课程统计Excel报表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportCourseStatistics(
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("导出课程统计报表: semester={}", semester);

        byte[] excelData = exportService.exportCourseStatistics(semester);

        String filename = "课程统计报表_" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * 导出成绩统计报表
     */
    @GetMapping("/export/grades")
    @Operation(summary = "导出成绩统计报表", description = "导出成绩统计Excel报表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportGradeStatistics(
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期")
            @RequestParam(required = false) String semester) {
        log.info("导出成绩统计报表: courseId={}, semester={}", courseId, semester);

        byte[] excelData = exportService.exportGradeStatistics(courseId, semester);

        String filename = "成绩统计报表_" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * 导出公告统计报表
     */
    @GetMapping("/export/announcements")
    @Operation(summary = "导出公告统计报表", description = "导出公告统计Excel报表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportAnnouncementStatistics(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam(defaultValue = "2024-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-12-31")
            @RequestParam(defaultValue = "2024-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("导出公告统计报表: startDate={}, endDate={}", startDate, endDate);

        byte[] excelData = exportService.exportAnnouncementStatistics(startDate, endDate);

        String filename = "公告统计报表_" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
}

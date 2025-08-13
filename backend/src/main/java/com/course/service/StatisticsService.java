package com.course.service;

import com.course.dto.ChartData;
import com.course.dto.StatisticsOverview;
import com.course.dto.TrendData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface StatisticsService {

    /**
     * 获取统计概览
     * 
     * @return 统计概览数据
     */
    StatisticsOverview getStatisticsOverview();

    /**
     * 获取用户统计数据
     * 
     * @return 用户统计数据
     */
    StatisticsOverview.UserStatistics getUserStatistics();

    /**
     * 获取课程统计数据
     * 
     * @return 课程统计数据
     */
    StatisticsOverview.CourseStatistics getCourseStatistics();

    /**
     * 获取成绩统计数据
     * 
     * @return 成绩统计数据
     */
    StatisticsOverview.GradeStatistics getGradeStatistics();

    /**
     * 获取公告统计数据
     * 
     * @return 公告统计数据
     */
    StatisticsOverview.AnnouncementStatistics getAnnouncementStatistics();

    /**
     * 获取用户增长趋势
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param timeRange 时间范围：day-日，week-周，month-月
     * @return 用户增长趋势数据
     */
    TrendData getUserGrowthTrend(LocalDate startDate, LocalDate endDate, String timeRange);

    /**
     * 获取课程选课趋势
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param timeRange 时间范围
     * @return 课程选课趋势数据
     */
    TrendData getCourseSelectionTrend(LocalDate startDate, LocalDate endDate, String timeRange);

    /**
     * 获取成绩分布图表数据
     * 
     * @param courseId 课程ID（可选）
     * @param semester 学期（可选）
     * @return 成绩分布图表数据
     */
    ChartData getGradeDistributionChart(Long courseId, String semester);

    /**
     * 获取课程类别分布图表数据
     * 
     * @return 课程类别分布图表数据
     */
    ChartData getCourseCategoryChart();

    /**
     * 获取用户角色分布图表数据
     * 
     * @return 用户角色分布图表数据
     */
    ChartData getUserRoleChart();

    /**
     * 获取公告类型分布图表数据
     * 
     * @return 公告类型分布图表数据
     */
    ChartData getAnnouncementTypeChart();

    /**
     * 获取月度活跃数据
     * 
     * @param year 年份
     * @return 月度活跃数据
     */
    ChartData getMonthlyActivityChart(Integer year);

    /**
     * 获取热门课程排行
     * 
     * @param limit 限制数量
     * @return 热门课程数据
     */
    List<Map<String, Object>> getPopularCourses(Integer limit);

    /**
     * 获取优秀学生排行
     * 
     * @param semester 学期
     * @param limit 限制数量
     * @return 优秀学生数据
     */
    List<Map<String, Object>> getTopStudents(String semester, Integer limit);

    /**
     * 获取教师课程统计
     * 
     * @param teacherId 教师ID（可选）
     * @return 教师课程统计数据
     */
    List<Map<String, Object>> getTeacherCourseStatistics(Long teacherId);

    /**
     * 获取学期对比数据
     * 
     * @param semester1 学期1
     * @param semester2 学期2
     * @return 学期对比数据
     */
    Map<String, Object> getSemesterComparison(String semester1, String semester2);

    /**
     * 获取系统使用情况统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 系统使用情况数据
     */
    Map<String, Object> getSystemUsageStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 导出统计报表
     * 
     * @param reportType 报表类型
     * @param params 参数
     * @return 报表数据
     */
    byte[] exportStatisticsReport(String reportType, Map<String, Object> params);
}

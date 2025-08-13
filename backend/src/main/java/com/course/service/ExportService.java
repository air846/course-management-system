package com.course.service;

import com.course.dto.StatisticsOverview;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表导出服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface ExportService {

    /**
     * 导出统计概览报表
     * 
     * @return Excel文件字节数组
     */
    byte[] exportStatisticsOverview();

    /**
     * 导出用户统计报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Excel文件字节数组
     */
    byte[] exportUserStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 导出课程统计报表
     * 
     * @param semester 学期
     * @return Excel文件字节数组
     */
    byte[] exportCourseStatistics(String semester);

    /**
     * 导出成绩统计报表
     * 
     * @param courseId 课程ID（可选）
     * @param semester 学期（可选）
     * @return Excel文件字节数组
     */
    byte[] exportGradeStatistics(Long courseId, String semester);

    /**
     * 导出公告统计报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Excel文件字节数组
     */
    byte[] exportAnnouncementStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 导出热门课程排行报表
     * 
     * @param limit 限制数量
     * @return Excel文件字节数组
     */
    byte[] exportPopularCourses(Integer limit);

    /**
     * 导出优秀学生排行报表
     * 
     * @param semester 学期
     * @param limit 限制数量
     * @return Excel文件字节数组
     */
    byte[] exportTopStudents(String semester, Integer limit);

    /**
     * 导出综合统计报表
     * 
     * @param reportType 报表类型
     * @param params 参数
     * @return Excel文件字节数组
     */
    byte[] exportComprehensiveReport(String reportType, Map<String, Object> params);
}

package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计概览DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "统计概览")
public class StatisticsOverview {

    /**
     * 用户统计
     */
    @Schema(description = "用户统计")
    private UserStatistics userStatistics;

    /**
     * 课程统计
     */
    @Schema(description = "课程统计")
    private CourseStatistics courseStatistics;

    /**
     * 成绩统计
     */
    @Schema(description = "成绩统计")
    private GradeStatistics gradeStatistics;

    /**
     * 公告统计
     */
    @Schema(description = "公告统计")
    private AnnouncementStatistics announcementStatistics;

    /**
     * 用户统计内部类
     */
    @Data
    @Schema(description = "用户统计")
    public static class UserStatistics {
        @Schema(description = "总用户数", example = "150")
        private Integer totalUsers;

        @Schema(description = "学生数量", example = "120")
        private Integer studentCount;

        @Schema(description = "教师数量", example = "25")
        private Integer teacherCount;

        @Schema(description = "管理员数量", example = "5")
        private Integer adminCount;

        @Schema(description = "活跃用户数", example = "135")
        private Integer activeUsers;

        @Schema(description = "本月新增用户", example = "15")
        private Integer newUsersThisMonth;

        @Schema(description = "用户活跃率", example = "90.0")
        private BigDecimal activeRate;
    }

    /**
     * 课程统计内部类
     */
    @Data
    @Schema(description = "课程统计")
    public static class CourseStatistics {
        @Schema(description = "总课程数", example = "45")
        private Integer totalCourses;

        @Schema(description = "开放课程数", example = "38")
        private Integer openCourses;

        @Schema(description = "关闭课程数", example = "7")
        private Integer closedCourses;

        @Schema(description = "必修课数量", example = "25")
        private Integer requiredCourses;

        @Schema(description = "选修课数量", example = "20")
        private Integer electiveCourses;

        @Schema(description = "平均选课人数", example = "28.5")
        private BigDecimal avgStudentsPerCourse;

        @Schema(description = "总选课次数", example = "1285")
        private Integer totalSelections;

        @Schema(description = "本学期新增课程", example = "8")
        private Integer newCoursesThisSemester;
    }

    /**
     * 成绩统计内部类
     */
    @Data
    @Schema(description = "成绩统计")
    public static class GradeStatistics {
        @Schema(description = "总成绩记录数", example = "1150")
        private Integer totalGrades;

        @Schema(description = "已录入成绩数", example = "980")
        private Integer gradedCount;

        @Schema(description = "未录入成绩数", example = "170")
        private Integer ungradedCount;

        @Schema(description = "平均成绩", example = "82.5")
        private BigDecimal averageScore;

        @Schema(description = "及格人数", example = "890")
        private Integer passedCount;

        @Schema(description = "不及格人数", example = "90")
        private Integer failedCount;

        @Schema(description = "及格率", example = "90.8")
        private BigDecimal passRate;

        @Schema(description = "优秀率", example = "25.5")
        private BigDecimal excellentRate;
    }

    /**
     * 公告统计内部类
     */
    @Data
    @Schema(description = "公告统计")
    public static class AnnouncementStatistics {
        @Schema(description = "总公告数", example = "85")
        private Integer totalAnnouncements;

        @Schema(description = "已发布公告数", example = "72")
        private Integer publishedCount;

        @Schema(description = "草稿公告数", example = "10")
        private Integer draftCount;

        @Schema(description = "置顶公告数", example = "3")
        private Integer topCount;

        @Schema(description = "本月发布公告数", example = "12")
        private Integer publishedThisMonth;

        @Schema(description = "总阅读次数", example = "2580")
        private Integer totalReadCount;

        @Schema(description = "平均阅读次数", example = "35.8")
        private BigDecimal avgReadCount;
    }
}

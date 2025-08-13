package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 成绩统计DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "成绩统计")
public class GradeStatistics {

    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 课程编码
     */
    @Schema(description = "课程编码", example = "CS101")
    private String courseCode;

    /**
     * 学期
     */
    @Schema(description = "学期", example = "2024春季")
    private String semester;

    /**
     * 总人数
     */
    @Schema(description = "总人数", example = "30")
    private Integer totalStudents;

    /**
     * 已录入成绩人数
     */
    @Schema(description = "已录入成绩人数", example = "28")
    private Integer gradedStudents;

    /**
     * 及格人数
     */
    @Schema(description = "及格人数", example = "25")
    private Integer passedStudents;

    /**
     * 不及格人数
     */
    @Schema(description = "不及格人数", example = "3")
    private Integer failedStudents;

    /**
     * 及格率
     */
    @Schema(description = "及格率", example = "89.29")
    private BigDecimal passRate;

    /**
     * 最高分
     */
    @Schema(description = "最高分", example = "98.5")
    private BigDecimal maxScore;

    /**
     * 最低分
     */
    @Schema(description = "最低分", example = "45.0")
    private BigDecimal minScore;

    /**
     * 平均分
     */
    @Schema(description = "平均分", example = "82.5")
    private BigDecimal avgScore;

    /**
     * 中位数
     */
    @Schema(description = "中位数", example = "85.0")
    private BigDecimal medianScore;

    /**
     * 标准差
     */
    @Schema(description = "标准差", example = "12.5")
    private BigDecimal standardDeviation;

    /**
     * A等级人数
     */
    @Schema(description = "A等级人数", example = "8")
    private Integer gradeACount;

    /**
     * B等级人数
     */
    @Schema(description = "B等级人数", example = "12")
    private Integer gradeBCount;

    /**
     * C等级人数
     */
    @Schema(description = "C等级人数", example = "5")
    private Integer gradeCCount;

    /**
     * D等级人数
     */
    @Schema(description = "D等级人数", example = "2")
    private Integer gradeDCount;

    /**
     * F等级人数
     */
    @Schema(description = "F等级人数", example = "1")
    private Integer gradeFCount;

    /**
     * 优秀率（A等级比例）
     */
    @Schema(description = "优秀率", example = "28.57")
    private BigDecimal excellentRate;

    /**
     * 良好率（B等级比例）
     */
    @Schema(description = "良好率", example = "42.86")
    private BigDecimal goodRate;
}

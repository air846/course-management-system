package com.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩响应DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "成绩响应")
public class GradeResponse {

    /**
     * 成绩ID
     */
    @Schema(description = "成绩ID", example = "1")
    private Long id;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "4")
    private Long studentId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    /**
     * 学生学号
     */
    @Schema(description = "学生学号", example = "student001")
    private String studentNumber;

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
     * 学分
     */
    @Schema(description = "学分", example = "3")
    private String credits;

    /**
     * 平时成绩
     */
    @Schema(description = "平时成绩", example = "85.0")
    private BigDecimal usualScore;

    /**
     * 期中成绩
     */
    @Schema(description = "期中成绩", example = "88.0")
    private BigDecimal midtermScore;

    /**
     * 期末成绩
     */
    @Schema(description = "期末成绩", example = "90.0")
    private BigDecimal finalScore;

    /**
     * 总成绩
     */
    @Schema(description = "总成绩", example = "88.0")
    private BigDecimal totalScore;

    /**
     * 等级：A+,A,A-,B+,B,B-,C+,C,C-,D,F
     */
    @Schema(description = "等级", example = "A-")
    private String gradeLevel;

    /**
     * 学期
     */
    @Schema(description = "学期", example = "2024春季")
    private String semester;

    /**
     * 是否及格
     */
    @Schema(description = "是否及格", example = "true")
    private Boolean isPass;

    /**
     * 排名
     */
    @Schema(description = "排名", example = "5")
    private Integer rank;

    /**
     * 班级总人数
     */
    @Schema(description = "班级总人数", example = "30")
    private Integer totalStudents;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间", example = "2024-02-20 09:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间", example = "2024-02-20 09:00:00")
    private LocalDateTime updateTime;
}

package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 成绩实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grades")
@Schema(description = "成绩信息")
public class Grade extends BaseEntity {

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @TableField("student_id")
    @Schema(description = "学生ID", example = "4")
    private Long studentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @TableField("course_id")
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 平时成绩
     */
    @DecimalMin(value = "0.00", message = "平时成绩不能小于0")
    @DecimalMax(value = "100.00", message = "平时成绩不能大于100")
    @TableField("usual_score")
    @Schema(description = "平时成绩", example = "85.0")
    private BigDecimal usualScore;

    /**
     * 期中成绩
     */
    @DecimalMin(value = "0.00", message = "期中成绩不能小于0")
    @DecimalMax(value = "100.00", message = "期中成绩不能大于100")
    @TableField("midterm_score")
    @Schema(description = "期中成绩", example = "88.0")
    private BigDecimal midtermScore;

    /**
     * 期末成绩
     */
    @DecimalMin(value = "0.00", message = "期末成绩不能小于0")
    @DecimalMax(value = "100.00", message = "期末成绩不能大于100")
    @TableField("final_score")
    @Schema(description = "期末成绩", example = "90.0")
    private BigDecimal finalScore;

    /**
     * 总成绩
     */
    @DecimalMin(value = "0.00", message = "总成绩不能小于0")
    @DecimalMax(value = "100.00", message = "总成绩不能大于100")
    @TableField("total_score")
    @Schema(description = "总成绩", example = "88.0")
    private BigDecimal totalScore;

    /**
     * 等级：A+,A,A-,B+,B,B-,C+,C,C-,D,F
     */
    @Size(max = 10, message = "等级长度不能超过10个字符")
    @TableField("grade_level")
    @Schema(description = "等级", example = "A-")
    private String gradeLevel;

    /**
     * 学期
     */
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @TableField("semester")
    @Schema(description = "学期", example = "2024春季")
    private String semester;

    /**
     * 学生姓名（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    /**
     * 学生学号（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学生学号")
    private String studentNumber;

    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 课程编码（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "课程编码")
    private String courseCode;

    /**
     * 学分（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学分")
    private String credits;
}

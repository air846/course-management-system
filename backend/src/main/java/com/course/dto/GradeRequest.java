package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 成绩录入请求DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "成绩录入请求")
public class GradeRequest {

    /**
     * 成绩ID（更新时需要）
     */
    @Schema(description = "成绩ID", example = "1")
    private Long id;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "4", required = true)
    private Long studentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", example = "1", required = true)
    private Long courseId;

    /**
     * 平时成绩
     */
    @DecimalMin(value = "0.00", message = "平时成绩不能小于0")
    @DecimalMax(value = "100.00", message = "平时成绩不能大于100")
    @Schema(description = "平时成绩", example = "85.0")
    private BigDecimal usualScore;

    /**
     * 期中成绩
     */
    @DecimalMin(value = "0.00", message = "期中成绩不能小于0")
    @DecimalMax(value = "100.00", message = "期中成绩不能大于100")
    @Schema(description = "期中成绩", example = "88.0")
    private BigDecimal midtermScore;

    /**
     * 期末成绩
     */
    @DecimalMin(value = "0.00", message = "期末成绩不能小于0")
    @DecimalMax(value = "100.00", message = "期末成绩不能大于100")
    @Schema(description = "期末成绩", example = "90.0")
    private BigDecimal finalScore;

    /**
     * 总成绩
     */
    @DecimalMin(value = "0.00", message = "总成绩不能小于0")
    @DecimalMax(value = "100.00", message = "总成绩不能大于100")
    @Schema(description = "总成绩", example = "88.0")
    private BigDecimal totalScore;

    /**
     * 等级：A+,A,A-,B+,B,B-,C+,C,C-,D,F
     */
    @Size(max = 10, message = "等级长度不能超过10个字符")
    @Schema(description = "等级", example = "A-")
    private String gradeLevel;

    /**
     * 学期
     */
    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Schema(description = "学期", example = "2024春季", required = true)
    private String semester;
}

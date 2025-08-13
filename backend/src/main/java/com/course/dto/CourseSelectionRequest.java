package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 选课请求DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "选课请求")
public class CourseSelectionRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", example = "1", required = true)
    private Long courseId;

    /**
     * 学生ID（可选，如果不传则使用当前登录用户）
     */
    @Schema(description = "学生ID", example = "4")
    private Long studentId;
}

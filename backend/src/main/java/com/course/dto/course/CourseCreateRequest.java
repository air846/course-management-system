package com.course.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 课程创建请求DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "课程创建请求")
public class CourseCreateRequest {

    @NotBlank(message = "课程编码不能为空")
    @Size(max = 20, message = "课程编码长度不能超过20个字符")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "课程编码只能包含大写字母和数字")
    @Schema(description = "课程编码", example = "CS101", required = true)
    private String courseCode;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @Schema(description = "课程名称", example = "计算机科学导论", required = true)
    private String courseName;

    @Size(max = 50, message = "课程分类长度不能超过50个字符")
    @Schema(description = "课程分类", example = "计算机科学")
    private String category;

    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.5", message = "学分不能小于0.5")
    @DecimalMax(value = "10.0", message = "学分不能大于10.0")
    @Schema(description = "学分", example = "3.0", required = true)
    private BigDecimal credits;

    @Min(value = 1, message = "课时不能小于1")
    @Max(value = 200, message = "课时不能大于200")
    @Schema(description = "课时", example = "48")
    private Integer hours;

    @Size(max = 500, message = "课程描述长度不能超过500个字符")
    @Schema(description = "课程描述", example = "这是一门计算机科学的入门课程")
    private String description;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID", example = "1", required = true)
    private Long teacherId;

    @NotNull(message = "最大学生数不能为空")
    @Min(value = 1, message = "最大学生数不能小于1")
    @Max(value = 500, message = "最大学生数不能大于500")
    @Schema(description = "最大学生数", example = "50", required = true)
    private Integer maxStudents;

    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Schema(description = "学期", example = "2024春季", required = true)
    private String semester;
}

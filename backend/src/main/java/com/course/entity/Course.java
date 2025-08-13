package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 课程实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("courses")
@Schema(description = "课程信息")
public class Course extends BaseEntity {

    /**
     * 课程编码
     */
    @NotBlank(message = "课程编码不能为空")
    @Size(max = 20, message = "课程编码长度不能超过20个字符")
    @TableField("course_code")
    @Schema(description = "课程编码", example = "CS001")
    private String courseCode;

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @TableField("course_name")
    @Schema(description = "课程名称", example = "Java程序设计")
    private String courseName;

    /**
     * 课程分类
     */
    @Size(max = 50, message = "课程分类长度不能超过50个字符")
    @TableField("category")
    @Schema(description = "课程分类", example = "计算机科学")
    private String category;

    /**
     * 学分
     */
    @DecimalMin(value = "0.0", message = "学分不能小于0")
    @DecimalMax(value = "10.0", message = "学分不能大于10")
    @TableField("credits")
    @Schema(description = "学分", example = "3.0")
    private BigDecimal credits;

    /**
     * 学时
     */
    @Min(value = 0, message = "学时不能小于0")
    @Max(value = 200, message = "学时不能大于200")
    @TableField("hours")
    @Schema(description = "学时", example = "48")
    private Integer hours;

    /**
     * 课程描述
     */
    @TableField("description")
    @Schema(description = "课程描述", example = "Java语言基础及面向对象编程")
    private String description;

    /**
     * 授课教师ID
     */
    @TableField("teacher_id")
    @Schema(description = "授课教师ID", example = "2")
    private Long teacherId;

    /**
     * 最大选课人数
     */
    @Min(value = 0, message = "最大选课人数不能小于0")
    @Max(value = 500, message = "最大选课人数不能大于500")
    @TableField("max_students")
    @Schema(description = "最大选课人数", example = "40")
    private Integer maxStudents;

    /**
     * 当前选课人数
     */
    @Min(value = 0, message = "当前选课人数不能小于0")
    @TableField("current_students")
    @Schema(description = "当前选课人数", example = "25")
    private Integer currentStudents;

    /**
     * 学期
     */
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @TableField("semester")
    @Schema(description = "学期", example = "2024春季")
    private String semester;

    /**
     * 状态：0-停课，1-正常
     */
    @TableField("status")
    @Schema(description = "状态：0-停课，1-正常", example = "1")
    private Integer status;

    /**
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 是否可以选课（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "是否可以选课")
    private Boolean canSelect;
}

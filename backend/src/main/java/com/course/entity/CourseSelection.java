package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 选课记录实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_selections")
@Schema(description = "选课记录信息")
public class CourseSelection extends BaseEntity {

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
     * 选课时间
     */
    @TableField("selection_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "选课时间", example = "2024-02-20 09:00:00")
    private LocalDateTime selectionTime;

    /**
     * 状态：0-已退课，1-已选课
     */
    @TableField("status")
    @Schema(description = "状态：0-已退课，1-已选课", example = "1")
    private Integer status;

    /**
     * 学生姓名（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

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
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 学分（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学分")
    private String credits;
}

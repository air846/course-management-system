package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalTime;

/**
 * 课表安排实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedules")
@Schema(description = "课表安排信息")
public class Schedule extends BaseEntity {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @TableField("course_id")
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 教室ID
     */
    @NotNull(message = "教室ID不能为空")
    @TableField("classroom_id")
    @Schema(description = "教室ID", example = "1")
    private Long classroomId;

    /**
     * 星期几：1-7表示周一到周日
     */
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    @TableField("day_of_week")
    @Schema(description = "星期几：1-7表示周一到周日", example = "1")
    private Integer dayOfWeek;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @TableField("start_time")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "开始时间", example = "08:00:00")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @TableField("end_time")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "结束时间", example = "09:40:00")
    private LocalTime endTime;

    /**
     * 开始周次
     */
    @Min(value = 1, message = "开始周次不能小于1")
    @Max(value = 30, message = "开始周次不能大于30")
    @TableField("week_start")
    @Schema(description = "开始周次", example = "1")
    private Integer weekStart;

    /**
     * 结束周次
     */
    @Min(value = 1, message = "结束周次不能小于1")
    @Max(value = 30, message = "结束周次不能大于30")
    @TableField("week_end")
    @Schema(description = "结束周次", example = "18")
    private Integer weekEnd;

    /**
     * 学期
     */
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @TableField("semester")
    @Schema(description = "学期", example = "2024春季")
    private String semester;

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
     * 教室名称（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教室名称")
    private String classroomName;

    /**
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教师姓名")
    private String teacherName;
}

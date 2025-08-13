package com.course.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程响应DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "课程响应")
public class CourseResponse {

    @Schema(description = "课程ID", example = "1")
    private Long id;

    @Schema(description = "课程编码", example = "CS101")
    private String courseCode;

    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    @Schema(description = "课程分类", example = "计算机科学")
    private String category;

    @Schema(description = "学分", example = "3.0")
    private BigDecimal credits;

    @Schema(description = "课时", example = "48")
    private Integer hours;

    @Schema(description = "课程描述", example = "这是一门计算机科学的入门课程")
    private String description;

    @Schema(description = "教师ID", example = "1")
    private Long teacherId;

    @Schema(description = "教师姓名", example = "张教授")
    private String teacherName;

    @Schema(description = "最大学生数", example = "50")
    private Integer maxStudents;

    @Schema(description = "当前学生数", example = "30")
    private Integer currentStudents;

    @Schema(description = "可选人数", example = "20")
    private Integer availableSpots;

    @Schema(description = "学期", example = "2024春季")
    private String semester;

    @Schema(description = "状态：0-停课，1-正常", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "正常")
    private String statusText;

    @Schema(description = "是否可选", example = "true")
    private Boolean canSelect;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取可选人数
     */
    public Integer getAvailableSpots() {
        if (maxStudents == null || currentStudents == null) {
            return 0;
        }
        return maxStudents - currentStudents;
    }

    /**
     * 获取状态描述
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "正常" : "停课";
    }

    /**
     * 判断是否可选
     */
    public Boolean getCanSelect() {
        if (status == null || status != 1) {
            return false;
        }
        return getAvailableSpots() > 0;
    }
}

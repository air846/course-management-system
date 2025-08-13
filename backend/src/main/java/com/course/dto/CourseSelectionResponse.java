package com.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选课响应DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "选课响应")
public class CourseSelectionResponse {

    /**
     * 选课记录ID
     */
    @Schema(description = "选课记录ID", example = "1")
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
     * 学生用户名
     */
    @Schema(description = "学生用户名", example = "student001")
    private String username;

    /**
     * 学生邮箱
     */
    @Schema(description = "学生邮箱", example = "student001@example.com")
    private String email;

    /**
     * 学生电话
     */
    @Schema(description = "学生电话", example = "13800138000")
    private String phone;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 课程编码
     */
    @Schema(description = "课程编码", example = "CS101")
    private String courseCode;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 课程分类
     */
    @Schema(description = "课程分类", example = "计算机科学")
    private String category;

    /**
     * 学分
     */
    @Schema(description = "学分", example = "3")
    private String credits;

    /**
     * 学期
     */
    @Schema(description = "学期", example = "2024春季")
    private String semester;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名", example = "李老师")
    private String teacherName;

    /**
     * 选课时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "选课时间", example = "2024-02-20 09:00:00")
    private LocalDateTime selectionTime;

    /**
     * 状态：0-已退课，1-已选课
     */
    @Schema(description = "状态：0-已退课，1-已选课", example = "1")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "已选课")
    private String statusText;

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

package com.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("announcements")
@Schema(description = "公告信息")
public class Announcement extends BaseEntity {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题长度不能超过200个字符")
    @TableField("title")
    @Schema(description = "公告标题", example = "关于期末考试安排的通知")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    @TableField("content")
    @Schema(description = "公告内容", example = "各位同学，期末考试将于下周开始...")
    private String content;

    /**
     * 公告类型：1-系统公告，2-课程公告，3-考试公告，4-活动公告
     */
    @NotNull(message = "公告类型不能为空")
    @TableField("type")
    @Schema(description = "公告类型：1-系统公告，2-课程公告，3-考试公告，4-活动公告", example = "1")
    private Integer type;

    /**
     * 优先级：1-低，2-中，3-高，4-紧急
     */
    @TableField("priority")
    @Schema(description = "优先级：1-低，2-中，3-高，4-紧急", example = "2")
    private Integer priority;

    /**
     * 发布者ID
     */
    @NotNull(message = "发布者ID不能为空")
    @TableField("publisher_id")
    @Schema(description = "发布者ID", example = "1")
    private Long publisherId;

    /**
     * 目标用户类型：1-全部用户，2-学生，3-教师，4-管理员
     */
    @TableField("target_type")
    @Schema(description = "目标用户类型：1-全部用户，2-学生，3-教师，4-管理员", example = "1")
    private Integer targetType;

    /**
     * 关联课程ID（课程公告时使用）
     */
    @TableField("course_id")
    @Schema(description = "关联课程ID", example = "1")
    private Long courseId;

    /**
     * 是否置顶：0-否，1-是
     */
    @TableField("is_top")
    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    /**
     * 状态：0-草稿，1-已发布，2-已撤回
     */
    @TableField("status")
    @Schema(description = "状态：0-草稿，1-已发布，2-已撤回", example = "1")
    private Integer status;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "发布时间", example = "2024-02-20 09:00:00")
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "过期时间", example = "2024-03-20 23:59:59")
    private LocalDateTime expireTime;

    /**
     * 阅读次数
     */
    @TableField("read_count")
    @Schema(description = "阅读次数", example = "100")
    private Integer readCount;

    /**
     * 附件URL
     */
    @Size(max = 500, message = "附件URL长度不能超过500个字符")
    @TableField("attachment_url")
    @Schema(description = "附件URL", example = "http://example.com/files/notice.pdf")
    private String attachmentUrl;

    /**
     * 附件名称
     */
    @Size(max = 100, message = "附件名称长度不能超过100个字符")
    @TableField("attachment_name")
    @Schema(description = "附件名称", example = "考试安排表.pdf")
    private String attachmentName;

    // 以下字段不存储在数据库中，用于关联查询时的数据展示

    /**
     * 发布者姓名
     */
    @TableField(exist = false)
    @Schema(description = "发布者姓名", example = "张老师")
    private String publisherName;

    /**
     * 课程名称
     */
    @TableField(exist = false)
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 公告类型描述
     */
    @TableField(exist = false)
    @Schema(description = "公告类型描述", example = "系统公告")
    private String typeText;

    /**
     * 优先级描述
     */
    @TableField(exist = false)
    @Schema(description = "优先级描述", example = "中等")
    private String priorityText;

    /**
     * 目标用户类型描述
     */
    @TableField(exist = false)
    @Schema(description = "目标用户类型描述", example = "全部用户")
    private String targetTypeText;

    /**
     * 状态描述
     */
    @TableField(exist = false)
    @Schema(description = "状态描述", example = "已发布")
    private String statusText;

    /**
     * 是否已读（当前用户）
     */
    @TableField(exist = false)
    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;

    /**
     * 是否过期
     */
    @TableField(exist = false)
    @Schema(description = "是否过期", example = "false")
    private Boolean isExpired;
}

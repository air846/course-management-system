package com.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告响应DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "公告响应")
public class AnnouncementResponse {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID", example = "1")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题", example = "关于期末考试安排的通知")
    private String title;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容", example = "各位同学，期末考试将于下周开始...")
    private String content;

    /**
     * 公告类型：1-系统公告，2-课程公告，3-考试公告，4-活动公告
     */
    @Schema(description = "公告类型", example = "1")
    private Integer type;

    /**
     * 公告类型描述
     */
    @Schema(description = "公告类型描述", example = "系统公告")
    private String typeText;

    /**
     * 优先级：1-低，2-中，3-高，4-紧急
     */
    @Schema(description = "优先级", example = "2")
    private Integer priority;

    /**
     * 优先级描述
     */
    @Schema(description = "优先级描述", example = "中等")
    private String priorityText;

    /**
     * 发布者ID
     */
    @Schema(description = "发布者ID", example = "1")
    private Long publisherId;

    /**
     * 发布者姓名
     */
    @Schema(description = "发布者姓名", example = "张老师")
    private String publisherName;

    /**
     * 目标用户类型：1-全部用户，2-学生，3-教师，4-管理员
     */
    @Schema(description = "目标用户类型", example = "1")
    private Integer targetType;

    /**
     * 目标用户类型描述
     */
    @Schema(description = "目标用户类型描述", example = "全部用户")
    private String targetTypeText;

    /**
     * 关联课程ID
     */
    @Schema(description = "关联课程ID", example = "1")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 是否置顶：0-否，1-是
     */
    @Schema(description = "是否置顶", example = "0")
    private Integer isTop;

    /**
     * 状态：0-草稿，1-已发布，2-已撤回
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "已发布")
    private String statusText;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "发布时间", example = "2024-02-20 09:00:00")
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "过期时间", example = "2024-03-20 23:59:59")
    private LocalDateTime expireTime;

    /**
     * 阅读次数
     */
    @Schema(description = "阅读次数", example = "100")
    private Integer readCount;

    /**
     * 附件URL
     */
    @Schema(description = "附件URL", example = "http://example.com/files/notice.pdf")
    private String attachmentUrl;

    /**
     * 附件名称
     */
    @Schema(description = "附件名称", example = "考试安排表.pdf")
    private String attachmentName;

    /**
     * 是否已读（当前用户）
     */
    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;

    /**
     * 是否过期
     */
    @Schema(description = "是否过期", example = "false")
    private Boolean isExpired;

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

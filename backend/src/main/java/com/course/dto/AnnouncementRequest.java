package com.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公告发布请求DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "公告发布请求")
public class AnnouncementRequest {

    /**
     * 公告ID（更新时需要）
     */
    @Schema(description = "公告ID", example = "1")
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题长度不能超过200个字符")
    @Schema(description = "公告标题", example = "关于期末考试安排的通知", required = true)
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容", example = "各位同学，期末考试将于下周开始...", required = true)
    private String content;

    /**
     * 公告类型：1-系统公告，2-课程公告，3-考试公告，4-活动公告
     */
    @NotNull(message = "公告类型不能为空")
    @Schema(description = "公告类型：1-系统公告，2-课程公告，3-考试公告，4-活动公告", example = "1", required = true)
    private Integer type;

    /**
     * 优先级：1-低，2-中，3-高，4-紧急
     */
    @Schema(description = "优先级：1-低，2-中，3-高，4-紧急", example = "2")
    private Integer priority;

    /**
     * 目标用户类型：1-全部用户，2-学生，3-教师，4-管理员
     */
    @Schema(description = "目标用户类型：1-全部用户，2-学生，3-教师，4-管理员", example = "1")
    private Integer targetType;

    /**
     * 关联课程ID（课程公告时使用）
     */
    @Schema(description = "关联课程ID", example = "1")
    private Long courseId;

    /**
     * 是否置顶：0-否，1-是
     */
    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    /**
     * 状态：0-草稿，1-已发布，2-已撤回
     */
    @Schema(description = "状态：0-草稿，1-已发布，2-已撤回", example = "1")
    private Integer status;

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
     * 附件URL
     */
    @Size(max = 500, message = "附件URL长度不能超过500个字符")
    @Schema(description = "附件URL", example = "http://example.com/files/notice.pdf")
    private String attachmentUrl;

    /**
     * 附件名称
     */
    @Size(max = 100, message = "附件名称长度不能超过100个字符")
    @Schema(description = "附件名称", example = "考试安排表.pdf")
    private String attachmentName;
}

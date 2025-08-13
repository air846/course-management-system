package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询请求类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "分页查询请求")
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createTime")
    private String sortField;

    /**
     * 排序方向：asc-升序，desc-降序
     */
    @Schema(description = "排序方向", example = "desc")
    private String sortOrder = "desc";

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词", example = "Java")
    private String keyword;
}

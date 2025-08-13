package com.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 趋势数据DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "趋势数据")
public class TrendData {

    /**
     * 数据类型：user-用户，course-课程，grade-成绩，announcement-公告
     */
    @Schema(description = "数据类型", example = "user")
    private String dataType;

    /**
     * 时间范围：day-日，week-周，month-月，year-年
     */
    @Schema(description = "时间范围", example = "month")
    private String timeRange;

    /**
     * 趋势数据点列表
     */
    @Schema(description = "趋势数据点列表")
    private List<TrendPoint> points;

    /**
     * 总体增长率
     */
    @Schema(description = "总体增长率", example = "15.5")
    private BigDecimal growthRate;

    /**
     * 趋势方向：up-上升，down-下降，stable-稳定
     */
    @Schema(description = "趋势方向", example = "up")
    private String trendDirection;

    /**
     * 趋势数据点内部类
     */
    @Data
    @Schema(description = "趋势数据点")
    public static class TrendPoint {
        /**
         * 日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "日期", example = "2024-02-20")
        private LocalDate date;

        /**
         * 数值
         */
        @Schema(description = "数值", example = "125")
        private Integer value;

        /**
         * 增长数量（相比上一期）
         */
        @Schema(description = "增长数量", example = "8")
        private Integer growth;

        /**
         * 增长率（相比上一期）
         */
        @Schema(description = "增长率", example = "6.8")
        private BigDecimal growthRate;

        /**
         * 标签（可选）
         */
        @Schema(description = "标签", example = "2月")
        private String label;
    }
}

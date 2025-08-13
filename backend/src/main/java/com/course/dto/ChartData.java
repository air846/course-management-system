package com.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 图表数据DTO
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "图表数据")
public class ChartData {

    /**
     * 图表标题
     */
    @Schema(description = "图表标题", example = "用户增长趋势")
    private String title;

    /**
     * 图表类型：line-折线图，bar-柱状图，pie-饼图，area-面积图
     */
    @Schema(description = "图表类型", example = "line")
    private String type;

    /**
     * X轴数据（标签）
     */
    @Schema(description = "X轴数据", example = "[\"1月\", \"2月\", \"3月\", \"4月\", \"5月\", \"6月\"]")
    private List<String> labels;

    /**
     * 数据系列
     */
    @Schema(description = "数据系列")
    private List<DataSeries> series;

    /**
     * 数据系列内部类
     */
    @Data
    @Schema(description = "数据系列")
    public static class DataSeries {
        /**
         * 系列名称
         */
        @Schema(description = "系列名称", example = "新增用户")
        private String name;

        /**
         * 数据值
         */
        @Schema(description = "数据值", example = "[10, 15, 20, 25, 30, 35]")
        private List<Object> data;

        /**
         * 系列类型（可选，用于混合图表）
         */
        @Schema(description = "系列类型", example = "line")
        private String type;

        /**
         * 系列颜色（可选）
         */
        @Schema(description = "系列颜色", example = "#409EFF")
        private String color;
    }
}

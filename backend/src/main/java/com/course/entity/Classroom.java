package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

/**
 * 教室实体类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classrooms")
@Schema(description = "教室信息")
public class Classroom extends BaseEntity {

    /**
     * 教室编号
     */
    @NotBlank(message = "教室编号不能为空")
    @Size(max = 20, message = "教室编号长度不能超过20个字符")
    @TableField("room_number")
    @Schema(description = "教室编号", example = "A101")
    private String roomNumber;

    /**
     * 教室名称
     */
    @NotBlank(message = "教室名称不能为空")
    @Size(max = 100, message = "教室名称长度不能超过100个字符")
    @TableField("room_name")
    @Schema(description = "教室名称", example = "多媒体教室A101")
    private String roomName;

    /**
     * 所在楼栋
     */
    @Size(max = 50, message = "楼栋名称长度不能超过50个字符")
    @TableField("building")
    @Schema(description = "所在楼栋", example = "A栋")
    private String building;

    /**
     * 楼层
     */
    @Min(value = 1, message = "楼层不能小于1")
    @Max(value = 50, message = "楼层不能大于50")
    @TableField("floor")
    @Schema(description = "楼层", example = "1")
    private Integer floor;

    /**
     * 容纳人数
     */
    @Min(value = 0, message = "容纳人数不能小于0")
    @Max(value = 1000, message = "容纳人数不能大于1000")
    @TableField("capacity")
    @Schema(description = "容纳人数", example = "60")
    private Integer capacity;

    /**
     * 设备信息
     */
    @TableField("equipment")
    @Schema(description = "设备信息", example = "投影仪,音响,空调")
    private String equipment;

    /**
     * 状态：0-维修中，1-可用
     */
    @TableField("status")
    @Schema(description = "状态：0-维修中，1-可用", example = "1")
    private Integer status;
}

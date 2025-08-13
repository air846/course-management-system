package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教室数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {

    /**
     * 根据教室编号查询教室
     * 
     * @param roomNumber 教室编号
     * @return 教室信息
     */
    @Select("SELECT * FROM classrooms WHERE room_number = #{roomNumber} AND deleted = 0")
    Classroom findByRoomNumber(@Param("roomNumber") String roomNumber);

    /**
     * 分页查询教室列表
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param building 楼栋
     * @param floor 楼层
     * @param status 状态
     * @return 教室分页列表
     */
    IPage<Classroom> selectClassroomPage(Page<Classroom> page,
                                        @Param("keyword") String keyword,
                                        @Param("building") String building,
                                        @Param("floor") Integer floor,
                                        @Param("status") Integer status);

    /**
     * 根据楼栋查询教室列表
     * 
     * @param building 楼栋
     * @return 教室列表
     */
    @Select("SELECT * FROM classrooms " +
            "WHERE building = #{building} AND deleted = 0 AND status = 1 " +
            "ORDER BY floor, room_number")
    List<Classroom> findByBuilding(@Param("building") String building);

    /**
     * 查询所有可用教室
     * 
     * @return 教室列表
     */
    @Select("SELECT * FROM classrooms " +
            "WHERE status = 1 AND deleted = 0 " +
            "ORDER BY building, floor, room_number")
    List<Classroom> findAllAvailable();

    /**
     * 查询指定时间段可用的教室
     * 
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @return 可用教室列表
     */
    @Select("SELECT c.* FROM classrooms c " +
            "WHERE c.status = 1 AND c.deleted = 0 " +
            "AND c.id NOT IN (" +
            "    SELECT DISTINCT s.classroom_id FROM schedules s " +
            "    INNER JOIN courses co ON s.course_id = co.id " +
            "    WHERE s.day_of_week = #{dayOfWeek} " +
            "    AND co.semester = #{semester} " +
            "    AND s.deleted = 0 AND co.deleted = 0 " +
            "    AND ((s.start_time <= #{startTime} AND s.end_time > #{startTime}) " +
            "    OR (s.start_time < #{endTime} AND s.end_time >= #{endTime}) " +
            "    OR (s.start_time >= #{startTime} AND s.end_time <= #{endTime}))" +
            ") " +
            "ORDER BY c.building, c.floor, c.room_number")
    List<Classroom> findAvailableAtTime(@Param("dayOfWeek") Integer dayOfWeek,
                                       @Param("startTime") String startTime,
                                       @Param("endTime") String endTime,
                                       @Param("semester") String semester);

    /**
     * 获取所有楼栋列表
     * 
     * @return 楼栋列表
     */
    @Select("SELECT DISTINCT building FROM classrooms " +
            "WHERE building IS NOT NULL AND building != '' AND deleted = 0 " +
            "ORDER BY building")
    List<String> getBuildingList();

    /**
     * 根据楼栋获取楼层列表
     * 
     * @param building 楼栋
     * @return 楼层列表
     */
    @Select("SELECT DISTINCT floor FROM classrooms " +
            "WHERE building = #{building} AND floor IS NOT NULL AND deleted = 0 " +
            "ORDER BY floor")
    List<Integer> getFloorListByBuilding(@Param("building") String building);

    /**
     * 统计教室使用情况
     * 
     * @param classroomId 教室ID
     * @param semester 学期
     * @return 使用次数
     */
    @Select("SELECT COUNT(*) FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "WHERE s.classroom_id = #{classroomId} " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 AND c.deleted = 0")
    Integer getUsageCount(@Param("classroomId") Long classroomId,
                         @Param("semester") String semester);
}

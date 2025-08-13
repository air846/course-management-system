package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课表安排数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 查询学生的课表（根据选课记录）
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 课表列表
     */
    @Select("SELECT s.*, " +
            "c.course_code, c.course_name, " +
            "cl.room_number as classroom_name, " +
            "u.real_name as teacher_name " +
            "FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "INNER JOIN course_selections cs ON c.id = cs.course_id " +
            "LEFT JOIN classrooms cl ON s.classroom_id = cl.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE cs.student_id = #{studentId} " +
            "AND cs.status = 1 AND cs.deleted = 0 " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 " +
            "ORDER BY s.day_of_week, s.start_time")
    List<Schedule> findStudentSchedule(@Param("studentId") Long studentId,
                                      @Param("semester") String semester);

    /**
     * 查询教师的课表
     * 
     * @param teacherId 教师ID
     * @param semester 学期
     * @return 课表列表
     */
    @Select("SELECT s.*, " +
            "c.course_code, c.course_name, " +
            "cl.room_number as classroom_name " +
            "FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "LEFT JOIN classrooms cl ON s.classroom_id = cl.id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 AND c.deleted = 0 " +
            "ORDER BY s.day_of_week, s.start_time")
    List<Schedule> findTeacherSchedule(@Param("teacherId") Long teacherId,
                                      @Param("semester") String semester);

    /**
     * 查询教室的课表
     * 
     * @param classroomId 教室ID
     * @param semester 学期
     * @return 课表列表
     */
    @Select("SELECT s.*, " +
            "c.course_code, c.course_name, " +
            "u.real_name as teacher_name " +
            "FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE s.classroom_id = #{classroomId} " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 AND c.deleted = 0 " +
            "ORDER BY s.day_of_week, s.start_time")
    List<Schedule> findClassroomSchedule(@Param("classroomId") Long classroomId,
                                        @Param("semester") String semester);

    /**
     * 分页查询课表安排（包含课程、教室、教师信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param courseId 课程ID
     * @param classroomId 教室ID
     * @param teacherId 教师ID
     * @param semester 学期
     * @param dayOfWeek 星期几
     * @return 课表分页列表
     */
    IPage<Schedule> selectSchedulePageWithDetails(Page<Schedule> page,
                                                 @Param("keyword") String keyword,
                                                 @Param("courseId") Long courseId,
                                                 @Param("classroomId") Long classroomId,
                                                 @Param("teacherId") Long teacherId,
                                                 @Param("semester") String semester,
                                                 @Param("dayOfWeek") Integer dayOfWeek);

    /**
     * 检查教室时间冲突
     * 
     * @param classroomId 教室ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课表ID（用于更新时排除自己）
     * @return 冲突的课表数量
     */
    @Select("SELECT COUNT(*) FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "WHERE s.classroom_id = #{classroomId} " +
            "AND s.day_of_week = #{dayOfWeek} " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 AND c.deleted = 0 " +
            "AND (#{excludeId} IS NULL OR s.id != #{excludeId}) " +
            "AND ((s.start_time <= #{startTime} AND s.end_time > #{startTime}) " +
            "OR (s.start_time < #{endTime} AND s.end_time >= #{endTime}) " +
            "OR (s.start_time >= #{startTime} AND s.end_time <= #{endTime}))")
    Integer checkClassroomConflict(@Param("classroomId") Long classroomId,
                                  @Param("dayOfWeek") Integer dayOfWeek,
                                  @Param("startTime") String startTime,
                                  @Param("endTime") String endTime,
                                  @Param("semester") String semester,
                                  @Param("excludeId") Long excludeId);

    /**
     * 检查教师时间冲突
     * 
     * @param teacherId 教师ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课表ID
     * @return 冲突的课表数量
     */
    @Select("SELECT COUNT(*) FROM schedules s " +
            "INNER JOIN courses c ON s.course_id = c.id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "AND s.day_of_week = #{dayOfWeek} " +
            "AND c.semester = #{semester} " +
            "AND s.deleted = 0 AND c.deleted = 0 " +
            "AND (#{excludeId} IS NULL OR s.id != #{excludeId}) " +
            "AND ((s.start_time <= #{startTime} AND s.end_time > #{startTime}) " +
            "OR (s.start_time < #{endTime} AND s.end_time >= #{endTime}) " +
            "OR (s.start_time >= #{startTime} AND s.end_time <= #{endTime}))")
    Integer checkTeacherConflict(@Param("teacherId") Long teacherId,
                                @Param("dayOfWeek") Integer dayOfWeek,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("semester") String semester,
                                @Param("excludeId") Long excludeId);

    /**
     * 根据课程ID查询课表安排
     * 
     * @param courseId 课程ID
     * @return 课表列表
     */
    @Select("SELECT s.*, cl.room_number as classroom_name " +
            "FROM schedules s " +
            "LEFT JOIN classrooms cl ON s.classroom_id = cl.id " +
            "WHERE s.course_id = #{courseId} AND s.deleted = 0 " +
            "ORDER BY s.day_of_week, s.start_time")
    List<Schedule> findByCourseId(@Param("courseId") Long courseId);
}

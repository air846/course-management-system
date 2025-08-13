package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 选课记录数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {

    /**
     * 查询学生的选课记录（包含课程和教师信息）
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @param status 选课状态
     * @return 选课记录列表
     */
    @Select("SELECT cs.*, " +
            "c.course_code, c.course_name, c.credits, c.semester, " +
            "u.real_name as teacher_name " +
            "FROM course_selections cs " +
            "INNER JOIN courses c ON cs.course_id = c.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE cs.student_id = #{studentId} " +
            "AND (#{semester} IS NULL OR c.semester = #{semester}) " +
            "AND (#{status} IS NULL OR cs.status = #{status}) " +
            "AND cs.deleted = 0 " +
            "ORDER BY cs.selection_time DESC")
    List<CourseSelection> findByStudentId(@Param("studentId") Long studentId,
                                         @Param("semester") String semester,
                                         @Param("status") Integer status);

    /**
     * 查询课程的选课学生列表
     * 
     * @param courseId 课程ID
     * @param status 选课状态
     * @return 选课记录列表
     */
    @Select("SELECT cs.*, " +
            "u.username, u.real_name as student_name, u.email, u.phone " +
            "FROM course_selections cs " +
            "INNER JOIN users u ON cs.student_id = u.id " +
            "WHERE cs.course_id = #{courseId} " +
            "AND (#{status} IS NULL OR cs.status = #{status}) " +
            "AND cs.deleted = 0 " +
            "ORDER BY cs.selection_time")
    List<CourseSelection> findByCourseId(@Param("courseId") Long courseId,
                                        @Param("status") Integer status);

    /**
     * 分页查询选课记录（包含学生和课程信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param courseId 课程ID
     * @param semester 学期
     * @param status 选课状态
     * @return 选课记录分页列表
     */
    IPage<CourseSelection> selectSelectionPageWithDetails(Page<CourseSelection> page,
                                                         @Param("keyword") String keyword,
                                                         @Param("courseId") Long courseId,
                                                         @Param("semester") String semester,
                                                         @Param("status") Integer status);

    /**
     * 检查学生是否已选择某课程
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录
     */
    @Select("SELECT * FROM course_selections " +
            "WHERE student_id = #{studentId} AND course_id = #{courseId} " +
            "AND deleted = 0")
    CourseSelection findByStudentAndCourse(@Param("studentId") Long studentId,
                                          @Param("courseId") Long courseId);

    /**
     * 统计学生在某学期的选课数量
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @param status 选课状态
     * @return 选课数量
     */
    @Select("SELECT COUNT(*) FROM course_selections cs " +
            "INNER JOIN courses c ON cs.course_id = c.id " +
            "WHERE cs.student_id = #{studentId} " +
            "AND c.semester = #{semester} " +
            "AND cs.status = #{status} " +
            "AND cs.deleted = 0")
    Integer countByStudentAndSemester(@Param("studentId") Long studentId,
                                     @Param("semester") String semester,
                                     @Param("status") Integer status);

    /**
     * 统计课程的选课人数
     * 
     * @param courseId 课程ID
     * @param status 选课状态
     * @return 选课人数
     */
    @Select("SELECT COUNT(*) FROM course_selections " +
            "WHERE course_id = #{courseId} AND status = #{status} AND deleted = 0")
    Integer countByCourseAndStatus(@Param("courseId") Long courseId,
                                  @Param("status") Integer status);

    /**
     * 统计学生选课数量
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @param status 选课状态
     * @return 选课数量
     */
    @Select("SELECT COUNT(*) FROM course_selections cs " +
            "INNER JOIN courses c ON cs.course_id = c.id " +
            "WHERE cs.student_id = #{studentId} " +
            "AND (#{semester} IS NULL OR c.semester = #{semester}) " +
            "AND (#{status} IS NULL OR cs.status = #{status}) " +
            "AND cs.deleted = 0")
    Integer countByStudentId(@Param("studentId") Long studentId,
                            @Param("semester") String semester,
                            @Param("status") Integer status);

    /**
     * 统计课程选课数量
     *
     * @param courseId 课程ID
     * @param status 选课状态
     * @return 选课数量
     */
    @Select("SELECT COUNT(*) FROM course_selections " +
            "WHERE course_id = #{courseId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND deleted = 0")
    Integer countByCourseId(@Param("courseId") Long courseId,
                           @Param("status") Integer status);

    /**
     * 查询学生在指定时间段是否有课程冲突
     *
     * @param studentId 学生ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @return 冲突的选课记录数量
     */
    @Select("SELECT COUNT(*) FROM course_selections cs " +
            "INNER JOIN courses c ON cs.course_id = c.id " +
            "INNER JOIN schedules s ON c.id = s.course_id " +
            "WHERE cs.student_id = #{studentId} " +
            "AND cs.status = 1 AND cs.deleted = 0 " +
            "AND c.semester = #{semester} " +
            "AND s.day_of_week = #{dayOfWeek} " +
            "AND s.deleted = 0 " +
            "AND ((s.start_time <= #{startTime} AND s.end_time > #{startTime}) " +
            "OR (s.start_time < #{endTime} AND s.end_time >= #{endTime}) " +
            "OR (s.start_time >= #{startTime} AND s.end_time <= #{endTime}))")
    Integer checkTimeConflict(@Param("studentId") Long studentId,
                             @Param("dayOfWeek") Integer dayOfWeek,
                             @Param("startTime") String startTime,
                             @Param("endTime") String endTime,
                             @Param("semester") String semester);
}

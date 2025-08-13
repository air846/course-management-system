package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 课程数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据课程编码查询课程
     * 
     * @param courseCode 课程编码
     * @return 课程信息
     */
    @Select("SELECT c.*, u.real_name as teacher_name " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.course_code = #{courseCode} AND c.deleted = 0")
    Course findByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 分页查询课程列表（包含教师信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param category 课程分类
     * @param teacherId 教师ID
     * @param semester 学期
     * @param status 课程状态
     * @return 课程分页列表
     */
    IPage<Course> selectCoursePageWithTeacher(Page<Course> page,
                                             @Param("keyword") String keyword,
                                             @Param("category") String category,
                                             @Param("teacherId") Long teacherId,
                                             @Param("semester") String semester,
                                             @Param("status") Integer status);

    /**
     * 根据教师ID查询课程列表
     * 
     * @param teacherId 教师ID
     * @param semester 学期
     * @return 课程列表
     */
    @Select("SELECT c.*, u.real_name as teacher_name " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "AND (#{semester} IS NULL OR c.semester = #{semester}) " +
            "AND c.deleted = 0 AND c.status = 1 " +
            "ORDER BY c.create_time DESC")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId, 
                                @Param("semester") String semester);

    /**
     * 根据学期查询课程列表
     * 
     * @param semester 学期
     * @return 课程列表
     */
    @Select("SELECT c.*, u.real_name as teacher_name " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.semester = #{semester} AND c.deleted = 0 AND c.status = 1 " +
            "ORDER BY c.course_code")
    List<Course> findBySemester(@Param("semester") String semester);

    /**
     * 查询可选课程列表（当前选课人数小于最大人数）
     * 
     * @param semester 学期
     * @return 可选课程列表
     */
    @Select("SELECT c.*, u.real_name as teacher_name, " +
            "(c.max_students - c.current_students) as available_spots " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.semester = #{semester} AND c.deleted = 0 AND c.status = 1 " +
            "AND c.current_students < c.max_students " +
            "ORDER BY c.course_code")
    List<Course> findAvailableCourses(@Param("semester") String semester);

    /**
     * 更新课程的当前选课人数
     * 
     * @param courseId 课程ID
     * @param increment 增量（正数表示增加，负数表示减少）
     * @return 影响行数
     */
    @Update("UPDATE courses SET current_students = current_students + #{increment} " +
            "WHERE id = #{courseId} AND deleted = 0")
    int updateCurrentStudents(@Param("courseId") Long courseId, 
                             @Param("increment") Integer increment);

    /**
     * 获取课程分类列表
     * 
     * @return 分类列表
     */
    @Select("SELECT DISTINCT category FROM courses " +
            "WHERE category IS NOT NULL AND category != '' AND deleted = 0 " +
            "ORDER BY category")
    List<String> getCategoryList();

    /**
     * 统计教师的课程数量
     * 
     * @param teacherId 教师ID
     * @param semester 学期
     * @return 课程数量
     */
    @Select("SELECT COUNT(*) FROM courses " +
            "WHERE teacher_id = #{teacherId} " +
            "AND (#{semester} IS NULL OR semester = #{semester}) " +
            "AND deleted = 0")
    Integer countByTeacherId(@Param("teacherId") Long teacherId, 
                            @Param("semester") String semester);
}

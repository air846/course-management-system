package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /**
     * 查询学生的成绩列表
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Select("SELECT g.*, " +
            "c.course_code, c.course_name, c.credits, " +
            "u.real_name as teacher_name " +
            "FROM grades g " +
            "INNER JOIN courses c ON g.course_id = c.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE g.student_id = #{studentId} " +
            "AND (#{semester} IS NULL OR g.semester = #{semester}) " +
            "AND g.deleted = 0 " +
            "ORDER BY g.semester DESC, c.course_code")
    List<Grade> findByStudentId(@Param("studentId") Long studentId,
                               @Param("semester") String semester);

    /**
     * 查询课程的成绩列表
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Select("SELECT g.*, " +
            "u.username as student_number, u.real_name as student_name " +
            "FROM grades g " +
            "INNER JOIN users u ON g.student_id = u.id " +
            "WHERE g.course_id = #{courseId} " +
            "AND g.semester = #{semester} " +
            "AND g.deleted = 0 " +
            "ORDER BY u.username")
    List<Grade> findByCourseId(@Param("courseId") Long courseId,
                              @Param("semester") String semester);

    /**
     * 分页查询成绩列表（包含学生和课程信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param semester 学期
     * @param gradeLevel 成绩等级
     * @return 成绩分页列表
     */
    IPage<Grade> selectGradePageWithDetails(Page<Grade> page,
                                           @Param("keyword") String keyword,
                                           @Param("courseId") Long courseId,
                                           @Param("studentId") Long studentId,
                                           @Param("semester") String semester,
                                           @Param("gradeLevel") String gradeLevel);

    /**
     * 查询学生和课程的成绩记录
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩记录
     */
    @Select("SELECT * FROM grades " +
            "WHERE student_id = #{studentId} AND course_id = #{courseId} " +
            "AND semester = #{semester} AND deleted = 0")
    Grade findByStudentAndCourse(@Param("studentId") Long studentId,
                                @Param("courseId") Long courseId,
                                @Param("semester") String semester);

    /**
     * 计算学生的平均成绩
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均成绩
     */
    @Select("SELECT AVG(total_score) FROM grades " +
            "WHERE student_id = #{studentId} " +
            "AND (#{semester} IS NULL OR semester = #{semester}) " +
            "AND total_score IS NOT NULL AND deleted = 0")
    BigDecimal calculateAverageScore(@Param("studentId") Long studentId,
                                    @Param("semester") String semester);

    /**
     * 计算学生的总学分
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总学分
     */
    @Select("SELECT SUM(c.credits) FROM grades g " +
            "INNER JOIN courses c ON g.course_id = c.id " +
            "WHERE g.student_id = #{studentId} " +
            "AND (#{semester} IS NULL OR g.semester = #{semester}) " +
            "AND g.total_score IS NOT NULL AND g.deleted = 0")
    BigDecimal calculateTotalCredits(@Param("studentId") Long studentId,
                                    @Param("semester") String semester);

    /**
     * 统计课程成绩分布
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩分布统计
     */
    @Select("SELECT " +
            "grade_level, " +
            "COUNT(*) as count, " +
            "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM grades WHERE course_id = #{courseId} AND semester = #{semester} AND deleted = 0), 2) as percentage " +
            "FROM grades " +
            "WHERE course_id = #{courseId} AND semester = #{semester} " +
            "AND grade_level IS NOT NULL AND deleted = 0 " +
            "GROUP BY grade_level " +
            "ORDER BY grade_level")
    List<Map<String, Object>> getGradeDistribution(@Param("courseId") Long courseId,
                                                   @Param("semester") String semester);

    /**
     * 查询课程的成绩统计信息
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "AVG(total_score) as avg_score, " +
            "MAX(total_score) as max_score, " +
            "MIN(total_score) as min_score, " +
            "COUNT(CASE WHEN total_score >= 90 THEN 1 END) as excellent_count, " +
            "COUNT(CASE WHEN total_score >= 80 AND total_score < 90 THEN 1 END) as good_count, " +
            "COUNT(CASE WHEN total_score >= 70 AND total_score < 80 THEN 1 END) as medium_count, " +
            "COUNT(CASE WHEN total_score >= 60 AND total_score < 70 THEN 1 END) as pass_count, " +
            "COUNT(CASE WHEN total_score < 60 THEN 1 END) as fail_count " +
            "FROM grades " +
            "WHERE course_id = #{courseId} AND semester = #{semester} " +
            "AND total_score IS NOT NULL AND deleted = 0")
    Map<String, Object> getCourseGradeStatistics(@Param("courseId") Long courseId,
                                                 @Param("semester") String semester);

    /**
     * 查询学生的学期排名
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 排名信息
     */
    @Select("SELECT " +
            "student_rank, " +
            "total_students, " +
            "avg_score " +
            "FROM (" +
            "    SELECT " +
            "        student_id, " +
            "        ROW_NUMBER() OVER (ORDER BY AVG(total_score) DESC) as student_rank, " +
            "        COUNT(*) OVER () as total_students, " +
            "        AVG(total_score) as avg_score " +
            "    FROM grades " +
            "    WHERE semester = #{semester} AND total_score IS NOT NULL AND deleted = 0 " +
            "    GROUP BY student_id" +
            ") ranked " +
            "WHERE student_id = #{studentId}")
    Map<String, Object> getStudentRanking(@Param("studentId") Long studentId,
                                         @Param("semester") String semester);
}

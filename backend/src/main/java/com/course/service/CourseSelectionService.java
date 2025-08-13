package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.entity.CourseSelection;

import java.util.List;

/**
 * 选课服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface CourseSelectionService {

    /**
     * 学生选课
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录
     */
    CourseSelection selectCourse(Long studentId, Long courseId);

    /**
     * 学生退课
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否退课成功
     */
    boolean dropCourse(Long studentId, Long courseId);

    /**
     * 查询学生的选课记录
     * 
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @param status 选课状态（可选）
     * @return 选课记录列表
     */
    List<CourseSelection> getStudentSelections(Long studentId, String semester, Integer status);

    /**
     * 查询课程的选课学生列表
     * 
     * @param courseId 课程ID
     * @param status 选课状态（可选）
     * @return 选课记录列表
     */
    List<CourseSelection> getCourseSelections(Long courseId, Integer status);

    /**
     * 分页查询选课记录
     * 
     * @param pageRequest 分页请求参数
     * @param courseId 课程ID（可选）
     * @param semester 学期（可选）
     * @param status 选课状态（可选）
     * @return 选课记录分页列表
     */
    IPage<CourseSelection> getSelectionPage(PageRequest pageRequest, Long courseId, String semester, Integer status);

    /**
     * 检查学生是否已选择某课程
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录（如果存在）
     */
    CourseSelection getSelectionByStudentAndCourse(Long studentId, Long courseId);

    /**
     * 检查学生是否可以选择某课程
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否可以选课
     */
    boolean canSelectCourse(Long studentId, Long courseId);

    /**
     * 检查时间冲突
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @return 是否有时间冲突
     */
    boolean hasTimeConflict(Long studentId, Long courseId, String semester);

    /**
     * 统计学生选课数量
     * 
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @param status 选课状态（可选）
     * @return 选课数量
     */
    Integer countStudentSelections(Long studentId, String semester, Integer status);

    /**
     * 统计课程选课数量
     * 
     * @param courseId 课程ID
     * @param status 选课状态（可选）
     * @return 选课数量
     */
    Integer countCourseSelections(Long courseId, Integer status);

    /**
     * 批量退课（按学期）
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 退课成功的数量
     */
    int batchDropCoursesBySemester(Long studentId, String semester);

    /**
     * 获取学生的课程表
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 选课记录列表（包含课程安排信息）
     */
    List<CourseSelection> getStudentSchedule(Long studentId, String semester);
}

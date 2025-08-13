package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface CourseService {

    /**
     * 创建课程
     * 
     * @param course 课程信息
     * @return 创建的课程
     */
    Course createCourse(Course course);

    /**
     * 根据ID获取课程
     * 
     * @param id 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long id);

    /**
     * 根据课程编码获取课程
     * 
     * @param courseCode 课程编码
     * @return 课程信息
     */
    Course getCourseByCourseCode(String courseCode);

    /**
     * 更新课程信息
     * 
     * @param course 课程信息
     * @return 更新后的课程
     */
    Course updateCourse(Course course);

    /**
     * 删除课程（逻辑删除）
     * 
     * @param id 课程ID
     * @return 是否删除成功
     */
    boolean deleteCourse(Long id);

    /**
     * 分页查询课程列表
     * 
     * @param pageRequest 分页请求参数
     * @param category 课程分类（可选）
     * @param teacherId 教师ID（可选）
     * @param semester 学期（可选）
     * @param status 课程状态（可选）
     * @return 课程分页列表
     */
    IPage<Course> getCoursePage(PageRequest pageRequest, String category, Long teacherId, 
                               String semester, Integer status);

    /**
     * 根据教师ID查询课程列表
     * 
     * @param teacherId 教师ID
     * @param semester 学期（可选）
     * @return 课程列表
     */
    List<Course> getCoursesByTeacher(Long teacherId, String semester);

    /**
     * 根据学期查询课程列表
     * 
     * @param semester 学期
     * @return 课程列表
     */
    List<Course> getCoursesBySemester(String semester);

    /**
     * 查询可选课程列表
     * 
     * @param semester 学期
     * @return 可选课程列表
     */
    List<Course> getAvailableCourses(String semester);

    /**
     * 检查课程编码是否存在
     * 
     * @param courseCode 课程编码
     * @param excludeId 排除的课程ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isCourseCodeExists(String courseCode, Long excludeId);

    /**
     * 更新课程的当前选课人数
     * 
     * @param courseId 课程ID
     * @param increment 增量（正数表示增加，负数表示减少）
     * @return 是否更新成功
     */
    boolean updateCurrentStudents(Long courseId, Integer increment);

    /**
     * 检查课程是否可以选课
     * 
     * @param courseId 课程ID
     * @return 是否可以选课
     */
    boolean canSelectCourse(Long courseId);

    /**
     * 获取课程分类列表
     * 
     * @return 分类列表
     */
    List<String> getCategoryList();

    /**
     * 统计教师的课程数量
     * 
     * @param teacherId 教师ID
     * @param semester 学期（可选）
     * @return 课程数量
     */
    Integer countCoursesByTeacher(Long teacherId, String semester);

    /**
     * 启用/禁用课程
     * 
     * @param id 课程ID
     * @param status 状态（0-停课，1-正常）
     * @return 是否操作成功
     */
    boolean updateCourseStatus(Long id, Integer status);

    /**
     * 批量导入课程
     * 
     * @param courses 课程列表
     * @return 导入成功的数量
     */
    int batchImportCourses(List<Course> courses);

    /**
     * 复制课程到新学期
     * 
     * @param courseId 原课程ID
     * @param newSemester 新学期
     * @return 新课程
     */
    Course copyCourseToNewSemester(Long courseId, String newSemester);
}

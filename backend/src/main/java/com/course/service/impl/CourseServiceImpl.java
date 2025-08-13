package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.PageRequest;
import com.course.entity.Course;
import com.course.mapper.CourseMapper;
import com.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Course createCourse(Course course) {
        log.info("创建课程: {}", course.getCourseName());
        
        // 验证课程编码唯一性
        if (isCourseCodeExists(course.getCourseCode(), null)) {
            throw new IllegalArgumentException("课程编码已存在");
        }
        
        // 设置默认值
        if (course.getStatus() == null) {
            course.setStatus(1); // 默认正常
        }
        if (course.getCurrentStudents() == null) {
            course.setCurrentStudents(0); // 默认0人选课
        }
        if (course.getMaxStudents() == null) {
            course.setMaxStudents(50); // 默认最大50人
        }
        
        courseMapper.insert(course);
        log.info("课程创建成功，ID: {}", course.getId());
        
        return course;
    }

    @Override
    public Course getCourseById(Long id) {
        if (id == null) {
            return null;
        }
        
        return courseMapper.selectById(id);
    }

    @Override
    public Course getCourseByCourseCode(String courseCode) {
        if (!StringUtils.hasText(courseCode)) {
            return null;
        }
        
        return courseMapper.findByCourseCode(courseCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Course updateCourse(Course course) {
        if (course.getId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        log.info("更新课程信息: {}", course.getId());
        
        // 验证课程是否存在
        Course existingCourse = courseMapper.selectById(course.getId());
        if (existingCourse == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // 验证课程编码唯一性
        if (StringUtils.hasText(course.getCourseCode()) && 
            isCourseCodeExists(course.getCourseCode(), course.getId())) {
            throw new IllegalArgumentException("课程编码已存在");
        }
        
        courseMapper.updateById(course);
        log.info("课程信息更新成功: {}", course.getId());
        
        return getCourseById(course.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCourse(Long id) {
        if (id == null) {
            return false;
        }
        
        log.info("删除课程: {}", id);
        
        // 验证课程是否存在
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // TODO: 检查课程是否有学生选课，如果有则不允许删除
        
        // 逻辑删除
        int result = courseMapper.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("课程删除成功: {}", id);
        } else {
            log.warn("课程删除失败: {}", id);
        }
        
        return success;
    }

    @Override
    public IPage<Course> getCoursePage(PageRequest pageRequest, String category, Long teacherId, 
                                      String semester, Integer status) {
        Page<Course> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        return courseMapper.selectCoursePageWithTeacher(page, pageRequest.getKeyword(), 
                                                       category, teacherId, semester, status);
    }

    @Override
    public List<Course> getCoursesByTeacher(Long teacherId, String semester) {
        if (teacherId == null) {
            return List.of();
        }
        
        return courseMapper.findByTeacherId(teacherId, semester);
    }

    @Override
    public List<Course> getCoursesBySemester(String semester) {
        if (!StringUtils.hasText(semester)) {
            return List.of();
        }
        
        return courseMapper.findBySemester(semester);
    }

    @Override
    public List<Course> getAvailableCourses(String semester) {
        if (!StringUtils.hasText(semester)) {
            return List.of();
        }
        
        return courseMapper.findAvailableCourses(semester);
    }

    @Override
    public boolean isCourseCodeExists(String courseCode, Long excludeId) {
        if (!StringUtils.hasText(courseCode)) {
            return false;
        }
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, courseCode);
        if (excludeId != null) {
            wrapper.ne(Course::getId, excludeId);
        }
        
        return courseMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCurrentStudents(Long courseId, Integer increment) {
        if (courseId == null || increment == null) {
            return false;
        }
        
        log.info("更新课程选课人数: {} -> {}", courseId, increment);
        
        // 验证课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // 检查人数限制
        if (increment > 0) {
            int newCount = course.getCurrentStudents() + increment;
            if (newCount > course.getMaxStudents()) {
                throw new IllegalArgumentException("选课人数超过限制");
            }
        } else {
            int newCount = course.getCurrentStudents() + increment;
            if (newCount < 0) {
                throw new IllegalArgumentException("选课人数不能为负数");
            }
        }
        
        int result = courseMapper.updateCurrentStudents(courseId, increment);
        boolean success = result > 0;
        
        if (success) {
            log.info("课程选课人数更新成功: {}", courseId);
        }
        
        return success;
    }

    @Override
    public boolean canSelectCourse(Long courseId) {
        if (courseId == null) {
            return false;
        }
        
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getStatus() != 1) {
            return false;
        }
        
        return course.getCurrentStudents() < course.getMaxStudents();
    }

    @Override
    public List<String> getCategoryList() {
        return courseMapper.getCategoryList();
    }

    @Override
    public Integer countCoursesByTeacher(Long teacherId, String semester) {
        if (teacherId == null) {
            return 0;
        }
        
        return courseMapper.countByTeacherId(teacherId, semester);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourseStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        
        log.info("更新课程状态: {} -> {}", id, status);
        
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        course.setStatus(status);
        int result = courseMapper.updateById(course);
        
        boolean success = result > 0;
        if (success) {
            log.info("课程状态更新成功: {} -> {}", id, status);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImportCourses(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return 0;
        }
        
        log.info("批量导入课程: {} 门", courses.size());
        
        int successCount = 0;
        for (Course course : courses) {
            try {
                // 检查课程编码是否已存在
                if (!isCourseCodeExists(course.getCourseCode(), null)) {
                    createCourse(course);
                    successCount++;
                } else {
                    log.warn("课程编码已存在，跳过: {}", course.getCourseCode());
                }
            } catch (Exception e) {
                log.error("导入课程失败: {}, 错误: {}", course.getCourseCode(), e.getMessage());
            }
        }
        
        log.info("批量导入完成，成功: {} 门", successCount);
        return successCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Course copyCourseToNewSemester(Long courseId, String newSemester) {
        if (courseId == null || !StringUtils.hasText(newSemester)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        log.info("复制课程到新学期: {} -> {}", courseId, newSemester);
        
        // 获取原课程
        Course originalCourse = courseMapper.selectById(courseId);
        if (originalCourse == null) {
            throw new IllegalArgumentException("原课程不存在");
        }
        
        // 创建新课程
        Course newCourse = new Course();
        newCourse.setCourseCode(originalCourse.getCourseCode() + "_" + newSemester);
        newCourse.setCourseName(originalCourse.getCourseName());
        newCourse.setCategory(originalCourse.getCategory());
        newCourse.setCredits(originalCourse.getCredits());
        newCourse.setHours(originalCourse.getHours());
        newCourse.setDescription(originalCourse.getDescription());
        newCourse.setTeacherId(originalCourse.getTeacherId());
        newCourse.setMaxStudents(originalCourse.getMaxStudents());
        newCourse.setCurrentStudents(0); // 新学期选课人数重置为0
        newCourse.setSemester(newSemester);
        newCourse.setStatus(1);
        
        // 确保课程编码唯一
        int suffix = 1;
        String baseCode = originalCourse.getCourseCode() + "_" + newSemester;
        while (isCourseCodeExists(newCourse.getCourseCode(), null)) {
            newCourse.setCourseCode(baseCode + "_" + suffix);
            suffix++;
        }
        
        courseMapper.insert(newCourse);
        log.info("课程复制成功，新课程ID: {}", newCourse.getId());
        
        return newCourse;
    }
}

package com.course.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.PageRequest;
import com.course.entity.Course;
import com.course.entity.CourseSelection;
import com.course.mapper.CourseSelectionMapper;
import com.course.service.CourseSelectionService;
import com.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSelectionServiceImpl implements CourseSelectionService {

    private final CourseSelectionMapper courseSelectionMapper;
    private final CourseService courseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseSelection selectCourse(Long studentId, Long courseId) {
        log.info("学生选课: studentId={}, courseId={}", studentId, courseId);
        
        // 验证参数
        if (studentId == null || courseId == null) {
            throw new IllegalArgumentException("学生ID和课程ID不能为空");
        }
        
        // 检查是否可以选课
        if (!canSelectCourse(studentId, courseId)) {
            throw new IllegalArgumentException("无法选择该课程");
        }
        
        // 检查是否已经选过该课程
        CourseSelection existing = getSelectionByStudentAndCourse(studentId, courseId);
        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new IllegalArgumentException("已经选择过该课程");
            } else {
                // 如果之前退过课，重新选课
                existing.setStatus(1);
                existing.setSelectionTime(LocalDateTime.now());
                courseSelectionMapper.updateById(existing);
                
                // 更新课程选课人数
                courseService.updateCurrentStudents(courseId, 1);
                
                log.info("重新选课成功: {}", existing.getId());
                return existing;
            }
        }
        
        // 创建新的选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(courseId);
        selection.setSelectionTime(LocalDateTime.now());
        selection.setStatus(1); // 已选课
        
        courseSelectionMapper.insert(selection);
        
        // 更新课程选课人数
        courseService.updateCurrentStudents(courseId, 1);
        
        log.info("选课成功: {}", selection.getId());
        return selection;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dropCourse(Long studentId, Long courseId) {
        log.info("学生退课: studentId={}, courseId={}", studentId, courseId);
        
        // 验证参数
        if (studentId == null || courseId == null) {
            throw new IllegalArgumentException("学生ID和课程ID不能为空");
        }
        
        // 查找选课记录
        CourseSelection selection = getSelectionByStudentAndCourse(studentId, courseId);
        if (selection == null || selection.getStatus() != 1) {
            throw new IllegalArgumentException("未找到有效的选课记录");
        }
        
        // 更新选课状态为已退课
        selection.setStatus(0);
        int result = courseSelectionMapper.updateById(selection);
        
        if (result > 0) {
            // 更新课程选课人数
            courseService.updateCurrentStudents(courseId, -1);
            log.info("退课成功: {}", selection.getId());
            return true;
        }
        
        return false;
    }

    @Override
    public List<CourseSelection> getStudentSelections(Long studentId, String semester, Integer status) {
        if (studentId == null) {
            return List.of();
        }
        
        return courseSelectionMapper.findByStudentId(studentId, semester, status);
    }

    @Override
    public List<CourseSelection> getCourseSelections(Long courseId, Integer status) {
        if (courseId == null) {
            return List.of();
        }
        
        return courseSelectionMapper.findByCourseId(courseId, status);
    }

    @Override
    public IPage<CourseSelection> getSelectionPage(PageRequest pageRequest, Long courseId, String semester, Integer status) {
        Page<CourseSelection> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        return courseSelectionMapper.selectSelectionPageWithDetails(page, pageRequest.getKeyword(), 
                                                                   courseId, semester, status);
    }

    @Override
    public CourseSelection getSelectionByStudentAndCourse(Long studentId, Long courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        
        return courseSelectionMapper.findByStudentAndCourse(studentId, courseId);
    }

    @Override
    public boolean canSelectCourse(Long studentId, Long courseId) {
        if (studentId == null || courseId == null) {
            return false;
        }
        
        // 检查课程是否可以选择
        if (!courseService.canSelectCourse(courseId)) {
            return false;
        }
        
        // 检查是否已经选过该课程
        CourseSelection existing = getSelectionByStudentAndCourse(studentId, courseId);
        if (existing != null && existing.getStatus() == 1) {
            return false;
        }
        
        // 可以添加更多业务规则检查，如：
        // - 学生选课数量限制
        // - 时间冲突检查
        // - 先修课程检查等
        
        return true;
    }

    @Override
    public boolean hasTimeConflict(Long studentId, Long courseId, String semester) {
        if (studentId == null || courseId == null) {
            return false;
        }
        
        // 这里需要结合课程安排表来检查时间冲突
        // 暂时返回false，后续可以完善
        return false;
    }

    @Override
    public Integer countStudentSelections(Long studentId, String semester, Integer status) {
        if (studentId == null) {
            return 0;
        }
        
        return courseSelectionMapper.countByStudentId(studentId, semester, status);
    }

    @Override
    public Integer countCourseSelections(Long courseId, Integer status) {
        if (courseId == null) {
            return 0;
        }
        
        return courseSelectionMapper.countByCourseId(courseId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDropCoursesBySemester(Long studentId, String semester) {
        if (studentId == null || semester == null) {
            return 0;
        }
        
        log.info("批量退课: studentId={}, semester={}", studentId, semester);
        
        // 获取该学期的所有选课记录
        List<CourseSelection> selections = getStudentSelections(studentId, semester, 1);
        
        int count = 0;
        for (CourseSelection selection : selections) {
            if (dropCourse(studentId, selection.getCourseId())) {
                count++;
            }
        }
        
        log.info("批量退课完成: 退课{}门", count);
        return count;
    }

    @Override
    public List<CourseSelection> getStudentSchedule(Long studentId, String semester) {
        if (studentId == null || semester == null) {
            return List.of();
        }
        
        // 获取学生在指定学期的所有已选课程
        return getStudentSelections(studentId, semester, 1);
    }
}

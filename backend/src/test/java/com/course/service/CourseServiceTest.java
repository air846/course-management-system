package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.PageRequest;
import com.course.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CourseService测试类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    void testCreateCourse() {
        // 创建测试课程
        Course course = new Course();
        course.setCourseCode("SVC001");
        course.setCourseName("服务测试课程");
        course.setCategory("测试分类");
        course.setCredits(new BigDecimal("3.0"));
        course.setHours(48);
        course.setDescription("这是一个服务层测试课程");
        course.setTeacherId(1L);
        course.setMaxStudents(50);
        course.setSemester("2024春季");

        // 创建课程
        Course createdCourse = courseService.createCourse(course);
        
        // 验证创建结果
        assertNotNull(createdCourse);
        assertNotNull(createdCourse.getId());
        assertEquals("SVC001", createdCourse.getCourseCode());
        assertEquals("服务测试课程", createdCourse.getCourseName());
        assertEquals("测试分类", createdCourse.getCategory());
        assertEquals(new BigDecimal("3.0"), createdCourse.getCredits());
        assertEquals(1, createdCourse.getStatus()); // 默认正常
        assertEquals(0, createdCourse.getCurrentStudents()); // 默认0人选课
        
        System.out.println("✅ 课程创建测试通过 - 课程ID: " + createdCourse.getId());
    }

    @Test
    void testCreateCourseWithDuplicateCode() {
        // 先创建一个课程
        Course course1 = new Course();
        course1.setCourseCode("DUPLICATE");
        course1.setCourseName("重复测试课程1");
        course1.setSemester("2024春季");
        
        courseService.createCourse(course1);

        // 尝试创建相同编码的课程
        Course course2 = new Course();
        course2.setCourseCode("DUPLICATE");
        course2.setCourseName("重复测试课程2");
        course2.setSemester("2024春季");

        // 应该抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseService.createCourse(course2);
        });
        
        assertEquals("课程编码已存在", exception.getMessage());
        System.out.println("✅ 课程编码重复检查测试通过");
    }

    @Test
    void testGetCourseById() {
        // 先创建一个课程
        Course course = new Course();
        course.setCourseCode("GETBYID");
        course.setCourseName("根据ID查询课程");
        course.setCategory("查询测试");
        course.setSemester("2024春季");
        
        Course createdCourse = courseService.createCourse(course);
        Long courseId = createdCourse.getId();

        // 根据ID查询课程
        Course foundCourse = courseService.getCourseById(courseId);
        
        // 验证查询结果
        assertNotNull(foundCourse);
        assertEquals(courseId, foundCourse.getId());
        assertEquals("GETBYID", foundCourse.getCourseCode());
        assertEquals("根据ID查询课程", foundCourse.getCourseName());
        
        System.out.println("✅ 根据ID查询课程测试通过 - 课程: " + foundCourse.getCourseName());
    }

    @Test
    void testUpdateCourse() {
        // 先创建一个课程
        Course course = new Course();
        course.setCourseCode("UPDATE");
        course.setCourseName("更新测试课程");
        course.setCategory("原分类");
        course.setCredits(new BigDecimal("2.0"));
        course.setSemester("2024春季");
        
        Course createdCourse = courseService.createCourse(course);
        Long courseId = createdCourse.getId();

        // 更新课程信息
        Course updateCourse = new Course();
        updateCourse.setId(courseId);
        updateCourse.setCourseName("已更新的课程");
        updateCourse.setCategory("新分类");
        updateCourse.setCredits(new BigDecimal("4.0"));
        updateCourse.setDescription("更新后的描述");
        
        Course updatedCourse = courseService.updateCourse(updateCourse);
        
        // 验证更新结果
        assertNotNull(updatedCourse);
        assertEquals("已更新的课程", updatedCourse.getCourseName());
        assertEquals("新分类", updatedCourse.getCategory());
        assertEquals(new BigDecimal("4.0"), updatedCourse.getCredits());
        assertEquals("更新后的描述", updatedCourse.getDescription());
        // 课程编码应该保持不变
        assertEquals("UPDATE", updatedCourse.getCourseCode());
        
        System.out.println("✅ 课程更新测试通过 - 新名称: " + updatedCourse.getCourseName());
    }

    @Test
    void testUpdateCurrentStudents() {
        // 先创建一个课程
        Course course = new Course();
        course.setCourseCode("STUDENTS");
        course.setCourseName("选课人数测试课程");
        course.setMaxStudents(50);
        course.setCurrentStudents(10);
        course.setSemester("2024春季");
        
        Course createdCourse = courseService.createCourse(course);
        Long courseId = createdCourse.getId();

        // 增加选课人数
        boolean result1 = courseService.updateCurrentStudents(courseId, 5);
        assertTrue(result1);
        
        // 验证人数更新
        Course updatedCourse = courseService.getCourseById(courseId);
        assertEquals(15, updatedCourse.getCurrentStudents().intValue());

        // 减少选课人数
        boolean result2 = courseService.updateCurrentStudents(courseId, -3);
        assertTrue(result2);
        
        // 验证人数更新
        Course updatedCourse2 = courseService.getCourseById(courseId);
        assertEquals(12, updatedCourse2.getCurrentStudents().intValue());
        
        System.out.println("✅ 选课人数更新测试通过 - 当前人数: " + updatedCourse2.getCurrentStudents());
    }

    @Test
    void testUpdateCurrentStudentsOverLimit() {
        // 先创建一个课程
        Course course = new Course();
        course.setCourseCode("OVERLIMIT");
        course.setCourseName("超限测试课程");
        course.setMaxStudents(50);
        course.setCurrentStudents(48);
        course.setSemester("2024春季");
        
        Course createdCourse = courseService.createCourse(course);
        Long courseId = createdCourse.getId();

        // 尝试增加超过限制的人数
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseService.updateCurrentStudents(courseId, 5);
        });
        
        assertEquals("选课人数超过限制", exception.getMessage());
        System.out.println("✅ 选课人数超限检查测试通过");
    }

    @Test
    void testCanSelectCourse() {
        // 创建可选课程
        Course availableCourse = new Course();
        availableCourse.setCourseCode("AVAILABLE");
        availableCourse.setCourseName("可选课程");
        availableCourse.setMaxStudents(50);
        availableCourse.setCurrentStudents(30);
        availableCourse.setStatus(1);
        availableCourse.setSemester("2024春季");
        
        Course createdAvailable = courseService.createCourse(availableCourse);

        // 创建已满课程
        Course fullCourse = new Course();
        fullCourse.setCourseCode("FULL");
        fullCourse.setCourseName("已满课程");
        fullCourse.setMaxStudents(50);
        fullCourse.setCurrentStudents(50);
        fullCourse.setStatus(1);
        fullCourse.setSemester("2024春季");
        
        Course createdFull = courseService.createCourse(fullCourse);

        // 测试可选性
        assertTrue(courseService.canSelectCourse(createdAvailable.getId()));
        assertFalse(courseService.canSelectCourse(createdFull.getId()));
        
        System.out.println("✅ 课程可选性检查测试通过");
    }

    @Test
    void testGetCoursePage() {
        // 先创建几个测试课程
        String[] categories = {"计算机科学", "数学", "计算机科学"};
        for (int i = 1; i <= 3; i++) {
            Course course = new Course();
            course.setCourseCode("PAGE00" + i);
            course.setCourseName("分页测试课程" + i);
            course.setCategory(categories[i - 1]);
            course.setSemester("2024春季");
            course.setStatus(1);
            
            courseService.createCourse(course);
        }

        // 分页查询课程
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(1);
        pageRequest.setSize(2);
        pageRequest.setKeyword("分页测试");
        
        IPage<Course> coursePage = courseService.getCoursePage(pageRequest, null, null, null, null);
        
        // 验证分页结果
        assertNotNull(coursePage);
        assertTrue(coursePage.getTotal() >= 3);
        assertTrue(coursePage.getRecords().size() <= 2);
        
        System.out.println("✅ 课程分页查询测试通过 - 总数: " + coursePage.getTotal() + 
                          ", 当前页数据: " + coursePage.getRecords().size());
    }

    @Test
    void testGetAvailableCourses() {
        // 创建几个不同状态的课程
        Course[] courses = {
            createTestCourse("AVAIL1", "可选课程1", 50, 30, 1),
            createTestCourse("AVAIL2", "可选课程2", 40, 40, 1), // 已满
            createTestCourse("AVAIL3", "可选课程3", 60, 25, 1),
            createTestCourse("AVAIL4", "停课课程", 50, 20, 0)  // 停课
        };
        
        for (Course course : courses) {
            courseService.createCourse(course);
        }

        // 查询可选课程
        List<Course> availableCourses = courseService.getAvailableCourses("2024春季");
        
        // 验证结果（应该只有2个可选课程）
        assertNotNull(availableCourses);
        assertEquals(2, availableCourses.size());
        
        System.out.println("✅ 查询可选课程测试通过 - 可选课程数量: " + availableCourses.size());
        availableCourses.forEach(c -> 
            System.out.println("   - " + c.getCourseCode() + ": " + c.getCourseName())
        );
    }

    @Test
    void testCopyCourseToNewSemester() {
        // 先创建一个原课程
        Course originalCourse = new Course();
        originalCourse.setCourseCode("ORIGINAL");
        originalCourse.setCourseName("原始课程");
        originalCourse.setCategory("计算机科学");
        originalCourse.setCredits(new BigDecimal("3.0"));
        originalCourse.setHours(48);
        originalCourse.setDescription("原始课程描述");
        originalCourse.setTeacherId(1L);
        originalCourse.setMaxStudents(50);
        originalCourse.setCurrentStudents(30);
        originalCourse.setSemester("2024春季");
        
        Course createdOriginal = courseService.createCourse(originalCourse);

        // 复制到新学期
        Course copiedCourse = courseService.copyCourseToNewSemester(createdOriginal.getId(), "2024秋季");
        
        // 验证复制结果
        assertNotNull(copiedCourse);
        assertNotEquals(createdOriginal.getId(), copiedCourse.getId());
        assertNotEquals(createdOriginal.getCourseCode(), copiedCourse.getCourseCode());
        assertEquals("原始课程", copiedCourse.getCourseName());
        assertEquals("计算机科学", copiedCourse.getCategory());
        assertEquals(new BigDecimal("3.0"), copiedCourse.getCredits());
        assertEquals("2024秋季", copiedCourse.getSemester());
        assertEquals(0, copiedCourse.getCurrentStudents()); // 新学期选课人数重置为0
        
        System.out.println("✅ 课程复制到新学期测试通过 - 新课程编码: " + copiedCourse.getCourseCode());
    }

    private Course createTestCourse(String code, String name, int maxStudents, int currentStudents, int status) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setMaxStudents(maxStudents);
        course.setCurrentStudents(currentStudents);
        course.setStatus(status);
        course.setSemester("2024春季");
        return course;
    }
}

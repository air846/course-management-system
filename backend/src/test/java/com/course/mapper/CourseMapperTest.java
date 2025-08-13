package com.course.mapper;

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
 * CourseMapper测试类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseMapperTest {

    @Autowired
    private CourseMapper courseMapper;

    @Test
    void testInsertCourse() {
        // 创建测试课程
        Course course = new Course();
        course.setCourseCode("TEST001");
        course.setCourseName("测试课程");
        course.setCategory("测试分类");
        course.setCredits(new BigDecimal("3.0"));
        course.setHours(48);
        course.setDescription("这是一个测试课程");
        course.setTeacherId(1L);
        course.setMaxStudents(50);
        course.setCurrentStudents(0);
        course.setSemester("2024春季");
        course.setStatus(1);

        // 插入课程
        int result = courseMapper.insert(course);
        
        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(course.getId());
        
        System.out.println("✅ 课程插入测试通过 - 课程ID: " + course.getId());
    }

    @Test
    void testSelectById() {
        // 先插入一个测试课程
        Course course = new Course();
        course.setCourseCode("SELECT001");
        course.setCourseName("查询测试课程");
        course.setCategory("计算机科学");
        course.setCredits(new BigDecimal("4.0"));
        course.setHours(64);
        course.setMaxStudents(40);
        course.setCurrentStudents(0);
        course.setSemester("2024春季");
        course.setStatus(1);
        
        courseMapper.insert(course);
        Long courseId = course.getId();

        // 根据ID查询课程
        Course foundCourse = courseMapper.selectById(courseId);
        
        // 验证查询结果
        assertNotNull(foundCourse);
        assertEquals("SELECT001", foundCourse.getCourseCode());
        assertEquals("查询测试课程", foundCourse.getCourseName());
        assertEquals("计算机科学", foundCourse.getCategory());
        
        System.out.println("✅ 根据ID查询课程测试通过 - 课程: " + foundCourse.getCourseName());
    }

    @Test
    void testFindByCourseCode() {
        // 先插入一个测试课程
        Course course = new Course();
        course.setCourseCode("CODE001");
        course.setCourseName("编码测试课程");
        course.setCategory("软件工程");
        course.setCredits(new BigDecimal("3.5"));
        course.setHours(56);
        course.setMaxStudents(35);
        course.setCurrentStudents(0);
        course.setSemester("2024春季");
        course.setStatus(1);
        
        courseMapper.insert(course);

        // 根据课程编码查询
        Course foundCourse = courseMapper.findByCourseCode("CODE001");
        
        // 验证查询结果
        assertNotNull(foundCourse);
        assertEquals("CODE001", foundCourse.getCourseCode());
        assertEquals("编码测试课程", foundCourse.getCourseName());
        
        System.out.println("✅ 根据课程编码查询测试通过 - 课程: " + foundCourse.getCourseName());
    }

    @Test
    void testUpdateCurrentStudents() {
        // 先插入一个测试课程
        Course course = new Course();
        course.setCourseCode("UPDATE001");
        course.setCourseName("更新测试课程");
        course.setMaxStudents(50);
        course.setCurrentStudents(10);
        course.setSemester("2024春季");
        course.setStatus(1);
        
        courseMapper.insert(course);
        Long courseId = course.getId();

        // 更新当前选课人数（增加5人）
        int result = courseMapper.updateCurrentStudents(courseId, 5);
        
        // 验证更新结果
        assertEquals(1, result);
        
        // 重新查询验证
        Course updatedCourse = courseMapper.selectById(courseId);
        assertEquals(15, updatedCourse.getCurrentStudents().intValue());
        
        System.out.println("✅ 更新选课人数测试通过 - 当前人数: " + updatedCourse.getCurrentStudents());
    }

    @Test
    void testFindBySemester() {
        // 插入几个不同学期的测试课程
        String[] semesters = {"2024春季", "2024秋季", "2024春季"};
        String[] courseNames = {"春季课程1", "秋季课程1", "春季课程2"};
        
        for (int i = 0; i < 3; i++) {
            Course course = new Course();
            course.setCourseCode("SEM00" + (i + 1));
            course.setCourseName(courseNames[i]);
            course.setSemester(semesters[i]);
            course.setStatus(1);
            
            courseMapper.insert(course);
        }

        // 查询2024春季的课程
        List<Course> springCourses = courseMapper.findBySemester("2024春季");
        
        // 验证查询结果
        assertNotNull(springCourses);
        assertEquals(2, springCourses.size());
        
        System.out.println("✅ 根据学期查询课程测试通过 - 2024春季课程数量: " + springCourses.size());
        springCourses.forEach(c -> System.out.println("   - " + c.getCourseCode() + ": " + c.getCourseName()));
    }

    @Test
    void testGetCategoryList() {
        // 插入几个不同分类的测试课程
        String[] categories = {"计算机科学", "数学", "计算机科学", "物理", "数学"};
        
        for (int i = 0; i < categories.length; i++) {
            Course course = new Course();
            course.setCourseCode("CAT00" + (i + 1));
            course.setCourseName("分类测试课程" + (i + 1));
            course.setCategory(categories[i]);
            course.setStatus(1);
            
            courseMapper.insert(course);
        }

        // 获取分类列表
        List<String> categoryList = courseMapper.getCategoryList();
        
        // 验证查询结果
        assertNotNull(categoryList);
        assertTrue(categoryList.contains("计算机科学"));
        assertTrue(categoryList.contains("数学"));
        assertTrue(categoryList.contains("物理"));
        
        System.out.println("✅ 获取课程分类列表测试通过 - 分类数量: " + categoryList.size());
        categoryList.forEach(category -> System.out.println("   - " + category));
    }

    @Test
    void testFindAvailableCourses() {
        // 插入几个测试课程，设置不同的选课情况
        Course[] courses = {
            createTestCourse("AVAIL001", "可选课程1", 50, 30),
            createTestCourse("AVAIL002", "可选课程2", 40, 40), // 已满
            createTestCourse("AVAIL003", "可选课程3", 60, 25)
        };
        
        for (Course course : courses) {
            courseMapper.insert(course);
        }

        // 查询可选课程（当前人数 < 最大人数）
        List<Course> availableCourses = courseMapper.findAvailableCourses("2024春季");
        
        // 验证查询结果（应该只有2个可选课程）
        assertNotNull(availableCourses);
        assertEquals(2, availableCourses.size());
        
        System.out.println("✅ 查询可选课程测试通过 - 可选课程数量: " + availableCourses.size());
        availableCourses.forEach(c -> 
            System.out.println("   - " + c.getCourseCode() + ": " + c.getCourseName() + 
                             " (已选: " + c.getCurrentStudents() + "/" + c.getMaxStudents() + ")")
        );
    }

    private Course createTestCourse(String code, String name, int maxStudents, int currentStudents) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setMaxStudents(maxStudents);
        course.setCurrentStudents(currentStudents);
        course.setSemester("2024春季");
        course.setStatus(1);
        return course;
    }
}

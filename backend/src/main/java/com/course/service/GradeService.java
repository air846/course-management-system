package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.GradeRequest;
import com.course.dto.GradeStatistics;
import com.course.dto.PageRequest;
import com.course.entity.Grade;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface GradeService {

    /**
     * 录入或更新成绩
     * 
     * @param gradeRequest 成绩请求
     * @return 成绩记录
     */
    Grade saveOrUpdateGrade(GradeRequest gradeRequest);

    /**
     * 批量录入成绩
     * 
     * @param gradeRequests 成绩请求列表
     * @return 成功录入的数量
     */
    int batchSaveGrades(List<GradeRequest> gradeRequests);

    /**
     * 删除成绩
     * 
     * @param id 成绩ID
     * @return 是否删除成功
     */
    boolean deleteGrade(Long id);

    /**
     * 根据ID查询成绩
     * 
     * @param id 成绩ID
     * @return 成绩记录
     */
    Grade getGradeById(Long id);

    /**
     * 查询学生的成绩列表
     * 
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 成绩列表
     */
    List<Grade> getStudentGrades(Long studentId, String semester);

    /**
     * 查询课程的成绩列表
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<Grade> getCourseGrades(Long courseId, String semester);

    /**
     * 分页查询成绩列表
     * 
     * @param pageRequest 分页请求参数
     * @param courseId 课程ID（可选）
     * @param studentId 学生ID（可选）
     * @param semester 学期（可选）
     * @param gradeLevel 成绩等级（可选）
     * @return 成绩分页列表
     */
    IPage<Grade> getGradePage(PageRequest pageRequest, Long courseId, Long studentId, 
                             String semester, String gradeLevel);

    /**
     * 查询学生在指定课程的成绩
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩记录
     */
    Grade getStudentCourseGrade(Long studentId, Long courseId, String semester);

    /**
     * 计算课程成绩统计
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩统计
     */
    GradeStatistics calculateCourseStatistics(Long courseId, String semester);

    /**
     * 计算学生的平均成绩
     * 
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 平均成绩
     */
    Double calculateStudentAverage(Long studentId, String semester);

    /**
     * 计算学生的总学分
     * 
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 总学分
     */
    Double calculateStudentTotalCredits(Long studentId, String semester);

    /**
     * 获取课程成绩分布
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩分布统计
     */
    List<Map<String, Object>> getCourseGradeDistribution(Long courseId, String semester);

    /**
     * 获取课程成绩统计信息
     * 
     * @param courseId 课程ID
     * @param semester 学期
     * @return 统计信息
     */
    Map<String, Object> getCourseGradeStatistics(Long courseId, String semester);

    /**
     * 获取学生的学期排名
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @return 排名信息
     */
    Map<String, Object> getStudentRanking(Long studentId, String semester);

    /**
     * 计算总成绩
     * 
     * @param usualScore 平时成绩
     * @param midtermScore 期中成绩
     * @param finalScore 期末成绩
     * @return 总成绩
     */
    Double calculateTotalScore(Double usualScore, Double midtermScore, Double finalScore);

    /**
     * 根据总成绩计算等级
     * 
     * @param totalScore 总成绩
     * @return 等级
     */
    String calculateGradeLevel(Double totalScore);

    /**
     * 检查是否可以录入成绩
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @return 是否可以录入
     */
    boolean canInputGrade(Long studentId, Long courseId, String semester);
}

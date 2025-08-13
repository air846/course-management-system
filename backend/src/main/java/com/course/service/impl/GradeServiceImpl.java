package com.course.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.GradeRequest;
import com.course.dto.GradeStatistics;
import com.course.dto.PageRequest;
import com.course.entity.Grade;
import com.course.mapper.GradeMapper;
import com.course.service.CourseSelectionService;
import com.course.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    private final CourseSelectionService courseSelectionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Grade saveOrUpdateGrade(GradeRequest gradeRequest) {
        log.info("录入或更新成绩: studentId={}, courseId={}", gradeRequest.getStudentId(), gradeRequest.getCourseId());
        
        // 验证参数
        if (gradeRequest.getStudentId() == null || gradeRequest.getCourseId() == null) {
            throw new IllegalArgumentException("学生ID和课程ID不能为空");
        }
        
        // 检查是否可以录入成绩
        if (!canInputGrade(gradeRequest.getStudentId(), gradeRequest.getCourseId(), gradeRequest.getSemester())) {
            throw new IllegalArgumentException("该学生未选择此课程，无法录入成绩");
        }
        
        Grade grade;
        if (gradeRequest.getId() != null) {
            // 更新现有成绩
            grade = gradeMapper.selectById(gradeRequest.getId());
            if (grade == null) {
                throw new IllegalArgumentException("成绩记录不存在");
            }
        } else {
            // 检查是否已存在成绩记录
            grade = gradeMapper.findByStudentAndCourse(
                gradeRequest.getStudentId(), 
                gradeRequest.getCourseId(), 
                gradeRequest.getSemester()
            );
            
            if (grade == null) {
                // 创建新的成绩记录
                grade = new Grade();
                grade.setStudentId(gradeRequest.getStudentId());
                grade.setCourseId(gradeRequest.getCourseId());
                grade.setSemester(gradeRequest.getSemester());
            }
        }
        
        // 更新成绩信息
        BeanUtils.copyProperties(gradeRequest, grade, "id");
        
        // 计算总成绩
        if (gradeRequest.getUsualScore() != null || gradeRequest.getMidtermScore() != null || gradeRequest.getFinalScore() != null) {
            Double totalScore = calculateTotalScore(
                gradeRequest.getUsualScore() != null ? gradeRequest.getUsualScore().doubleValue() : null,
                gradeRequest.getMidtermScore() != null ? gradeRequest.getMidtermScore().doubleValue() : null,
                gradeRequest.getFinalScore() != null ? gradeRequest.getFinalScore().doubleValue() : null
            );
            
            if (totalScore != null) {
                grade.setTotalScore(BigDecimal.valueOf(totalScore));
                // 计算等级
                String gradeLevel = calculateGradeLevel(totalScore);
                grade.setGradeLevel(gradeLevel);
            }
        }
        
        // 保存或更新
        if (grade.getId() == null) {
            gradeMapper.insert(grade);
            log.info("成绩录入成功: {}", grade.getId());
        } else {
            gradeMapper.updateById(grade);
            log.info("成绩更新成功: {}", grade.getId());
        }
        
        return grade;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSaveGrades(List<GradeRequest> gradeRequests) {
        log.info("批量录入成绩: 数量={}", gradeRequests.size());
        
        List<Grade> grades = new ArrayList<>();
        for (GradeRequest request : gradeRequests) {
            Grade grade = new Grade();
            BeanUtils.copyProperties(request, grade);
            
            // 计算总成绩和等级
            if (request.getUsualScore() != null || request.getMidtermScore() != null || request.getFinalScore() != null) {
                Double totalScore = calculateTotalScore(
                    request.getUsualScore() != null ? request.getUsualScore().doubleValue() : null,
                    request.getMidtermScore() != null ? request.getMidtermScore().doubleValue() : null,
                    request.getFinalScore() != null ? request.getFinalScore().doubleValue() : null
                );
                
                if (totalScore != null) {
                    grade.setTotalScore(BigDecimal.valueOf(totalScore));
                    grade.setGradeLevel(calculateGradeLevel(totalScore));
                }
            }
            
            grades.add(grade);
        }
        
        // 批量插入
        int result = gradeMapper.batchInsert(grades);
        log.info("批量录入成绩完成: 成功{}条", result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGrade(Long id) {
        log.info("删除成绩: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("成绩ID不能为空");
        }
        
        int result = gradeMapper.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("删除成绩成功: {}", id);
        } else {
            log.warn("删除成绩失败: {}", id);
        }
        
        return success;
    }

    @Override
    public Grade getGradeById(Long id) {
        if (id == null) {
            return null;
        }
        return gradeMapper.selectById(id);
    }

    @Override
    public List<Grade> getStudentGrades(Long studentId, String semester) {
        if (studentId == null) {
            return List.of();
        }
        return gradeMapper.findByStudentId(studentId, semester);
    }

    @Override
    public List<Grade> getCourseGrades(Long courseId, String semester) {
        if (courseId == null || semester == null) {
            return List.of();
        }
        return gradeMapper.findByCourseId(courseId, semester);
    }

    @Override
    public IPage<Grade> getGradePage(PageRequest pageRequest, Long courseId, Long studentId, 
                                    String semester, String gradeLevel) {
        Page<Grade> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        return gradeMapper.selectGradePageWithDetails(page, pageRequest.getKeyword(), 
                                                     courseId, studentId, semester, gradeLevel);
    }

    @Override
    public Grade getStudentCourseGrade(Long studentId, Long courseId, String semester) {
        if (studentId == null || courseId == null || semester == null) {
            return null;
        }
        return gradeMapper.findByStudentAndCourse(studentId, courseId, semester);
    }

    @Override
    public GradeStatistics calculateCourseStatistics(Long courseId, String semester) {
        if (courseId == null || semester == null) {
            return null;
        }
        
        Map<String, Object> stats = gradeMapper.getCourseGradeStatistics(courseId, semester);
        if (stats == null || stats.isEmpty()) {
            return null;
        }
        
        GradeStatistics statistics = new GradeStatistics();
        statistics.setCourseId(courseId);
        statistics.setSemester(semester);
        
        // 设置基本统计信息
        statistics.setTotalStudents(((Number) stats.get("total_count")).intValue());
        statistics.setAvgScore(new BigDecimal(stats.get("avg_score").toString()));
        statistics.setMaxScore(new BigDecimal(stats.get("max_score").toString()));
        statistics.setMinScore(new BigDecimal(stats.get("min_score").toString()));
        
        // 设置等级统计
        statistics.setGradeACount(((Number) stats.get("excellent_count")).intValue());
        statistics.setGradeBCount(((Number) stats.get("good_count")).intValue());
        statistics.setGradeCCount(((Number) stats.get("medium_count")).intValue());
        statistics.setGradeDCount(((Number) stats.get("pass_count")).intValue());
        statistics.setGradeFCount(((Number) stats.get("fail_count")).intValue());
        
        // 计算及格人数和及格率
        int passedStudents = statistics.getTotalStudents() - statistics.getGradeFCount();
        statistics.setPassedStudents(passedStudents);
        statistics.setFailedStudents(statistics.getGradeFCount());
        
        if (statistics.getTotalStudents() > 0) {
            BigDecimal passRate = BigDecimal.valueOf(passedStudents)
                .divide(BigDecimal.valueOf(statistics.getTotalStudents()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            statistics.setPassRate(passRate);
            
            // 计算优秀率和良好率
            BigDecimal excellentRate = BigDecimal.valueOf(statistics.getGradeACount())
                .divide(BigDecimal.valueOf(statistics.getTotalStudents()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            statistics.setExcellentRate(excellentRate);
            
            BigDecimal goodRate = BigDecimal.valueOf(statistics.getGradeBCount())
                .divide(BigDecimal.valueOf(statistics.getTotalStudents()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            statistics.setGoodRate(goodRate);
        }
        
        return statistics;
    }

    @Override
    public Double calculateStudentAverage(Long studentId, String semester) {
        if (studentId == null) {
            return null;
        }
        
        BigDecimal avg = gradeMapper.calculateAverageScore(studentId, semester);
        return avg != null ? avg.doubleValue() : null;
    }

    @Override
    public Double calculateStudentTotalCredits(Long studentId, String semester) {
        if (studentId == null) {
            return null;
        }
        
        BigDecimal credits = gradeMapper.calculateTotalCredits(studentId, semester);
        return credits != null ? credits.doubleValue() : null;
    }

    @Override
    public List<Map<String, Object>> getCourseGradeDistribution(Long courseId, String semester) {
        if (courseId == null || semester == null) {
            return List.of();
        }
        return gradeMapper.getGradeDistribution(courseId, semester);
    }

    @Override
    public Map<String, Object> getCourseGradeStatistics(Long courseId, String semester) {
        if (courseId == null || semester == null) {
            return Map.of();
        }
        return gradeMapper.getCourseGradeStatistics(courseId, semester);
    }

    @Override
    public Map<String, Object> getStudentRanking(Long studentId, String semester) {
        if (studentId == null || semester == null) {
            return Map.of();
        }
        return gradeMapper.getStudentRanking(studentId, semester);
    }

    @Override
    public Double calculateTotalScore(Double usualScore, Double midtermScore, Double finalScore) {
        // 成绩计算权重：平时成绩30%，期中成绩30%，期末成绩40%
        double total = 0.0;
        double weightSum = 0.0;
        
        if (usualScore != null) {
            total += usualScore * 0.3;
            weightSum += 0.3;
        }
        
        if (midtermScore != null) {
            total += midtermScore * 0.3;
            weightSum += 0.3;
        }
        
        if (finalScore != null) {
            total += finalScore * 0.4;
            weightSum += 0.4;
        }
        
        if (weightSum == 0) {
            return null;
        }
        
        // 如果不是所有成绩都有，按比例调整
        if (weightSum < 1.0) {
            total = total / weightSum;
        }
        
        return Math.round(total * 100.0) / 100.0; // 保留两位小数
    }

    @Override
    public String calculateGradeLevel(Double totalScore) {
        if (totalScore == null) {
            return null;
        }
        
        if (totalScore >= 95) {
            return "A+";
        } else if (totalScore >= 90) {
            return "A";
        } else if (totalScore >= 85) {
            return "A-";
        } else if (totalScore >= 82) {
            return "B+";
        } else if (totalScore >= 78) {
            return "B";
        } else if (totalScore >= 75) {
            return "B-";
        } else if (totalScore >= 72) {
            return "C+";
        } else if (totalScore >= 68) {
            return "C";
        } else if (totalScore >= 65) {
            return "C-";
        } else if (totalScore >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    @Override
    public boolean canInputGrade(Long studentId, Long courseId, String semester) {
        if (studentId == null || courseId == null || semester == null) {
            return false;
        }
        
        // 检查学生是否选择了该课程
        return courseSelectionService.getSelectionByStudentAndCourse(studentId, courseId) != null;
    }
}

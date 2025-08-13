package com.course.service.impl;

import com.course.dto.StatisticsOverview;
import com.course.service.ExportService;
import com.course.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 报表导出服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final StatisticsService statisticsService;

    @Override
    public byte[] exportStatisticsOverview() {
        log.info("导出统计概览报表");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // 获取统计数据
            StatisticsOverview overview = statisticsService.getStatisticsOverview();
            
            // 创建工作表
            Sheet sheet = workbook.createSheet("统计概览");
            
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // 创建标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("课程管理系统统计概览报表");
            titleCell.setCellStyle(headerStyle);
            
            // 生成时间
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("生成时间：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dateCell.setCellStyle(dataStyle);
            
            rowNum++; // 空行
            
            // 用户统计
            rowNum = addUserStatistics(sheet, overview.getUserStatistics(), rowNum, headerStyle, dataStyle);
            rowNum++; // 空行
            
            // 课程统计
            rowNum = addCourseStatistics(sheet, overview.getCourseStatistics(), rowNum, headerStyle, dataStyle);
            rowNum++; // 空行
            
            // 成绩统计
            rowNum = addGradeStatistics(sheet, overview.getGradeStatistics(), rowNum, headerStyle, dataStyle);
            rowNum++; // 空行
            
            // 公告统计
            addAnnouncementStatistics(sheet, overview.getAnnouncementStatistics(), rowNum, headerStyle, dataStyle);
            
            // 自动调整列宽
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("导出统计概览报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    @Override
    public byte[] exportUserStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("导出用户统计报表: startDate={}, endDate={}", startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户统计");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // 标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("用户统计报表");
            titleCell.setCellStyle(headerStyle);
            
            // 时间范围
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(String.format("统计时间：%s 至 %s", 
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            dateCell.setCellStyle(dataStyle);
            
            rowNum++; // 空行
            
            // 获取用户统计数据
            StatisticsOverview.UserStatistics userStats = statisticsService.getUserStatistics();
            
            // 表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"统计项目", "数值", "说明"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 数据行
            addDataRow(sheet, rowNum++, "总用户数", userStats.getTotalUsers(), "系统注册用户总数", dataStyle);
            addDataRow(sheet, rowNum++, "学生数量", userStats.getStudentCount(), "学生角色用户数量", dataStyle);
            addDataRow(sheet, rowNum++, "教师数量", userStats.getTeacherCount(), "教师角色用户数量", dataStyle);
            addDataRow(sheet, rowNum++, "管理员数量", userStats.getAdminCount(), "管理员角色用户数量", dataStyle);
            addDataRow(sheet, rowNum++, "活跃用户", userStats.getActiveUsers(), "最近30天活跃用户", dataStyle);
            addDataRow(sheet, rowNum++, "本月新增", userStats.getNewUsersThisMonth(), "本月新注册用户", dataStyle);
            addDataRow(sheet, rowNum++, "活跃率", userStats.getActiveRate() + "%", "活跃用户占比", dataStyle);
            
            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("导出用户统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    @Override
    public byte[] exportCourseStatistics(String semester) {
        log.info("导出课程统计报表: semester={}", semester);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("课程统计");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // 标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("课程统计报表");
            titleCell.setCellStyle(headerStyle);
            
            // 学期信息
            if (semester != null && !semester.isEmpty()) {
                Row semesterRow = sheet.createRow(rowNum++);
                Cell semesterCell = semesterRow.createCell(0);
                semesterCell.setCellValue("学期：" + semester);
                semesterCell.setCellStyle(dataStyle);
            }
            
            rowNum++; // 空行
            
            // 获取课程统计数据
            StatisticsOverview.CourseStatistics courseStats = statisticsService.getCourseStatistics();
            
            // 表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"统计项目", "数值", "说明"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 数据行
            addDataRow(sheet, rowNum++, "总课程数", courseStats.getTotalCourses(), "系统中所有课程", dataStyle);
            addDataRow(sheet, rowNum++, "开放课程", courseStats.getOpenCourses(), "当前开放选课的课程", dataStyle);
            addDataRow(sheet, rowNum++, "关闭课程", courseStats.getClosedCourses(), "已关闭选课的课程", dataStyle);
            addDataRow(sheet, rowNum++, "必修课程", courseStats.getRequiredCourses(), "必修课程数量", dataStyle);
            addDataRow(sheet, rowNum++, "选修课程", courseStats.getElectiveCourses(), "选修课程数量", dataStyle);
            addDataRow(sheet, rowNum++, "总选课次数", courseStats.getTotalSelections(), "所有学生选课总次数", dataStyle);
            addDataRow(sheet, rowNum++, "平均选课人数", courseStats.getAvgStudentsPerCourse(), "每门课程平均选课人数", dataStyle);
            addDataRow(sheet, rowNum++, "本学期新增", courseStats.getNewCoursesThisSemester(), "本学期新开设课程", dataStyle);
            
            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("导出课程统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    @Override
    public byte[] exportGradeStatistics(Long courseId, String semester) {
        log.info("导出成绩统计报表: courseId={}, semester={}", courseId, semester);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩统计");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // 标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("成绩统计报表");
            titleCell.setCellStyle(headerStyle);
            
            // 筛选条件
            if (courseId != null) {
                Row courseRow = sheet.createRow(rowNum++);
                Cell courseCell = courseRow.createCell(0);
                courseCell.setCellValue("课程ID：" + courseId);
                courseCell.setCellStyle(dataStyle);
            }
            
            if (semester != null && !semester.isEmpty()) {
                Row semesterRow = sheet.createRow(rowNum++);
                Cell semesterCell = semesterRow.createCell(0);
                semesterCell.setCellValue("学期：" + semester);
                semesterCell.setCellStyle(dataStyle);
            }
            
            rowNum++; // 空行
            
            // 获取成绩统计数据
            StatisticsOverview.GradeStatistics gradeStats = statisticsService.getGradeStatistics();
            
            // 表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"统计项目", "数值", "说明"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 数据行
            addDataRow(sheet, rowNum++, "总成绩记录", gradeStats.getTotalGrades(), "所有成绩记录数", dataStyle);
            addDataRow(sheet, rowNum++, "已录入成绩", gradeStats.getGradedCount(), "已录入成绩的记录数", dataStyle);
            addDataRow(sheet, rowNum++, "未录入成绩", gradeStats.getUngradedCount(), "未录入成绩的记录数", dataStyle);
            addDataRow(sheet, rowNum++, "平均成绩", gradeStats.getAverageScore(), "所有已录入成绩的平均值", dataStyle);
            addDataRow(sheet, rowNum++, "及格人数", gradeStats.getPassedCount(), "成绩≥60分的人数", dataStyle);
            addDataRow(sheet, rowNum++, "不及格人数", gradeStats.getFailedCount(), "成绩<60分的人数", dataStyle);
            addDataRow(sheet, rowNum++, "及格率", gradeStats.getPassRate() + "%", "及格人数占比", dataStyle);
            addDataRow(sheet, rowNum++, "优秀率", gradeStats.getExcellentRate() + "%", "成绩≥90分的人数占比", dataStyle);
            
            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("导出成绩统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    @Override
    public byte[] exportAnnouncementStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("导出公告统计报表: startDate={}, endDate={}", startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("公告统计");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // 标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("公告统计报表");
            titleCell.setCellStyle(headerStyle);
            
            // 时间范围
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(String.format("统计时间：%s 至 %s", 
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            dateCell.setCellStyle(dataStyle);
            
            rowNum++; // 空行
            
            // 获取公告统计数据
            StatisticsOverview.AnnouncementStatistics announcementStats = statisticsService.getAnnouncementStatistics();
            
            // 表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"统计项目", "数值", "说明"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 数据行
            addDataRow(sheet, rowNum++, "总公告数", announcementStats.getTotalAnnouncements(), "系统中所有公告", dataStyle);
            addDataRow(sheet, rowNum++, "已发布公告", announcementStats.getPublishedCount(), "已发布状态的公告", dataStyle);
            addDataRow(sheet, rowNum++, "草稿公告", announcementStats.getDraftCount(), "草稿状态的公告", dataStyle);
            addDataRow(sheet, rowNum++, "置顶公告", announcementStats.getTopCount(), "设置为置顶的公告", dataStyle);
            addDataRow(sheet, rowNum++, "本月发布", announcementStats.getPublishedThisMonth(), "本月发布的公告数", dataStyle);
            addDataRow(sheet, rowNum++, "总阅读次数", announcementStats.getTotalReadCount(), "所有公告的总阅读次数", dataStyle);
            addDataRow(sheet, rowNum++, "平均阅读次数", announcementStats.getAvgReadCount(), "每条公告的平均阅读次数", dataStyle);
            
            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("导出公告统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    @Override
    public byte[] exportPopularCourses(Integer limit) {
        // 实现热门课程排行导出
        return new byte[0]; // 简化实现
    }

    @Override
    public byte[] exportTopStudents(String semester, Integer limit) {
        // 实现优秀学生排行导出
        return new byte[0]; // 简化实现
    }

    @Override
    public byte[] exportComprehensiveReport(String reportType, Map<String, Object> params) {
        // 实现综合报表导出
        return new byte[0]; // 简化实现
    }

    // 辅助方法
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private int addUserStatistics(Sheet sheet, StatisticsOverview.UserStatistics userStats, int startRow, 
                                  CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        // 用户统计标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("用户统计");
        titleCell.setCellStyle(headerStyle);
        
        // 用户统计数据
        addDataRow(sheet, rowNum++, "总用户数", userStats.getTotalUsers(), "", dataStyle);
        addDataRow(sheet, rowNum++, "学生数量", userStats.getStudentCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "教师数量", userStats.getTeacherCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "管理员数量", userStats.getAdminCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "活跃用户", userStats.getActiveUsers(), "", dataStyle);
        addDataRow(sheet, rowNum++, "本月新增", userStats.getNewUsersThisMonth(), "", dataStyle);
        addDataRow(sheet, rowNum++, "活跃率", userStats.getActiveRate() + "%", "", dataStyle);
        
        return rowNum;
    }

    private int addCourseStatistics(Sheet sheet, StatisticsOverview.CourseStatistics courseStats, int startRow,
                                   CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        // 课程统计标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("课程统计");
        titleCell.setCellStyle(headerStyle);
        
        // 课程统计数据
        addDataRow(sheet, rowNum++, "总课程数", courseStats.getTotalCourses(), "", dataStyle);
        addDataRow(sheet, rowNum++, "开放课程", courseStats.getOpenCourses(), "", dataStyle);
        addDataRow(sheet, rowNum++, "关闭课程", courseStats.getClosedCourses(), "", dataStyle);
        addDataRow(sheet, rowNum++, "必修课程", courseStats.getRequiredCourses(), "", dataStyle);
        addDataRow(sheet, rowNum++, "选修课程", courseStats.getElectiveCourses(), "", dataStyle);
        addDataRow(sheet, rowNum++, "总选课次数", courseStats.getTotalSelections(), "", dataStyle);
        addDataRow(sheet, rowNum++, "平均选课人数", courseStats.getAvgStudentsPerCourse(), "", dataStyle);
        
        return rowNum;
    }

    private int addGradeStatistics(Sheet sheet, StatisticsOverview.GradeStatistics gradeStats, int startRow,
                                  CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        // 成绩统计标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("成绩统计");
        titleCell.setCellStyle(headerStyle);
        
        // 成绩统计数据
        addDataRow(sheet, rowNum++, "总成绩记录", gradeStats.getTotalGrades(), "", dataStyle);
        addDataRow(sheet, rowNum++, "已录入成绩", gradeStats.getGradedCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "未录入成绩", gradeStats.getUngradedCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "平均成绩", gradeStats.getAverageScore(), "", dataStyle);
        addDataRow(sheet, rowNum++, "及格人数", gradeStats.getPassedCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "不及格人数", gradeStats.getFailedCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "及格率", gradeStats.getPassRate() + "%", "", dataStyle);
        addDataRow(sheet, rowNum++, "优秀率", gradeStats.getExcellentRate() + "%", "", dataStyle);
        
        return rowNum;
    }

    private int addAnnouncementStatistics(Sheet sheet, StatisticsOverview.AnnouncementStatistics announcementStats, 
                                         int startRow, CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        // 公告统计标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("公告统计");
        titleCell.setCellStyle(headerStyle);
        
        // 公告统计数据
        addDataRow(sheet, rowNum++, "总公告数", announcementStats.getTotalAnnouncements(), "", dataStyle);
        addDataRow(sheet, rowNum++, "已发布公告", announcementStats.getPublishedCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "草稿公告", announcementStats.getDraftCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "置顶公告", announcementStats.getTopCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "本月发布", announcementStats.getPublishedThisMonth(), "", dataStyle);
        addDataRow(sheet, rowNum++, "总阅读次数", announcementStats.getTotalReadCount(), "", dataStyle);
        addDataRow(sheet, rowNum++, "平均阅读次数", announcementStats.getAvgReadCount(), "", dataStyle);
        
        return rowNum;
    }

    private void addDataRow(Sheet sheet, int rowNum, String label, Object value, String description, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value.toString() : "0");
        valueCell.setCellStyle(style);
        
        if (description != null && !description.isEmpty()) {
            Cell descCell = row.createCell(2);
            descCell.setCellValue(description);
            descCell.setCellStyle(style);
        }
    }
}

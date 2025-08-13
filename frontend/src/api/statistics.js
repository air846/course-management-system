import { request } from '@/utils/request'
import service from '@/utils/request'

export const statisticsApi = {
  // 获取统计概览
  getStatisticsOverview() {
    return request.get('/statistics/overview')
  },

  // 获取用户统计数据
  getUserStatistics() {
    return request.get('/statistics/users')
  },

  // 获取课程统计数据
  getCourseStatistics() {
    return request.get('/statistics/courses')
  },

  // 获取成绩统计数据
  getGradeStatistics() {
    return request.get('/statistics/grades')
  },

  // 获取公告统计数据
  getAnnouncementStatistics() {
    return request.get('/statistics/announcements')
  },

  // 获取用户增长趋势
  getUserGrowthTrend(params) {
    return request.get('/statistics/trends/users', params)
  },

  // 获取课程选课趋势
  getCourseSelectionTrend(params) {
    return request.get('/statistics/trends/course-selections', params)
  },

  // 获取成绩分布图表
  getGradeDistributionChart(params) {
    return request.get('/statistics/charts/grade-distribution', params)
  },

  // 获取课程类别分布图表
  getCourseCategoryChart() {
    return request.get('/statistics/charts/course-category')
  },

  // 获取用户角色分布图表
  getUserRoleChart() {
    return request.get('/statistics/charts/user-role')
  },

  // 获取公告类型分布图表
  getAnnouncementTypeChart() {
    return request.get('/statistics/charts/announcement-type')
  },

  // 获取月度活跃数据图表
  getMonthlyActivityChart(params) {
    return request.get('/statistics/charts/monthly-activity', params)
  },

  // 获取热门课程排行
  getPopularCourses(params) {
    return request.get('/statistics/rankings/popular-courses', params)
  },

  // 获取优秀学生排行
  getTopStudents(params) {
    return request.get('/statistics/rankings/top-students', params)
  },

  // 获取教师课程统计
  getTeacherCourseStatistics(params) {
    return request.get('/statistics/teachers/course-statistics', params)
  },

  // 获取学期对比数据
  getSemesterComparison(params) {
    return request.get('/statistics/comparisons/semester', params)
  },

  // 获取系统使用情况统计
  getSystemUsageStatistics(params) {
    return request.get('/statistics/system-usage', params)
  },

  // 导出统计概览报表
  exportStatisticsOverview() {
    return service.get('/statistics/export/overview', {
      responseType: 'blob',
      timeout: 60000
    })
  },

  // 导出用户统计报表
  exportUserStatistics(params) {
    return service.get('/statistics/export/users', {
      params: params,
      responseType: 'blob',
      timeout: 60000
    })
  },

  // 导出课程统计报表
  exportCourseStatistics(params) {
    return service.get('/statistics/export/courses', {
      params: params,
      responseType: 'blob',
      timeout: 60000
    })
  },

  // 导出成绩统计报表
  exportGradeStatistics(params) {
    return service.get('/statistics/export/grades', {
      params: params,
      responseType: 'blob',
      timeout: 60000
    })
  },

  // 导出公告统计报表
  exportAnnouncementStatistics(params) {
    return service.get('/statistics/export/announcements', {
      params: params,
      responseType: 'blob',
      timeout: 60000
    })
  }
}

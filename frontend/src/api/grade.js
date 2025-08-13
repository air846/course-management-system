import { request } from '@/utils/request'

export const gradeApi = {
  // 录入或更新成绩
  saveOrUpdateGrade(data) {
    return request.post('/grades', data)
  },

  // 批量录入成绩
  batchSaveGrades(data) {
    return request.post('/grades/batch', data)
  },

  // 删除成绩
  deleteGrade(id) {
    return request.delete(`/grades/${id}`)
  },

  // 查询成绩详情
  getGradeById(id) {
    return request.get(`/grades/${id}`)
  },

  // 查询学生成绩列表
  getStudentGrades(studentId, params) {
    return request.get(`/grades/student/${studentId}`, params)
  },

  // 查询我的成绩
  getMyGrades(params) {
    return request.get('/grades/my', params)
  },

  // 查询课程成绩列表
  getCourseGrades(courseId, params) {
    return request.get(`/grades/course/${courseId}`, params)
  },

  // 分页查询成绩列表
  getGradePage(params) {
    return request.get('/grades', params)
  },

  // 查询课程成绩统计
  getCourseStatistics(courseId, params) {
    return request.get(`/grades/statistics/course/${courseId}`, params)
  },

  // 查询学生平均成绩
  getStudentAverage(studentId, params) {
    return request.get(`/grades/average/student/${studentId}`, params)
  },

  // 查询课程成绩分布
  getCourseGradeDistribution(courseId, params) {
    return request.get(`/grades/distribution/course/${courseId}`, params)
  },

  // 查询学生排名
  getStudentRanking(studentId, params) {
    return request.get(`/grades/ranking/student/${studentId}`, params)
  }
}

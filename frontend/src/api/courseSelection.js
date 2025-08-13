import { request } from '@/utils/request'

export const courseSelectionApi = {
  // 学生选课
  selectCourse(data) {
    return request.post('/course-selections', data)
  },

  // 学生退课
  dropCourse(courseId, studentId) {
    const params = studentId ? { studentId } : {}
    return request.delete(`/course-selections/${courseId}`, params)
  },

  // 查询学生的选课记录
  getStudentSelections(studentId, params) {
    return request.get(`/course-selections/student/${studentId}`, params)
  },

  // 查询我的选课记录
  getMySelections(params) {
    return request.get('/course-selections/my', params)
  },

  // 查询课程的选课学生列表
  getCourseSelections(courseId, params) {
    return request.get(`/course-selections/course/${courseId}`, params)
  },

  // 分页查询选课记录
  getSelectionPage(params) {
    return request.get('/course-selections', params)
  },

  // 检查是否可以选课
  canSelectCourse(courseId, studentId) {
    const params = studentId ? { studentId } : {}
    return request.get(`/course-selections/check/${courseId}`, params)
  },

  // 统计学生选课数量
  countStudentSelections(studentId, params) {
    return request.get(`/course-selections/count/student/${studentId}`, params)
  },

  // 统计课程选课数量
  countCourseSelections(courseId, params) {
    return request.get(`/course-selections/count/course/${courseId}`, params)
  }
}

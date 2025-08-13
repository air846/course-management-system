import { request } from '@/utils/request'

export const courseApi = {
  // 创建课程
  createCourse(data) {
    return request.post('/courses', data)
  },

  // 获取课程详情
  getCourseById(id) {
    return request.get(`/courses/${id}`)
  },

  // 更新课程信息
  updateCourse(id, data) {
    return request.put(`/courses/${id}`, data)
  },

  // 删除课程
  deleteCourse(id) {
    return request.delete(`/courses/${id}`)
  },

  // 分页查询课程
  getCoursePage(params) {
    return request.get('/courses', params)
  },

  // 根据学期查询课程
  getCoursesBySemester(semester, params) {
    return request.get(`/courses/semester/${semester}`, params)
  },

  // 查询可选课程
  getAvailableCourses(params) {
    return request.get('/courses/available', params)
  },

  // 根据教师查询课程
  getCoursesByTeacher(teacherId, params) {
    return request.get(`/courses/teacher/${teacherId}`, params)
  },

  // 更新课程状态
  updateCourseStatus(id, status) {
    return request.put(`/courses/${id}/status`, { status })
  },

  // 复制课程到新学期
  copyCourse(id, data) {
    return request.post(`/courses/${id}/copy`, data)
  },

  // 获取课程分类列表
  getCategoryList() {
    return request.get('/courses/categories')
  },

  // 检查课程编码是否可用
  checkCourseCode(courseCode, excludeId) {
    return request.get('/courses/check/code', { courseCode, excludeId })
  },

  // 统计教师课程数量
  getTeacherCourseCount(teacherId) {
    return request.get(`/courses/teacher/${teacherId}/count`)
  }
}

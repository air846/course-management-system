import { request } from '@/utils/request'

export const userApi = {
  // 创建用户
  createUser(data) {
    return request.post('/users', data)
  },

  // 获取用户详情
  getUserById(id) {
    return request.get(`/users/${id}`)
  },

  // 更新用户信息
  updateUser(id, data) {
    return request.put(`/users/${id}`, data)
  },

  // 删除用户
  deleteUser(id) {
    return request.delete(`/users/${id}`)
  },

  // 分页查询用户
  getUserPage(params) {
    return request.get('/users', params)
  },

  // 修改密码
  changePassword(id, data) {
    return request.put(`/users/${id}/password`, data)
  },

  // 重置密码
  resetPassword(id, data) {
    return request.put(`/users/${id}/password/reset`, data)
  },

  // 更新用户状态
  updateUserStatus(id, status) {
    return request.put(`/users/${id}/status`, { status })
  },

  // 根据角色查询用户
  getUsersByRole(roleCode, params) {
    return request.get(`/users/role/${roleCode}`, params)
  },

  // 检查用户名是否可用
  checkUsername(username, excludeId) {
    return request.get('/users/check/username', { username, excludeId })
  }
}

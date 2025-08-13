import { request } from '@/utils/request'

export const dashboardApi = {
  // 获取仪表盘统计数据
  getStats() {
    return request.get('/dashboard/stats')
  },

  // 获取用户角色分布统计
  getUserRoleStats() {
    return request.get('/dashboard/user-role-stats')
  }
}

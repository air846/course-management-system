import { request } from '@/utils/request'

export const authApi = {
  // 用户登录
  login(data) {
    return request.post('/auth/login', data)
  },

  // 用户登出
  logout() {
    return request.post('/auth/logout')
  },

  // 刷新令牌
  refreshToken(data) {
    return request.post('/auth/refresh', data)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request.get('/auth/me')
  },

  // 检查令牌有效性
  checkToken() {
    return request.get('/auth/check')
  }
}

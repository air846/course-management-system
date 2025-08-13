import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Cookies from 'js-cookie'
import { authApi } from '@/api/auth'

const TOKEN_KEY = 'course_token'
const REFRESH_TOKEN_KEY = 'course_refresh_token'
const USER_INFO_KEY = 'course_user_info'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(Cookies.get(TOKEN_KEY) || '')
  const refreshToken = ref(Cookies.get(REFRESH_TOKEN_KEY) || '')
  const userInfo = ref(JSON.parse(localStorage.getItem(USER_INFO_KEY) || 'null'))
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.roleCodes?.includes('ADMIN'))
  const isTeacher = computed(() => userInfo.value?.roleCodes?.includes('TEACHER'))
  const isStudent = computed(() => userInfo.value?.roleCodes?.includes('STUDENT'))

  // 登录
  const login = async (loginForm) => {
    loading.value = true
    try {
      const response = await authApi.login(loginForm)
      
      // 保存token
      token.value = response.accessToken
      refreshToken.value = response.refreshToken
      userInfo.value = response.userInfo
      
      // 持久化存储
      Cookies.set(TOKEN_KEY, response.accessToken, { expires: 7 })
      Cookies.set(REFRESH_TOKEN_KEY, response.refreshToken, { expires: 7 })
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(response.userInfo))
      
      return response
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      if (token.value) {
        await authApi.logout()
      }
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地数据
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      
      Cookies.remove(TOKEN_KEY)
      Cookies.remove(REFRESH_TOKEN_KEY)
      localStorage.removeItem(USER_INFO_KEY)
    }
  }

  // 刷新token
  const refreshAccessToken = async () => {
    if (!refreshToken.value) {
      throw new Error('没有刷新令牌')
    }
    
    try {
      const response = await authApi.refreshToken({
        refreshToken: refreshToken.value
      })
      
      token.value = response.accessToken
      Cookies.set(TOKEN_KEY, response.accessToken, { expires: 7 })
      
      return response.accessToken
    } catch (error) {
      // 刷新失败，清除所有认证信息
      await logout()
      throw error
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    try {
      const user = await authApi.getCurrentUser()
      userInfo.value = user
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(user))
      return user
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 检查认证状态
  const checkAuth = async () => {
    if (!token.value) {
      return false
    }
    
    try {
      await authApi.checkToken()
      return true
    } catch (error) {
      // token无效，尝试刷新
      try {
        await refreshAccessToken()
        return true
      } catch (refreshError) {
        await logout()
        return false
      }
    }
  }

  // 检查权限
  const hasPermission = (permission) => {
    if (!userInfo.value || !userInfo.value.roleCodes) {
      return false
    }
    
    // 管理员拥有所有权限
    if (userInfo.value.roleCodes.includes('ADMIN')) {
      return true
    }
    
    // 检查具体权限
    return userInfo.value.roleCodes.includes(permission)
  }

  // 检查角色
  const hasRole = (role) => {
    if (!userInfo.value || !userInfo.value.roleCodes) {
      return false
    }
    
    return userInfo.value.roleCodes.includes(role)
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    loading,
    
    // 计算属性
    isLoggedIn,
    isAdmin,
    isTeacher,
    isStudent,
    
    // 方法
    login,
    logout,
    refreshAccessToken,
    getCurrentUser,
    checkAuth,
    hasPermission,
    hasRole
  }
})

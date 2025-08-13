import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ 
  showSpinner: false,
  minimum: 0.2,
  speed: 500
})

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 使用代理
  timeout: 15000,
  withCredentials: false,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 开始进度条
    NProgress.start()
    
    // 添加认证token
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    
    return config
  },
  error => {
    NProgress.done()
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    NProgress.done()
    
    const { data } = response
    
    // 如果是文件下载等特殊响应，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 统一处理响应格式
    if (data.code === 200) {
      return data.data
    } else if (data.code === 401) {
      // 未认证，清除token并跳转到登录页
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
      ElMessage.error(data.message || '登录已过期，请重新登录')
      return Promise.reject(new Error(data.message || '未认证'))
    } else if (data.code === 403) {
      // 权限不足
      ElMessage.error(data.message || '权限不足')
      return Promise.reject(new Error(data.message || '权限不足'))
    } else {
      // 其他错误
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  error => {
    NProgress.done()
    
    console.error('响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          ElMessage.error('登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default service

// 封装常用的请求方法
export const request = {
  get(url, params = {}) {
    return service.get(url, { params })
  },
  
  post(url, data = {}) {
    return service.post(url, data)
  },
  
  put(url, data = {}) {
    return service.put(url, data)
  },
  
  delete(url, params = {}) {
    return service.delete(url, { params })
  },
  
  upload(url, formData) {
    return service.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  download(url, params = {}) {
    return service.get(url, {
      params,
      responseType: 'blob'
    })
  }
}

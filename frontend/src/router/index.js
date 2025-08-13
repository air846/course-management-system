import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '仪表盘',
          icon: 'House'
        }
      },
      {
        path: '/users',
        name: 'UserManagement',
        component: () => import('@/views/user/UserList.vue'),
        meta: {
          title: '用户管理',
          icon: 'User',
          roles: ['ADMIN']
        }
      },
      {
        path: '/courses',
        name: 'CourseManagement',
        component: () => import('@/views/course/CourseList.vue'),
        meta: {
          title: '课程管理',
          icon: 'Reading',
          roles: ['ADMIN', 'TEACHER']
        }
      },
      {
        path: '/course-selection',
        name: 'CourseSelection',
        component: () => import('@/views/course/CourseSelection.vue'),
        meta: {
          title: '选课管理',
          icon: 'DocumentAdd',
          roles: ['STUDENT']
        }
      },
      {
        path: '/course-students/:courseId',
        name: 'CourseStudents',
        component: () => import('@/views/course/CourseStudents.vue'),
        meta: {
          title: '选课学生',
          icon: 'User',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true // 隐藏在菜单中，通过其他页面跳转访问
        }
      },
      {
        path: '/my-grades',
        name: 'MyGrades',
        component: () => import('@/views/grade/MyGrades.vue'),
        meta: {
          title: '我的成绩',
          icon: 'TrendCharts',
          roles: ['STUDENT']
        }
      },
      {
        path: '/grade-management/:courseId',
        name: 'GradeManagement',
        component: () => import('@/views/grade/GradeManagement.vue'),
        meta: {
          title: '成绩管理',
          icon: 'EditPen',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true // 隐藏在菜单中，通过其他页面跳转访问
        }
      },
      {
        path: '/announcements',
        name: 'AnnouncementList',
        component: () => import('@/views/announcement/AnnouncementList.vue'),
        meta: {
          title: '通知公告',
          icon: 'Bell',
          roles: ['ADMIN', 'TEACHER', 'STUDENT']
        }
      },
      {
        path: '/announcement-management',
        name: 'AnnouncementManagement',
        component: () => import('@/views/announcement/AnnouncementManagement.vue'),
        meta: {
          title: '公告管理',
          icon: 'EditPen',
          roles: ['ADMIN', 'TEACHER']
        }
      },
      {
        path: '/statistics-dashboard',
        name: 'StatisticsDashboard',
        component: () => import('@/views/statistics/StatisticsDashboard.vue'),
        meta: {
          title: '数据统计',
          icon: 'DataAnalysis',
          roles: ['ADMIN', 'TEACHER']
        }
      },
      {
        path: '/statistics-reports',
        name: 'StatisticsReports',
        component: () => import('@/views/statistics/StatisticsReports.vue'),
        meta: {
          title: '统计报表',
          icon: 'Document',
          roles: ['ADMIN', 'TEACHER']
        }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心',
          icon: 'UserFilled'
        }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '权限不足'
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const authStore = useAuthStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 课程管理系统` : '课程管理系统'
  
  // 检查是否需要认证
  if (to.meta.requiresAuth !== false) {
    if (!authStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    // 检查认证状态
    const isValid = await authStore.checkAuth()
    if (!isValid) {
      ElMessage.error('登录已过期，请重新登录')
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      const hasRole = to.meta.roles.some(role => authStore.hasRole(role))
      if (!hasRole) {
        ElMessage.error('权限不足')
        next('/403')
        return
      }
    }
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router

<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">仪表盘</h1>
      <p>欢迎使用课程管理系统，{{ userInfo?.realName || userInfo?.username }}！</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :xs="24" :md="12">
        <el-card class="quick-actions" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="actions-grid">
            <div 
              v-for="action in quickActions" 
              :key="action.title"
              class="action-item"
              @click="handleQuickAction(action)"
            >
              <el-icon :size="32" :color="action.color">
                <component :is="action.icon" />
              </el-icon>
              <span>{{ action.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="recent-activities" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <div class="activities-list">
            <div 
              v-for="activity in recentActivities" 
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-icon">
                <el-icon :color="activity.color">
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card class="system-info" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="当前用户">{{ userInfo?.username }}</el-descriptions-item>
            <el-descriptions-item label="用户角色">
              <el-tag 
                v-for="role in userInfo?.roles" 
                :key="role.id"
                :type="getRoleTagType(role.roleCode)"
                size="small"
                class="mr-5"
              >
                {{ role.roleName }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="登录时间">{{ loginTime }}</el-descriptions-item>
            <el-descriptions-item label="API文档">
              <el-link type="primary" href="/swagger-ui/index.html" target="_blank">
                查看API文档
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="技术栈">
              Spring Boot + Vue 3 + Element Plus
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { dashboardApi } from '@/api/dashboard'

const router = useRouter()
const authStore = useAuthStore()

const userInfo = computed(() => authStore.userInfo)
const loginTime = ref(new Date().toLocaleString())

// 统计数据
const stats = ref([
  {
    title: '总用户数',
    value: '0',
    icon: 'User',
    color: '#409eff'
  },
  {
    title: '总课程数',
    value: '0',
    icon: 'Reading',
    color: '#67c23a'
  },
  {
    title: '在线用户',
    value: '0',
    icon: 'UserFilled',
    color: '#e6a23c'
  },
  {
    title: '系统消息',
    value: '0',
    icon: 'Bell',
    color: '#f56c6c'
  }
])

// 加载统计数据
const loadStats = async () => {
  try {
    // utils/request.js 的响应拦截器已将成功响应直接返回 data
    // 因此这里拿到的就是后端 data 对象，而不是带有 code/message 的包装
    const data = await dashboardApi.getStats()
    if (data) {
      stats.value[0].value = String(data.totalUsers ?? 0)
      stats.value[1].value = String(data.totalCourses ?? 0)
      stats.value[2].value = String(data.onlineUsers ?? 0)
      stats.value[3].value = String(data.systemMessages ?? 0)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 快捷操作
const quickActions = computed(() => {
  const actions = []
  
  if (authStore.hasRole('ADMIN')) {
    actions.push(
      {
        title: '添加用户',
        icon: 'UserFilled',
        color: '#409eff',
        action: 'addUser'
      },
      {
        title: '用户管理',
        icon: 'User',
        color: '#67c23a',
        action: 'userManagement'
      }
    )
  }
  
  if (authStore.hasRole('ADMIN') || authStore.hasRole('TEACHER')) {
    actions.push(
      {
        title: '添加课程',
        icon: 'Plus',
        color: '#e6a23c',
        action: 'addCourse'
      },
      {
        title: '课程管理',
        icon: 'Reading',
        color: '#f56c6c',
        action: 'courseManagement'
      }
    )
  }
  
  return actions
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    title: '用户 张三 注册了账号',
    time: '2分钟前',
    icon: 'User',
    color: '#409eff'
  },
  {
    id: 2,
    title: '课程 《Java程序设计》 已发布',
    time: '10分钟前',
    icon: 'Reading',
    color: '#67c23a'
  },
  {
    id: 3,
    title: '系统进行了安全更新',
    time: '1小时前',
    icon: 'Lock',
    color: '#e6a23c'
  },
  {
    id: 4,
    title: '数据库备份完成',
    time: '2小时前',
    icon: 'Folder',
    color: '#909399'
  }
])

// 处理快捷操作
const handleQuickAction = (action) => {
  switch (action.action) {
    case 'addUser':
      ElMessage.info('添加用户功能开发中...')
      break
    case 'userManagement':
      router.push('/users')
      break
    case 'addCourse':
      ElMessage.info('添加课程功能开发中...')
      break
    case 'courseManagement':
      router.push('/courses')
      break
    default:
      ElMessage.info('功能开发中...')
  }
}

// 获取角色标签类型
const getRoleTagType = (roleCode) => {
  switch (roleCode) {
    case 'ADMIN':
      return 'danger'
    case 'TEACHER':
      return 'warning'
    case 'STUDENT':
      return 'info'
    default:
      return ''
  }
}

onMounted(() => {
  // 加载统计数据
  loadStats()
  console.log('仪表盘加载完成')
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.stats-row {
  margin-bottom: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  font-weight: 600;
  color: #303133;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
  transform: translateY(-2px);
}

.action-item span {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}

.activities-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 12px;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.mr-5 {
  margin-right: 5px;
}

/* 响应式 */
@media (max-width: 768px) {
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-item {
    padding: 16px;
  }
}
</style>

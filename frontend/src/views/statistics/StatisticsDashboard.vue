<template>
  <div class="statistics-dashboard">
    <div class="page-header">
      <el-page-header @back="$router.go(-1)" content="数据统计" />
    </div>

    <div class="page-content">
    
    <!-- 统计概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-number">{{ overview.userStatistics?.totalUsers || 0 }}</div>
              <div class="card-label">总用户数</div>
              <div class="card-trend">
                <span class="trend-text">本月新增: {{ overview.userStatistics?.newUsersThisMonth || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon course-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-number">{{ overview.courseStatistics?.totalCourses || 0 }}</div>
              <div class="card-label">总课程数</div>
              <div class="card-trend">
                <span class="trend-text">开放课程: {{ overview.courseStatistics?.openCourses || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon grade-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-number">{{ overview.gradeStatistics?.averageScore || 0 }}</div>
              <div class="card-label">平均成绩</div>
              <div class="card-trend">
                <span class="trend-text">及格率: {{ overview.gradeStatistics?.passRate || 0 }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon announcement-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-number">{{ overview.announcementStatistics?.totalAnnouncements || 0 }}</div>
              <div class="card-label">总公告数</div>
              <div class="card-trend">
                <span class="trend-text">本月发布: {{ overview.announcementStatistics?.publishedThisMonth || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 月度活跃数据 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>月度活跃数据</span>
              <el-select v-model="selectedYear" @change="loadMonthlyActivity" style="width: 100px">
                <el-option label="2024" value="2024" />
                <el-option label="2025" value="2025" />
              </el-select>
            </div>
          </template>
          <div id="monthlyChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      
      <!-- 用户角色分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>用户角色分布</span>
          </template>
          <div id="userRoleChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 课程类别分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>课程类别分布</span>
          </template>
          <div id="courseCategoryChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      
      <!-- 成绩分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>成绩分布</span>
              <el-select v-model="selectedSemester" @change="loadGradeDistribution" style="width: 120px">
                <el-option label="全部学期" value="" />
                <el-option label="2024春季" value="2024春季" />
                <el-option label="2024秋季" value="2024秋季" />
                <el-option label="2025春季" value="2025春季" />
              </el-select>
            </div>
          </template>
          <div id="gradeDistributionChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计表格 -->
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>详细统计数据</span>
              <div class="header-buttons">
                <el-button type="primary" @click="handleExport">导出报表</el-button>
                <el-button @click="handleRefresh">刷新数据</el-button>
              </div>
            </div>
          </template>

          <div class="stats-tabs-container">
            <el-tabs v-model="activeTab" @tab-change="handleTabChange">
              <el-tab-pane label="用户统计" name="users">
                <div class="stats-grid">
                  <div class="stat-item">
                    <div class="stat-label">学生数量</div>
                    <div class="stat-value">{{ overview.userStatistics?.studentCount || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">教师数量</div>
                    <div class="stat-value">{{ overview.userStatistics?.teacherCount || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">管理员数量</div>
                    <div class="stat-value">{{ overview.userStatistics?.adminCount || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">活跃用户</div>
                    <div class="stat-value">{{ overview.userStatistics?.activeUsers || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">活跃率</div>
                    <div class="stat-value">{{ overview.userStatistics?.activeRate || 0 }}%</div>
                  </div>
                  <!-- 添加更多统计项目来测试滚动 -->
                  <div class="stat-item">
                    <div class="stat-label">本月新增用户</div>
                    <div class="stat-value">{{ overview.userStatistics?.newUsersThisMonth || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">用户总数</div>
                    <div class="stat-value">{{ overview.userStatistics?.totalUsers || 0 }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">活跃用户数</div>
                    <div class="stat-value">{{ overview.userStatistics?.activeUsers || 0 }}</div>
                  </div>
                </div>
              </el-tab-pane>
            
            <el-tab-pane label="课程统计" name="courses">
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">必修课程</div>
                  <div class="stat-value">{{ overview.courseStatistics?.requiredCourses || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">选修课程</div>
                  <div class="stat-value">{{ overview.courseStatistics?.electiveCourses || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">总选课次数</div>
                  <div class="stat-value">{{ overview.courseStatistics?.totalSelections || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">平均选课人数</div>
                  <div class="stat-value">{{ overview.courseStatistics?.avgStudentsPerCourse || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">本学期新增</div>
                  <div class="stat-value">{{ overview.courseStatistics?.newCoursesThisSemester || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">总课程数</div>
                  <div class="stat-value">{{ overview.courseStatistics?.totalCourses || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">开放课程</div>
                  <div class="stat-value">{{ overview.courseStatistics?.openCourses || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">关闭课程</div>
                  <div class="stat-value">{{ overview.courseStatistics?.closedCourses || 0 }}</div>
                </div>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="成绩统计" name="grades">
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">总成绩记录</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.totalGrades || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">已录入成绩</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.gradedCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">及格人数</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.passedCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">不及格人数</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.failedCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">优秀率</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.excellentRate || 0 }}%</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">平均成绩</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.averageScore || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">及格率</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.passRate || 0 }}%</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">未录入成绩</div>
                  <div class="stat-value">{{ overview.gradeStatistics?.ungradedCount || 0 }}</div>
                </div>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="公告统计" name="announcements">
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">已发布公告</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.publishedCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">草稿公告</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.draftCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">置顶公告</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.topCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">总阅读次数</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.totalReadCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">平均阅读次数</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.avgReadCount || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">总公告数</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.totalAnnouncements || 0 }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">本月发布</div>
                  <div class="stat-value">{{ overview.announcementStatistics?.publishedThisMonth || 0 }}</div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
          </div>
        </el-card>
      </el-col>
    </el-row>

    </div> <!-- 关闭 page-content -->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { User, Reading, TrendCharts, Bell } from '@element-plus/icons-vue'
import { statisticsApi } from '@/api/statistics'
import * as echarts from 'echarts'

// 响应式数据
const loading = ref(false)
const overview = ref({})
const selectedYear = ref('2024')
const selectedSemester = ref('')
const activeTab = ref('users')

// 图表实例
const chartInstances = reactive({
  monthly: null,
  userRole: null,
  courseCategory: null,
  gradeDistribution: null
})

// 页面初始化
onMounted(() => {
  loadOverviewData()
  loadCharts()
})

// 组件卸载时销毁图表
onUnmounted(() => {
  Object.values(chartInstances).forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })
})

// 加载概览数据
const loadOverviewData = async () => {
  loading.value = true
  try {
    const response = await statisticsApi.getStatisticsOverview()
    overview.value = response
  } catch (error) {
    console.error('加载统计概览失败:', error)
    ElMessage.error('加载统计概览失败')
  } finally {
    loading.value = false
  }
}

// 加载所有图表
const loadCharts = () => {
  loadMonthlyActivity()
  loadUserRoleChart()
  loadCourseCategoryChart()
  loadGradeDistribution()
}

// 加载月度活跃数据图表
const loadMonthlyActivity = async () => {
  try {
    const response = await statisticsApi.getMonthlyActivityChart({ year: selectedYear.value })
    renderChart('monthlyChart', response, 'monthly')
  } catch (error) {
    console.error('加载月度活跃数据失败:', error)
  }
}

// 加载用户角色分布图表
const loadUserRoleChart = async () => {
  try {
    const response = await statisticsApi.getUserRoleChart()
    renderChart('userRoleChart', response, 'userRole')
  } catch (error) {
    console.error('加载用户角色分布失败:', error)
  }
}

// 加载课程类别分布图表
const loadCourseCategoryChart = async () => {
  try {
    const response = await statisticsApi.getCourseCategoryChart()
    renderChart('courseCategoryChart', response, 'courseCategory')
  } catch (error) {
    console.error('加载课程类别分布失败:', error)
  }
}

// 加载成绩分布图表
const loadGradeDistribution = async () => {
  try {
    const params = selectedSemester.value ? { semester: selectedSemester.value } : {}
    const response = await statisticsApi.getGradeDistributionChart(params)
    renderChart('gradeDistributionChart', response, 'gradeDistribution')
  } catch (error) {
    console.error('加载成绩分布失败:', error)
  }
}

// 渲染图表
const renderChart = (containerId, chartData, instanceKey) => {
  const chartDom = document.getElementById(containerId)
  if (!chartDom) return

  if (chartInstances[instanceKey]) {
    chartInstances[instanceKey].dispose()
  }

  chartInstances[instanceKey] = echarts.init(chartDom)

  let option = {}

  if (chartData.type === 'line') {
    option = {
      title: { text: chartData.title, left: 'center' },
      tooltip: { trigger: 'axis' },
      legend: { bottom: 0 },
      xAxis: { type: 'category', data: chartData.labels },
      yAxis: { type: 'value' },
      series: chartData.series.map(series => ({
        name: series.name,
        type: series.type || 'line',
        data: series.data,
        smooth: true
      }))
    }
  } else if (chartData.type === 'pie') {
    const pieData = chartData.labels.map((label, index) => ({
      name: label,
      value: chartData.series[0].data[index]
    }))

    option = {
      title: { text: chartData.title, left: 'center' },
      tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        name: chartData.series[0].name,
        type: 'pie',
        radius: '60%',
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
  } else if (chartData.type === 'bar') {
    option = {
      title: { text: chartData.title, left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: chartData.labels },
      yAxis: { type: 'value' },
      series: chartData.series.map(series => ({
        name: series.name,
        type: 'bar',
        data: series.data,
        itemStyle: { color: series.color || '#409EFF' }
      }))
    }
  }

  if (chartInstances[instanceKey]) {
    chartInstances[instanceKey].setOption(option)
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
}

// 导出报表
const handleExport = async () => {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在生成报表，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const response = await statisticsApi.exportStatisticsOverview()

    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `统计概览报表_${new Date().toISOString().split('T')[0]}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('报表导出成功！')
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  } finally {
    loadingInstance.close()
  }
}

// 刷新数据
const handleRefresh = () => {
  loadOverviewData()
  loadCharts()
}
</script>

<style scoped>
.statistics-dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  flex-shrink: 0;
  padding: 20px 20px 0 20px;
  background: white;
  z-index: 10;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px 20px 40px 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
}

.card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.grade-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.announcement-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-info {
  flex: 1;
}

.card-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.card-trend {
  font-size: 12px;
}

.trend-text {
  color: #67C23A;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.table-row {
  margin-bottom: 20px;
}

.stats-tabs-container {
  /* 移除固定高度，让内容自然展开 */
}

.stats-tabs-container .el-tabs__content {
  /* 移除固定高度限制 */
}

.stats-tabs-container .el-tab-pane {
  /* 移除固定高度限制 */
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fafafa;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
</style>

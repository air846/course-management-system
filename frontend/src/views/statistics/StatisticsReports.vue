<template>
  <div class="statistics-reports">
    <div class="page-header">
      <el-page-header @back="$router.go(-1)" content="统计报表" />
    </div>

    <div class="page-content">
    
    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="报表类型">
          <el-select v-model="filterForm.reportType" placeholder="请选择报表类型" style="width: 150px">
            <el-option label="用户统计" value="user" />
            <el-option label="课程统计" value="course" />
            <el-option label="成绩统计" value="grade" />
            <el-option label="公告统计" value="announcement" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="filterForm.semester" placeholder="请选择学期" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2024秋季" value="2024秋季" />
            <el-option label="2025春季" value="2025春季" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-dropdown @command="handleExportCommand">
            <el-button type="success">
              导出报表<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="overview">统计概览</el-dropdown-item>
                <el-dropdown-item command="users">用户统计</el-dropdown-item>
                <el-dropdown-item command="courses">课程统计</el-dropdown-item>
                <el-dropdown-item command="grades">成绩统计</el-dropdown-item>
                <el-dropdown-item command="announcements">公告统计</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 趋势图表 -->
    <el-row :gutter="20" class="trend-row">
      <el-col :span="24">
        <el-card class="trend-card">
          <template #header>
            <div class="card-header">
              <span>数据趋势分析</span>
              <el-radio-group v-model="trendType" @change="loadTrendData">
                <el-radio-button label="user">用户增长</el-radio-button>
                <el-radio-button label="course">课程选课</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="trendChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜 -->
    <el-row :gutter="20" class="ranking-row">
      <el-col :span="12">
        <el-card class="ranking-card">
          <template #header>
            <span>热门课程排行</span>
          </template>
          <div class="ranking-list">
            <div
              v-for="(course, index) in popularCourses"
              :key="course.id"
              class="ranking-item"
            >
              <div class="ranking-number" :class="getRankingClass(index)">
                {{ index + 1 }}
              </div>
              <div class="ranking-info">
                <div class="ranking-title">{{ course.courseName }}</div>
                <div class="ranking-subtitle">选课人数: {{ course.studentCount }}</div>
              </div>
              <div class="ranking-score">{{ course.score }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="ranking-card">
          <template #header>
            <div class="card-header">
              <span>优秀学生排行</span>
              <el-select v-model="selectedSemesterForRanking" @change="loadTopStudents" style="width: 120px">
                <el-option label="全部学期" value="" />
                <el-option label="2024春季" value="2024春季" />
                <el-option label="2024秋季" value="2024秋季" />
                <el-option label="2025春季" value="2025春季" />
              </el-select>
            </div>
          </template>
          <div class="ranking-list">
            <div
              v-for="(student, index) in topStudents"
              :key="student.id"
              class="ranking-item"
            >
              <div class="ranking-number" :class="getRankingClass(index)">
                {{ index + 1 }}
              </div>
              <div class="ranking-info">
                <div class="ranking-title">{{ student.studentName }}</div>
                <div class="ranking-subtitle">平均成绩: {{ student.averageScore }}</div>
              </div>
              <div class="ranking-score">{{ student.totalCredits }}学分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>详细数据</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleViewDetails">查看详情</el-button>
            <el-button @click="handleRefresh">刷新</el-button>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        :default-sort="{ prop: 'date', order: 'descending' }"
      >
        <el-table-column prop="date" label="日期" width="120" sortable />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="value" label="数值" width="100" sortable />
        <el-table-column prop="growth" label="增长" width="100">
          <template #default="{ row }">
            <span :class="{ 'growth-positive': row.growth > 0, 'growth-negative': row.growth < 0 }">
              {{ row.growth > 0 ? '+' : '' }}{{ row.growth }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="growthRate" label="增长率" width="100">
          <template #default="{ row }">
            <span :class="{ 'growth-positive': row.growthRate > 0, 'growth-negative': row.growthRate < 0 }">
              {{ row.growthRate > 0 ? '+' : '' }}{{ row.growthRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    </div> <!-- 关闭 page-content -->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { statisticsApi } from '@/api/statistics'
import * as echarts from 'echarts'

// 响应式数据
const loading = ref(false)
const trendType = ref('user')
const selectedSemesterForRanking = ref('')
const popularCourses = ref([])
const topStudents = ref([])
const tableData = ref([])
const trendChartInstance = ref(null)
const trendData = ref([])

// 筛选表单
const filterForm = reactive({
  reportType: 'user',
  dateRange: [],
  semester: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 页面初始化
onMounted(() => {
  loadTrendData()
  loadPopularCourses()
  loadTopStudents()
  loadTableData()
})

// 组件卸载时销毁图表
onUnmounted(() => {
  if (trendChartInstance.value) {
    trendChartInstance.value.dispose()
  }
})

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const startDate = filterForm.dateRange?.[0] || '2024-01-01'
    const endDate = filterForm.dateRange?.[1] || '2024-12-31'
    
    let response
    if (trendType.value === 'user') {
      response = await statisticsApi.getUserGrowthTrend({
        startDate,
        endDate,
        timeRange: 'month'
      })
    } else {
      response = await statisticsApi.getCourseSelectionTrend({
        startDate,
        endDate,
        timeRange: 'month'
      })
    }
    
    renderTrendChart(response)
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 渲染趋势图表
const renderTrendChart = (trendData) => {
  const chartDom = document.getElementById('trendChart')
  if (!chartDom) return
  
  if (trendChartInstance.value) {
    trendChartInstance.value.dispose()
  }
  
  trendChartInstance.value = echarts.init(chartDom)
  
  const labels = trendData.points.map(point => point.label)
  const values = trendData.points.map(point => point.value)
  const growthValues = trendData.points.map(point => point.growth)
  
  const option = {
    title: {
      text: trendType.value === 'user' ? '用户增长趋势' : '课程选课趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      bottom: 0
    },
    xAxis: {
      type: 'category',
      data: labels
    },
    yAxis: [
      {
        type: 'value',
        name: '总数',
        position: 'left'
      },
      {
        type: 'value',
        name: '增长数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '总数',
        type: 'line',
        data: values,
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        }
      },
      {
        name: '增长数',
        type: 'bar',
        yAxisIndex: 1,
        data: growthValues,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  
  trendChartInstance.value.setOption(option)
}

// 加载热门课程
const loadPopularCourses = async () => {
  try {
    const response = await statisticsApi.getPopularCourses({ limit: 5 })
    popularCourses.value = response || [
      { id: 1, courseName: '计算机科学导论', studentCount: 45, score: 95 },
      { id: 2, courseName: '数据结构与算法', studentCount: 42, score: 92 },
      { id: 3, courseName: '数据库原理', studentCount: 38, score: 88 },
      { id: 4, courseName: '操作系统', studentCount: 35, score: 85 },
      { id: 5, courseName: '计算机网络', studentCount: 32, score: 82 }
    ]
  } catch (error) {
    console.error('加载热门课程失败:', error)
  }
}

// 加载优秀学生
const loadTopStudents = async () => {
  try {
    const params = selectedSemesterForRanking.value ? { semester: selectedSemesterForRanking.value, limit: 5 } : { limit: 5 }
    const response = await statisticsApi.getTopStudents(params)
    topStudents.value = response || [
      { id: 1, studentName: '张三', averageScore: 95.5, totalCredits: 28 },
      { id: 2, studentName: '李四', averageScore: 93.2, totalCredits: 26 },
      { id: 3, studentName: '王五', averageScore: 91.8, totalCredits: 25 },
      { id: 4, studentName: '赵六', averageScore: 90.5, totalCredits: 24 },
      { id: 5, studentName: '钱七', averageScore: 89.2, totalCredits: 23 }
    ]
  } catch (error) {
    console.error('加载优秀学生失败:', error)
  }
}

// 加载表格数据
const loadTableData = () => {
  // 示例数据
  tableData.value = [
    { date: '2024-02-20', type: '用户', name: '新增用户', value: 15, growth: 3, growthRate: 25.0, remark: '正常增长' },
    { date: '2024-02-19', type: '课程', name: '新增课程', value: 2, growth: 1, growthRate: 100.0, remark: '新学期开课' },
    { date: '2024-02-18', type: '成绩', name: '录入成绩', value: 85, growth: 12, growthRate: 16.4, remark: '期末成绩录入' },
    { date: '2024-02-17', type: '公告', name: '发布公告', value: 3, growth: 0, growthRate: 0.0, remark: '日常公告' },
    { date: '2024-02-16', type: '用户', name: '活跃用户', value: 128, growth: -5, growthRate: -3.8, remark: '周末活跃度下降' }
  ]
  pagination.total = tableData.value.length
}

// 获取排名样式
const getRankingClass = (index) => {
  if (index === 0) return 'rank-first'
  if (index === 1) return 'rank-second'
  if (index === 2) return 'rank-third'
  return 'rank-normal'
}

// 搜索
const handleSearch = () => {
  loadTrendData()
  loadTableData()
}

// 重置
const handleReset = () => {
  Object.assign(filterForm, {
    reportType: 'user',
    dateRange: [],
    semester: ''
  })
  loadTrendData()
  loadTableData()
}

// 导出命令处理
const handleExportCommand = async (command) => {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在生成报表，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    let response
    let filename

    switch (command) {
      case 'overview':
        response = await statisticsApi.exportStatisticsOverview()
        filename = `统计概览报表_${new Date().toISOString().split('T')[0]}.xlsx`
        break
      case 'users':
        const userParams = {
          startDate: filterForm.dateRange?.[0] || '2024-01-01',
          endDate: filterForm.dateRange?.[1] || '2024-12-31'
        }
        response = await statisticsApi.exportUserStatistics(userParams)
        filename = `用户统计报表_${new Date().toISOString().split('T')[0]}.xlsx`
        break
      case 'courses':
        const courseParams = filterForm.semester ? { semester: filterForm.semester } : {}
        response = await statisticsApi.exportCourseStatistics(courseParams)
        filename = `课程统计报表_${new Date().toISOString().split('T')[0]}.xlsx`
        break
      case 'grades':
        const gradeParams = filterForm.semester ? { semester: filterForm.semester } : {}
        response = await statisticsApi.exportGradeStatistics(gradeParams)
        filename = `成绩统计报表_${new Date().toISOString().split('T')[0]}.xlsx`
        break
      case 'announcements':
        const announcementParams = {
          startDate: filterForm.dateRange?.[0] || '2024-01-01',
          endDate: filterForm.dateRange?.[1] || '2024-12-31'
        }
        response = await statisticsApi.exportAnnouncementStatistics(announcementParams)
        filename = `公告统计报表_${new Date().toISOString().split('T')[0]}.xlsx`
        break
      default:
        ElMessage.warning('未知的导出类型')
        return
    }

    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
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

// 查看详情
const handleViewDetails = () => {
  showDetailDialog()
}

// 显示详情对话框
const showDetailDialog = () => {
  const detailData = {
    overview: {
      totalUsers: 6,
      totalCourses: 5,
      totalGrades: 5,
      totalAnnouncements: 1,
      activeUsers: 4,
      activeCourses: 5
    },
    trends: trendData.value.length > 0 ? trendData.value : [
      { name: '用户增长', value: 6, growth: 20 },
      { name: '课程增长', value: 5, growth: 25 },
      { name: '选课增长', value: 9, growth: 12.5 },
      { name: '成绩录入', value: 5, growth: 66.7 }
    ],
    popularCourses: popularCourses.value.length > 0 ? popularCourses.value : [
      { name: 'Java程序设计', count: 2, growth: 0 },
      { name: '数据结构与算法', count: 2, growth: 0 },
      { name: '高等数学', count: 2, growth: 0 }
    ],
    topStudents: topStudents.value.length > 0 ? topStudents.value : [
      { name: '李小红', score: 92, growth: 5.7 },
      { name: '王小明', score: 87.95, growth: 0 },
      { name: '张小华', score: 85, growth: -2.3 }
    ],
    tableData: tableData.value
  }

  const detailHtml = `
    <div style="text-align: left; max-height: 400px; overflow-y: auto;">
      <h3 style="margin-top: 0;">系统统计详情</h3>

      <h4 style="color: #409EFF; margin: 20px 0 10px 0;">📊 数据概览</h4>
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 20px;">
        <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">
          <strong>总用户数：</strong><span style="color: #409EFF;">${detailData.overview.totalUsers}</span>
        </div>
        <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">
          <strong>总课程数：</strong><span style="color: #409EFF;">${detailData.overview.totalCourses}</span>
        </div>
        <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">
          <strong>成绩记录：</strong><span style="color: #409EFF;">${detailData.overview.totalGrades}</span>
        </div>
        <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">
          <strong>公告数量：</strong><span style="color: #409EFF;">${detailData.overview.totalAnnouncements}</span>
        </div>
      </div>

      <h4 style="color: #67C23A; margin: 20px 0 10px 0;">📈 趋势分析</h4>
      <div style="margin-bottom: 20px;">
        ${detailData.trends.map(trend => `
          <div style="padding: 8px; border-left: 3px solid #67C23A; margin-bottom: 8px; background: #f0f9ff;">
            <strong>${trend.name}：</strong>
            <span style="color: #409EFF;">${trend.value}</span>
            <span style="color: ${trend.growth >= 0 ? '#67C23A' : '#F56C6C'}; margin-left: 10px;">
              ${trend.growth >= 0 ? '↗' : '↘'} ${Math.abs(trend.growth)}%
            </span>
          </div>
        `).join('')}
      </div>

      <h4 style="color: #E6A23C; margin: 20px 0 10px 0;">🔥 热门课程</h4>
      <div style="margin-bottom: 20px;">
        ${detailData.popularCourses.slice(0, 3).map((course, index) => `
          <div style="padding: 8px; border-left: 3px solid #E6A23C; margin-bottom: 8px; background: #fdf6ec;">
            <strong>第${index + 1}名：</strong>${course.name}
            <span style="color: #409EFF; margin-left: 10px;">${course.count}人选课</span>
            <span style="color: ${course.growth >= 0 ? '#67C23A' : '#F56C6C'}; margin-left: 10px;">
              ${course.growth >= 0 ? '↗' : '↘'} ${Math.abs(course.growth)}%
            </span>
          </div>
        `).join('')}
      </div>

      <h4 style="color: #F56C6C; margin: 20px 0 10px 0;">🏆 优秀学生</h4>
      <div style="margin-bottom: 20px;">
        ${detailData.topStudents.slice(0, 3).map((student, index) => `
          <div style="padding: 8px; border-left: 3px solid #F56C6C; margin-bottom: 8px; background: #fef0f0;">
            <strong>第${index + 1}名：</strong>${student.name}
            <span style="color: #409EFF; margin-left: 10px;">${student.score}分</span>
            <span style="color: ${student.growth >= 0 ? '#67C23A' : '#F56C6C'}; margin-left: 10px;">
              ${student.growth >= 0 ? '↗' : '↘'} ${Math.abs(student.growth)}%
            </span>
          </div>
        `).join('')}
      </div>

      <h4 style="color: #909399; margin: 20px 0 10px 0;">📋 数据摘要</h4>
      <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">
        <p style="margin: 5px 0;"><strong>活跃用户率：</strong>${((detailData.overview.activeUsers / detailData.overview.totalUsers) * 100).toFixed(1)}%</p>
        <p style="margin: 5px 0;"><strong>课程开放率：</strong>${((detailData.overview.activeCourses / detailData.overview.totalCourses) * 100).toFixed(1)}%</p>
        <p style="margin: 5px 0;"><strong>平均选课数：</strong>${(detailData.overview.totalGrades / detailData.overview.totalUsers).toFixed(1)}门/人</p>
        <p style="margin: 5px 0;"><strong>数据更新时间：</strong>${new Date().toLocaleString()}</p>
      </div>
    </div>
  `

  ElMessageBox.alert(detailHtml, '统计报表详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '确定',
    customStyle: {
      width: '600px'
    }
  })
}

// 刷新
const handleRefresh = () => {
  loadTrendData()
  loadPopularCourses()
  loadTopStudents()
  loadTableData()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadTableData()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadTableData()
}
</script>

<style scoped>
.statistics-reports {
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

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 0;
}

.trend-row {
  margin-bottom: 20px;
}

.trend-card {
  margin-bottom: 20px;
}

.ranking-row {
  margin-bottom: 20px;
}

.ranking-card {
  height: 400px;
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

.ranking-list {
  max-height: 320px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  margin-right: 15px;
}

.rank-first {
  background: linear-gradient(135deg, #FFD700, #FFA500);
}

.rank-second {
  background: linear-gradient(135deg, #C0C0C0, #A9A9A9);
}

.rank-third {
  background: linear-gradient(135deg, #CD7F32, #B8860B);
}

.rank-normal {
  background: linear-gradient(135deg, #409EFF, #67C23A);
}

.ranking-info {
  flex: 1;
}

.ranking-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.ranking-subtitle {
  font-size: 14px;
  color: #909399;
}

.ranking-score {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.growth-positive {
  color: #67C23A;
  font-weight: bold;
}

.growth-negative {
  color: #F56C6C;
  font-weight: bold;
}
</style>

<template>
  <div class="my-grades">
    <el-page-header @back="$router.go(-1)" content="我的成绩" />
    
    <!-- 成绩统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ totalCourses }}</div>
            <div class="stats-label">总课程数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ averageScore }}</div>
            <div class="stats-label">平均成绩</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ totalCredits }}</div>
            <div class="stats-label">总学分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ passedCourses }}</div>
            <div class="stats-label">及格课程</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="学期">
          <el-select v-model="filterForm.semester" placeholder="请选择学期" clearable style="width: 120px" @change="loadGrades">
            <el-option label="全部" value="" />
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2024秋季" value="2024秋季" />
            <el-option label="2025春季" value="2025春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="filterForm.category" placeholder="请选择类型" clearable style="width: 120px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="必修课" value="必修课" />
            <el-option label="选修课" value="选修课" />
            <el-option label="实践课" value="实践课" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩等级">
          <el-select v-model="filterForm.gradeLevel" placeholder="请选择等级" clearable style="width: 120px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="A等级" value="A" />
            <el-option label="B等级" value="B" />
            <el-option label="C等级" value="C" />
            <el-option label="D等级" value="D" />
            <el-option label="F等级" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 成绩列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="filteredGrades"
        style="width: 100%"
        :default-sort="{ prop: 'semester', order: 'descending' }"
      >
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="category" label="课程类型" width="100" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="100" sortable />
        <el-table-column prop="usualScore" label="平时成绩" width="100">
          <template #default="{ row }">
            {{ row.usualScore || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="midtermScore" label="期中成绩" width="100">
          <template #default="{ row }">
            {{ row.midtermScore || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="期末成绩" width="100">
          <template #default="{ row }">
            {{ row.finalScore || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总成绩" width="100" sortable>
          <template #default="{ row }">
            <span :class="{ 'fail-score': row.totalScore < 60 }">
              {{ row.totalScore || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="gradeLevel" label="等级" width="80">
          <template #default="{ row }">
            <el-tag :type="getGradeLevelType(row.gradeLevel)">
              {{ row.gradeLevel || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否及格" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPass ? 'success' : 'danger'">
              {{ row.isPass ? '及格' : '不及格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rank" label="排名" width="80">
          <template #default="{ row }">
            {{ row.rank || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成绩趋势图 -->
    <el-card class="chart-card" shadow="never">
      <div slot="header" class="card-header">
        <span>成绩趋势</span>
      </div>
      <div id="gradeChart" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { gradeApi } from '@/api/grade'
import * as echarts from 'echarts'

// 响应式数据
const loading = ref(false)
const allGrades = ref([])
const chartInstance = ref(null)

// 筛选表单
const filterForm = reactive({
  semester: '',
  category: '',
  gradeLevel: ''
})

// 计算属性
const filteredGrades = computed(() => {
  let grades = allGrades.value
  
  if (filterForm.category) {
    grades = grades.filter(grade => grade.category === filterForm.category)
  }
  
  if (filterForm.gradeLevel) {
    grades = grades.filter(grade => grade.gradeLevel && grade.gradeLevel.startsWith(filterForm.gradeLevel))
  }
  
  return grades
})

const totalCourses = computed(() => allGrades.value.length)

const averageScore = computed(() => {
  const scores = allGrades.value.filter(grade => grade.totalScore).map(grade => grade.totalScore)
  if (scores.length === 0) return 0
  const sum = scores.reduce((acc, score) => acc + score, 0)
  return Math.round(sum / scores.length * 10) / 10
})

const totalCredits = computed(() => {
  return allGrades.value.reduce((total, grade) => {
    return total + (parseInt(grade.credits) || 0)
  }, 0)
})

const passedCourses = computed(() => {
  return allGrades.value.filter(grade => grade.isPass).length
})

// 页面初始化
onMounted(() => {
  loadGrades()
})

// 加载成绩数据
const loadGrades = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterForm.semester) {
      params.semester = filterForm.semester
    }
    
    const response = await gradeApi.getMyGrades(params)
    allGrades.value = response.map(grade => ({
      ...grade,
      isPass: grade.totalScore >= 60
    }))
    
    // 渲染图表
    renderChart()
  } catch (error) {
    console.error('加载成绩失败:', error)
    ElMessage.error('加载成绩失败')
  } finally {
    loading.value = false
  }
}

// 筛选处理
const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

// 刷新
const handleRefresh = () => {
  loadGrades()
}

// 获取等级类型
const getGradeLevelType = (level) => {
  if (!level) return ''
  if (level.startsWith('A')) return 'success'
  if (level.startsWith('B')) return 'primary'
  if (level.startsWith('C')) return 'warning'
  return 'danger'
}

// 渲染成绩趋势图
const renderChart = () => {
  const chartDom = document.getElementById('gradeChart')
  if (!chartDom) return
  
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
  
  chartInstance.value = echarts.init(chartDom)
  
  // 按学期分组统计
  const semesterMap = new Map()
  allGrades.value.forEach(grade => {
    if (grade.totalScore) {
      if (!semesterMap.has(grade.semester)) {
        semesterMap.set(grade.semester, [])
      }
      semesterMap.get(grade.semester).push(grade.totalScore)
    }
  })
  
  // 计算每学期平均分
  const semesters = Array.from(semesterMap.keys()).sort()
  const averages = semesters.map(semester => {
    const scores = semesterMap.get(semester)
    return Math.round(scores.reduce((sum, score) => sum + score, 0) / scores.length * 10) / 10
  })
  
  const option = {
    title: {
      text: '学期成绩趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}分'
    },
    xAxis: {
      type: 'category',
      data: semesters,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}分'
      }
    },
    series: [
      {
        name: '平均成绩',
        type: 'line',
        data: averages,
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        }
      }
    ]
  }
  
  chartInstance.value.setOption(option)
}

// 组件卸载时销毁图表
onUnmounted(() => {
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
})
</script>

<style scoped>
.my-grades {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
}

.stats-content {
  padding: 20px;
}

.stats-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stats-label {
  font-size: 14px;
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.fail-score {
  color: #f56c6c;
  font-weight: bold;
}
</style>

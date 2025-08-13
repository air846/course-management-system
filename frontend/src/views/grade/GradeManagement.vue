<template>
  <div class="grade-management">
    <el-page-header @back="$router.go(-1)" content="成绩管理" />
    
    <!-- 课程信息 -->
    <el-card class="course-info-card" shadow="never" v-if="courseInfo">
      <div class="course-info">
        <h3>{{ courseInfo.courseName }}</h3>
        <div class="course-details">
          <el-tag>{{ courseInfo.courseCode }}</el-tag>
          <el-tag type="info">{{ courseInfo.category }}</el-tag>
          <el-tag type="success">{{ courseInfo.credits }}学分</el-tag>
          <el-tag type="warning">{{ currentSemester }}</el-tag>
        </div>
        <div class="course-stats">
          <span>选课人数：{{ courseInfo.currentStudents }}</span>
          <span class="ml-20">已录入成绩：{{ gradedCount }}</span>
          <span class="ml-20">未录入成绩：{{ ungradedCount }}</span>
        </div>
      </div>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="学期">
          <el-select v-model="currentSemester" placeholder="请选择学期" style="width: 120px" @change="loadGradeList">
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2024秋季" value="2024秋季" />
            <el-option label="2025春季" value="2025春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="学生姓名/学号"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="action-buttons">
        <el-button type="success" @click="handleBatchInput">批量录入</el-button>
        <el-button type="info" @click="handleViewStatistics">成绩统计</el-button>
        <el-button type="warning" @click="handleExport">导出成绩</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>
    </el-card>

    <!-- 成绩列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="usualScore" label="平时成绩" width="100">
          <template #default="{ row }">
            <el-input-number
              v-model="row.usualScore"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              @change="handleScoreChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="midtermScore" label="期中成绩" width="100">
          <template #default="{ row }">
            <el-input-number
              v-model="row.midtermScore"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              @change="handleScoreChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="期末成绩" width="100">
          <template #default="{ row }">
            <el-input-number
              v-model="row.finalScore"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              @change="handleScoreChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总成绩" width="100">
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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hasGrade ? 'success' : 'warning'">
              {{ row.hasGrade ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :loading="savingGrades.has(row.studentId)"
              @click="handleSaveGrade(row)"
            >
              保存
            </el-button>
            <el-button
              v-if="row.hasGrade"
              type="danger"
              size="small"
              @click="handleDeleteGrade(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批量录入对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量录入成绩"
      width="600px"
    >
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="平时成绩">
          <el-input-number
            v-model="batchForm.usualScore"
            :min="0"
            :max="100"
            :precision="1"
            placeholder="留空表示不修改"
          />
        </el-form-item>
        <el-form-item label="期中成绩">
          <el-input-number
            v-model="batchForm.midtermScore"
            :min="0"
            :max="100"
            :precision="1"
            placeholder="留空表示不修改"
          />
        </el-form-item>
        <el-form-item label="期末成绩">
          <el-input-number
            v-model="batchForm.finalScore"
            :min="0"
            :max="100"
            :precision="1"
            placeholder="留空表示不修改"
          />
        </el-form-item>
        <el-form-item label="应用范围">
          <el-radio-group v-model="batchForm.applyTo">
            <el-radio label="selected">选中的学生</el-radio>
            <el-radio label="all">所有学生</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchSave">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 成绩统计对话框 -->
    <el-dialog
      v-model="statisticsDialogVisible"
      title="成绩统计"
      width="800px"
    >
      <div v-if="statistics" class="statistics-content">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.totalStudents }}</div>
              <div class="stat-label">总人数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.avgScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.passRate }}%</div>
              <div class="stat-label">及格率</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.excellentRate }}%</div>
              <div class="stat-label">优秀率</div>
            </div>
          </el-col>
        </el-row>
        
        <el-divider />
        
        <h4>成绩分布</h4>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="grade-dist">
              <div class="grade-label">A等级</div>
              <div class="grade-count">{{ statistics.gradeACount }}人</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="grade-dist">
              <div class="grade-label">B等级</div>
              <div class="grade-count">{{ statistics.gradeBCount }}人</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="grade-dist">
              <div class="grade-label">C等级</div>
              <div class="grade-count">{{ statistics.gradeCCount }}人</div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="grade-dist">
              <div class="grade-label">D等级</div>
              <div class="grade-count">{{ statistics.gradeDCount }}人</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="grade-dist">
              <div class="grade-label">F等级</div>
              <div class="grade-count">{{ statistics.gradeFCount }}人</div>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statisticsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi } from '@/api/course'
import { gradeApi } from '@/api/grade'
import { courseSelectionApi } from '@/api/courseSelection'

const route = useRoute()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const courseInfo = ref(null)
const currentSemester = ref('2025春季')
const batchDialogVisible = ref(false)
const statisticsDialogVisible = ref(false)
const statistics = ref(null)
const savingGrades = ref(new Set())

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 批量录入表单
const batchForm = reactive({
  usualScore: null,
  midtermScore: null,
  finalScore: null,
  applyTo: 'selected'
})

// 计算属性
const gradedCount = computed(() => {
  return tableData.value.filter(item => item.hasGrade).length
})

const ungradedCount = computed(() => {
  return tableData.value.length - gradedCount.value
})

// 页面初始化
onMounted(() => {
  const courseId = route.params.courseId || route.query.courseId
  if (courseId) {
    loadCourseInfo(courseId)
    loadGradeList()
  } else {
    ElMessage.error('缺少课程ID参数')
  }
})

// 加载课程信息
const loadCourseInfo = async (courseId) => {
  try {
    const response = await courseApi.getCourseById(courseId)
    courseInfo.value = response
  } catch (error) {
    console.error('加载课程信息失败:', error)
    ElMessage.error('加载课程信息失败')
  }
}

// 加载成绩列表
const loadGradeList = async () => {
  const courseId = route.params.courseId || route.query.courseId
  if (!courseId) return

  loading.value = true
  try {
    // 先获取选课学生列表
    const selections = await courseSelectionApi.getCourseSelections(courseId, { status: 1 })
    
    // 获取已有成绩
    const grades = await gradeApi.getCourseGrades(courseId, { semester: currentSemester.value })
    
    // 合并数据
    const gradeMap = new Map()
    grades.forEach(grade => {
      gradeMap.set(grade.studentId, grade)
    })
    
    tableData.value = selections.map(selection => {
      const grade = gradeMap.get(selection.studentId)
      return {
        studentId: selection.studentId,
        studentName: selection.studentName,
        studentNumber: selection.username,
        usualScore: grade?.usualScore || null,
        midtermScore: grade?.midtermScore || null,
        finalScore: grade?.finalScore || null,
        totalScore: grade?.totalScore || null,
        gradeLevel: grade?.gradeLevel || null,
        hasGrade: !!grade,
        gradeId: grade?.id || null
      }
    })
  } catch (error) {
    console.error('加载成绩列表失败:', error)
    ElMessage.error('加载成绩列表失败')
  } finally {
    loading.value = false
  }
}

// 成绩变化处理
const handleScoreChange = (row) => {
  // 计算总成绩
  const { usualScore, midtermScore, finalScore } = row
  if (usualScore !== null || midtermScore !== null || finalScore !== null) {
    let total = 0
    let weightSum = 0
    
    if (usualScore !== null) {
      total += usualScore * 0.3
      weightSum += 0.3
    }
    if (midtermScore !== null) {
      total += midtermScore * 0.3
      weightSum += 0.3
    }
    if (finalScore !== null) {
      total += finalScore * 0.4
      weightSum += 0.4
    }
    
    if (weightSum > 0) {
      if (weightSum < 1.0) {
        total = total / weightSum
      }
      row.totalScore = Math.round(total * 10) / 10
      
      // 计算等级
      row.gradeLevel = calculateGradeLevel(row.totalScore)
    }
  }
}

// 计算等级
const calculateGradeLevel = (score) => {
  if (score >= 95) return 'A+'
  if (score >= 90) return 'A'
  if (score >= 85) return 'A-'
  if (score >= 82) return 'B+'
  if (score >= 78) return 'B'
  if (score >= 75) return 'B-'
  if (score >= 72) return 'C+'
  if (score >= 68) return 'C'
  if (score >= 65) return 'C-'
  if (score >= 60) return 'D'
  return 'F'
}

// 获取等级类型
const getGradeLevelType = (level) => {
  if (!level) return ''
  if (level.startsWith('A')) return 'success'
  if (level.startsWith('B')) return 'primary'
  if (level.startsWith('C')) return 'warning'
  return 'danger'
}

// 保存成绩
const handleSaveGrade = async (row) => {
  const courseId = route.params.courseId || route.query.courseId
  
  savingGrades.value.add(row.studentId)
  try {
    const gradeData = {
      id: row.gradeId,
      studentId: row.studentId,
      courseId: parseInt(courseId),
      usualScore: row.usualScore,
      midtermScore: row.midtermScore,
      finalScore: row.finalScore,
      semester: currentSemester.value
    }
    
    await gradeApi.saveOrUpdateGrade(gradeData)
    ElMessage.success('成绩保存成功')
    
    // 刷新数据
    loadGradeList()
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error(error.message || '保存成绩失败')
  } finally {
    savingGrades.value.delete(row.studentId)
  }
}

// 删除成绩
const handleDeleteGrade = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除 "${row.studentName}" 的成绩吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await gradeApi.deleteGrade(row.gradeId)
    ElMessage.success('删除成功')
    
    // 刷新数据
    loadGradeList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除成绩失败:', error)
      ElMessage.error(error.message || '删除成绩失败')
    }
  }
}

// 批量录入
const handleBatchInput = () => {
  if (batchForm.applyTo === 'selected' && selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要批量录入的学生')
    return
  }
  batchDialogVisible.value = true
}

// 批量保存
const handleBatchSave = async () => {
  const courseId = route.params.courseId || route.query.courseId
  
  try {
    const targetRows = batchForm.applyTo === 'selected' ? selectedRows.value : tableData.value
    const gradeRequests = targetRows.map(row => ({
      id: row.gradeId,
      studentId: row.studentId,
      courseId: parseInt(courseId),
      usualScore: batchForm.usualScore !== null ? batchForm.usualScore : row.usualScore,
      midtermScore: batchForm.midtermScore !== null ? batchForm.midtermScore : row.midtermScore,
      finalScore: batchForm.finalScore !== null ? batchForm.finalScore : row.finalScore,
      semester: currentSemester.value
    }))
    
    await gradeApi.batchSaveGrades(gradeRequests)
    ElMessage.success('批量录入成功')
    
    batchDialogVisible.value = false
    loadGradeList()
  } catch (error) {
    console.error('批量录入失败:', error)
    ElMessage.error(error.message || '批量录入失败')
  }
}

// 查看统计
const handleViewStatistics = async () => {
  const courseId = route.params.courseId || route.query.courseId
  
  try {
    const response = await gradeApi.getCourseStatistics(courseId, { semester: currentSemester.value })
    statistics.value = response
    statisticsDialogVisible.value = true
  } catch (error) {
    console.error('获取统计信息失败:', error)
    ElMessage.error('获取统计信息失败')
  }
}

// 导出成绩
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 搜索
const handleSearch = () => {
  loadGradeList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  loadGradeList()
}

// 刷新
const handleRefresh = () => {
  const courseId = route.params.courseId || route.query.courseId
  if (courseId) {
    loadCourseInfo(courseId)
  }
  loadGradeList()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}
</script>

<style scoped>
.grade-management {
  padding: 20px;
}

.course-info-card {
  margin-bottom: 20px;
}

.course-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.course-details {
  margin-bottom: 10px;
}

.course-details .el-tag {
  margin-right: 8px;
}

.course-stats {
  color: #666;
  font-size: 14px;
}

.ml-20 {
  margin-left: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.search-form {
  display: inline-block;
}

.action-buttons {
  float: right;
}

.table-card {
  margin-bottom: 20px;
}

.fail-score {
  color: #f56c6c;
  font-weight: bold;
}

.statistics-content {
  padding: 20px 0;
}

.stat-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.grade-dist {
  text-align: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 10px;
}

.grade-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.grade-count {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
</style>

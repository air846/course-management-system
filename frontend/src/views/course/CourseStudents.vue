<template>
  <div class="course-students">
    <el-page-header @back="$router.go(-1)" content="选课学生管理" />

    <!-- 课程信息 -->
    <el-card class="course-info-card" shadow="never" v-if="courseInfo">
      <div class="course-info">
        <h3>{{ courseInfo.courseName }}</h3>
        <div class="course-details">
          <el-tag>{{ courseInfo.courseCode }}</el-tag>
          <el-tag type="info">{{ courseInfo.category }}</el-tag>
          <el-tag type="success">{{ courseInfo.credits }}学分</el-tag>
          <el-tag type="warning">{{ courseInfo.semester }}</el-tag>
        </div>
        <div class="course-stats">
          <span>选课人数：{{ courseInfo.currentStudents }}/{{ courseInfo.maxStudents }}</span>
          <span class="ml-20">教师：{{ courseInfo.teacherName }}</span>
        </div>
      </div>
    </el-card>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="学生姓名/学号"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="已选课" :value="1" />
            <el-option label="已退课" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="action-buttons">
        <el-button type="success" @click="handleExport">导出名单</el-button>
        <el-button type="info" @click="handleRefresh">刷新</el-button>
      </div>
    </el-card>

    <!-- 学生列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="选课ID" width="80" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="选课时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.selectionTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewStudent(row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleRemoveStudent(row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="loadStudentList"
        class="pagination"
      />
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="学生详情"
      width="600px"
    >
      <div v-if="selectedStudent" class="student-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学号">{{ selectedStudent.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ selectedStudent.studentName }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedStudent.email }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ selectedStudent.phone }}</el-descriptions-item>
          <el-descriptions-item label="选课时间">{{ formatDateTime(selectedStudent.selectionTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedStudent.status === 1 ? 'success' : 'warning'">
              {{ selectedStudent.statusText }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi } from '@/api/course'
import { courseSelectionApi } from '@/api/courseSelection'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const courseInfo = ref(null)
const detailDialogVisible = ref(false)
const selectedStudent = ref(null)
// 当前课程ID（解析并校验后保存）
const courseIdRef = ref(null)


// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: 1 // 默认显示已选课的学生
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 页面初始化
onMounted(() => {
  console.log('CourseStudents mounted')
  console.log('route.params:', route.params)
  console.log('route.query:', route.query)

  const idRaw = route.params.courseId || route.query.courseId
  console.log('idRaw:', idRaw)

  const parsedId = Number(idRaw)
  console.log('parsedId:', parsedId)

  if (!parsedId || isNaN(parsedId)) {
    console.error('无效的课程ID:', idRaw)
    ElMessage.error('缺少有效的课程ID参数')
    router.push('/courses')
    return
  }

  courseIdRef.value = parsedId
  console.log('courseIdRef.value:', courseIdRef.value)

  loadCourseInfo()
  loadStudentList()
})

// 加载课程信息
const loadCourseInfo = async () => {
  const courseId = courseIdRef.value
  if (!courseId) return
  try {
    const response = await courseApi.getCourseById(courseId)
    courseInfo.value = response
  } catch (error) {
    console.error('加载课程信息失败:', error)
    ElMessage.error('加载课程信息失败')
  }
}

// 加载学生列表
const loadStudentList = async () => {
  const courseId = courseIdRef.value
  if (!courseId) return

  loading.value = true
  try {
    const params = {
      status: searchForm.status
    }

    const response = await courseSelectionApi.getCourseSelections(courseId, params)
    tableData.value = response || []
    pagination.total = (response && response.length) ? response.length : 0
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadStudentList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = 1
  pagination.current = 1
  loadStudentList()
}

// 刷新
const handleRefresh = () => {
  loadCourseInfo()
  loadStudentList()
}

// 导出名单
const handleExport = () => {
  // 这里可以实现导出功能
  ElMessage.info('导出功能开发中...')
}

// 查看学生详情
const handleViewStudent = (row) => {
  selectedStudent.value = row
  detailDialogVisible.value = true
}

// 移除学生
const handleRemoveStudent = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要移除学生 "${row.studentName}" 吗？`, '确认移除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await courseSelectionApi.dropCourse(row.courseId, row.studentId)
    ElMessage.success('移除成功')

    // 刷新数据
    handleRefresh()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除学生失败:', error)
      ElMessage.error(error.message || '移除学生失败')
    }
  }
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadStudentList()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}
</script>

<style scoped>
.course-students {
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

.search-card {
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

.pagination {
  margin-top: 20px;
  text-align: right;
}

.student-detail {
  padding: 20px 0;
}
</style>

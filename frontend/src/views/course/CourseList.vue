<template>
  <div class="course-list">
    <div class="page-header">
      <h1 class="page-title">课程管理</h1>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="课程名称/课程编码"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable style="width: 120px">
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2024秋季" value="2024秋季" />
            <el-option label="2025春季" value="2025春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="开放" :value="1" />
            <el-option label="关闭" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增课程
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课程表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="hours" label="学时" width="80" />
        <el-table-column prop="semester" label="学期" width="100" />
        <el-table-column prop="maxStudents" label="最大人数" width="100" />
        <el-table-column prop="currentStudents" label="当前人数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleViewStudents(row)">
              选课学生
            </el-button>
            <el-button type="success" size="small" @click="handleGradeManagement(row)">
              成绩管理
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '关闭' : '开放' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
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

    <!-- 课程表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程编码" prop="courseCode">
              <el-input v-model="courseForm.courseCode" placeholder="请输入课程编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-input v-model="courseForm.category" placeholder="请输入课程分类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="courseForm.semester" placeholder="请选择学期" style="width: 100%">
                <el-option label="2024春季" value="2024春季" />
                <el-option label="2024秋季" value="2024秋季" />
                <el-option label="2025春季" value="2025春季" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学分" prop="credits">
              <el-input-number
                v-model="courseForm.credits"
                :min="0.5"
                :max="10"
                :step="0.5"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学时" prop="hours">
              <el-input-number
                v-model="courseForm.hours"
                :min="1"
                :max="200"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number
                v-model="courseForm.maxStudents"
                :min="1"
                :max="500"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教师ID" prop="teacherId">
              <el-input-number
                v-model="courseForm.teacherId"
                :min="1"
                style="width: 100%"
                placeholder="请输入教师ID"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="courseForm.status">
                <el-radio :label="1">开放</el-radio>
                <el-radio :label="0">关闭</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi } from '@/api/course'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const courseFormRef = ref()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  semester: '',
  status: null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 课程表单
const courseForm = reactive({
  id: null,
  courseCode: '',
  courseName: '',
  category: '',
  credits: 1,
  hours: 16,
  description: '',
  teacherId: null,
  maxStudents: 50,
  currentStudents: 0,
  semester: '',
  status: 1
})

// 表单验证规则
const courseRules = {
  courseCode: [
    { required: true, message: '请输入课程编码', trigger: 'blur' },
    { min: 2, max: 20, message: '课程编码长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请输入课程分类', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  teacherId: [
    { required: true, message: '请输入教师ID', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑课程' : '新增课程')

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

// 加载课程列表
const loadCourseList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      semester: searchForm.semester || undefined,
      status: searchForm.status
    }
    
    const response = await courseApi.getCoursePage(params)
    tableData.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadCourseList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.semester = ''
  searchForm.status = null
  pagination.current = 1
  loadCourseList()
}

// 新增课程
const handleAdd = () => {
  isEdit.value = false
  resetCourseForm()
  dialogVisible.value = true
}

// 查看选课学生
const handleViewStudents = (row) => {
  console.log('handleViewStudents called with row:', row)
  console.log('courseId:', row.id)

  router.push({
    name: 'CourseStudents',
    params: { courseId: String(row.id) },
    query: { courseName: row.courseName }
  })
}

// 成绩管理
const handleGradeManagement = (row) => {
  console.log('handleGradeManagement called with row:', row)
  console.log('courseId:', row.id)

  router.push({
    name: 'GradeManagement',
    params: { courseId: String(row.id) },
    query: { courseName: row.courseName }
  })
}

// 编辑课程
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(courseForm, row)
  dialogVisible.value = true
}

// 删除课程
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程 "${row.courseName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await courseApi.deleteCourse(row.id)
    ElMessage.success('删除成功')
    loadCourseList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
    }
  }
}

// 切换课程状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '开放' : '关闭'
  
  try {
    await ElMessageBox.confirm(`确定要${action}课程 "${row.courseName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await courseApi.updateCourseStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadCourseList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新课程状态失败:', error)
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
  loadCourseList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadCourseList()
}

// 提交表单
const handleSubmit = async () => {
  if (!courseFormRef.value) return
  
  try {
    await courseFormRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await courseApi.updateCourse(courseForm.id, courseForm)
      ElMessage.success('更新成功')
    } else {
      await courseApi.createCourse(courseForm)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadCourseList()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetCourseForm()
  if (courseFormRef.value) {
    courseFormRef.value.clearValidate()
  }
}

// 重置课程表单
const resetCourseForm = () => {
  Object.assign(courseForm, {
    id: null,
    courseCode: '',
    courseName: '',
    category: '',
    credits: 1,
    hours: 16,
    description: '',
    teacherId: null,
    maxStudents: 50,
    currentStudents: 0,
    semester: '',
    status: 1
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadCourseList()
})
</script>

<style scoped>
.course-list {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 768px) {
  .search-form .el-form-item {
    margin-bottom: 10px;
  }
  
  .el-table {
    font-size: 12px;
  }
}
</style>

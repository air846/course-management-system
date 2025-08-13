<template>
  <div class="course-selection">
    <el-page-header @back="$router.go(-1)" content="选课管理" />
    
    <!-- 选课统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ selectedCount }}</div>
            <div class="stats-label">已选课程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ availableCount }}</div>
            <div class="stats-label">可选课程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ totalCredits }}</div>
            <div class="stats-label">已选学分</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" class="selection-tabs">
      <!-- 可选课程 -->
      <el-tab-pane label="可选课程" name="available">
        <el-card shadow="never">
          <!-- 搜索栏 -->
          <el-form :model="searchForm" :inline="true" class="search-form">
            <el-form-item label="学期">
              <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable style="width: 120px">
                <el-option label="2024春季" value="2024春季" />
                <el-option label="2024秋季" value="2024秋季" />
                <el-option label="2025春季" value="2025春季" />
              </el-select>
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="searchForm.category" placeholder="请选择分类" clearable style="width: 120px">
                <el-option 
                  v-for="category in categories" 
                  :key="category" 
                  :label="category" 
                  :value="category" 
                />
              </el-select>
            </el-form-item>
            <el-form-item label="关键词">
              <el-input
                v-model="searchForm.keyword"
                placeholder="课程名称/课程编码"
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

          <!-- 可选课程表格 -->
          <el-table
            v-loading="availableLoading"
            :data="availableCourses"
            style="width: 100%"
          >
            <el-table-column prop="courseCode" label="课程编码" width="120" />
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="credits" label="学分" width="80" />
            <el-table-column prop="hours" label="学时" width="80" />
            <el-table-column prop="semester" label="学期" width="100" />
            <el-table-column prop="teacherName" label="教师" width="100" />
            <el-table-column label="选课情况" width="120">
              <template #default="{ row }">
                <span>{{ row.currentStudents }}/{{ row.maxStudents }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '开放' : '关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  :disabled="!canSelect(row)"
                  :loading="selectingCourses.has(row.id)"
                  @click="handleSelectCourse(row)"
                >
                  选课
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            v-model:current-page="availablePagination.current"
            v-model:page-size="availablePagination.size"
            :total="availablePagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleAvailableSizeChange"
            @current-change="loadAvailableCourses"
            class="pagination"
          />
        </el-card>
      </el-tab-pane>

      <!-- 已选课程 -->
      <el-tab-pane label="已选课程" name="selected">
        <el-card shadow="never">
          <!-- 已选课程表格 -->
          <el-table
            v-loading="selectedLoading"
            :data="selectedCourses"
            style="width: 100%"
          >
            <el-table-column prop="courseCode" label="课程编码" width="120" />
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="credits" label="学分" width="80" />
            <el-table-column prop="hours" label="学时" width="80" />
            <el-table-column prop="semester" label="学期" width="100" />
            <el-table-column prop="teacherName" label="教师" width="100" />
            <el-table-column label="选课时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.selectionTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                  {{ row.statusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  size="small"
                  :loading="droppingCourses.has(row.courseId)"
                  @click="handleDropCourse(row)"
                >
                  退课
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi } from '@/api/course'
import { courseSelectionApi } from '@/api/courseSelection'

// 响应式数据
const activeTab = ref('available')
const availableLoading = ref(false)
const selectedLoading = ref(false)
const availableCourses = ref([])
const selectedCourses = ref([])
const categories = ref([])
const selectingCourses = ref(new Set())
const droppingCourses = ref(new Set())

// 搜索表单
const searchForm = reactive({
  keyword: '',
  semester: '2025春季',
  category: ''
})

// 分页
const availablePagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 统计数据
const selectedCount = computed(() => selectedCourses.value.length)
const availableCount = computed(() => availableCourses.value.length)
const totalCredits = computed(() => {
  return selectedCourses.value.reduce((total, course) => {
    return total + (parseInt(course.credits) || 0)
  }, 0)
})

// 页面初始化
onMounted(() => {
  loadCategories()
  loadAvailableCourses()
  loadSelectedCourses()
})

// 加载课程分类
const loadCategories = async () => {
  try {
    const response = await courseApi.getCategoryList()
    categories.value = response || []
  } catch (error) {
    console.error('加载课程分类失败:', error)
  }
}

// 加载可选课程
const loadAvailableCourses = async () => {
  availableLoading.value = true
  try {
    const params = {
      current: availablePagination.current,
      size: availablePagination.size,
      keyword: searchForm.keyword || undefined,
      semester: searchForm.semester || undefined,
      category: searchForm.category || undefined,
      status: 1 // 只显示开放的课程
    }
    
    const response = await courseApi.getCoursePage(params)
    availableCourses.value = response.records || []
    availablePagination.total = response.total || 0
  } catch (error) {
    console.error('加载可选课程失败:', error)
    ElMessage.error('加载可选课程失败')
  } finally {
    availableLoading.value = false
  }
}

// 加载已选课程
const loadSelectedCourses = async () => {
  selectedLoading.value = true
  try {
    const params = {
      semester: searchForm.semester || undefined,
      status: 1 // 只显示已选课程
    }
    
    const response = await courseSelectionApi.getMySelections(params)
    selectedCourses.value = response || []
  } catch (error) {
    console.error('加载已选课程失败:', error)
    ElMessage.error('加载已选课程失败')
  } finally {
    selectedLoading.value = false
  }
}

// 检查是否可以选课
const canSelect = (course) => {
  // 检查课程是否开放
  if (course.status !== 1) {
    return false
  }
  
  // 检查是否已满员
  if (course.currentStudents >= course.maxStudents) {
    return false
  }
  
  // 检查是否已经选过该课程
  const isSelected = selectedCourses.value.some(selected => selected.courseId === course.id)
  if (isSelected) {
    return false
  }
  
  return true
}

// 选课
const handleSelectCourse = async (course) => {
  try {
    await ElMessageBox.confirm(`确定要选择课程 "${course.courseName}" 吗？`, '确认选课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    selectingCourses.value.add(course.id)
    
    await courseSelectionApi.selectCourse({
      courseId: course.id
    })
    
    ElMessage.success('选课成功')
    
    // 刷新数据
    loadAvailableCourses()
    loadSelectedCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('选课失败:', error)
      ElMessage.error(error.message || '选课失败')
    }
  } finally {
    selectingCourses.value.delete(course.id)
  }
}

// 退课
const handleDropCourse = async (selection) => {
  try {
    await ElMessageBox.confirm(`确定要退选课程 "${selection.courseName}" 吗？`, '确认退课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    droppingCourses.value.add(selection.courseId)
    
    await courseSelectionApi.dropCourse(selection.courseId)
    
    ElMessage.success('退课成功')
    
    // 刷新数据
    loadAvailableCourses()
    loadSelectedCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退课失败:', error)
      ElMessage.error(error.message || '退课失败')
    }
  } finally {
    droppingCourses.value.delete(selection.courseId)
  }
}

// 搜索
const handleSearch = () => {
  availablePagination.current = 1
  loadAvailableCourses()
  loadSelectedCourses()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.semester = '2025春季'
  searchForm.category = ''
  availablePagination.current = 1
  loadAvailableCourses()
  loadSelectedCourses()
}

// 分页大小变化
const handleAvailableSizeChange = (size) => {
  availablePagination.size = size
  availablePagination.current = 1
  loadAvailableCourses()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}
</script>

<style scoped>
.course-selection {
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

.selection-tabs {
  margin-top: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>

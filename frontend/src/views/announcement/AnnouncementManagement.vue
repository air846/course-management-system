<template>
  <div class="announcement-management">
    <el-page-header @back="$router.go(-1)" content="公告管理" />
    
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="标题/内容"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="系统公告" :value="1" />
            <el-option label="课程公告" :value="2" />
            <el-option label="考试公告" :value="3" />
            <el-option label="活动公告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已撤回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
        <el-button type="success" :disabled="selectedRows.length === 0" @click="handleBatchPublish">批量发布</el-button>
        <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>
    </el-card>

    <!-- 公告列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="typeText" label="类型" width="100" />
        <el-table-column prop="priorityText" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ row.priorityText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetTypeText" label="目标用户" width="100" />
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column prop="readCount" label="阅读次数" width="100" />
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isTop ? 'warning' : 'info'">
              {{ row.isTop ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160">
          <template #default="{ row }">
            {{ row.publishTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="success" 
              size="small" 
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button 
              v-if="row.status === 1"
              type="warning" 
              size="small" 
              @click="handleWithdraw(row)"
            >
              撤回
            </el-button>
            <el-button 
              :type="row.isTop ? 'warning' : 'info'" 
              size="small" 
              @click="handleToggleTop(row)"
            >
              {{ row.isTop ? '取消置顶' : '置顶' }}
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

    <!-- 公告编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="系统公告" :value="1" />
            <el-option label="课程公告" :value="2" />
            <el-option label="考试公告" :value="3" />
            <el-option label="活动公告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
            <el-option label="紧急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标用户" prop="targetType">
          <el-select v-model="form.targetType" placeholder="请选择目标用户" style="width: 100%">
            <el-option label="全部用户" :value="1" />
            <el-option label="学生" :value="2" />
            <el-option label="教师" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联课程" v-if="form.type === 2">
          <el-select v-model="form.courseId" placeholder="请选择课程" clearable style="width: 100%">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择过期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="info" @click="handleSaveDraft">保存草稿</el-button>
          <el-button type="primary" @click="handleSaveAndPublish">保存并发布</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="公告详情"
      width="800px"
    >
      <div v-if="selectedAnnouncement" class="announcement-detail">
        <div class="detail-header">
          <h2>{{ selectedAnnouncement.title }}</h2>
          <div class="detail-meta">
            <el-tag :type="getPriorityType(selectedAnnouncement.priority)">
              {{ selectedAnnouncement.priorityText }}
            </el-tag>
            <el-tag type="info">{{ selectedAnnouncement.typeText }}</el-tag>
            <el-tag>{{ selectedAnnouncement.targetTypeText }}</el-tag>
            <span class="publish-info">
              {{ selectedAnnouncement.publisherName }} 发布于 {{ selectedAnnouncement.publishTime }}
            </span>
          </div>
        </div>
        <div class="detail-content">
          <div v-html="selectedAnnouncement.content.replace(/\n/g, '<br>')"></div>
        </div>
        <div class="detail-footer">
          <p>阅读次数：{{ selectedAnnouncement.readCount }}</p>
          <p v-if="selectedAnnouncement.expireTime">过期时间：{{ selectedAnnouncement.expireTime }}</p>
        </div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { announcementApi } from '@/api/announcement'
import { courseApi } from '@/api/course'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedAnnouncement = ref(null)
const courseList = ref([])
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  title: '',
  content: '',
  type: 1,
  priority: 2,
  targetType: 1,
  courseId: null,
  isTop: 0,
  expireTime: null
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return form.id ? '编辑公告' : '新增公告'
})

// 页面初始化
onMounted(() => {
  loadAnnouncementList()
  loadCourseList()
})

// 加载公告列表
const loadAnnouncementList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword,
      type: searchForm.type || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    
    const response = await announcementApi.getAnnouncementPage(params)
    tableData.value = response.records
    pagination.total = response.total
  } catch (error) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

// 加载课程列表
const loadCourseList = async () => {
  try {
    const response = await courseApi.getCoursePage({ current: 1, size: 1000 })
    courseList.value = response.records || response
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = { 1: 'info', 2: 'primary', 3: 'warning', 4: 'danger' }
  return types[priority] || 'info'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

// 新增公告
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    title: row.title,
    content: row.content,
    type: row.type,
    priority: row.priority,
    targetType: row.targetType,
    courseId: row.courseId,
    isTop: row.isTop,
    expireTime: row.expireTime
  })
  dialogVisible.value = true
}

// 查看公告
const handleView = (row) => {
  selectedAnnouncement.value = row
  detailDialogVisible.value = true
}

// 发布公告
const handlePublish = async (row) => {
  try {
    await announcementApi.publishAnnouncement(row.id)
    ElMessage.success('发布成功')
    loadAnnouncementList()
  } catch (error) {
    console.error('发布公告失败:', error)
    ElMessage.error(error.message || '发布公告失败')
  }
}

// 撤回公告
const handleWithdraw = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要撤回公告 "${row.title}" 吗？`, '确认撤回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await announcementApi.withdrawAnnouncement(row.id)
    ElMessage.success('撤回成功')
    loadAnnouncementList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤回公告失败:', error)
      ElMessage.error(error.message || '撤回公告失败')
    }
  }
}

// 切换置顶
const handleToggleTop = async (row) => {
  try {
    const isTop = !row.isTop
    await announcementApi.setTopAnnouncement(row.id, isTop)
    ElMessage.success(isTop ? '置顶成功' : '取消置顶成功')
    loadAnnouncementList()
  } catch (error) {
    console.error('设置置顶失败:', error)
    ElMessage.error(error.message || '设置置顶失败')
  }
}

// 删除公告
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除公告 "${row.title}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await announcementApi.deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    loadAnnouncementList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error(error.message || '删除公告失败')
    }
  }
}

// 批量发布
const handleBatchPublish = async () => {
  try {
    const ids = selectedRows.value.map(row => row.id)
    await announcementApi.batchPublishAnnouncements(ids)
    ElMessage.success('批量发布成功')
    loadAnnouncementList()
  } catch (error) {
    console.error('批量发布失败:', error)
    ElMessage.error(error.message || '批量发布失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条公告吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const ids = selectedRows.value.map(row => row.id)
    await announcementApi.batchDeleteAnnouncements(ids)
    ElMessage.success('批量删除成功')
    loadAnnouncementList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    await formRef.value.validate()
    
    const data = {
      ...form,
      status: 0 // 草稿状态
    }
    
    await announcementApi.saveOrUpdateAnnouncement(data)
    ElMessage.success('保存草稿成功')
    dialogVisible.value = false
    loadAnnouncementList()
  } catch (error) {
    if (error.message) {
      console.error('保存草稿失败:', error)
      ElMessage.error(error.message || '保存草稿失败')
    }
  }
}

// 保存并发布
const handleSaveAndPublish = async () => {
  try {
    await formRef.value.validate()
    
    const data = {
      ...form,
      status: 1 // 发布状态
    }
    
    await announcementApi.saveOrUpdateAnnouncement(data)
    ElMessage.success('保存并发布成功')
    dialogVisible.value = false
    loadAnnouncementList()
  } catch (error) {
    if (error.message) {
      console.error('保存并发布失败:', error)
      ElMessage.error(error.message || '保存并发布失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    title: '',
    content: '',
    type: 1,
    priority: 2,
    targetType: 1,
    courseId: null,
    isTop: 0,
    expireTime: null
  })
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadAnnouncementList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    type: '',
    status: ''
  })
  pagination.current = 1
  loadAnnouncementList()
}

// 刷新
const handleRefresh = () => {
  loadAnnouncementList()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadAnnouncementList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadAnnouncementList()
}
</script>

<style scoped>
.announcement-management {
  padding: 20px;
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

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.announcement-detail {
  padding: 20px 0;
}

.detail-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.detail-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.publish-info {
  color: #666;
  font-size: 14px;
}

.detail-content {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #606266;
}

.detail-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
  color: #909399;
  font-size: 14px;
}

.detail-footer p {
  margin: 5px 0;
}
</style>

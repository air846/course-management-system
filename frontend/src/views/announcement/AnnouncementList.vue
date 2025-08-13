<template>
  <div class="announcement-list">
    <el-page-header @back="$router.go(-1)" content="通知公告" />
    
    <!-- 置顶公告 -->
    <el-card v-if="topAnnouncements.length > 0" class="top-announcements-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>📌 置顶公告</span>
        </div>
      </template>
      <div class="top-announcements">
        <div
          v-for="announcement in topAnnouncements"
          :key="announcement.id"
          class="top-announcement-item"
          @click="handleView(announcement)"
        >
          <div class="announcement-title">
            <el-tag :type="getPriorityType(announcement.priority)" size="small">
              {{ announcement.priorityText }}
            </el-tag>
            <span class="title-text">{{ announcement.title }}</span>
          </div>
          <div class="announcement-meta">
            <span>{{ announcement.publisherName }}</span>
            <span>{{ announcement.publishTime }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="关键词">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索标题/内容"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="filterForm.type" placeholder="请选择类型" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="系统公告" :value="1" />
            <el-option label="课程公告" :value="2" />
            <el-option label="考试公告" :value="3" />
            <el-option label="活动公告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleRefresh">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 公告列表 -->
    <el-card class="announcements-card" shadow="never">
      <div v-loading="loading" class="announcements-list">
        <div
          v-for="announcement in announcements"
          :key="announcement.id"
          class="announcement-item"
          @click="handleView(announcement)"
        >
          <div class="announcement-header">
            <div class="announcement-title">
              <el-tag :type="getPriorityType(announcement.priority)" size="small">
                {{ announcement.priorityText }}
              </el-tag>
              <el-tag type="info" size="small">{{ announcement.typeText }}</el-tag>
              <span class="title-text">{{ announcement.title }}</span>
            </div>
            <div class="announcement-meta">
              <span class="publisher">{{ announcement.publisherName }}</span>
              <span class="publish-time">{{ announcement.publishTime }}</span>
              <span class="read-count">阅读 {{ announcement.readCount }}</span>
            </div>
          </div>
          <div class="announcement-content">
            {{ getContentPreview(announcement.content) }}
          </div>
          <div class="announcement-footer">
            <el-tag v-if="announcement.courseName" size="small">
              课程：{{ announcement.courseName }}
            </el-tag>
            <el-tag v-if="announcement.expireTime" type="warning" size="small">
              {{ getExpireText(announcement.expireTime) }}
            </el-tag>
          </div>
        </div>
        
        <div v-if="!loading && announcements.length === 0" class="empty-state">
          <el-empty description="暂无公告" />
        </div>
      </div>
    </el-card>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="公告详情"
      width="800px"
      @open="handleDialogOpen"
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
            <el-tag v-if="selectedAnnouncement.courseName" type="success">
              {{ selectedAnnouncement.courseName }}
            </el-tag>
          </div>
          <div class="publish-info">
            <span>{{ selectedAnnouncement.publisherName }} 发布于 {{ selectedAnnouncement.publishTime }}</span>
            <span class="read-count">阅读次数：{{ selectedAnnouncement.readCount }}</span>
          </div>
        </div>
        <div class="detail-content">
          <div v-html="selectedAnnouncement.content.replace(/\n/g, '<br>')"></div>
        </div>
        <div v-if="selectedAnnouncement.attachmentUrl" class="detail-attachment">
          <h4>附件</h4>
          <el-link :href="selectedAnnouncement.attachmentUrl" target="_blank" type="primary">
            <el-icon><Download /></el-icon>
            {{ selectedAnnouncement.attachmentName || '下载附件' }}
          </el-link>
        </div>
        <div class="detail-footer">
          <p v-if="selectedAnnouncement.expireTime">
            过期时间：{{ selectedAnnouncement.expireTime }}
          </p>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { announcementApi } from '@/api/announcement'

// 响应式数据
const loading = ref(false)
const announcements = ref([])
const topAnnouncements = ref([])
const detailDialogVisible = ref(false)
const selectedAnnouncement = ref(null)

// 筛选表单
const filterForm = reactive({
  keyword: '',
  type: ''
})

// 页面初始化
onMounted(() => {
  loadTopAnnouncements()
  loadAnnouncements()
})

// 加载置顶公告
const loadTopAnnouncements = async () => {
  try {
    const response = await announcementApi.getTopAnnouncements({ limit: 3 })
    topAnnouncements.value = response
  } catch (error) {
    console.error('加载置顶公告失败:', error)
  }
}

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filterForm.keyword || undefined,
      type: filterForm.type || undefined
    }
    
    const response = await announcementApi.getVisibleAnnouncements(params)
    announcements.value = response
  } catch (error) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

// 查看公告详情
const handleView = async (announcement) => {
  try {
    const response = await announcementApi.getAnnouncementById(announcement.id)
    selectedAnnouncement.value = response
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载公告详情失败:', error)
    ElMessage.error('加载公告详情失败')
  }
}

// 对话框打开时的处理
const handleDialogOpen = () => {
  // 可以在这里添加阅读统计等逻辑
}

// 获取内容预览
const getContentPreview = (content) => {
  if (!content) return ''
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}

// 获取过期文本
const getExpireText = (expireTime) => {
  if (!expireTime) return ''
  const now = new Date()
  const expire = new Date(expireTime)
  const diff = expire.getTime() - now.getTime()
  
  if (diff < 0) {
    return '已过期'
  } else if (diff < 24 * 60 * 60 * 1000) {
    return '即将过期'
  } else {
    return `过期时间：${expireTime}`
  }
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = { 1: 'info', 2: 'primary', 3: 'warning', 4: 'danger' }
  return types[priority] || 'info'
}

// 搜索
const handleSearch = () => {
  loadAnnouncements()
}

// 重置搜索
const handleReset = () => {
  Object.assign(filterForm, {
    keyword: '',
    type: ''
  })
  loadAnnouncements()
}

// 刷新
const handleRefresh = () => {
  loadTopAnnouncements()
  loadAnnouncements()
}
</script>

<style scoped>
.announcement-list {
  padding: 20px;
}

.top-announcements-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.top-announcements {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.top-announcement-item {
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.top-announcement-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 0;
}

.announcements-card {
  margin-bottom: 20px;
}

.announcements-list {
  min-height: 400px;
}

.announcement-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.announcement-item:hover {
  background-color: #f5f7fa;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.title-text {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.announcement-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
}

.announcement-footer {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
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
  margin-bottom: 10px;
}

.publish-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}

.detail-content {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #606266;
}

.detail-attachment {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-attachment h4 {
  margin: 0 0 10px 0;
  color: #303133;
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

<template>
  <div class="profile">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>

    <el-row :gutter="20">
      <!-- 用户信息卡片 -->
      <el-col :xs="24" :md="8">
        <el-card class="user-card" shadow="hover">
          <div class="user-info">
            <div class="avatar-section">
              <el-avatar :size="80" :src="userInfo?.avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <el-button type="text" class="change-avatar-btn">
                更换头像
              </el-button>
            </div>

            <div class="user-details">
              <h3>{{ userInfo?.realName || userInfo?.username }}</h3>
              <p class="username">@{{ userInfo?.username }}</p>

              <div class="user-tags">
                <el-tag
                  v-for="role in userInfo?.roles"
                  :key="role.id"
                  :type="getRoleTagType(role.roleCode)"
                  size="small"
                  class="role-tag"
                >
                  {{ role.roleName }}
                </el-tag>
              </div>

              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-label">邮箱</span>
                  <span class="stat-value">{{ userInfo?.email }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">手机</span>
                  <span class="stat-value">{{ userInfo?.phone || '未设置' }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">性别</span>
                  <span class="stat-value">{{ getGenderText(userInfo?.gender) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 个人信息编辑 -->
      <el-col :xs="24" :md="16">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEdit">
                <el-icon><Edit /></el-icon>
                编辑信息
              </el-button>
            </div>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">
              {{ userInfo?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名">
              {{ userInfo?.realName }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userInfo?.email }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userInfo?.phone || '未设置' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ getGenderText(userInfo?.gender) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userInfo?.status === 1 ? 'success' : 'danger'" size="small">
                {{ userInfo?.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="角色" :span="2">
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
          </el-descriptions>
        </el-card>

        <!-- 修改密码 -->
        <el-card class="password-card mt-20" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
            </div>
          </template>

          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            style="max-width: 400px"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
      @close="handleEditDialogClose"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender" @change="handleGenderChange">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">未知</el-radio>
          </el-radio-group>
          <div style="margin-top: 5px; font-size: 12px; color: #999;">
            当前选择: {{ getGenderText(editForm.gender) }} (值: {{ editForm.gender }})
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user'

const authStore = useAuthStore()

// 响应式数据
const passwordLoading = ref(false)
const editLoading = ref(false)
const editDialogVisible = ref(false)
const passwordFormRef = ref()
const editFormRef = ref()

// 用户信息
const userInfo = computed(() => authStore.userInfo)


// 性别文案
const getGenderText = (g) => {
  const v = Number(g)
  if (v === 1) return '男'
  if (v === 2) return '女'
  return '未知'
}

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 编辑信息表单
const editForm = reactive({
  id: null,
  realName: '',
  email: '',
  phone: '',
  gender: 0
})

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 编辑表单验证规则
const editRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
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

// 性别变化处理
const handleGenderChange = (value) => {
  console.log('性别选择变化:', value, '类型:', typeof value)
  console.log('editForm.gender 更新前:', editForm.gender)
  editForm.gender = value
  console.log('editForm.gender 更新后:', editForm.gender)
}

// 监控 editForm.gender 的变化
watch(() => editForm.gender, (newVal, oldVal) => {
  console.log('editForm.gender 监听到变化:', oldVal, '->', newVal)
}, { immediate: true })

// 编辑个人信息
const handleEdit = () => {
  Object.assign(editForm, {
    id: userInfo.value?.id,
    realName: userInfo.value?.realName || '',
    email: userInfo.value?.email || '',
    phone: userInfo.value?.phone || '',
    gender: userInfo.value?.gender ?? 0
  })
  console.log('编辑对话框打开，初始性别值:', editForm.gender)
  editDialogVisible.value = true
}

// 保存编辑
const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    editLoading.value = true

    // 调试：打印发送的数据
    console.log('发送的编辑数据:', editForm)
    console.log('性别值:', editForm.gender, '类型:', typeof editForm.gender)

    await userApi.updateUser(userInfo.value.id, editForm)

    // 更新本地用户信息
    await authStore.getCurrentUser()

    ElMessage.success('信息更新成功')
    editDialogVisible.value = false
  } catch (error) {
    console.error('更新信息失败:', error)
  } finally {
    editLoading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true

    await userApi.changePassword(userInfo.value.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 编辑对话框关闭
const handleEditDialogClose = () => {
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

onMounted(() => {
  // 刷新用户信息
  authStore.getCurrentUser()
})
</script>

<style scoped>
.profile {
  padding: 0;
}

.user-card {
  margin-bottom: 20px;
}

.user-info {
  text-align: center;
}

.avatar-section {
  margin-bottom: 20px;
}

.change-avatar-btn {
  display: block;
  margin: 10px auto 0;
  font-size: 12px;
}

.user-details h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #303133;
}

.username {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}

.user-tags {
  margin-bottom: 20px;
}

.role-tag {
  margin-right: 5px;
}

.user-stats {
  text-align: left;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.password-card {
  margin-bottom: 20px;
}

.mr-5 {
  margin-right: 5px;
}

.mt-20 {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 768px) {
  .user-card {
    margin-bottom: 15px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-header .el-button {
    margin-top: 10px;
  }
}
</style>

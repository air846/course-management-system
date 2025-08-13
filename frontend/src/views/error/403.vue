<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <el-icon :size="120" color="#f56c6c">
          <Lock />
        </el-icon>
      </div>
      <h1 class="error-title">403</h1>
      <p class="error-message">抱歉，您没有权限访问此页面</p>
      <p class="error-description">
        请联系管理员获取相应权限，或返回首页继续浏览
      </p>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">
          <el-icon><House /></el-icon>
          返回首页
        </el-button>
        <el-button @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-content {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 60px 40px;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  max-width: 500px;
}

.error-icon {
  margin-bottom: 20px;
}

.error-title {
  font-size: 72px;
  font-weight: 700;
  color: #f56c6c;
  margin: 0 0 20px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-message {
  font-size: 24px;
  color: #303133;
  margin: 0 0 15px 0;
  font-weight: 500;
}

.error-description {
  font-size: 16px;
  color: #606266;
  margin: 0 0 40px 0;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.error-actions .el-button {
  padding: 12px 24px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .error-content {
    padding: 40px 20px;
    margin: 20px;
  }
  
  .error-title {
    font-size: 60px;
  }
  
  .error-message {
    font-size: 20px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .el-button {
    width: 200px;
  }
}
</style>

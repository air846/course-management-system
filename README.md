# 课程管理系统 (Course Management System)

## 🎓 项目简介
基于 Spring Boot + Vue 3 + Element Plus 的现代化课程管理系统，采用前后端分离架构，支持用户管理、课程管理、角色权限控制等核心功能。

## 🚀 技术栈

### 后端技术
- **Spring Boot 2.7.14** - 主框架
- **Spring Security + JWT** - 安全认证
- **MyBatis Plus** - ORM框架
- **MySQL/H2** - 数据库
- **Swagger 3** - API文档
- **Maven** - 项目管理

### 前端技术
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Vite** - 构建工具
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP客户端

## 项目结构
```
course-management-system/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/com/course/
│   │   ├── controller/     # 控制器层
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # 数据访问层
│   │   ├── entity/         # 实体类
│   │   ├── dto/           # 数据传输对象
│   │   ├── config/        # 配置类
│   │   └── utils/         # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/        # MyBatis映射文件
│   │   ├── application.yml # 配置文件
│   │   └── sql/           # SQL脚本
│   └── pom.xml
└── frontend/               # Vue前端
    ├── src/
    │   ├── components/     # 公共组件
    │   ├── views/         # 页面组件
    │   ├── router/        # 路由配置
    │   ├── store/         # 状态管理
    │   ├── api/           # API接口
    │   └── utils/         # 工具函数
    ├── package.json
    └── vite.config.js
```

## 🛠️ 核心功能模块

### 1. 用户管理
- ✅ 用户注册、登录、登出
- ✅ 用户信息管理（增删改查）
- ✅ 密码修改、重置
- ✅ 用户状态管理
- ✅ 角色权限控制

### 2. 课程管理
- ✅ 课程信息管理（增删改查）
- ✅ 课程分类管理
- ✅ 课程状态控制
- ✅ 学期管理
- ✅ 教师课程关联

### 3. 角色权限
- ✅ 管理员（ADMIN）- 系统管理权限
- ✅ 教师（TEACHER）- 课程管理权限
- ✅ 学生（STUDENT）- 基础查看权限
- ✅ 基于角色的访问控制（RBAC）

### 4. 安全认证
- ✅ JWT无状态认证
- ✅ 密码BCrypt加密
- ✅ 令牌自动刷新
- ✅ 权限拦截器
- ✅ CORS跨域支持

## 📊 开发进度
- ✅ 项目初始化和架构设计
- ✅ 数据库设计和建表
- ✅ 后端API开发（用户管理、课程管理）
- ✅ 安全认证模块（JWT + Spring Security）
- ✅ 前端页面开发（登录、用户管理、课程管理）
- ✅ 前后端集成和联调
- ✅ 系统测试（48个测试用例全部通过）
- ✅ 选课管理模块（学生选课、教师审核）
- ✅ 成绩管理模块（成绩录入、查询、统计）
- ✅ 公告管理模块（公告发布、分类、置顶）
- ✅ 统计分析模块（数据图表、趋势分析）
- ✅ 权限控制完善（基于角色的细粒度权限）
- ✅ API文档完善（Swagger集成）

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+（可选，默认使用H2）
- Maven 3.6+

### 后端启动

1. **克隆项目**
```bash
git clone <repository-url>
cd course-management
```

2. **配置数据库**（可选，默认使用H2内存数据库）
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/course_management
    username: your_username
    password: your_password
```

3. **启动后端**
```bash
cd backend
mvn spring-boot:run
```

4. **访问API文档**
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API文档: http://localhost:8080/v3/api-docs

### 前端启动

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

3. **访问应用**
- 前端地址: http://localhost:3000
- 默认账户: admin / 123456

## 📊 数据库设计

### 核心表结构

#### 用户表 (users)
- id, username, password, email, phone
- real_name, gender, avatar, status
- create_time, update_time, deleted

#### 角色表 (roles)
- id, role_name, role_code, description
- status, create_time, update_time, deleted

#### 课程表 (courses)
- id, course_code, course_name, category
- credits, hours, description, teacher_id
- max_students, current_students, semester
- status, create_time, update_time, deleted

#### 用户角色关联表 (user_roles)
- id, user_id, role_id, create_time

## 🔧 API接口

### 认证接口
- `POST /auth/login` - 用户登录
- `POST /auth/logout` - 用户登出
- `POST /auth/refresh` - 刷新令牌
- `GET /auth/me` - 获取当前用户信息
- `GET /auth/check` - 检查令牌有效性

### 用户管理接口
- `GET /users` - 分页查询用户
- `POST /users` - 创建用户
- `GET /users/{id}` - 获取用户详情
- `PUT /users/{id}` - 更新用户信息
- `DELETE /users/{id}` - 删除用户

### 课程管理接口
- `GET /courses` - 分页查询课程
- `POST /courses` - 创建课程
- `GET /courses/{id}` - 获取课程详情
- `PUT /courses/{id}` - 更新课程信息
- `DELETE /courses/{id}` - 删除课程

## 🧪 测试

### 后端测试
```bash
cd backend
mvn test
```

**测试结果**: 48个测试用例全部通过 ✅
- 安全认证测试: 6个
- Controller层测试: 7个
- Service层测试: 19个
- Mapper层测试: 14个
- 应用启动测试: 2个

### 前端测试
```bash
cd frontend
npm run test
```

## 🎯 使用指南

### 默认账户
系统自动创建的默认账户：

| 角色 | 用户名 | 密码 | 姓名 | 权限说明 |
|------|--------|------|------|----------|
| 管理员 | admin | 123456 | 系统管理员 | 所有功能权限 |
| 教师 | teacher1 | 123456 | 张教授 | 课程和成绩管理 |
| 教师 | teacher2 | 123456 | 李老师 | 课程和成绩管理 |
| 学生 | student1 | 123456 | 王小明 | 选课和查看成绩 |
| 学生 | student2 | 123456 | 李小红 | 选课和查看成绩 |
| 学生 | student3 | 123456 | 张小华 | 选课和查看成绩 |

### 角色权限说明
- **管理员（ADMIN）**: 拥有所有权限，可以管理用户和课程
- **教师（TEACHER）**: 可以管理课程，查看学生信息
- **学生（STUDENT）**: 可以查看课程信息，管理个人资料

### 前端页面
- `/login` - 登录页面
- `/dashboard` - 仪表盘（首页）
- `/users` - 用户管理（仅管理员）
- `/courses` - 课程管理（管理员/教师）
- `/profile` - 个人中心

## 🔮 未来计划
- [ ] 文件上传模块（课程资料、作业提交）
- [ ] 在线考试模块
- [ ] 课程评价系统
- [ ] 消息通知系统
- [ ] 移动端适配
- [ ] Docker容器化部署
- [ ] 微服务架构改造

## 👥 开发团队
- **课程管理系统开发团队**
- 邮箱: dev@course.com

---

**感谢使用课程管理系统！** 🎓

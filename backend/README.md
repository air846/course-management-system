# 课程管理系统后端

## 项目简介
这是课程管理系统的后端服务，基于Spring Boot 2.7.14开发，使用Maven作为构建工具。

## 技术栈
- **框架**: Spring Boot 2.7.14
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.3.1
- **文档**: SpringDoc OpenAPI 3
- **构建工具**: Maven 3.8+
- **Java版本**: Java 17

## 项目结构
```
backend/
├── src/main/java/com/course/
│   ├── CourseManagementApplication.java  # 主启动类
│   ├── entity/                           # 实体类
│   │   ├── BaseEntity.java              # 基础实体
│   │   ├── User.java                    # 用户实体
│   │   ├── Role.java                    # 角色实体
│   │   ├── Course.java                  # 课程实体
│   │   ├── Classroom.java               # 教室实体
│   │   ├── CourseSelection.java         # 选课记录实体
│   │   ├── Schedule.java                # 课表安排实体
│   │   └── Grade.java                   # 成绩实体
│   └── dto/                             # 数据传输对象
│       ├── Result.java                  # 统一响应结果
│       ├── PageRequest.java             # 分页查询请求
│       ├── LoginRequest.java            # 登录请求
│       └── LoginResponse.java           # 登录响应
├── src/main/resources/
│   ├── application.yml                  # 应用配置
│   └── sql/                            # SQL脚本
│       ├── schema.sql                  # 建表脚本
│       └── data.sql                    # 初始化数据
├── src/test/java/                      # 测试代码
├── pom.xml                             # Maven配置
└── start.bat                           # 启动脚本
```

## 环境要求
- Java 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本

## 快速开始

### 1. 数据库准备
1. 确保MySQL服务已启动
2. 创建数据库：
   ```sql
   CREATE DATABASE course_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 执行建表脚本：`src/main/resources/sql/schema.sql`
4. 执行初始化数据脚本：`src/main/resources/sql/data.sql`

### 2. 配置数据库连接
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/course_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
```

### 3. 启动应用

#### 方式一：使用启动脚本（推荐）
```bash
start.bat
```

#### 方式二：使用Maven命令
```bash
mvn spring-boot:run
```

#### 方式三：编译后运行
```bash
mvn clean package
java -jar target/course-management-backend-1.0.0.jar
```

### 4. 验证启动
启动成功后，访问以下地址：
- 应用首页：http://localhost:8080/api
- API文档：http://localhost:8080/api/swagger-ui.html

## 默认用户账号
系统初始化了以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 系统管理员 |
| teacher1 | 123456 | 教师 | 张教授 |
| teacher2 | 123456 | 教师 | 李老师 |
| student1 | 123456 | 学生 | 王小明 |
| student2 | 123456 | 学生 | 李小红 |
| student3 | 123456 | 学生 | 张小华 |

## 开发说明

### 编译项目
```bash
mvn clean compile
```

### 运行测试
```bash
mvn test
```

### 打包项目
```bash
mvn clean package
```

### 清理项目
```bash
mvn clean
```

## 注意事项
1. 首次启动可能需要下载Maven依赖，请确保网络连接正常
2. 确保MySQL服务已启动且数据库已创建
3. 如果遇到端口冲突，可以在application.yml中修改server.port配置
4. 生产环境部署前请修改JWT密钥和数据库密码

## 下一步开发
- [ ] 实现Mapper接口和Service层
- [ ] 开发Controller层API
- [ ] 完善JWT认证和权限控制
- [ ] 添加全局异常处理
- [ ] 实现业务逻辑
- [ ] 编写单元测试

## 联系方式
如有问题，请联系开发团队。

-- 课程管理系统初始化数据
USE course_management;

-- 插入角色数据
INSERT INTO roles (role_name, role_code, description) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('教师', 'TEACHER', '教师用户，可以管理课程和成绩'),
('学生', 'STUDENT', '学生用户，可以选课和查看成绩');

-- 插入用户数据（密码都是123456，已加密）
INSERT INTO users (username, password, email, phone, real_name, gender, status) VALUES
('admin', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'admin@course.com', '13800000001', '系统管理员', 1, 1),
('teacher1', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'teacher1@course.com', '13800000002', '张教授', 1, 1),
('teacher2', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'teacher2@course.com', '13800000003', '李老师', 2, 1),
('student1', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'student1@course.com', '13800000004', '王小明', 1, 1),
('student2', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'student2@course.com', '13800000005', '李小红', 2, 1),
('student3', '$2a$10$7JB720yubVSOfvVWWbXeF.xaWqHezQiDbHn5Lbx8YrYhpXo0q0Rn2', 'student3@course.com', '13800000006', '张小华', 1, 1);

-- 插入用户角色关联数据
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- teacher1 -> TEACHER
(3, 2), -- teacher2 -> TEACHER
(4, 3), -- student1 -> STUDENT
(5, 3), -- student2 -> STUDENT
(6, 3); -- student3 -> STUDENT

-- 插入教室数据
INSERT INTO classrooms (room_number, room_name, building, floor, capacity, equipment, status) VALUES
('A101', '多媒体教室A101', 'A栋', 1, 60, '投影仪,音响,空调', 1),
('A102', '普通教室A102', 'A栋', 1, 50, '黑板,空调', 1),
('A201', '计算机教室A201', 'A栋', 2, 40, '电脑40台,投影仪,空调', 1),
('B101', '大型阶梯教室B101', 'B栋', 1, 120, '投影仪,音响,空调,话筒', 1),
('B201', '实验室B201', 'B栋', 2, 30, '实验设备,投影仪', 1),
('C101', '小型研讨室C101', 'C栋', 1, 20, '圆桌,投影仪,白板', 1);

-- 插入课程数据
INSERT INTO courses (course_code, course_name, category, credits, hours, description, teacher_id, max_students, semester, status) VALUES
('CS001', 'Java程序设计', '计算机科学', 3.0, 48, 'Java语言基础及面向对象编程', 2, 40, '2024春季', 1),
('CS002', '数据结构与算法', '计算机科学', 4.0, 64, '数据结构基础和常用算法', 2, 35, '2024春季', 1),
('MATH001', '高等数学', '数学', 4.0, 64, '微积分基础理论与应用', 3, 60, '2024春季', 1),
('ENG001', '大学英语', '外语', 2.0, 32, '英语听说读写综合训练', 3, 50, '2024春季', 1),
('CS003', '数据库原理', '计算机科学', 3.0, 48, '关系数据库理论与SQL应用', 2, 40, '2024春季', 1);

-- 插入课表安排数据
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, week_start, week_end, semester) VALUES
(1, 1, 1, '08:00:00', '09:40:00', 1, 18, '2024春季'), -- Java程序设计 周一1-2节
(1, 1, 3, '10:00:00', '11:40:00', 1, 18, '2024春季'), -- Java程序设计 周三3-4节
(2, 3, 2, '14:00:00', '15:40:00', 1, 18, '2024春季'), -- 数据结构 周二7-8节
(2, 3, 4, '08:00:00', '09:40:00', 1, 18, '2024春季'), -- 数据结构 周四1-2节
(3, 4, 1, '14:00:00', '15:40:00', 1, 18, '2024春季'), -- 高等数学 周一7-8节
(3, 4, 3, '14:00:00', '15:40:00', 1, 18, '2024春季'), -- 高等数学 周三7-8节
(4, 2, 2, '08:00:00', '09:40:00', 1, 18, '2024春季'), -- 大学英语 周二1-2节
(5, 1, 5, '10:00:00', '11:40:00', 1, 18, '2024春季'); -- 数据库原理 周五3-4节

-- 插入选课记录数据
INSERT INTO course_selections (student_id, course_id, selection_time, status) VALUES
(4, 1, '2024-02-20 09:00:00', 1), -- 王小明选择Java程序设计
(4, 2, '2024-02-20 09:05:00', 1), -- 王小明选择数据结构
(4, 3, '2024-02-20 09:10:00', 1), -- 王小明选择高等数学
(5, 1, '2024-02-20 10:00:00', 1), -- 李小红选择Java程序设计
(5, 3, '2024-02-20 10:05:00', 1), -- 李小红选择高等数学
(5, 4, '2024-02-20 10:10:00', 1), -- 李小红选择大学英语
(6, 2, '2024-02-20 11:00:00', 1), -- 张小华选择数据结构
(6, 4, '2024-02-20 11:05:00', 1), -- 张小华选择大学英语
(6, 5, '2024-02-20 11:10:00', 1); -- 张小华选择数据库原理

-- 更新课程的当前选课人数
UPDATE courses SET current_students = (
    SELECT COUNT(*) FROM course_selections 
    WHERE course_selections.course_id = courses.id AND course_selections.status = 1
);

-- 插入成绩数据（部分课程）
INSERT INTO grades (student_id, course_id, usual_score, midterm_score, final_score, total_score, grade_level, semester) VALUES
(4, 1, 85.0, 88.0, 90.0, 88.0, 'A-', '2024春季'),
(4, 3, 78.0, 82.0, 85.0, 82.0, 'B+', '2024春季'),
(5, 1, 92.0, 90.0, 95.0, 92.0, 'A', '2024春季'),
(5, 4, 88.0, 85.0, 87.0, 87.0, 'A-', '2024春季');

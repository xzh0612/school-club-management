-- 插入测试用户数据
INSERT INTO users (username, password, real_name, role, email, phone) VALUES
('admin', '123456', '系统管理员', 'admin', 'admin@school.edu.cn', '13800138000'),
('teacher1', '123456', '张老师', 'teacher', 'zhang@school.edu.cn', '13800138001'),
('leader1', '123456', '李明', 'club_leader', 'liming@student.school.edu.cn', '13800138002'),
('student1', '123456', '王小红', 'student', 'wangxiaohong@student.school.edu.cn', '13800138003'),
('student2', '123456', '赵小明', 'student', 'zhaoxiaoming@student.school.edu.cn', '13800138004');

-- 插入测试社团数据
INSERT INTO clubs (club_name, description, founder_id, advisor_id, status) VALUES
('计算机协会', '专注于编程和技术分享的学生组织', 3, 2, 'approved'),
('摄影社', '爱好摄影的同学交流学习的平台', 3, 2, 'pending'),
('辩论社', '提高口才和思辨能力的社团', 3, 2, 'approved');

-- 插入测试活动数据
INSERT INTO activities (club_id, title, content, start_time, end_time, location, status) VALUES
(1, 'Java编程入门讲座', '面向初学者的Java基础课程', '2026-04-01 14:00:00', '2026-04-01 16:00:00', '教学楼A101', 'approved'),
(1, '算法竞赛培训', 'ACM竞赛经验分享', '2026-04-08 18:00:00', '2026-04-08 20:00:00', '实验楼301', 'pending_approval'),
(3, '新生辩论赛', '欢迎新生参与的辩论比赛', '2026-04-15 13:00:00', '2026-04-15 17:00:00', '图书馆报告厅', 'draft');

-- 插入测试公告数据
INSERT INTO announcements (title, content, publisher_id, target_type, target_id, is_top) VALUES
('关于社团招新通知', '各社团请注意，本学期招新工作即将开始，请做好相关准备工作。', 1, 'all', NULL, 1),
('计算机协会活动预告', '本周六将举办Java编程入门讲座，欢迎同学们踊跃参加！', 3, 'all', NULL, 0);

-- 插入测试审批数据
INSERT INTO approvals (type, related_id, applicant_id, approver_id, status, comments) VALUES
('club_creation', 2, 3, NULL, 'pending', '等待审批'),
('activity_application', 2, 3, NULL, 'pending', '活动方案需进一步完善');
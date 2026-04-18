-- 清空现有数据（可选）
-- TRUNCATE TABLE club_members;
-- TRUNCATE TABLE activities;
-- TRUNCATE TABLE announcements;
-- TRUNCATE TABLE approvals;
-- TRUNCATE TABLE clubs;
-- TRUNCATE TABLE users;

-- 插入用户数据（包含截图中的用户）
INSERT INTO users (username, password, real_name, role, email, phone) VALUES
-- 管理员
('admin', '123456', '马子健', 'admin', 'mazijian@school.edu.cn', '13800138000'),
('mazijian', '123456', '马子健', 'admin', 'mazijian@school.edu.cn', '13800138000'),

-- 指导老师
('teacher01', '123456', '张老师', 'teacher', 'zhanglaoshi@school.edu.cn', '13800138001'),
('zhangteacher', '123456', '张老师', 'teacher', 'zhanglaoshi@school.edu.cn', '13800138001'),

-- 社团负责人
('leader01', '123456', '奚梓恒', 'club_leader', 'xiziheng@school.edu.cn', '13800138002'),
('xiziheng', '123456', '奚梓恒', 'club_leader', 'xiziheng@school.edu.cn', '13800138002'),
('leader02', '123456', '王宇航', 'club_leader', 'wangyuhang@school.edu.cn', '13800138003'),
('wangyuhang', '123456', '王宇航', 'club_leader', 'wangyuhang@school.edu.cn', '13800138003'),

-- 普通学生
('student01', '123456', '刘硕', 'student', 'liushuo@school.edu.cn', '13800138004'),
('liushuo', '123456', '刘硕', 'student', 'liushuo@school.edu.cn', '13800138004'),
('student02', '123456', '王小明', 'student', 'wangxiaoming@school.edu.cn', '13800138005'),
('student03', '123456', '李小红', 'student', 'lixiaohong@school.edu.cn', '13800138006'),
('student04', '123456', '张伟', 'student', 'zhangwei@school.edu.cn', '13800138007'),
('student05', '123456', '陈静', 'student', 'chenjing@school.edu.cn', '13800138008'),
('student06', '123456', '杨洋', 'student', 'yangyang@school.edu.cn', '13800138009'),
('student07', '123456', '赵敏', 'student', 'zhaomin@school.edu.cn', '13800138010'),
('student08', '123456', '周杰', 'student', 'zhoujie@school.edu.cn', '13800138011'),
('student09', '123456', '吴芳', 'student', 'wufang@school.edu.cn', '13800138012'),
('student10', '123456', '郑强', 'student', 'zhengqiang@school.edu.cn', '13800138013');

-- 插入社团数据
INSERT INTO clubs (club_name, description, founder_id, advisor_id, status) VALUES
('计算机协会', '专注于编程和技术分享的学生组织，定期举办技术讲座和编程竞赛', 3, 2, 'approved'),
('摄影社', '爱好摄影的同学交流学习的平台，组织外拍活动和摄影展览', 6, 2, 'approved'),
('文学社', '热爱文学创作的同学聚集地，开展读书分享和写作交流活动', 7, 2, 'approved'),
('篮球社', '篮球爱好者社团，组织训练和友谊赛', 3, 2, 'approved'),
('音乐社', '音乐爱好者的家园，涵盖各种乐器和声乐', 6, 2, 'pending'),
('辩论社', '提高口才和思辨能力的社团，定期举办辩论赛', 7, 2, 'approved');

-- 插入社团成员关系
INSERT INTO club_members (club_id, user_id, role, status) VALUES
-- 计算机协会成员
(1, 3, 'leader', 'active'),    -- 奚梓恒 - 社长
(1, 6, 'member', 'active'),    -- 王宇航 - 成员
(1, 4, 'member', 'active'),    -- 刘硕 - 成员
(1, 5, 'deputy_leader', 'active'), -- 王小明 - 副社长

-- 摄影社成员
(2, 6, 'leader', 'active'),    -- 王宇航 - 社长
(2, 8, 'member', 'active'),    -- 李小红 - 成员
(2, 9, 'member', 'active'),    -- 张伟 - 成员

-- 文学社成员
(3, 7, 'leader', 'active'),    -- 王宇航 - 社长
(3, 10, 'member', 'active'),   -- 陈静 - 成员
(3, 11, 'member', 'active'),   -- 杨洋 - 成员

-- 篮球社成员
(4, 3, 'leader', 'active'),    -- 奚梓恒 - 社长
(4, 4, 'member', 'active'),    -- 刘硕 - 成员
(4, 12, 'member', 'active'),   -- 赵敏 - 成员

-- 音乐社成员
(5, 6, 'leader', 'active'),    -- 王宇航 - 社长
(5, 13, 'member', 'active'),   -- 周杰 - 成员

-- 辩论社成员
(6, 7, 'leader', 'active'),    -- 王宇航 - 社长
(6, 14, 'member', 'active');   -- 吴芳 - 成员

-- 插入活动数据
INSERT INTO activities (club_id, title, content, start_time, end_time, location, status) VALUES
(1, 'Java编程入门讲座', '面向初学者的Java基础课程，从语法到实战项目', '2026-04-01 14:00:00', '2026-04-01 16:00:00', '教学楼A101', 'approved'),
(1, '算法竞赛培训', 'ACM竞赛经验分享，提高算法思维能力', '2026-04-08 18:00:00', '2026-04-08 20:00:00', '实验楼301', 'pending_approval'),
(1, 'Python数据分析工作坊', '学习使用Python进行数据分析和可视化', '2026-04-15 19:00:00', '2026-04-15 21:00:00', '图书馆电子阅览室', 'draft'),
(2, '春季外拍活动', '春暖花开，组织摄影爱好者外出采风', '2026-04-05 09:00:00', '2026-04-05 17:00:00', '校园及周边景点', 'approved'),
(2, '摄影技巧分享会', '资深摄影师分享人像摄影技巧', '2026-04-12 14:00:00', '2026-04-12 16:00:00', '艺术楼201', 'approved'),
(3, '经典文学作品读书会', '共读《百年孤独》，分享阅读感悟', '2026-04-03 19:00:00', '2026-04-03 21:00:00', '图书馆研讨室', 'approved'),
(4, '新生篮球赛', '欢迎新生参与的篮球比赛', '2026-04-10 13:00:00', '2026-04-10 17:00:00', '体育馆篮球场', 'draft'),
(6, '校园辩论赛', '主题：人工智能是否会取代人类工作', '2026-04-18 14:00:00', '2026-04-18 17:00:00', '教学楼报告厅', 'approved');

-- 插入公告数据
INSERT INTO announcements (title, content, publisher_id, target_type, target_id, is_top, publish_time) VALUES
('关于2026年春季社团年审工作的通知', '各社团请注意，本学期社团年审工作即将开始，请各社团负责人于4月1日前提交年审材料。', 1, 'all', NULL, 1, '2026-03-15 09:00:00'),
('社团管理系统升级维护通知', '系统将于3月25日晚上10点至次日凌晨2点进行升级维护，届时系统将暂停服务。', 1, 'all', NULL, 1, '2026-03-10 10:00:00'),
('2026年度优秀社团评选通知', '2026年度优秀社团评选工作现已启动，请各社团积极申报。评选标准包括活动质量、成员参与度、社会影响等。', 1, 'all', NULL, 1, '2026-03-08 11:00:00'),
('计算机协会编程马拉松报名开启', '一年一度的编程马拉松大赛即将开始，欢迎各位同学组队参赛。比赛时间为4月20日-21日。', 3, 'all', NULL, 0, '2026-03-18 14:00:00'),
('篮球社新生杯赛程公布', '2026年篮球社新生杯赛程已确定，请各参赛队伍准时参加比赛。', 3, 'all', NULL, 0, '2026-03-16 16:00:00'),
('摄影社春季作品展征稿启事', '摄影社将举办春季作品展，现面向全校征集优秀摄影作品，截止日期4月5日。', 6, 'all', NULL, 0, '2026-03-20 09:00:00');

-- 插入审批数据
INSERT INTO approvals (type, related_id, applicant_id, approver_id, status, comments, create_time) VALUES
('club_creation', 5, 6, NULL, 'pending', '音乐社成立申请，等待审批', '2026-03-19 10:00:00'),
('activity_application', 2, 3, NULL, 'pending', '算法竞赛培训活动申请', '2026-03-18 15:00:00'),
('activity_application', 4, 6, NULL, 'pending', '新生篮球赛活动申请', '2026-03-17 11:00:00'),
('activity_application', 7, 3, 1, 'approved', '春季外拍活动已批准', '2026-03-16 09:00:00'),
('activity_application', 8, 7, 1, 'rejected', '辩论赛场地冲突，建议改期', '2026-03-15 14:00:00');
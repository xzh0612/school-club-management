USE `School-Club-Management-System`;

SET @pwd = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

INSERT INTO users(username, password, real_name, role, status, club_id, student_id, department, class_name, register_time, email, phone)
VALUES
('admin', @pwd, '系统管理员', 'admin', 'active', NULL, 'A0001', '信息化办公室', NULL, NOW(), 'admin@school.edu.cn', '13800000001'),
('teacher1', @pwd, '张老师', 'teacher', 'active', NULL, 'T0001', '计算机学院', NULL, NOW(), 'teacher1@school.edu.cn', '13800000002'),
('leader_cs', @pwd, '陈一帆', 'club_leader', 'active', NULL, 'L0001', '计算机学院', '软工2301', NOW(), 'leader_cs@school.edu.cn', '13800000003'),
('leader_photo', @pwd, '林若溪', 'club_leader', 'active', NULL, 'L0002', '艺术学院', '视觉2302', NOW(), 'leader_photo@school.edu.cn', '13800000004'),
('student1', @pwd, '李明', 'student', 'active', NULL, 'S20260001', '计算机学院', '软工2301', NOW(), 'student1@school.edu.cn', '13800000005'),
('student2', @pwd, '王小雨', 'student', 'active', NULL, 'S20260002', '经管学院', '会计2301', NOW(), 'student2@school.edu.cn', '13800000006'),
('student_empty', @pwd, '赵未入社', 'student', 'active', NULL, 'S20260003', '外国语学院', '英语2301', NOW(), 'student_empty@school.edu.cn', '13800000007'),
('disabled_user', @pwd, '停用学生', 'student', 'inactive', NULL, 'S20260004', '计算机学院', '软工2302', NOW(), 'disabled@school.edu.cn', '13800000008')
ON DUPLICATE KEY UPDATE
password = VALUES(password),
real_name = VALUES(real_name),
role = VALUES(role),
status = VALUES(status),
student_id = VALUES(student_id),
department = VALUES(department),
class_name = VALUES(class_name),
email = VALUES(email),
phone = VALUES(phone);

SET @admin_id = (SELECT user_id FROM users WHERE username = 'admin');
SET @teacher_id = (SELECT user_id FROM users WHERE username = 'teacher1');
SET @leader_cs_id = (SELECT user_id FROM users WHERE username = 'leader_cs');
SET @leader_photo_id = (SELECT user_id FROM users WHERE username = 'leader_photo');
SET @student1_id = (SELECT user_id FROM users WHERE username = 'student1');
SET @student2_id = (SELECT user_id FROM users WHERE username = 'student2');
SET @student_empty_id = (SELECT user_id FROM users WHERE username = 'student_empty');

INSERT INTO clubs(club_name, description, club_type, founder_id, advisor_id, status)
VALUES
('计算机协会', '面向全校学生的编程、开源与竞赛交流社团。', 'academic', @leader_cs_id, @teacher_id, 'approved'),
('摄影社', '组织摄影采风、后期交流和校园影像记录。', 'culture', @leader_photo_id, @teacher_id, 'approved'),
('篮球社', '日常训练、院系联赛和新手基础训练。', 'sports', @teacher_id, @teacher_id, 'approved'),
('空白社团', '用于测试无成员、无活动、无招新的边界社团。', 'general', @leader_cs_id, @teacher_id, 'approved'),
('机器人创新社', '待审批的新社团申请，用于审批边界测试。', 'innovation', @student1_id, @teacher_id, 'pending'),
('旧动漫社', '已驳回的历史社团申请，用于状态边界测试。', 'culture', @student2_id, @teacher_id, 'rejected')
ON DUPLICATE KEY UPDATE
description = VALUES(description),
club_type = VALUES(club_type),
founder_id = VALUES(founder_id),
advisor_id = VALUES(advisor_id),
status = VALUES(status);

SET @club_cs_id = (SELECT club_id FROM clubs WHERE club_name = '计算机协会');
SET @club_photo_id = (SELECT club_id FROM clubs WHERE club_name = '摄影社');
SET @club_basket_id = (SELECT club_id FROM clubs WHERE club_name = '篮球社');
SET @club_empty_id = (SELECT club_id FROM clubs WHERE club_name = '空白社团');
SET @club_robot_id = (SELECT club_id FROM clubs WHERE club_name = '机器人创新社');
SET @club_old_id = (SELECT club_id FROM clubs WHERE club_name = '旧动漫社');

UPDATE users SET club_id = @club_cs_id WHERE username = 'leader_cs';
UPDATE users SET club_id = @club_photo_id WHERE username = 'leader_photo';

INSERT INTO club_members(club_id, user_id, role, status)
VALUES
(@club_cs_id, @leader_cs_id, 'leader', 'active'),
(@club_cs_id, @student1_id, 'member', 'active'),
(@club_cs_id, @student2_id, 'deputy_leader', 'active'),
(@club_photo_id, @leader_photo_id, 'leader', 'active'),
(@club_photo_id, @student2_id, 'member', 'active'),
(@club_basket_id, @student1_id, 'member', 'quit')
ON DUPLICATE KEY UPDATE role = VALUES(role), status = VALUES(status);

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_cs_id, '开源项目工作坊', '围绕真实开源仓库完成 Issue 拆解和 Pull Request 演练。', 'workshop', 30, DATE_ADD(NOW(), INTERVAL 5 DAY), '陈一帆', 'leader_cs@school.edu.cn', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 3 HOUR), '实验楼 A305', 'approved'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_cs_id AND title = '开源项目工作坊');
UPDATE activities SET content = '围绕真实开源仓库完成 Issue 拆解和 Pull Request 演练。', type = 'workshop', max_participants = 30, registration_deadline = DATE_ADD(NOW(), INTERVAL 5 DAY), organizer = '陈一帆', contact = 'leader_cs@school.edu.cn', start_time = DATE_ADD(NOW(), INTERVAL 7 DAY), end_time = DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 3 HOUR), location = '实验楼 A305', status = 'approved' WHERE club_id = @club_cs_id AND title = '开源项目工作坊';

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_cs_id, '满员边界测试活动', '仅允许 1 人报名，用于验证人数上限。', 'test', 1, DATE_ADD(NOW(), INTERVAL 2 DAY), '陈一帆', 'leader_cs@school.edu.cn', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), '实验楼 A101', 'approved'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_cs_id AND title = '满员边界测试活动');
UPDATE activities SET content = '仅允许 1 人报名，用于验证人数上限。', type = 'test', max_participants = 1, registration_deadline = DATE_ADD(NOW(), INTERVAL 2 DAY), organizer = '陈一帆', contact = 'leader_cs@school.edu.cn', start_time = DATE_ADD(NOW(), INTERVAL 3 DAY), end_time = DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), location = '实验楼 A101', status = 'approved' WHERE club_id = @club_cs_id AND title = '满员边界测试活动';

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_photo_id, '校园夜景摄影', '夜景拍摄实践与构图分享。', 'practice', 20, DATE_ADD(NOW(), INTERVAL 1 DAY), '林若溪', 'leader_photo@school.edu.cn', DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 2 HOUR), '图书馆广场', 'approved'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_photo_id AND title = '校园夜景摄影');
UPDATE activities SET content = '夜景拍摄实践与构图分享。', type = 'practice', max_participants = 20, registration_deadline = DATE_ADD(NOW(), INTERVAL 1 DAY), organizer = '林若溪', contact = 'leader_photo@school.edu.cn', start_time = DATE_ADD(NOW(), INTERVAL 4 DAY), end_time = DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 2 HOUR), location = '图书馆广场', status = 'approved' WHERE club_id = @club_photo_id AND title = '校园夜景摄影';

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_photo_id, '已结束影展复盘', '历史活动，用于测试 completed 状态不可报名。', 'review', 20, DATE_SUB(NOW(), INTERVAL 15 DAY), '林若溪', 'leader_photo@school.edu.cn', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 14 DAY), INTERVAL 2 HOUR), '艺术楼 201', 'completed'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_photo_id AND title = '已结束影展复盘');
UPDATE activities SET content = '历史活动，用于测试 completed 状态不可报名。', type = 'review', max_participants = 20, registration_deadline = DATE_SUB(NOW(), INTERVAL 15 DAY), organizer = '林若溪', contact = 'leader_photo@school.edu.cn', start_time = DATE_SUB(NOW(), INTERVAL 14 DAY), end_time = DATE_ADD(DATE_SUB(NOW(), INTERVAL 14 DAY), INTERVAL 2 HOUR), location = '艺术楼 201', status = 'completed' WHERE club_id = @club_photo_id AND title = '已结束影展复盘';

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_basket_id, '被取消的友谊赛', '用于测试 cancelled 状态不可报名。', 'competition', 10, DATE_ADD(NOW(), INTERVAL 1 DAY), '篮球社负责人', 'basket@school.edu.cn', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), '体育馆', 'cancelled'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_basket_id AND title = '被取消的友谊赛');
UPDATE activities SET content = '用于测试 cancelled 状态不可报名。', type = 'competition', max_participants = 10, registration_deadline = DATE_ADD(NOW(), INTERVAL 1 DAY), organizer = '篮球社负责人', contact = 'basket@school.edu.cn', start_time = DATE_ADD(NOW(), INTERVAL 2 DAY), end_time = DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), location = '体育馆', status = 'cancelled' WHERE club_id = @club_basket_id AND title = '被取消的友谊赛';

INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status)
SELECT @club_cs_id, '待审批算法讲座', '用于测试 activity_application 审批流。', 'lecture', 50, DATE_ADD(NOW(), INTERVAL 8 DAY), '陈一帆', 'leader_cs@school.edu.cn', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 10 DAY), INTERVAL 2 HOUR), '报告厅', 'pending_approval'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE club_id = @club_cs_id AND title = '待审批算法讲座');
UPDATE activities SET content = '用于测试 activity_application 审批流。', type = 'lecture', max_participants = 50, registration_deadline = DATE_ADD(NOW(), INTERVAL 8 DAY), organizer = '陈一帆', contact = 'leader_cs@school.edu.cn', start_time = DATE_ADD(NOW(), INTERVAL 10 DAY), end_time = DATE_ADD(DATE_ADD(NOW(), INTERVAL 10 DAY), INTERVAL 2 HOUR), location = '报告厅', status = 'pending_approval' WHERE club_id = @club_cs_id AND title = '待审批算法讲座';

SET @activity_open_id = (SELECT activity_id FROM activities WHERE title = '开源项目工作坊' AND club_id = @club_cs_id LIMIT 1);
SET @activity_full_id = (SELECT activity_id FROM activities WHERE title = '满员边界测试活动' AND club_id = @club_cs_id LIMIT 1);
SET @activity_pending_id = (SELECT activity_id FROM activities WHERE title = '待审批算法讲座' AND club_id = @club_cs_id LIMIT 1);

INSERT INTO activity_signups(activity_id, user_id, status)
VALUES
(@activity_open_id, @student1_id, 'approved'),
(@activity_open_id, @student2_id, 'pending'),
(@activity_full_id, @student1_id, 'approved')
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO recruitments(club_id, title, requirements, description, quota, start_time, end_time, status)
SELECT @club_cs_id, '计算机协会 2026 春季招新', '每周至少参加一次技术分享。', '欢迎对编程、开源和竞赛感兴趣的同学加入。', 20, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 14 DAY), 'active'
WHERE NOT EXISTS (SELECT 1 FROM recruitments WHERE club_id = @club_cs_id AND title = '计算机协会 2026 春季招新');
UPDATE recruitments SET requirements = '每周至少参加一次技术分享。', description = '欢迎对编程、开源和竞赛感兴趣的同学加入。', quota = 20, start_time = DATE_SUB(NOW(), INTERVAL 1 DAY), end_time = DATE_ADD(NOW(), INTERVAL 14 DAY), status = 'active' WHERE club_id = @club_cs_id AND title = '计算机协会 2026 春季招新';

INSERT INTO recruitments(club_id, title, requirements, description, quota, start_time, end_time, status)
SELECT @club_photo_id, '摄影社 校园影像组招新', '自备手机或相机均可。', '面向零基础和进阶同学，提供拍摄实践与后期交流。', 12, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 'active'
WHERE NOT EXISTS (SELECT 1 FROM recruitments WHERE club_id = @club_photo_id AND title = '摄影社 校园影像组招新');
UPDATE recruitments SET requirements = '自备手机或相机均可。', description = '面向零基础和进阶同学，提供拍摄实践与后期交流。', quota = 12, start_time = DATE_SUB(NOW(), INTERVAL 2 DAY), end_time = DATE_ADD(NOW(), INTERVAL 7 DAY), status = 'active' WHERE club_id = @club_photo_id AND title = '摄影社 校园影像组招新';

INSERT INTO recruitments(club_id, title, requirements, description, quota, start_time, end_time, status)
SELECT @club_basket_id, '篮球社补招', '能参加周末训练。', '测试 closed 状态的招新计划。', 5, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'closed'
WHERE NOT EXISTS (SELECT 1 FROM recruitments WHERE club_id = @club_basket_id AND title = '篮球社补招');
UPDATE recruitments SET requirements = '能参加周末训练。', description = '测试 closed 状态的招新计划。', quota = 5, start_time = DATE_SUB(NOW(), INTERVAL 20 DAY), end_time = DATE_SUB(NOW(), INTERVAL 2 DAY), status = 'closed' WHERE club_id = @club_basket_id AND title = '篮球社补招';

INSERT INTO recruitments(club_id, title, requirements, description, quota, start_time, end_time, status)
SELECT @club_empty_id, '空白社团边界招新', '暂无。', '用于测试 0 申请、0 成员的招新边界。', 1, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'pending'
WHERE NOT EXISTS (SELECT 1 FROM recruitments WHERE club_id = @club_empty_id AND title = '空白社团边界招新');
UPDATE recruitments SET requirements = '暂无。', description = '用于测试 0 申请、0 成员的招新边界。', quota = 1, start_time = DATE_ADD(NOW(), INTERVAL 1 DAY), end_time = DATE_ADD(NOW(), INTERVAL 10 DAY), status = 'pending' WHERE club_id = @club_empty_id AND title = '空白社团边界招新';

SET @recruit_cs_id = (SELECT recruitment_id FROM recruitments WHERE club_id = @club_cs_id AND title = '计算机协会 2026 春季招新' LIMIT 1);

INSERT INTO applications(user_id, club_id, recruitment_id, introduction, status, comments)
SELECT @student_empty_id, @club_cs_id, @recruit_cs_id, '我还没有加入任何社团，希望加入计算机协会学习开源协作。', 'pending', NULL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE user_id = @student_empty_id AND club_id = @club_cs_id);
UPDATE applications SET recruitment_id = @recruit_cs_id, introduction = '我还没有加入任何社团，希望加入计算机协会学习开源协作。', status = 'pending', comments = NULL WHERE user_id = @student_empty_id AND club_id = @club_cs_id;

INSERT INTO applications(user_id, club_id, recruitment_id, introduction, status, comments)
SELECT @student2_id, @club_photo_id, NULL, '已有摄影基础，希望参与校园影像记录。', 'approved', '符合要求'
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE user_id = @student2_id AND club_id = @club_photo_id);
UPDATE applications SET introduction = '已有摄影基础，希望参与校园影像记录。', status = 'approved', comments = '符合要求' WHERE user_id = @student2_id AND club_id = @club_photo_id;

INSERT INTO applications(user_id, club_id, recruitment_id, introduction, status, comments)
SELECT @student1_id, @club_photo_id, NULL, '时间冲突，申请用于驳回边界数据。', 'rejected', '本学期活动时间不匹配'
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE user_id = @student1_id AND club_id = @club_photo_id);
UPDATE applications SET introduction = '时间冲突，申请用于驳回边界数据。', status = 'rejected', comments = '本学期活动时间不匹配' WHERE user_id = @student1_id AND club_id = @club_photo_id;

INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps, approval_time)
SELECT 'club_creation', @club_robot_id, @student1_id, NULL, 'pending', '申请创建机器人创新社', 1, 2, NULL
WHERE NOT EXISTS (SELECT 1 FROM approvals WHERE type = 'club_creation' AND related_id = @club_robot_id AND applicant_id = @student1_id);
UPDATE approvals SET approver_id = NULL, status = 'pending', comments = '申请创建机器人创新社', current_step = 1, total_steps = 2, approval_time = NULL WHERE type = 'club_creation' AND related_id = @club_robot_id AND applicant_id = @student1_id;

INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps, approval_time)
SELECT 'club_creation', @club_old_id, @student2_id, @teacher_id, 'rejected', '定位不清晰，建议完善指导老师与活动计划', 2, 2, NOW()
WHERE NOT EXISTS (SELECT 1 FROM approvals WHERE type = 'club_creation' AND related_id = @club_old_id AND applicant_id = @student2_id);
UPDATE approvals SET approver_id = @teacher_id, status = 'rejected', comments = '定位不清晰，建议完善指导老师与活动计划', current_step = 2, total_steps = 2, approval_time = NOW() WHERE type = 'club_creation' AND related_id = @club_old_id AND applicant_id = @student2_id;

INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps, approval_time)
SELECT 'activity_application', @activity_pending_id, @leader_cs_id, NULL, 'pending', '申请举办算法讲座', 1, 2, NULL
WHERE NOT EXISTS (SELECT 1 FROM approvals WHERE type = 'activity_application' AND related_id = @activity_pending_id AND applicant_id = @leader_cs_id);
UPDATE approvals SET approver_id = NULL, status = 'pending', comments = '申请举办算法讲座', current_step = 1, total_steps = 2, approval_time = NULL WHERE type = 'activity_application' AND related_id = @activity_pending_id AND applicant_id = @leader_cs_id;

INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, publish_time, is_top, view_count)
SELECT '全校社团开放日通知', '本周五下午在学生活动中心举行社团开放日，各社团负责人请按时布展。', @admin_id, 'all', NULL, 'published', NOW(), 1, 12
WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title = '全校社团开放日通知');
UPDATE announcements SET content = '本周五下午在学生活动中心举行社团开放日，各社团负责人请按时布展。', publisher_id = @admin_id, target_type = 'all', target_id = NULL, status = 'published', is_top = 1, view_count = 12 WHERE title = '全校社团开放日通知';

INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, publish_time, is_top, view_count)
SELECT '计算机协会工作坊提醒', '报名开源项目工作坊的同学请提前安装 Git 和 JDK。', @leader_cs_id, 'club', @club_cs_id, 'published', NOW(), 1, 5
WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title = '计算机协会工作坊提醒');
UPDATE announcements SET content = '报名开源项目工作坊的同学请提前安装 Git 和 JDK。', publisher_id = @leader_cs_id, target_type = 'club', target_id = @club_cs_id, status = 'published', is_top = 1, view_count = 5 WHERE title = '计算机协会工作坊提醒';

INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, publish_time, is_top, view_count)
SELECT '摄影社外拍延期通知', '因天气原因，校园夜景摄影活动顺延一天。', @leader_photo_id, 'club', @club_photo_id, 'published', NOW(), 0, 3
WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title = '摄影社外拍延期通知');
UPDATE announcements SET content = '因天气原因，校园夜景摄影活动顺延一天。', publisher_id = @leader_photo_id, target_type = 'club', target_id = @club_photo_id, status = 'published', is_top = 0, view_count = 3 WHERE title = '摄影社外拍延期通知';

INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, publish_time, is_top, view_count)
SELECT '草稿公告边界数据', '用于测试 draft 状态不会被误当成正式公告。', @admin_id, 'all', NULL, 'draft', NOW(), 0, 0
WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title = '草稿公告边界数据');
UPDATE announcements SET content = '用于测试 draft 状态不会被误当成正式公告。', publisher_id = @admin_id, target_type = 'all', target_id = NULL, status = 'draft', is_top = 0, view_count = 0 WHERE title = '草稿公告边界数据';

INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, publish_time, is_top, view_count)
SELECT '撤销公告边界数据', '用于测试 revoked 状态。', @admin_id, 'all', NULL, 'revoked', NOW(), 0, 1
WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title = '撤销公告边界数据');
UPDATE announcements SET content = '用于测试 revoked 状态。', publisher_id = @admin_id, target_type = 'all', target_id = NULL, status = 'revoked', is_top = 0, view_count = 1 WHERE title = '撤销公告边界数据';

INSERT INTO operation_logs(operator, module, action, description, ip, user_agent, status)
SELECT '系统管理员', '用户管理', '导入边界数据', '插入真实数据库边界用户、社团、活动与审批数据', '127.0.0.1', 'seed-script', 'success'
WHERE NOT EXISTS (SELECT 1 FROM operation_logs WHERE user_agent = 'seed-script' AND action = '导入边界数据');

INSERT INTO operation_logs(operator, module, action, description, ip, user_agent, status)
SELECT '系统管理员', '审批管理', '边界失败样例', '用于测试失败日志筛选', '127.0.0.1', 'seed-script', 'failed'
WHERE NOT EXISTS (SELECT 1 FROM operation_logs WHERE user_agent = 'seed-script' AND action = '边界失败样例');

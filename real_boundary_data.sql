USE `School-Club-Management-System`;

SET NAMES utf8mb4;

DELETE FROM operation_logs;
DELETE FROM approvals;
DELETE FROM activity_signups;
DELETE FROM recruitment_plans;
DELETE FROM activities;
DELETE FROM club_members;
DELETE FROM announcements;
DELETE FROM clubs;
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE clubs AUTO_INCREMENT = 1;
ALTER TABLE club_members AUTO_INCREMENT = 1;
ALTER TABLE activities AUTO_INCREMENT = 1;
ALTER TABLE recruitment_plans AUTO_INCREMENT = 1;
ALTER TABLE activity_signups AUTO_INCREMENT = 1;
ALTER TABLE approvals AUTO_INCREMENT = 1;
ALTER TABLE announcements AUTO_INCREMENT = 1;
ALTER TABLE operation_logs AUTO_INCREMENT = 1;

-- password for all seed accounts: 123456
INSERT INTO users (user_id, username, password, real_name, role, status, club_id, student_id, department, class_name, email, phone, register_time, last_login_time) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 'active', NULL, 'T0001', '校团委', '管理岗', 'admin@school.edu.cn', '13800000001', '2025-09-01 08:00:00', '2026-05-12 09:00:00'),
(2, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 'teacher', 'active', NULL, 'T1001', '计算机学院', '指导教师', 'teacher1@school.edu.cn', '13800000002', '2025-09-01 08:10:00', '2026-05-11 19:10:00'),
(3, 'leader_cs', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈思远', 'club_leader', 'active', 1, '2022001', '计算机学院', '软件工程1班', 'leader.cs@school.edu.cn', '13800000003', '2025-09-03 10:00:00', '2026-05-12 08:30:00'),
(4, 'leader_photo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵雨晴', 'club_leader', 'active', 2, '2022002', '艺术学院', '视觉传达1班', 'leader.photo@school.edu.cn', '13800000004', '2025-09-03 10:05:00', '2026-05-10 12:00:00'),
(5, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李小明', 'student', 'active', NULL, '2023001', '计算机学院', '软件工程2班', 'student1@school.edu.cn', '13800000005', '2025-09-10 12:00:00', '2026-05-12 10:12:00'),
(6, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙浩然', 'student', 'active', NULL, '2023002', '体育学院', '体教1班', 'student2@school.edu.cn', '13800000006', '2025-09-10 12:05:00', '2026-05-11 10:12:00'),
(7, 'student_empty', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周未入', 'student', 'active', NULL, '2023003', '外国语学院', '英语1班', 'student.empty@school.edu.cn', '13800000007', '2025-09-11 09:00:00', NULL),
(8, 'disabled_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '停用账号', 'student', 'inactive', NULL, '2023999', '测试学院', '边界班', 'disabled@school.edu.cn', '13800000999', '2025-09-11 09:00:00', NULL);

INSERT INTO clubs (club_id, club_name, description, founder_id, advisor_id, status, create_time) VALUES
(1, '计算机协会', '组织编程竞赛、技术分享和项目实践。', 3, 2, 'approved', '2025-09-05 09:00:00'),
(2, '摄影社', '校园影像记录、外拍和摄影展。', 4, 2, 'approved', '2025-09-06 09:00:00'),
(3, '篮球社', '篮球训练、院系联赛和新生杯。', 6, 2, 'approved', '2025-09-07 09:00:00'),
(4, '空白社团', '用于测试无成员、无活动边界。', NULL, 2, 'approved', '2026-01-01 09:00:00'),
(5, '机器人创新社', '待审批的新社团申请。', 5, 2, 'pending', '2026-05-01 09:00:00'),
(6, '旧动漫社', '被驳回的社团申请。', 5, 2, 'rejected', '2026-04-01 09:00:00');

INSERT INTO club_members (club_id, user_id, role, status, join_time) VALUES
(1, 3, 'president', 'active', '2025-09-05 09:30:00'),
(1, 5, 'member', 'active', '2025-09-12 14:00:00'),
(2, 4, 'president', 'active', '2025-09-06 09:30:00'),
(2, 5, 'core', 'active', '2025-10-01 18:00:00'),
(3, 6, 'president', 'active', '2025-09-07 09:30:00');

INSERT INTO activities (activity_id, club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status, create_time) VALUES
(1, 1, '编程马拉松大赛', '24小时团队开发，主题为校园智能服务。', '学术科技', 2, '2026-05-20 23:59:00', '陈思远', '13800000003', '2026-05-22 09:00:00', '2026-05-23 18:00:00', '信息楼A301', 'approved', '2026-05-01 10:00:00'),
(2, 2, '春季摄影外拍', '走进植物园进行春季摄影创作。', '文艺体育', 3, '2026-05-15 18:00:00', '赵雨晴', '13800000004', '2026-05-18 08:00:00', '2026-05-18 17:00:00', '植物园', 'approved', '2026-05-02 10:00:00'),
(3, 3, '新生杯篮球赛', '小组赛加淘汰赛制。', '文艺体育', 1, '2026-05-10 12:00:00', '孙浩然', '13800000006', '2026-05-13 14:00:00', '2026-05-13 18:00:00', '体育馆', 'approved', '2026-05-03 10:00:00'),
(4, 1, '技术分享会-已结束', 'AI工具链复盘分享。', '学术科技', 50, '2026-04-10 12:00:00', '陈思远', '13800000003', '2026-04-12 19:00:00', '2026-04-12 21:00:00', '信息楼B205', 'completed', '2026-04-01 10:00:00'),
(5, 2, '被取消的摄影展', '场地冲突取消。', '文艺体育', 30, '2026-05-12 12:00:00', '赵雨晴', '13800000004', '2026-05-16 09:00:00', '2026-05-16 17:00:00', '艺术楼展厅', 'cancelled', '2026-05-04 10:00:00'),
(6, 1, '待审批活动', '需要老师审核的活动。', '学术科技', 20, '2026-05-25 12:00:00', '陈思远', '13800000003', '2026-05-28 19:00:00', '2026-05-28 21:00:00', '线上会议', 'pending_approval', '2026-05-05 10:00:00');

INSERT INTO recruitment_plans (recruitment_id, club_id, title, quota, requirements, description, status, start_date, end_date) VALUES
(1, 1, '计算机协会夏季招新', 2, '对编程有兴趣即可。', '包含算法、前端、后端三个方向。', 'open', '2026-05-01', '2026-05-31'),
(2, 2, '摄影社补招', 1, '自备设备优先。', '新手也可报名。', 'open', '2026-05-01', '2026-05-20'),
(3, 3, '篮球社满员招新', 1, '有篮球基础。', '用于测试名额边界。', 'closed', '2026-04-01', '2026-04-30');

INSERT INTO activity_signups (activity_id, user_id, signup_time, checkin_time, status) VALUES
(1, 5, '2026-05-02 11:00:00', NULL, 'registered'),
(1, 6, '2026-05-02 11:05:00', NULL, 'registered'),
(2, 5, '2026-05-03 12:00:00', NULL, 'registered'),
(3, 6, '2026-05-04 13:00:00', NULL, 'registered'),
(4, 5, '2026-04-02 10:00:00', '2026-04-12 18:55:00', 'attended');

INSERT INTO approvals (approval_id, type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps, create_time) VALUES
(1, 'club_creation', 5, 5, NULL, 'pending', '申请成立机器人创新社', 1, 2, '2026-05-01 09:10:00'),
(2, 'club_creation', 6, 5, 2, 'rejected', '资料不完整', 2, 2, '2026-04-01 09:10:00'),
(3, 'activity_application', 6, 3, NULL, 'pending', '待审批活动申请', 1, 2, '2026-05-05 10:10:00'),
(4, 'recruitment_application', 1, 7, NULL, 'pending', '未加入任何社团的学生申请加入计算机协会', 1, 1, '2026-05-06 08:00:00'),
(5, 'recruitment_application', 2, 6, 2, 'approved', '摄影基础良好', 1, 1, '2026-05-07 08:00:00');

INSERT INTO announcements (announcement_id, title, content, publisher_id, target_type, target_id, status, is_top, view_count, publish_time) VALUES
(1, '系统维护通知', '系统将于周六凌晨进行维护，请提前保存资料。', 1, 'all', NULL, 'published', 1, 245, '2026-05-01 08:00:00'),
(2, '社团年审材料提交提醒', '各社团需在月底前提交年审材料。', 1, 'all', NULL, 'published', 1, 188, '2026-05-02 08:00:00'),
(3, '计算机协会招新说明会', '本周五晚举行招新说明会。', 3, 'club', 1, 'published', 0, 76, '2026-05-03 08:00:00'),
(4, '摄影社外拍注意事项', '请参加外拍的成员携带学生证。', 4, 'club', 2, 'published', 0, 53, '2026-05-04 08:00:00'),
(5, '草稿公告边界', '该公告处于草稿状态，不应作为正式公告重点展示。', 1, 'all', NULL, 'draft', 0, 0, '2026-05-05 08:00:00');

INSERT INTO operation_logs (operator, module, action, description, ip, user_agent, status, create_time) VALUES
('系统管理员', '用户管理', 'CREATE', '创建边界测试用户', '127.0.0.1', 'SeedScript', 'success', '2026-05-01 09:00:00'),
('张老师', '审批管理', 'REJECT', '驳回旧动漫社成立申请', '127.0.0.1', 'SeedScript', 'success', '2026-05-02 09:00:00'),
('陈思远', '活动管理', 'CREATE', '创建编程马拉松大赛', '127.0.0.1', 'SeedScript', 'success', '2026-05-03 09:00:00'),
('系统', '活动报名', 'SIGNUP_FULL', '新生杯篮球赛名额已满边界', '127.0.0.1', 'SeedScript', 'success', '2026-05-04 09:00:00'),
('系统', '登录', 'LOGIN_FAIL', '停用账号尝试登录', '127.0.0.1', 'SeedScript', 'failed', '2026-05-05 09:00:00');

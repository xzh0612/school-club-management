USE `School-Club-Management-System`;

DROP PROCEDURE IF EXISTS add_column_if_missing;

DELIMITER //
CREATE PROCEDURE add_column_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND COLUMN_NAME = p_column_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', p_table_name, '` ADD COLUMN ', p_column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL add_column_if_missing('users', 'status', "`status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '用户状态：active/inactive' AFTER `role`");
CALL add_column_if_missing('users', 'club_id', "`club_id` INT DEFAULT NULL COMMENT '负责人所属社团ID' AFTER `status`");
CALL add_column_if_missing('users', 'student_id', "`student_id` VARCHAR(50) DEFAULT NULL COMMENT '学号/工号' AFTER `club_id`");
CALL add_column_if_missing('users', 'department', "`department` VARCHAR(100) DEFAULT NULL COMMENT '院系' AFTER `student_id`");
CALL add_column_if_missing('users', 'class_name', "`class_name` VARCHAR(100) DEFAULT NULL COMMENT '班级' AFTER `department`");
CALL add_column_if_missing('users', 'register_time', "`register_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间' AFTER `class_name`");
CALL add_column_if_missing('users', 'last_login_time', "`last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间' AFTER `register_time`");

CALL add_column_if_missing('clubs', 'club_type', "`club_type` VARCHAR(50) NOT NULL DEFAULT 'general' COMMENT '社团类型' AFTER `description`");
CALL add_column_if_missing('activities', 'type', "`type` VARCHAR(50) DEFAULT NULL COMMENT '活动类型' AFTER `content`");
CALL add_column_if_missing('activities', 'max_participants', "`max_participants` INT DEFAULT NULL COMMENT '人数上限，NULL表示不限' AFTER `type`");
CALL add_column_if_missing('activities', 'registration_deadline', "`registration_deadline` DATETIME DEFAULT NULL COMMENT '报名截止时间' AFTER `max_participants`");
CALL add_column_if_missing('activities', 'organizer', "`organizer` VARCHAR(100) DEFAULT NULL COMMENT '活动负责人' AFTER `registration_deadline`");
CALL add_column_if_missing('activities', 'contact', "`contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式' AFTER `organizer`");

CALL add_column_if_missing('activity_signups', 'checkin_time', "`checkin_time` DATETIME DEFAULT NULL COMMENT '签到时间' AFTER `status`");

ALTER TABLE approvals
    MODIFY COLUMN type ENUM('club_creation','activity_application','recruitment_application') NOT NULL COMMENT '审批类型：社团成立、活动申请、入社申请';
CALL add_column_if_missing('approvals', 'current_step', "`current_step` INT NOT NULL DEFAULT 1 COMMENT '当前审批步骤' AFTER `comments`");
CALL add_column_if_missing('approvals', 'total_steps', "`total_steps` INT NOT NULL DEFAULT 1 COMMENT '总审批步骤' AFTER `current_step`");
CALL add_column_if_missing('approvals', 'update_time', "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `approval_time`");

CALL add_column_if_missing('announcements', 'status', "`status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '公告状态：draft/published/revoked' AFTER `target_id`");
CALL add_column_if_missing('announcements', 'view_count', "`view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数' AFTER `is_top`");
CALL add_column_if_missing('announcements', 'create_time', "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `view_count`");
CALL add_column_if_missing('announcements', 'update_time', "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`");

CALL add_column_if_missing('recruitments', 'requirements', "`requirements` TEXT DEFAULT NULL COMMENT '招新要求' AFTER `quota`");

UPDATE users u
LEFT JOIN club_members cm ON cm.user_id = u.user_id AND cm.role = 'leader' AND cm.status = 'active'
SET u.club_id = COALESCE(u.club_id, cm.club_id),
    u.status = COALESCE(NULLIF(u.status, ''), 'active'),
    u.register_time = COALESCE(u.register_time, u.create_time, NOW())
WHERE u.role = 'club_leader';

UPDATE users
SET status = COALESCE(NULLIF(status, ''), 'active'),
    register_time = COALESCE(register_time, create_time, NOW());

UPDATE clubs
SET club_type = CASE
        WHEN club_type IS NULL OR club_type = '' THEN CASE
            WHEN club_name LIKE '%计算机%' THEN 'academic'
            WHEN club_name LIKE '%摄影%' THEN 'culture'
            WHEN club_name LIKE '%篮球%' THEN 'sports'
            WHEN club_name LIKE '%志愿%' THEN 'volunteer'
            WHEN club_name LIKE '%机器人%' THEN 'innovation'
            ELSE 'general'
        END
        ELSE club_type
    END;

DROP PROCEDURE IF EXISTS add_column_if_missing;

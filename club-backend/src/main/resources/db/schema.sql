-- ==========================================
-- School Club Management System Schema
-- Aligned with current backend mappers
-- ==========================================

CREATE DATABASE IF NOT EXISTS club_management
  DEFAULT CHARSET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE club_management;

SET NAMES utf8mb4;

-- 1) Users
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'student',
    status VARCHAR(20) DEFAULT 'active',
    club_id INT DEFAULT NULL,
    student_id VARCHAR(30) DEFAULT NULL,
    department VARCHAR(100) DEFAULT NULL,
    class_name VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    register_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2) Clubs
DROP TABLE IF EXISTS clubs;
CREATE TABLE clubs (
    club_id INT PRIMARY KEY AUTO_INCREMENT,
    club_name VARCHAR(100) NOT NULL,
    description TEXT,
    club_type VARCHAR(50) NOT NULL DEFAULT 'general',
    founder_id INT DEFAULT NULL,
    advisor_id INT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3) Club members (used by test data and profile-related features)
DROP TABLE IF EXISTS club_members;
CREATE TABLE club_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    club_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'member',
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_club_user (club_id, user_id)
);

-- 4) Activities
DROP TABLE IF EXISTS activities;
CREATE TABLE activities (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    club_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(50) DEFAULT NULL,
    max_participants INT NOT NULL DEFAULT 50,
    registration_deadline DATETIME DEFAULT NULL,
    organizer VARCHAR(50) DEFAULT NULL,
    contact VARCHAR(100) DEFAULT NULL,
    start_time DATETIME DEFAULT NULL,
    end_time DATETIME DEFAULT NULL,
    location VARCHAR(200) DEFAULT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'draft',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5) Recruitment plans
DROP TABLE IF EXISTS recruitment_plans;
CREATE TABLE recruitment_plans (
    recruitment_id INT PRIMARY KEY AUTO_INCREMENT,
    club_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    quota INT NOT NULL,
    requirements TEXT,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'open',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6) Activity signups (reserved for signup features)
DROP TABLE IF EXISTS activity_signups;
CREATE TABLE activity_signups (
    signup_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT NOT NULL,
    user_id INT NOT NULL,
    signup_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkin_time DATETIME DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'registered',
    UNIQUE KEY uk_activity_user (activity_id, user_id)
);

-- 7) Approvals
DROP TABLE IF EXISTS approvals;
CREATE TABLE approvals (
    approval_id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    related_id INT DEFAULT NULL,
    applicant_id INT NOT NULL,
    approver_id INT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    comments TEXT,
    current_step INT NOT NULL DEFAULT 1,
    total_steps INT NOT NULL DEFAULT 4,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8) Announcements
DROP TABLE IF EXISTS announcements;
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    publisher_id INT NOT NULL,
    target_type VARCHAR(20) NOT NULL DEFAULT 'all',
    target_id INT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    is_top TINYINT(1) NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 9) Operation logs
DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operator VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    ip VARCHAR(50) DEFAULT NULL,
    user_agent VARCHAR(300) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'success',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_clubs_status ON clubs(status);
CREATE INDEX idx_activities_club_id ON activities(club_id);
CREATE INDEX idx_activities_status ON activities(status);
CREATE INDEX idx_recruitment_plans_club_id ON recruitment_plans(club_id);
CREATE INDEX idx_recruitment_plans_status ON recruitment_plans(status);
CREATE INDEX idx_approvals_status ON approvals(status);
CREATE INDEX idx_announcements_publish_time ON announcements(publish_time);
CREATE INDEX idx_operation_logs_create_time ON operation_logs(create_time);

-- Minimal bootstrap data (password: 123456, BCrypt encoded)
INSERT INTO users (username, password, real_name, role, email, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 'admin@school.edu.cn', '13800138000'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 'teacher', 'zhang@school.edu.cn', '13800138001'),
('leader1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '社团负责人', 'club_leader', 'leader@school.edu.cn', '13800138002'),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通学生', 'student', 'student@school.edu.cn', '13800138003');

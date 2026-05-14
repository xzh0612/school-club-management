-- ==========================================
-- School Club Management System Schema
-- Aligned with current backend mappers
-- ==========================================

CREATE DATABASE IF NOT EXISTS club_management
  DEFAULT CHARSET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE club_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
    token_version INT NOT NULL DEFAULT 0,
    register_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('admin', 'teacher', 'club_leader', 'student')),
    CONSTRAINT chk_users_status CHECK (status IN ('active', 'inactive'))
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
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_clubs_status CHECK (status IN ('pending', 'approved', 'rejected', 'inactive', 'archived'))
);

-- 3) Club members (used by test data and profile-related features)
DROP TABLE IF EXISTS club_members;
CREATE TABLE club_members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    club_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'member',
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_club_user (club_id, user_id),
    CONSTRAINT chk_club_members_role CHECK (role IN ('member', 'leader', 'club_leader')),
    CONSTRAINT chk_club_members_status CHECK (status IN ('active', 'inactive', 'removed')),
    CONSTRAINT fk_club_members_club FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    CONSTRAINT fk_club_members_user FOREIGN KEY (user_id) REFERENCES users(user_id)
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
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_activities_status CHECK (status IN ('draft', 'pending_approval', 'approved', 'rejected', 'completed', 'cancelled', 'archived')),
    CONSTRAINT chk_activities_capacity CHECK (max_participants > 0),
    CONSTRAINT chk_activities_time CHECK (start_time IS NULL OR end_time IS NULL OR end_time >= start_time),
    CONSTRAINT fk_activities_club FOREIGN KEY (club_id) REFERENCES clubs(club_id)
);

-- 5) Recruitment plans
DROP TABLE IF EXISTS activity_change_requests;
DROP TABLE IF EXISTS recruitments;
CREATE TABLE activity_change_requests (
    change_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT NOT NULL,
    requester_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(50) DEFAULT NULL,
    max_participants INT NOT NULL,
    registration_deadline DATETIME DEFAULT NULL,
    organizer VARCHAR(50) DEFAULT NULL,
    contact VARCHAR(100) DEFAULT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    location VARCHAR(200) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_activity_change_status CHECK (status IN ('pending', 'approved', 'rejected', 'archived')),
    CONSTRAINT chk_activity_change_capacity CHECK (max_participants > 0),
    CONSTRAINT chk_activity_change_time CHECK (end_time >= start_time),
    CONSTRAINT fk_activity_change_activity FOREIGN KEY (activity_id) REFERENCES activities(activity_id),
    CONSTRAINT fk_activity_change_requester FOREIGN KEY (requester_id) REFERENCES users(user_id)
);

-- 6) Recruitment plans
CREATE TABLE recruitments (
    recruitment_id INT PRIMARY KEY AUTO_INCREMENT,
    club_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    quota INT NOT NULL,
    requirements TEXT,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    start_time DATE NOT NULL,
    end_time DATE NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_recruitments_status CHECK (status IN ('active', 'closed', 'inactive', 'archived')),
    CONSTRAINT chk_recruitments_quota CHECK (quota > 0),
    CONSTRAINT chk_recruitments_time CHECK (end_time >= start_time),
    CONSTRAINT fk_recruitments_club FOREIGN KEY (club_id) REFERENCES clubs(club_id)
);

-- 7) Join applications
DROP TABLE IF EXISTS applications;
CREATE TABLE applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    club_id INT NOT NULL,
    recruitment_id INT DEFAULT NULL,
    introduction TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    comments TEXT,
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_applications_status CHECK (status IN ('pending', 'approved', 'rejected', 'cancelled', 'archived')),
    CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_applications_club FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    CONSTRAINT fk_applications_recruitment FOREIGN KEY (recruitment_id) REFERENCES recruitments(recruitment_id)
);

-- 8) Activity signups (reserved for signup features)
DROP TABLE IF EXISTS activity_signups;
CREATE TABLE activity_signups (
    signup_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT NOT NULL,
    user_id INT NOT NULL,
    signup_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkin_time DATETIME DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    CONSTRAINT chk_activity_signups_status CHECK (status IN ('pending', 'approved', 'cancelled', 'attended', 'rejected')),
    CONSTRAINT fk_activity_signups_activity FOREIGN KEY (activity_id) REFERENCES activities(activity_id),
    CONSTRAINT fk_activity_signups_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 9) Approvals
DROP TABLE IF EXISTS approval_histories;
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
    approval_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_approvals_type CHECK (type IN ('club_creation', 'activity_application')),
    CONSTRAINT chk_approvals_status CHECK (status IN ('pending', 'approved', 'rejected', 'archived')),
    CONSTRAINT chk_approvals_steps CHECK (current_step >= 1 AND total_steps >= current_step),
    CONSTRAINT fk_approvals_applicant FOREIGN KEY (applicant_id) REFERENCES users(user_id),
    CONSTRAINT fk_approvals_approver FOREIGN KEY (approver_id) REFERENCES users(user_id)
);

CREATE TABLE approval_histories (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    approval_id INT NOT NULL,
    step_no INT NOT NULL,
    operator_id INT DEFAULT NULL,
    action VARCHAR(30) NOT NULL,
    comments TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_approval_histories_action CHECK (action IN ('created', 'advanced', 'approved', 'rejected', 'archived')),
    CONSTRAINT fk_approval_histories_approval FOREIGN KEY (approval_id) REFERENCES approvals(approval_id),
    CONSTRAINT fk_approval_histories_operator FOREIGN KEY (operator_id) REFERENCES users(user_id)
);

-- 10) Announcements
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
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_announcements_target CHECK (target_type IN ('all', 'club')),
    CONSTRAINT chk_announcements_status CHECK (status IN ('draft', 'published', 'archived')),
    CONSTRAINT chk_announcements_view_count CHECK (view_count >= 0),
    CONSTRAINT fk_announcements_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id)
);

-- 11) Operation logs
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
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_operation_logs_status CHECK (status IN ('success', 'warning', 'failed', 'error'))
);

SET FOREIGN_KEY_CHECKS = 1;

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_clubs_status ON clubs(status);
CREATE INDEX idx_activities_club_id ON activities(club_id);
CREATE INDEX idx_activities_status ON activities(status);
CREATE INDEX idx_activity_change_activity_status ON activity_change_requests(activity_id, status);
CREATE INDEX idx_recruitments_club_id ON recruitments(club_id);
CREATE INDEX idx_recruitments_status ON recruitments(status);
CREATE INDEX idx_applications_club_status ON applications(club_id, status);
CREATE INDEX idx_applications_user_club_status ON applications(user_id, club_id, status);
CREATE INDEX idx_approvals_status ON approvals(status);
CREATE INDEX idx_announcements_publish_time ON announcements(publish_time);
CREATE INDEX idx_operation_logs_create_time ON operation_logs(create_time);

-- Demo users are intentionally not seeded by default. Use app.bootstrap.default-users for local demos only.

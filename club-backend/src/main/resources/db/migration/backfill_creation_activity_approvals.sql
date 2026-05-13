USE `School-Club-Management-System`;

DROP PROCEDURE IF EXISTS add_index_if_missing;

DELIMITER //
CREATE PROCEDURE add_index_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_index_name VARCHAR(64),
    IN p_index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', p_table_name, '` ADD INDEX `', p_index_name, '` ', p_index_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps)
SELECT
    'club_creation',
    c.club_id,
    c.founder_id,
    NULL,
    CASE WHEN c.status IN ('approved', 'rejected') THEN c.status ELSE 'pending' END,
    CONCAT('历史数据补齐：社团成立申请 - ', c.club_name),
    CASE WHEN c.status IN ('approved', 'rejected') THEN 2 ELSE 1 END,
    2
FROM clubs c
WHERE c.founder_id IS NOT NULL
  AND c.status IN ('pending', 'approved', 'rejected')
  AND NOT EXISTS (
      SELECT 1 FROM approvals ap
      WHERE ap.type = 'club_creation' AND ap.related_id = c.club_id
  );

INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps)
SELECT
    'activity_application',
    a.activity_id,
    COALESCE(c.founder_id, c.advisor_id),
    NULL,
    CASE WHEN a.status IN ('approved', 'rejected') THEN a.status ELSE 'pending' END,
    CONCAT('历史数据补齐：活动申请 - ', a.title),
    CASE WHEN a.status IN ('approved', 'rejected') THEN 2 ELSE 1 END,
    2
FROM activities a
INNER JOIN clubs c ON a.club_id = c.club_id
WHERE COALESCE(c.founder_id, c.advisor_id) IS NOT NULL
  AND a.status IN ('pending_approval', 'approved', 'rejected')
  AND NOT EXISTS (
      SELECT 1 FROM approvals ap
      WHERE ap.type = 'activity_application' AND ap.related_id = a.activity_id
  );

CALL add_index_if_missing('approvals', 'idx_approvals_type_related', '(type, related_id)');
CALL add_index_if_missing('announcements', 'idx_announcements_status_target', '(status, target_type, target_id)');
CALL add_index_if_missing('applications', 'idx_applications_user_club_status', '(user_id, club_id, status)');

DROP PROCEDURE IF EXISTS add_index_if_missing;

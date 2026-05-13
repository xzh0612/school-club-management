USE `School-Club-Management-System`;

INSERT INTO applications(user_id, club_id, recruitment_id, introduction, status, comments, apply_time, review_time)
SELECT
    ap.applicant_id,
    ap.related_id,
    (
        SELECT r.recruitment_id
        FROM recruitments r
        WHERE r.club_id = ap.related_id
        ORDER BY
            CASE r.status WHEN 'active' THEN 1 WHEN 'pending' THEN 2 ELSE 3 END,
            r.create_time DESC
        LIMIT 1
    ),
    ap.comments,
    ap.status,
    ap.comments,
    COALESCE(ap.create_time, NOW()),
    ap.approval_time
FROM approvals ap
WHERE ap.type = 'recruitment_application'
  AND NOT EXISTS (
      SELECT 1
      FROM applications app
      WHERE app.user_id = ap.applicant_id
        AND app.club_id = ap.related_id
        AND app.status = ap.status
  );

DELETE app
FROM applications app
JOIN applications keep_app
  ON keep_app.user_id = app.user_id
 AND keep_app.club_id = app.club_id
 AND keep_app.status = app.status
 AND keep_app.application_id < app.application_id;

DELETE FROM approvals WHERE type = 'recruitment_application';

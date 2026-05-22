INSERT INTO team (id, name, description)
VALUES (1, 'Default Team', 'Bookmark Hub default team');

INSERT INTO users (id, username, password_hash, display_name, email, status)
VALUES (
  1,
  'admin',
  '$2a$10$eLvgxTnwA1PML85QYk/8X.GufPZhqDpATJynafs5h1a8aQ7ABzwsq',
  'System Administrator',
  'admin@bookmarkhub.local',
  'ACTIVE'
);

INSERT INTO team_member (id, team_id, user_id, role, joined_at)
VALUES (1, 1, 1, 'ADMIN', CURRENT_TIMESTAMP);

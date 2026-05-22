CREATE TABLE import_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  operator_user_id BIGINT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  total_count INT NOT NULL,
  success_count INT NOT NULL,
  failed_count INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_import_record_team FOREIGN KEY (team_id) REFERENCES team (id),
  CONSTRAINT fk_import_record_operator FOREIGN KEY (operator_user_id) REFERENCES users (id)
);

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  parent_id BIGINT,
  name VARCHAR(100) NOT NULL,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_category_team FOREIGN KEY (team_id) REFERENCES team (id),
  CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id),
  CONSTRAINT fk_category_creator FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE bookmark (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  url VARCHAR(1000) NOT NULL,
  description VARCHAR(1000),
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bookmark_team FOREIGN KEY (team_id) REFERENCES team (id),
  CONSTRAINT fk_bookmark_category FOREIGN KEY (category_id) REFERENCES category (id),
  CONSTRAINT fk_bookmark_creator FOREIGN KEY (created_by) REFERENCES users (id)
);

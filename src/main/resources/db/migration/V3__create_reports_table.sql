CREATE TABLE reports (
     id BIGSERIAL PRIMARY KEY,
     project_id BIGINT NOT NULL,
     file_name VARCHAR(255) NOT NULL,
     version VARCHAR(50),
     status VARCHAR(20) NOT NULL,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     completed_at TIMESTAMP,
     FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
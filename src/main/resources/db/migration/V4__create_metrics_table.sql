CREATE TABLE metrics (
     id BIGSERIAL PRIMARY KEY,
     report_id BIGINT NOT NULL,
     artifact_type VARCHAR(20) NOT NULL,
     package_name VARCHAR(255),
     class_name VARCHAR(255),
     method_name VARCHAR(255),
     loc INT,
     num_methods INT,
     num_attributes INT,
     cyclomatic_complexity INT,
     cbo INT,
     dit INT,
     noc INT,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
);
CREATE TABLE todo (
  timestamp TIMESTAMP,
  title VARCHAR(255),
  date TIMESTAMP,
  message VARCHAR(255)
);

CREATE TABLE monitor (
  time BIGINT NOT NULL,
  host VARCHAR(64) NOT NULL,
  title VARCHAR(64) NOT NULL,
  level INT NOT NULL DEFAULT 0,
  value DOUBLE NOT NULL
);

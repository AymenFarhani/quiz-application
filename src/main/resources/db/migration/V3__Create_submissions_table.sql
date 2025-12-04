CREATE TABLE submissions
(
    id           SERIAL PRIMARY KEY,
    user_id      INT NOT NULL UNIQUE,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
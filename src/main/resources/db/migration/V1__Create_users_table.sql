CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    enabled  BOOLEAN             NOT NULL DEFAULT TRUE
);

INSERT INTO users (email, password, enabled)
VALUES ('john.doe@example.com', '$2a$10$QoZcq4zNtpTdGc1a8.uxlO5T88j3XvSPlZkwjaLAU6n7vUvlxdjxG', true);

INSERT INTO users (email, password, enabled)
VALUES ('sarah.connor@example.com', '$2a$10$QoZcq4zNtpTdGc1a8.uxlO5T88j3XvSPlZkwjaLAU6n7vUvlxdjxG', true);

INSERT INTO users (email, password, enabled)
VALUES ('mike.jordan@example.com', '$2a$10$QoZcq4zNtpTdGc1a8.uxlO5T88j3XvSPlZkwjaLAU6n7vUvlxdjxG', true);

INSERT INTO users (email, password, enabled)
VALUES ('emma.wilson@example.com', '$2a$10$QoZcq4zNtpTdGc1a8.uxlO5T88j3XvSPlZkwjaLAU6n7vUvlxdjxG', true);

INSERT INTO users (email, password, enabled)
VALUES ('david.miller@example.com', '$2a$10$QoZcq4zNtpTdGc1a8.uxlO5T88j3XvSPlZkwjaLAU6n7vUvlxdjxG', true);
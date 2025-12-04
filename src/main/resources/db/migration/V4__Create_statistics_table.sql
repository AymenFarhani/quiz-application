CREATE TABLE statistics
(
    id          SERIAL PRIMARY KEY,
    question_number INTEGER NOT NULL,
    go_count    INTEGER NOT NULL DEFAULT 0,
    no_go_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_statistics_question
        FOREIGN KEY (question_number)
            REFERENCES questions (id)
            ON DELETE CASCADE
);
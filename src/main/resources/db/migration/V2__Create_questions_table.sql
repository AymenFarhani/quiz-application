CREATE TABLE questions
(
    id   SERIAL PRIMARY KEY,
    text VARCHAR(500) NOT NULL
);

INSERT INTO questions (text)
VALUES ('Would you find it useful to have a physical or digital card dedicated to insurance reimbursements?'),
       ('Would a reimbursement card help you manage your healthcare expenses more easily?'),
       ('Would you prefer receiving reimbursements automatically on a dedicated card rather than your bank account?'),
       ('Would a reimbursement card encourage you to track your medical expenses more regularly?'),
       ('Would you feel more secure using a card exclusively for healthcare-related payments?'),
       ('Would you use a reimbursement card if it also provided a clear overview of your pending and completed refunds?'),
       ('Do you believe a reimbursement card would simplify administrative steps during reimbursements?'),
       ('Would you support replacing paper reimbursement documents with a digital service linked to a reimbursement card?'),
       ('Would you be comfortable receiving real-time notifications on the card for each reimbursement processed?'),
       ('If MLOZ introduced a reimbursement card, would you be likely to activate and use it?');

CREATE TABLE users
(
    email          TEXT NOT NULL,
    hashedPassword TEXT NOT NULL,
    role           TEXT NOT NULL,
    firstName      TEXT,
    lastName       TEXT,
    company        TEXT
);

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (email);

INSERT INTO users(email,
                  hashedPassword,
                  role,
                  firstName,
                  lastName,
                  company)
VALUES ('christopher@nolan.com', 'secret', 'ADMIN', 'Christopher', 'Nolan', 'Google');

INSERT INTO users(email,
                  hashedPassword,
                  role,
                  firstName,
                  lastName,
                  company)
VALUES ('johnny@depp.com', 'another_secret', 'RECRUITER', 'Johnny', 'Depp', 'Amazon');
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
VALUES ('christopher@nolan.com', '$2a$10$wjYnY4RXhmgIAuf6ZGVSiOeScly6.lzSTWpsLTxCoAM4QyK4C5Xr6', 'ADMIN', 'Christopher', 'Nolan', 'Google');

INSERT INTO users(email,
                  hashedPassword,
                  role,
                  firstName,
                  lastName,
                  company)
VALUES ('johnny@depp.com', '$2a$10$wjYnY4RXhmgIAuf6ZGVSiOeScly6.lzSTWpsLTxCoAM4QyK4C5Xr6', 'RECRUITER', 'Johnny', 'Depp', 'Amazon');

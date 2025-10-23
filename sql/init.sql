CREATE DATABASE board;
\c board;

CREATE TABLE jobs
(
    id          uuid             DEFAULT gen_random_uuid(),
    date        bigint  NOT NULL,
    ownerEmail  text    NOT NULL,
    company     text    NOT NULL,
    title       text    NOT NULL,
    description text    NOT NULL,
    externalUrl text    NOT NULL,
    location    text    NOT NULL,
    remote      boolean NOT NULL,
    salaryLow   integer,
    salaryHigh  integer,
    currency    text,
    country     text,
    tags        text[],
    image       text,
    seniority   text,
    other       text,
    active      boolean NOT NULL DEFAULT false
);

ALTER TABLE jobs
    ADD CONSTRAINT pk_jobs PRIMARY KEY (id);

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

CREATE TABLE tokens (
    email      text NOT NULL,
    token      text NOT NULL,
    expiration bigint NOT NULL
);

ALTER TABLE tokens
    ADD CONSTRAINT pk_tokens PRIMARY KEY(email);

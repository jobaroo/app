CREATE TABLE tokens (
    email text NOT NULL,
    token text NOT NULL,
    expiration bigint NOT NULL
);

ALTER TABLE tokens
    ADD CONSTRAINT pk_tokens PRIMARY KEY(email);

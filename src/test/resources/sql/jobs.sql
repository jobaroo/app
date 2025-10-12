CREATE TABLE jobs(
     id uuid DEFAULT gen_random_uuid(),
     date bigint NOT NULL,
     ownerEmail text NOT NULL,
     company text NOT NULL,
     title text NOT NULL,
     description text NOT NULL,
     externalUrl text NOT NULL,
     location text NOT NULL,
     remote boolean NOT NULL,
     salaryLow integer,
     salaryHigh integer,
     currency text,
     country text,
     tags text[],
     image text,
     seniority text,
     other text,
     active boolean NOT NULL DEFAULT false
);

ALTER TABLE jobs
ADD CONSTRAINT pk_jobs PRIMARY KEY (id);

INSERT INTO jobs(
    id,
    date,
    ownerEmail,
    company,
    title,
    description,
    externalUrl,
    location,
    remote,
    salaryLow,
    salaryHigh,
    currency,
    country,
    tags,
    image,
    seniority,
    other,
    active
) VALUES (
    '19a941d0-aa19-477b-9ab0-a7033ae65c2b',
    1659186086,
    'some@email.com',
    'Apple',
    'Software Engineer',
    'Cassandra Storage',
    'https://apple.com/something',
    'From remote',
    true,
    2000,
     3500,
    'EUR',
    'France',
    ARRAY [ 'scala', 'scala-3', 'cats', 'akka', 'spark', 'flink', 'zio'],
    NULL,
    'High',
    NULL,
    false
);
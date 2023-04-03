CREATE SCHEMA IF NOT EXISTS dictionary;

SET SCHEMA dictionary;

CREATE TABLE IF NOT EXISTS dictionary.languages
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR_IGNORECASE(50) UNIQUE
);

INSERT INTO dictionary.languages (name)
VALUES ('polish'),
       ('english');

CREATE TABLE IF NOT EXISTS dictionary.words
(
    id          UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    content     VARCHAR_IGNORECASE(50),
    language_id UUID
);
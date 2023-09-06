--drop table IF EXISTS records;

CREATE TABLE IF NOT EXISTS records (

  id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  app           VARCHAR(255) NOT NULL,
  uri           VARCHAR(255) NOT NULL,
  ip            VARCHAR(255) NOT NULL,
  timestamp     TIMESTAMP NOT NULL

);
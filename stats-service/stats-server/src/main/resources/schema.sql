CREATE TABLE IF NOT EXISTS stats
(
  id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  app VARCHAR(255) NOT NULL,
  ip  VARCHAR(15)  NOT NULL,
  timestamp TIMESTAMP    NOT NULL,
  uri      VARCHAR(512) NOT NULL
);

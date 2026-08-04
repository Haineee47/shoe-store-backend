CREATE TABLE repository_test_entities
(
    id      BINARY(16)   NOT NULL,
    version BIGINT       NOT NULL,
    name    VARCHAR(100) NOT NULL,

    CONSTRAINT pk_repository_test_entities
        PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

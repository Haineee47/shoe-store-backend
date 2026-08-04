CREATE TABLE auditing_test_entities
(
    id         BINARY(16)   NOT NULL,
    version    BIGINT       NOT NULL,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,

    CONSTRAINT pk_auditing_test_entities
        PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

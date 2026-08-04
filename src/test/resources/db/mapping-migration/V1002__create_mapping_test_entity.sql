CREATE TABLE mapping_test_entities
(
    id                 BINARY(16)   NOT NULL,
    version            BIGINT       NOT NULL,
    display_name       VARCHAR(120) NOT NULL,
    description        VARCHAR(500) NULL,
    status             VARCHAR(32)  NOT NULL,
    external_reference VARCHAR(64)  NOT NULL,
    details_note       VARCHAR(255) NULL,

    CONSTRAINT pk_mapping_test_entities
        PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

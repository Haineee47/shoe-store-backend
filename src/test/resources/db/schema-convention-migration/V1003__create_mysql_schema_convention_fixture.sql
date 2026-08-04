CREATE TABLE mysql_schema_test_records
(
    id          BINARY(16)    NOT NULL,
    version     BIGINT        NOT NULL,
    record_code VARCHAR(64)   NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    quantity    INT           NOT NULL,
    amount      DECIMAL(19, 2) NOT NULL,
    active      BOOLEAN       NOT NULL,
    status      VARCHAR(32)   NOT NULL,
    occurred_at DATETIME(6)   NOT NULL,
    optional_note VARCHAR(255) NULL,

    CONSTRAINT pk_mysql_schema_test_records
        PRIMARY KEY (id),

    CONSTRAINT uk_mysql_schema_test_records__record_code
        UNIQUE (record_code),

    CONSTRAINT ck_mysql_schema_test_records__quantity_non_negative
        CHECK (quantity >= 0),

    CONSTRAINT ck_mysql_schema_test_records__amount_non_negative
        CHECK (amount >= 0),

    CONSTRAINT ck_mysql_schema_test_records__status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),

    INDEX ix_mysql_schema_test_records__occurred_at (occurred_at)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

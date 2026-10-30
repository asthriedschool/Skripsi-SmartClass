-- Manual, additive, and repeatable for MySQL/MariaDB.
-- Confirm the target database and table before running. Existing rows remain
-- unchanged because all newly added columns are nullable.
-- No approved_by foreign key is added because the live SQL types and
-- constraints for tb_users.id_user have not been verified.

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'tb_schedules'
       AND COLUMN_NAME = 'requester_name') = 0,
    'ALTER TABLE tb_schedules ADD COLUMN requester_name VARCHAR(100) NULL',
    'SELECT ''requester_name already exists'''
);
PREPARE migration_statement FROM @migration_sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'tb_schedules'
       AND COLUMN_NAME = 'approved_by') = 0,
    'ALTER TABLE tb_schedules ADD COLUMN approved_by BIGINT NULL',
    'SELECT ''approved_by already exists'''
);
PREPARE migration_statement FROM @migration_sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'tb_schedules'
       AND COLUMN_NAME = 'approved_at') = 0,
    'ALTER TABLE tb_schedules ADD COLUMN approved_at DATETIME NULL',
    'SELECT ''approved_at already exists'''
);
PREPARE migration_statement FROM @migration_sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

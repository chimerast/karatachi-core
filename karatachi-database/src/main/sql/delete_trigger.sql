CREATE TABLE system.deleted (
  schema_name VARCHAR(64) NOT NULL,
  table_name VARCHAR(64) NOT NULL,
  pkey BIGINT NOT NULL
);

CREATE OR REPLACE FUNCTION mark_deleted() RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO system.deleted VALUES(TG_TABLE_SCHEMA, TG_TABLE_NAME, OLD.id);
  RETURN NULL;
END
$$ LANGUAGE plpgsql;

CREATE TABLE test (
  id SERIAL PRIMARY KEY,
  value TEXT
);

DROP TRIGGER mark_delete_on_test ON test;
CREATE TRIGGER mark_delete_on_test AFTER DELETE ON test
  FOR EACH ROW EXECUTE PROCEDURE mark_deleted();

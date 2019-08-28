BEGIN;
CREATE OR REPLACE FUNCTION insert_layer()
RETURNS event_trigger
AS $$
    DECLARE r RECORD;
    BEGIN
            --RAISE NOTICE 'event for % ', tg_tag;
            -- I would like to execute this
            RAISE LOG 'ddl command';
            FOR r IN SELECT * FROM pg_event_trigger_ddl_commands() LOOP
                RAISE LOG 'ELEMENTS %, %, %, % ', r.command_tag, r.object_type, r.schema_name, r.object_identity;
                IF r.object_type = 'table' THEN
                    PERFORM create_distributed_table(r.object_identity, 'fid');
                END IF; 
                        
            END LOOP;
    END;
$$
LANGUAGE plpgsql;

CREATE EVENT TRIGGER insert_layer_event ON ddl_command_end
WHEN TAG IN ('CREATE TABLE')
EXECUTE PROCEDURE insert_layer();
COMMIT;
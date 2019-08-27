#!/bin/sh

set -e

# Perform all actions as $POSTGRES_USER
export PGUSER="$POSTGRES_USER"

# Create the 'template_postgis' template db
"${psql[@]}" <<- 'EOSQL'
CREATE DATABASE template_postgis;
UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template_postgis';
EOSQL

# Load PostGIS into both template_database and $POSTGRES_DB
for DB in template_postgis "$POSTGRES_DB"; do
	echo "Loading PostGIS extensions into $DB"
	"${psql[@]}" --dbname="$DB" <<-'EOSQL'
		CREATE EXTENSION IF NOT EXISTS postgis;
		CREATE EXTENSION IF NOT EXISTS postgis_topology;
		CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
		CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;
        CREATE OR REPLACE FUNCTION insert_layer()
        RETURNS event_trigger
        AS $$
            DECLARE r RECORD;
            BEGIN
                    RAISE NOTICE 'event for % ', tg_tag;
                    -- I would like to execute this
                    r := pg_event_trigger_ddl_commands(); 
                    SELECT create_distributed_table(r.object_identity, 'id');
            END;
        $$
        LANGUAGE plpgsql;

        CREATE EVENT TRIGGER insert_layer_event ON ddl_command_end
        WHEN TAG IN ('CREATE TABLE')
        EXECUTE PROCEDURE insert_layer();

        CREATE EXTENSION IF NOT EXISTS citus;

        -- add Docker flag to node metadata
        UPDATE pg_dist_node_metadata SET metadata=jsonb_insert(metadata, '{docker}', 'true');
        COMMIT;

EOSQL
done

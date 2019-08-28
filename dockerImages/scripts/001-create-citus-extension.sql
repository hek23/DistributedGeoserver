BEGIN;
-- CONNECT geoserver;
-- wrap in transaction to ensure Docker flag always visible

CREATE EXTENSION IF NOT EXISTS citus;

-- add Docker flag to node metadata
UPDATE pg_dist_node_metadata SET metadata=jsonb_insert(metadata, '{docker}', 'true');


CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;


--CREATE EXTENSION IF NOT EXISTS citus;

-- add Docker flag to node metadata
--UPDATE pg_dist_node_metadata SET metadata=jsonb_insert(metadata, '{docker}', 'true');
COMMIT;

--SELECT run_command_on_workers($cmd$ CREATE USER docker WITH ENCRYPTED PASSWORD 'docker' $cmd$);
--SELECT run_command_on_workers($cmd$ GRANT ALL PRIVILEGES ON DATABASE postgres TO docker; $cmd$);


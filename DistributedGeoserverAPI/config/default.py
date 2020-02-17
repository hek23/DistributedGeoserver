DBNAME = "gis"
DBUSER = "docker"
DBHOST = "mainPostgres"
DBPWD = "docker"
DBPORT = 5432
GEOSERVER = "http://localhost:8600/geoserver"
GEOSERVERUSR = "admin"
GEOSERVERPWD = "geoserver"
REDISHOST = ""
REDISPORT = ""
REDISDB = "1"
PORT = 5000
THREADS = 8
CONN_STRING = "dbname='{}' user='{}' password='{}' host='{}' port='{}'".format(DBNAME, DBUSER, DBPWD, DBHOST, DBPORT)

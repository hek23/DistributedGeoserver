DBNAME = "gis"
DBUSER = "docker"
DBHOST = "mainPostgres"
DBPWD = "docker"
DBPORT = 5432
GEOSERVER = "http://geoservertester:8080/geoserver"
GEOSERVERUSR = "admin"
GEOSERVERPWD = "geoserver"
PORT = 8000
THREADS = 2
CONN_STRING = "dbname='{}' user='{}' password='{}' host='{}' port='{}'".format(DBNAME, DBUSER, DBPWD, DBHOST, DBPORT)

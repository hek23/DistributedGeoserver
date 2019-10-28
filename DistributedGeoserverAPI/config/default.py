DBNAME = "gis"
DBUSER = "docker"
DBHOST = "mainpostgres"
DBPWD = "docker"
DBPORT = 5432
GEOSERVER = "http://geoserver:8080/geoserver"
GEOSERVERUSR = "admin"
GEOSERVERPWD = "geoserver"
PORT = 5000
THREADS = 2
CONN_STRING = "dbname='{}' user='{}' password='{}' host='{}' port='{}'".format(DBNAME, DBUSER, DBPWD, DBHOST, DBPORT)
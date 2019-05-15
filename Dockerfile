FROM alpine AS unzipper
RUN mkdir importer && cd importer && wget https://sourceforge.net//projects/geoserver/files/GeoServer/2.15.0/extensions/geoserver-2.15.0-importer-plugin.zip -O importer.zip && unzip importer.zip
FROM kartoza/geoserver:2.15.0
COPY --from=unzipper /importer /usr/local/tomcat/webapps/geoserver/WEB-INF/lib
FROM alpine AS unzipper
RUN mkdir importer && cd importer && wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.0/extensions/geoserver-2.15.0-importer-plugin.zip -O importer.zip && unzip importer.zip
RUN wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.0/extensions/geoserver-2.15.0-wps-plugin.zip -O wps.zip && unzip wps.zip
RUN wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.0/extensions/geoserver-2.15.0-wps-cluster-hazelcast-plugin.zip -O wps-hazelcast-cluster.zip && unzip wps-hazelcast-cluster.zip
RUN rm *.zip
FROM kartoza/geoserver:2.15.0
COPY --from=unzipper /importer /usr/local/tomcat/webapps/geoserver/WEB-INF/lib
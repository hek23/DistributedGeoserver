FROM alpine AS unzipper
RUN mkdir importer && cd importer 
RUN wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.2/extensions/geoserver-2.15.2-importer-plugin.zip -O importer.zip 
RUN wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.2/extensions/geoserver-2.15.2-wps-plugin.zip -O wps.zip 
RUN wget https://sourceforge.net/projects/geoserver/files/GeoServer/2.15.2/extensions/geoserver-2.15.2-wps-cluster-hazelcast-plugin.zip -O wps-hazelcast-cluster.zip
RUN mkdir /importer/pluginImport 
RUN unzip importer.zip -d /importer/pluginImport
RUN mkdir /importer/wps && unzip wps.zip -d /importer/wps
RUN mkdir /importer/hazelcast && unzip wps-hazelcast-cluster.zip -d /importer/hazelcast
RUN rm *.zip
FROM oscarfonts/geoserver:2.15.2
COPY --from=unzipper /importer /var/local/geoserver-exts/

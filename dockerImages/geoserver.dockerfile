FROM alpine AS unzipper
RUN mkdir importer && cd importer 
ADD https://ufpr.dl.sourceforge.net/project/geoserver/GeoServer/2.15.2/extensions/geoserver-2.15.2-importer-plugin.zip ./importer.zip 
ADD https://ufpr.dl.sourceforge.net/project/geoserver/GeoServer/2.15.2/extensions/geoserver-2.15.2-wps-plugin.zip ./wps.zip 
ADD https://ufpr.dl.sourceforge.net/project/geoserver/GeoServer/2.15.2/extensions/geoserver-2.15.2-wps-cluster-hazelcast-plugin.zip ./wps-hazelcast-cluster.zip
RUN ls -la
RUN mkdir /importer/pluginImport 
RUN unzip importer.zip -d /importer/pluginImport
RUN mkdir /importer/wps && unzip wps.zip -d /importer/wps
RUN mkdir /importer/hazelcast && unzip wps-hazelcast-cluster.zip -d /importer/hazelcast
RUN rm *.zip
FROM oscarfonts/geoserver:2.15.2
COPY --from=unzipper /importer /var/local/geoserver-exts/

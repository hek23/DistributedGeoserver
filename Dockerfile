FROM kartoza/geoserver:2.15.0
#Install JMS Plugin 
ADD https://build.geoserver.org/geoserver/2.15.x/community-latest/geoserver-2.15-SNAPSHOT-jms-cluster-plugin.zip /tmp/resources/plugins/
RUN ls /tmp/resources/plugins
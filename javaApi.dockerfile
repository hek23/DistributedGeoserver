FROM gradle:alpine

ADD  --chown=gradle:gradle ./DistributedGeoserverAPI/src /API/src
ADD  --chown=gradle:gradle ./DistributedGeoserverAPI/build.gradle /API/build.gradle
ADD  --chown=gradle:gradle ./DistributedGeoserverAPI/gradlew /API/gradlew
ADD  --chown=gradle:gradle ./DistributedGeoserverAPI/settings.gradle /API/settings.gradle
ADD  --chown=gradle:gradle ./DistributedGeoserverAPI/gradle /API/gradle
WORKDIR /API
USER root  
RUN chown -R gradle /API
USER gradle
RUN gradle build --stacktrace
ENTRYPOINT java -jar /API/build/libs/DistributedGeoserverAPI-0.0.1-SNAPSHOT.war
#RUN java -jar build/libs/DistributedGeoserverAPI-0.0.1-SNAPSHOT.jar
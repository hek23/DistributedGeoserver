FROM maven:3.5.2-jdk-8-alpine AS MAVEN_TOOL_CHAIN
RUN mkdir /API
COPY ./DistributedGeoserverAPI/src /API/src
COPY ./DistributedGeoserverAPI/pom.xml /API/pom.xml
WORKDIR /API
RUN mvn package

FROM openjdk:8-jre-alpine 
COPY --from=MAVEN_TOOL_CHAIN /API/target/DistributedGeoprocessing-0.0.1-SNAPSHOT.jar ./distAPI.jar
EXPOSE 8080
ENTRYPOINT java -jar ./distAPI.jar
#RUN java -jar build/libs/DistributedGeoserverAPI-0.0.1-SNAPSHOT.jar
#FROM openjdk:11-jdk-alpine
FROM openjdk:11
COPY build/libs/*.jar dockerservice.jar
ENTRYPOINT ["java","-jar","/dockerservice.jar"]

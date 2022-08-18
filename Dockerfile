FROM openjdk:11-jdk
COPY build/libs/api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
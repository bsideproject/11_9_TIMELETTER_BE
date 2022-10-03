FROM openjdk:11-jdk


ENV TZ=Asia/Seoul
RUN apt-get install -y tzdata

COPY build/libs/api-0.0.1-SNAPSHOT.jar app.jar
ARG ENVIRONMENT
ENV SPRING_PROFILES_ACTIVE=${ENVIRONMENT}
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
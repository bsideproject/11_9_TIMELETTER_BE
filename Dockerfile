FROM openjdk:11-jdk
COPY build/libs/api-0.0.1-SNAPSHOT.jar app.jar

RUN apk add --update tzdata
ARG ENVIRONMENT
ENV SPRING_PROFILES_ACTIVE=${ENVIRONMENT}


ENV TZ=Asia/Seoul
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
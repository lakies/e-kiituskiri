FROM openjdk:11.0.7-jdk-slim
COPY target/kirjad-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
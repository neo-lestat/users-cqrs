FROM eclipse-temurin:21-jdk-alpine
RUN mkdir /opt/app
COPY build/libs/users-api-0.0.1-SNAPSHOT.jar /opt/app/users-api.jar
CMD ["java", "-jar", "/opt/app/users-api.jar"]
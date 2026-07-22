FROM eclipse-temurin:25-jdk-alpine
RUN mkdir /opt/app
COPY build/libs/users-command-0.0.1-SNAPSHOT.jar /opt/app/users-command.jar
CMD ["java", "-jar", "/opt/app/users-command.jar"]
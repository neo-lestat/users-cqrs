FROM eclipse-temurin:21-jdk-alpine
RUN mkdir /opt/app
COPY build/libs/users-cud-0.0.1-SNAPSHOT.jar /opt/app/users-cud.jar
CMD ["java", "-jar", "/opt/app/users-cud.jar"]
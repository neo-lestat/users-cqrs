# Getting Started

### Requirements
For test this project you will need

* java 21
* docker
* gradle optional

### Guides

To build the root project 

`./gradlew build` 

To test the project run `docker compose up` at root

Then execute the services with the following command in each subproject

`./gradlew bootRun` 

or run the jar file generated by the build command with:
- `java -jar users-api/build/libs/users-api-0.0.1-SNAPSHOT.jar`
- `java -jar users-cud/build/libs/users-cud-0.0.1-SNAPSHOT.jar`
- `java -jar users-read/build/libs/users-read-0.0.1-SNAPSHOT.jar`

Another way to run the services is to build docker images with `docker-image.sh` script 
in each subproject and run them with `docker compose -f docker-compose-users-services.yml up`

There is **swagger ui** at http://localhost:8080/swagger-ui/index.html available
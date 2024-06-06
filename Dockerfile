#
# Build stage
#
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build
WORKDIR /web-ui
COPY pom.xml .
RUN mvn verify
COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip=true"]

#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /web-ui
COPY --from=build /web-ui/target/*.jar web-ui.jar
ENTRYPOINT ["java", "-jar", "web-ui.jar" ]
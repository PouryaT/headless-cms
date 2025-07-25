FROM openjdk:17-jdk-alpine as build

WORKDIR /app

COPY target/headless-cms-0.0.1-SNAPSHOT.jar app.jar

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
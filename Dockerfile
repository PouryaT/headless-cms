# Step 1: Build the Spring Boot app
FROM openjdk:17-jdk-alpine as build

WORKDIR /app

# Copy the jar file from target
COPY target/headless-cms-0.0.1-SNAPSHOT.jar app.jar

# Step 2: Run the Spring Boot app
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/app.jar app.jar

EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]

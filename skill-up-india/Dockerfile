# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copy the source code
COPY src src

# Build the application and create the JAR file
# Skipping tests to speed up the build process in this context
RUN mvn package -DskipTests

# Stage 2: Create the final runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Expose the port the application runs on
EXPOSE 8080

# Copy the JAR file from the build stage
COPY --from=build /app/target/skill-up-india-0.0.1-SNAPSHOT.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

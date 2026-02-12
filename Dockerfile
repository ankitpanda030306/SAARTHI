# Stage 1: Build the Application
# CHANGED: Using JDK 21 to match your project version
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# CHANGED: Removed '-Pproduction' to fix the profile error
# Running standard clean package
RUN mvn clean package -DskipTests

# Stage 2: Run the Application
# CHANGED: Using JRE 21 to match the build
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
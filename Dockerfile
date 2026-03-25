# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Build the app in production mode (optimizes Vaadin frontend)
# This may take a few minutes as it downloads Node.js and dependencies
RUN mvn clean package -Pproduction -DskipTests

# Stage 2: Run the Application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
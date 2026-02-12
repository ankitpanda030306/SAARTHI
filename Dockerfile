# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Skip tests and build the jar
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

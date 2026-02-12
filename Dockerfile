# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# CRITICAL CHANGE: Added '-Pproduction' 
# This tells Vaadin to compile the index.html and JS files into the classpath
RUN mvn clean package -Pproduction -DskipTests

# Stage 2: Run the Application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

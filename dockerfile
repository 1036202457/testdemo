# Dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/demo-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
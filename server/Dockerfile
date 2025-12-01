FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ../shareit-server/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
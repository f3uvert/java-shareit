FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем JAR файл
COPY target/shareit-gateway-*.jar app.jar

EXPOSE 8080

# Простой запуск
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM openjdk:17-jdk-slim

WORKDIR /app


COPY wait-for-postgres.sh /wait-for-postgres.sh
RUN chmod +x /wait-for-postgres.sh


COPY target/shareit-server-*.jar app.jar

EXPOSE 9090

# Ждем PostgreSQL, затем запускаем приложение
ENTRYPOINT ["/wait-for-postgres.sh", "postgres", "java", "-jar", "app.jar"]
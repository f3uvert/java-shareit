#!/bin/bash

# Создаем директории которые ожидаются
mkdir -p gateway server

# Копируем JAR файлы в ожидаемые места
cp shareit-gateway/target/*.jar gateway/app.jar 2>/dev/null || true
cp shareit-server/target/*.jar server/app.jar 2>/dev/null || true

# Создаем простые Dockerfile
cat > gateway/Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

cat > server/Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY app.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Даем права
chmod a+x ./tests/.github/workflows/wait-for-it.sh

# Запускаем
docker compose -f docker-compose.yml up --detach &&
echo "Docker is up" &&
./tests/.github/workflows/wait-for-it.sh -t 60 localhost:9090 &&
echo "Server is up" &&
./tests/.github/workflows/wait-for-it.sh -t 60 localhost:8080 &&
echo "Gateway is up"
result=$?
docker compose -f docker-compose.yml logs
exit $result
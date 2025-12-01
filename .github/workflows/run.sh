#!/bin/bash

# Создаем необходимые директории которые ожидают тесты
mkdir -p gateway server

# Создаем символические ссылки на реальные директории
ln -sfn shareit-gateway gateway
ln -sfn shareit-server server

# Копируем Dockerfile если нужно
if [ ! -f "gateway/Dockerfile" ] && [ -f "shareit-gateway/Dockerfile" ]; then
    cp shareit-gateway/Dockerfile gateway/
fi

if [ ! -f "server/Dockerfile" ] && [ -f "shareit-server/Dockerfile" ]; then
    cp shareit-server/Dockerfile server/
fi

# Даем права на выполнение wait-for-it.sh
chmod a+x ./tests/.github/workflows/wait-for-it.sh

# Запускаем docker compose
docker compose -f docker-compose.yml up --detach

echo "Docker is up"

# Ждем пока сервер запустится
./tests/.github/workflows/wait-for-it.sh -t 60 localhost:9090
echo "Server is up"

# Ждем пока gateway запустится
./tests/.github/workflows/wait-for-it.sh -t 60 localhost:8080
echo "Gateway is up"

result=$?

# Показываем логи
docker compose -f docker-compose.yml logs

# Останавливаем контейнеры
docker compose -f docker-compose.yml down

exit $result
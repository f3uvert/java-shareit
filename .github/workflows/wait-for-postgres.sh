#!/bin/bash
# wait-for-postgres.sh

set -e

host="$1"
shift
cmd="$@"

echo "Waiting for PostgreSQL at $host:5432..."

# Ждем пока PostgreSQL станет доступен
until PGPASSWORD=shareit psql -h "$host" -U shareit -d shareit -c '\q'; do
  >&2 echo "PostgreSQL is unavailable - sleeping"
  sleep 2
done

>&2 echo "PostgreSQL is up - executing command"
exec $cmd
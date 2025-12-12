# Используем Ubuntu как базу (можно заменить на alpine для меньшего размера)
FROM ubuntu:22.04

# Установим необходимые пакеты
RUN apt-get update && apt-get install -y \
    awscli \
    postgresql-client \
    cron \
    gzip \
    mailutils \
    && rm -rf /var/lib/apt/lists/*

# Создадим директорию для скриптов
RUN mkdir -p /app

# Скопируем скрипт бэкапа
COPY backup.sh /app/backup.sh
RUN chmod +x /app/backup.sh

# Настроим cron для ежедневного запуска в 02:00
RUN echo "0 2 * * * /app/backup.sh >> /var/log/backup.log 2>&1" > /etc/cron.d/backup-cron \
    && chmod 0644 /etc/cron.d/backup-cron \
    && crontab /etc/cron.d/backup-cron

# Создадим лог-файл
RUN touch /var/log/backup.log

# Entrypoint: запустим cron в фоне и держим контейнер живым
CMD cron -f
